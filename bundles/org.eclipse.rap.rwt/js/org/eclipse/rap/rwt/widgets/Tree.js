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
 * This class encapulates the qx.ui.treefullcontrol.Tree o make it more
 * suitable for usage in RWT.
 * The style parameter mimics the RWT style flag. Possible values (strings)
 * are: multi, check
 */
qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.Tree",
  qx.ui.treefullcontrol.Tree,
  function( style ) {
    var trs = qx.ui.treefullcontrol.TreeRowStructure.getInstance().standard( "" );
    qx.ui.treefullcontrol.Tree.call( this, trs );
    this.setOverflow( qx.constant.Style.OVERFLOW_AUTO );
    this.setHideNode( true );
    this.setUseTreeLines( true );
    this.setUseDoubleClick( false ); // true supresses dblclick events !
    // TODO [rh] this is only to make the tree fousable at all
    this.setTabIndex( 1 );
    this._rwtStyle = style;
    this._selectionListeners = false;
    this._treeListeners = false;
    var manager = this.getManager();
    manager.setMultiSelection( qx.lang.String.contains( style, "multi" ) );
    manager.addEventListener( "changeSelection", this._onChangeSelection, this );
    this.addEventListener( "treeOpenWithContent", this._onItemExpanded, this );
    this.addEventListener( "treeClose", this._onItemCollapsed, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
  }
);

/**
 * Are there any server-side SelectionListeners attached? If so, selecting an
 * item causes a request to be sent that informs the server-side listeners.
 */
qx.Proto.setSelectionListeners = function( value ) {
  this._selectionListeners = value;
}

qx.Proto.hasSelectionListeners = function() {
  return this._selectionListeners;
}

/**
 * Are there any server-side TreeListeners attached? If so, expanding/collapsing
 * an item causes a request to be sent that informs the server-side listeners.
 */
qx.Proto.setTreeListeners = function( value ) {
  this._treeListeners = value;  
}

qx.Proto.getRWTStyle = function() {
  return this._rwtStyle;  
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  var manager = this.getManager();
  manager.removeEventListener( "changeSelection", this._onChangeSelection, this );
  this.removeEventListener( "treeOpenWithContent", this._onItemExpanded, this );
  this.removeEventListener( "treeClose", this._onItemCollapsed, this );
  this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
  return qx.ui.treefullcontrol.Tree.prototype.dispose.call( this );
}

//
// Event Listener
//

qx.Proto._onChangeSelection = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = wm.findIdByWidget( this );
    var item = this.getManager().getLeadItem();
    req.addParameter( id + ".selection", this._getSelectionIndices() );
    // TODO [rst] Prevent selecting the root item.
    //      When first visible item is selected and arrow up is pressed the root
    //      item ( == this ) is selected which results in an invisible selection. 
    if( item == this ) {
//      this.getFirstVisibleChildOfFolder().setSelected( true );
//      this.setSelected( false );
    } else {
      if( this._selectionListeners ) {
        this._suspendClicks();
        var itemId = wm.findIdByWidget( item );
        var eventName = "org.eclipse.rap.rwt.events.widgetSelected";
        req.addEvent( eventName, id );
        req.addParameter( eventName + ".item", itemId );
        req.send();
      }
    }
  }
}

qx.Proto._onItemExpanded = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var treeItemId = wm.findIdByWidget( evt.getData() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( treeItemId + ".state", "expanded" );
    if( this._treeListeners ) {
      req.addEvent( "org.eclipse.rap.rwt.events.treeExpanded", treeItemId );
      req.send();
    }
  }
}

qx.Proto._onItemCollapsed = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var treeItemId = wm.findIdByWidget( evt.getData() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( treeItemId + ".state", "collapsed" );
    if( this._treeListeners ) {
      req.addEvent( "org.eclipse.rap.rwt.events.treeCollapsed", treeItemId );
      req.send();
    }
  }
}

qx.Proto._onContextMenu = function( evt ) {
  var menu = this.getContextMenu();
  if( menu != null ) {
    menu.setLocation( evt.getPageX(), evt.getPageY() );
    menu.setOpener( this );
    menu.show();
    evt.stopPropagation();
  }
}

/*
 * Pass enablement to tree items
 */
qx.Proto._onChangeEnabled = function( evt ) {
  var newValue = evt.getData();
  var items = this.getItems();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    if( item.getLabelObject() != null ) {
      var label = item.getLabelObject();
      label.setEnabled( newValue );
    } else {
      // TODO [rh] revise this: how to remove/dispose of the listener?
      item.addEventListener( "appear", function( evt ) {
        this.getLabelObject().setEnabled( newValue );
      }, item );
    }
    item.setEnabled( newValue );
  }
}

/*
 * handle click on tree item
 * called by org.eclipse.rap.rwt.widgets.TreeItem
 */
qx.Proto._notifyItemClick = function( item ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( this._selectionListeners && !this._clicksSuspended ) {
      this._suspendClicks();
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var itemId = wm.findIdByWidget( item );
      var req = org.eclipse.rap.rwt.Request.getInstance();
      var eventName = "org.eclipse.rap.rwt.events.widgetSelected";
      req.addEvent( eventName, id );
      req.addParameter( eventName + ".item", itemId );
      req.send();
    }
  }
}

/*
 * handle double click on tree item
 * called by org.eclipse.rap.rwt.widgets.TreeItem
 */
qx.Proto._notifyItemDblClick = function( item ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( this._selectionListeners ) {
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var itemId = wm.findIdByWidget( item );
      var req = org.eclipse.rap.rwt.Request.getInstance();
      var eventName = "org.eclipse.rap.rwt.events.widgetDefaultSelected";
      req.addEvent( eventName, id );
      req.addParameter( eventName + ".item", itemId );
      req.send();
    }
  }
}

/*
 * handle change of the check state of a tree item's check box
 * called by org.eclipse.rap.rwt.widgets.TreeItem
 */
qx.Proto._notifyChangeItemCheck = function( item ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( this._selectionListeners ) {
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var itemId = wm.findIdByWidget( item );
      var req = org.eclipse.rap.rwt.Request.getInstance();
      var eventName = "org.eclipse.rap.rwt.events.widgetSelected";
      req.addEvent( eventName, id );
      req.addParameter( eventName + ".item", itemId );
      req.addParameter( eventName + ".detail", "check" );
      req.send();
    }
  }
}

/*
 * Returns the current selection as comma separated string
 */
// TODO [rh] handle multi selection
qx.Proto._getSelectionIndices = function() {
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var selectedItemIds = "";
  if( this.getManager().getMultiSelection() ) {
    var selectedItems = this.getManager().getSelectedItems();
    for( var i = 0; i < selectedItems.length; i++ ) {
      if( i > 0 ) {
        selectedItemIds += ",";
      }
      selectedItemIds += wm.findIdByWidget( selectedItems[ i ] );
    }
  } else {
    selectedItemIds = wm.findIdByWidget( this.getManager().getSelectedItem() );
  }
  return selectedItemIds;
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
