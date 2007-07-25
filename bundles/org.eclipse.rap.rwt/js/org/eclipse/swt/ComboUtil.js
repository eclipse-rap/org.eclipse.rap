
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
 * This class contains static functions for the Combo widget.
 */
qx.Class.define( "org.eclipse.swt.ComboUtil", {

  statics : {

    initialize : function( combo ) {
      combo.addEventListener( "changeFont",
                              org.eclipse.swt.ComboUtil._onChangeFont );
      combo.addEventListener( "changeBackgroundColor",
                              org.eclipse.swt.ComboUtil._onChangeBackgoundColor );
      combo.addEventListener( "changeTextColor",
                              org.eclipse.swt.ComboUtil._onChangeTextColor );
    },
    
    deinitialize : function( combo ) {
      combo.removeEventListener( "changeFont",
                                 org.eclipse.swt.ComboUtil._onChangeFont );
      combo.removeEventListener( "changeBackgroundColor",
                                 org.eclipse.swt.ComboUtil._onChangeBackgoundColor );
      combo.removeEventListener( "changeTextColor",
                                 org.eclipse.swt.ComboUtil._onChangeTextColor );
    },
    
    onSelectionChanged : function( evt ) {
      // TODO [rst] This listener was also called on focus out, if no item was
      //      selected. This fix should work since combos cannot be deselected.
      if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getData() != null ) {
        var combo = evt.getTarget();
        var list = combo.getList();
        var listItem = list.getSelectedItem();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var cboId = widgetManager.findIdByWidget( combo );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( cboId + ".selectedItem", list.indexOf( listItem ) );
      }
    },

    onSelectionChangedAction : function( evt ) {
      // TODO [rst] This listener was also called on focus out, if no item was
      //      selected. This fix should work since combos cannot be deselected.
      if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getData() != null ) {
        org.eclipse.swt.ComboUtil.onSelectionChanged( evt );      
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    },
    
    /** Populates the Combo denoted by the given id with the items. */
    createComboBoxItems : function( combo, items ) {
      org.eclipse.swt.ComboUtil._doCreateItems( combo, items );
      // TODO [rst] Find workaround for reparenting problems 
      /*
      if( !combo.getUserData( "pooled" ) ) {
        org.eclipse.swt.ComboUtil._doCreateItems( combo, items );      	
      } else {
        combo.setUserData( "onAppear.setItems", items );
        combo.addEventListener( "appear",
                                org.eclipse.swt.ComboUtil._onAppearCreateItems );
      }
      */
    },

    _doCreateItems : function( combo, items ) {
      combo.removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        item.setFont( combo.getFont() );
        combo.add( item );
      }
    },

/*
    _onAppearCreateItems : function( evt ) {
      var combo = evt.getTarget();
      var items = combo.getUserData( "onAppear.setItems" );
      org.eclipse.swt.ComboUtil._doCreateItems( combo, items );
      combo.setUserData( "onAppear.setItems", null );
      combo.removeEventListener( "appear", 
                                 org.eclipse.swt.ComboUtil._onAppearCreateItems );
    },
*/

    /** Selects a combo box item. */
    select : function( combo, index ) {
      var items = combo.getList().getChildren();
      var item = null;
      if( index >= 0 && index <= items.length - 1 ) {
        item = items[ index ];
      }
      combo.setSelected( item );
    },
    
    _onChangeFont : function( evt ) {
      var combo = evt.getTarget();
      // Apply changed font to embedded text-field and drop-down-button
      var children = combo.getChildren();
      for( var i = 0; i < children.length; i++ ) {
        children[ i ].setFont( combo.getFont() );
      }  
      // Apply changed font to items
      var items = combo.getList().getChildren();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setFont( combo.getFont() );
      }
    },
    
    // workaround for broken property on qx ComboBox
    _onChangeBackgoundColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo.debug( "_____ bg " + value );
      combo._field.setBackgroundColor( value );
      combo._list.setBackgroundColor( value );
    },
    
    // workaround for broken property on qx ComboBox
    _onChangeTextColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo.debug( "_____ fg " + value );
      combo._field.setTextColor( value );
      combo._list.setTextColor( value );
    },
    
    // workaround for broken context menu on qx ComboBox
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=465
    applyContextMenu : function( combo ) {
      var menu = combo.getContextMenu();
      combo._field.setContextMenu( menu );
      combo._button.setContextMenu( menu );
    }
  }
});
