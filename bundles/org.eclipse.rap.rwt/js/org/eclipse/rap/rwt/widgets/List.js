/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class extends qx.ui.form.List to make its API more SWT-like.
 */
qx.OO.defineClass( 
  "org.eclipse.rap.rwt.widgets.List", 
  qx.ui.form.List,
  function( multiSelection ) {
    qx.ui.form.List.call( this );
    // Should changeSelection events passed to the server-side? 
    // state == no, action == yes
    this._changeSelectionNotification = "state";
    this.setMarkLeadingItem( true );
    // qx.manager.selection.SelectionManager
    var manager = this.getManager();
    manager.setMultiSelection( multiSelection );
    manager.addEventListener( "changeSelection", this._onChangeSelection, this );
    manager.addEventListener( "changeLeadItem", this._onChangeLeadItem, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
  } );

/** Sets the given aray of items. */
qx.Proto.setItems = function( items ) {
  // preserve selection and focused item
  var manager = this.getManager();
  var oldLeadItem = manager.getLeadItem();
  var oldAnchorItem = manager.getAnchorItem();
  var oldSelection = manager.getSelectedItems();
  // exchange/add/remove items
  var oldItems = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    if( i < oldItems.length ) {
      oldItems[ i ].setLabel( items[ i ] );
    } else {
      // TODO [rh] optimize this: context menu should be handled by the List
      //      itself for all its ListItems
      var item = new qx.ui.form.ListItem( items[ i ] );
      // prevent items from being drawn outside the list
      item.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN ); 
      item.setContextMenu( this.getContextMenu() );
      this.add( item );
    }
  }
  while( this.getChildrenLength() > items.length ) {
    this.removeAt( this.getChildrenLength() - 1 );
  }
  // restore previous selection and focusItem
  manager.setSelectedItems( oldSelection );
  manager.setLeadItem( oldLeadItem );
  if( manager.getMultiSelection() ) {
    manager.setAnchorItem( oldAnchorItem );
  }
}

/**
 * Sets the single selection for the List to the item specified by the given 
 * itemIndex (-1 to clear selection).
 */
qx.Proto.selectItem = function( itemIndex ) {
  if( itemIndex == -1 ) {
    this.getManager().deselectAll();
  } else {
    var item = this.getChildren()[ itemIndex ];
    this.getManager().setSelectedItem( item );  
    // TODO [rh] second parameter has no effect, figure out what it is for
    this.getManager().scrollItemIntoView( item, true );
  }
}

/**
 * Sets the multi selection for the List to the items specified by the given 
 * itemIndices array (empty array to clear selection).
 */
qx.Proto.selectItems = function( itemIndices ) {
  if( itemIndices.length == 0 ){
    this.getManager().deselectAll();
  } else {
    for( var i = 0; i < itemIndices.length; i++ ) {    
      var item = this.getChildren()[ itemIndices[ i ] ];
      this.getManager().setItemSelected( item, true );  
    }
  }
}

/**
 * Sets the focused item the List to the item specified by the given 
 * itemIndex (-1 for no focused item).
 */
qx.Proto.focusItem = function( itemIndex ) {
  if( itemIndex == -1 ) {
    this.getManager().setLeadItem( null );
  } else {
    var items = this.getManager().getItems();
    this.getManager().setLeadItem( items[ itemIndex ] );
  }
}

/**
 * Selects all item if the List is multi-select. Does nothing for single-
 * select Lists.
 */
qx.Proto.selectAll = function() {
  if( this.getManager().getMultiSelection() == true ) {
    this.getManager().selectAll();
  }
}

qx.Proto.setChangeSelectionNotification = function( value ) {
  this._changeSelectionNotification = value;
}

qx.Proto.setFont = function( value ) {
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var items = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    wm.setFont( items[ i ], 
                value.getName(), 
                value.getSize(), 
                value.getBold(), 
                value.getItalic() );
  }
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  var manager = this.getManager();
  manager.removeEventListener( "changeSelection", this._onChangeSelection, this );
  manager.removeEventListener( "changeLeadItem", this._onChangeLeadItem, this );
  this.removeEventListener( "focus", this._onFocusIn, this );
  this.removeEventListener( "blur", this._onFocusOut, this );
  this.removeEventListener( "click", this._onClick, this );
  this.removeEventListener( "dblclick", this._onDblClick, this );
  this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
  return qx.ui.form.List.prototype.dispose.call( this );
}

qx.Proto._onChangeSelection = function( evt ) {
  this._updateSelectedItemState();
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = wm.findIdByWidget( this );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( id + ".selection", this._getSelectionIndices() );
  if( this._changeSelectionNotification == "action" ) {
    this._suspendClicks();
    req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
    req.send();
  }
}

qx.Proto._getSelectionIndices = function() {
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = wm.findIdByWidget( this );
  var selectionIndices = "";
  var selectedItems = this.getManager().getSelectedItems();
  for( var i = 0; i < selectedItems.length; i++ ) {
    var index = this.indexOf( selectedItems[ i ] );
    // TODO [rh] find out why sometimes index == -1, cannot be reproduced
    //      in standalone qooxdoo application
    if( index >= 0 ) {  
      if( selectionIndices != "" ) {
        selectionIndices += ",";
      }
      selectionIndices += String( index );
    }
  }
  return selectionIndices;
}

qx.Proto._onChangeLeadItem = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var focusIndex = this.indexOf( this.getManager().getLeadItem() );
    req.addParameter( id + ".focusIndex", focusIndex );
  }
}

qx.Proto._onClick = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( this._changeSelectionNotification == "action" ) {
      if( !this._clicksSuspended ) {
        this._suspendClicks();
        var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var req = org.eclipse.rap.rwt.Request.getInstance();
        req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
        req.send();
      }
    }
  }
}

qx.Proto._onDblClick = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( this._changeSelectionNotification == "action" ) {
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var req = org.eclipse.rap.rwt.Request.getInstance();
      req.addEvent( "org.eclipse.rap.rwt.events.widgetDefaultSelected", id );
      req.send();
    }
  }
}

/*
 * Suspends the processing of click events to avoid sending multiple
 * widgetSelected events to the server.
 */
qx.Proto._suspendClicks = function() {
  this._clicksSuspended = true;
  qx.client.Timer.once( this._enableClicks, this, 500 );
}

qx.Proto._enableClicks = function() {
  this._clicksSuspended = false;
}

/*
 * Passes enablement to list items
 */
qx.Proto._onChangeEnabled = function( evt ) {
  var newValue = evt.getData();
  var items = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setEnabled( newValue );
  }
}

qx.Proto._onFocusIn = function( evt ) {
  this._updateSelectedItemState();
}

qx.Proto._onFocusOut = function( evt ) {
  this._updateSelectedItemState();
}

qx.Proto._updateSelectedItemState = function() {
  var selectedItems = this.getManager().getSelectedItems();
  for( var i = 0; i < selectedItems.length; i++ ) {
    if( this.getFocused() ) {
      selectedItems[ i ].addState( "focused" );
    } else {
      selectedItems[ i ].removeState( "focused" );
    }
  }
}
