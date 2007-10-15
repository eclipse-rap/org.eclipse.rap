
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
      combo.addEventListener( "changeValue",
                              org.eclipse.swt.ComboUtil._onChangeValue );
      org.eclipse.swt.ComboUtil.hijackAutoCompletition( combo );
    },
    
    deinitialize : function( combo ) {
      combo.removeEventListener( "changeFont",
                                 org.eclipse.swt.ComboUtil._onChangeFont );
      combo.removeEventListener( "changeBackgroundColor",
                                 org.eclipse.swt.ComboUtil._onChangeBackgoundColor );
      combo.removeEventListener( "changeTextColor",
                                 org.eclipse.swt.ComboUtil._onChangeTextColor );
      combo.removeEventListener( "changeValue",
                                 org.eclipse.swt.ComboUtil._onChangeValue );
    },
    
    // workaround for qx bug 555 (ComboBox prevents input when list is visible)
    // http://bugzilla.qooxdoo.org/show_bug.cgi?id=555
    hijackAutoCompletition : function( combo ) {
      combo.removeEventListener( "keyinput", combo._onkeyinput );
      combo._onkeyinput = function( e ) {};
      combo.addEventListener( "keyinput", combo._onkeyinput );
      
      // TODO: need to prevent clearing the input field when
      // closing list with Escape key
    },
    
    modifyText : function( evt ) {
      var combo = evt.getTarget();
      // If the drop-down list is not visible, the target is the text field
      // instead of the combo.
      if( !( combo instanceof qx.ui.form.ComboBox ) ) {
      	combo = combo.getParent();
      }
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && org.eclipse.swt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) ) 
      {
        // if not yet done, register an event listener that adds a request param
        // with the text widgets' content just before the request is sent
        if( !org.eclipse.swt.TextUtil._isModified( combo ) ) {
          var req = org.eclipse.swt.Request.getInstance();
          req.addEventListener( "send", org.eclipse.swt.ComboUtil._onSend, combo );
          org.eclipse.swt.TextUtil._setModified( combo, true );
        }
      }
      org.eclipse.swt.TextUtil.updateSelection( combo.getField(), combo );
    },
    
    /**
     * This function gets assigned to the 'keyup' event of a text widget if 
     * there was a server-side ModifyListener registered.
     */
    modifyTextAction : function( evt ) {
      var combo = evt.getTarget();
      // If the drop-down list is not visible, the target is the text field
      // instead of the combo.
      if( !( combo instanceof qx.ui.form.ComboBox ) ) {
      	combo = combo.getParent();
      }
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && !org.eclipse.swt.TextUtil._isModified( combo ) 
          && org.eclipse.swt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) ) 
      {
        var req = org.eclipse.swt.Request.getInstance();
        // Register 'send'-listener that adds a request param with current text
        if( !org.eclipse.swt.TextUtil._isModified( combo ) ) {
          req.addEventListener( "send", org.eclipse.swt.ComboUtil._onSend, combo );
          org.eclipse.swt.TextUtil._setModified( combo, true );
        }
        // add modifyText-event with sender-id to request parameters
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( combo );
        req.addEvent( "org.eclipse.swt.events.modifyText", id );
        // register listener that is notified when a request is sent
        qx.client.Timer.once( org.eclipse.swt.TextUtil._delayedModifyText, 
                              combo, 
                              500 );
      }
      org.eclipse.swt.TextUtil.updateSelection( combo.getField(), combo );
    },
    
    /**
     * This function gets assigned to the 'blur' event of a text widget if there
     * was a server-side ModifyListener registered.
     */
    modifyTextOnBlur : function( evt ) {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && org.eclipse.swt.TextUtil._isModified( evt.getTarget() ) ) 
      {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.modifyText", id );
        req.send();
      }
    },
    
    _onSend : function( evt ) {
      // NOTE: 'this' references the combo widget
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".text", this.getField().getComputedValue() );
      // remove the _onSend listener and change the text widget state to 'unmodified'
      req.removeEventListener( "send", org.eclipse.swt.ComboUtil._onSend, this );
      org.eclipse.swt.TextUtil._setModified( this, false );
      // Update the value property (which is qooxdoo-wise only updated on
      // focus-lost) to be in sync with server-side
      if( this.getFocused() ) {
        this.setValue( this.getField().getComputedValue() );
      }
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
    setItems : function( combo, items ) {
      org.eclipse.swt.ComboUtil._doSetItems( combo, items );
      // TODO [rst] Find workaround for reparenting problems 
      /*
      if( !combo.getUserData( "pooled" ) ) {
        org.eclipse.swt.ComboUtil._doSetItems( combo, items );      	
      } else {
        combo.setUserData( "onAppear.setItems", items );
        combo.addEventListener( "appear",
                                org.eclipse.swt.ComboUtil._onAppearSetItems );
      }
      */
    },

    _doSetItems : function( combo, items ) {
      combo.removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        item.setFont( combo.getFont() );
        combo.add( item );
      }
//      org.eclipse.swt.ComboUtil._updatePopupHeight( combo );
    },

/*
    _onAppearSetItems : function( evt ) {
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
    
    setVisibleItemCount : function( combo, count ) {
      combo.setUserData( "visibleItems", count );
//      org.eclipse.swt.ComboUtil._updatePopupHeight( combo );
    },
    
    setMaxPopupHeight : function( combo, maxHeight ) {
      combo.getPopup().setMaxHeight( maxHeight );
    },
    
    _updatePopupHeight : function( combo ) {
      var items = combo.getList().getChildren();
      var count = combo.getUserData( "visibleItems" );
      if( count == null ) {
        count = 5;
      }
      if( items.length > 0 ) {
        var itemHeight = items[ 0 ].getBoxHeight();
        var item = items[ 0 ];
        combo.getPopup().setMaxHeight( itemHeight * count );
      }
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
//      org.eclipse.swt.ComboUtil._updatePopupHeight( combo );
    },
    
    // workaround for broken property on qx ComboBox
    _onChangeBackgoundColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo._field.setBackgroundColor( value );
      combo._list.setBackgroundColor( value );
    },
    
    // workaround for broken property on qx ComboBox
    _onChangeTextColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo._field.setTextColor( value );
      combo._list.setTextColor( value );
    },
    
    // workaround for broken context menu on qx ComboBox
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=465
    applyContextMenu : function( combo ) {
      var menu = combo.getContextMenu();
      combo._field.setContextMenu( menu );
      combo._button.setContextMenu( menu );
    },
    
    _onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getData() != null ) {
        var combo = evt.getTarget();
        var value = combo.getValue();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( combo );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".text", value );
      }
    }
  }
});
