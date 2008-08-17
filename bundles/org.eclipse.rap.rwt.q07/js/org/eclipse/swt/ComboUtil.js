/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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
      org.eclipse.swt.TextUtil._updateSelection( combo.getField(), combo );
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
        qx.client.Timer.once( org.eclipse.swt.TextUtil._delayedSend, 
                              combo, 
                              500 );
      }
      org.eclipse.swt.TextUtil._updateSelection( combo.getField(), combo );
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
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
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
    }

  }

} );
