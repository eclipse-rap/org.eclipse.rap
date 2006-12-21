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
    this.setOverflow( "scrollY" );
    // Should changeSelection events passed to the server-side? 
    // state == no, action == yes
    this._changeSelectionNotification = "state";
    var manager = this.getManager();
    manager.setMultiSelection( multiSelection );
    manager.addEventListener( "changeSelection", this._onChangeSelection, this );
  }
);

/** Sets the given aray of items. */
qx.Proto.setItems = function( items ) {
  // TODO [rh] preserve selection
  this.removeAll();
  // TODO [rh] improve: replace existing items with new string and
  //      add/remove only what's left
  for( var i = 0; i < items.length; i++ ) {
    this.add( new qx.ui.form.ListItem( items[ i ] ) );
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

qx.Proto._onChangeSelection = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( this );
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
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", selectionIndices );
    if( this._changeSelectionNotification == "action" ) {
      req.addParameter( "org.eclipse.rap.rwt.events.widgetSelected", id );
      req.send();
    }
  }
}