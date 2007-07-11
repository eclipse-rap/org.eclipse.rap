
/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.TextUtil", {

  statics : {
    
    ///////////////////////////////////////////////////////////////
    // Functions for ModifyEvents and maintenance of the text/value
    
    modifyText : function( evt ) {
      var text = evt.getTarget();
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && org.eclipse.swt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) ) 
      {
        // if not yet done, register an event listener that adds a request param
        // with the text widgets' content just before the request is sent
        if( !org.eclipse.swt.TextUtil._isModified( text ) ) {
          var req = org.eclipse.swt.Request.getInstance();
          req.addEventListener( "send", org.eclipse.swt.TextUtil._onSend, text );
          org.eclipse.swt.TextUtil._setModified( text, true );
        }
      }
      org.eclipse.swt.TextUtil.updateSelection( text );
    },

    /**
     * This function gets assigned to the 'keyup' event of a text widget if 
     * there was a server-side ModifyListener registered.
     */
    modifyTextAction : function( evt ) {
      var text = evt.getTarget();
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && !org.eclipse.swt.TextUtil._isModified( text ) 
          && org.eclipse.swt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) ) 
      {
        var req = org.eclipse.swt.Request.getInstance();
        // Register 'send'-listener that adds a request param with current text
        if( !org.eclipse.swt.TextUtil._isModified( text ) ) {
          req.addEventListener( "send", org.eclipse.swt.TextUtil._onSend, text );
          org.eclipse.swt.TextUtil._setModified( text, true );
        }
        // add modifyText-event with sender-id to request parameters
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget(text);
        req.addEvent( "org.eclipse.swt.events.modifyText", id );
        // register listener that is notified when a request is sent
        qx.client.Timer.once( org.eclipse.swt.TextUtil._delayedModifyText, 
                              text, 
                              500 );
      }
      org.eclipse.swt.TextUtil.updateSelection( text );
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

    _onSend : function(evt) {
      // NOTE: 'this' references the text widget
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".text", this.getComputedValue() );

      // remove the _onSend listener and change the text widget state to 'unmodified'
      req.removeEventListener( "send", org.eclipse.swt.TextUtil._onSend, this );
      org.eclipse.swt.TextUtil._setModified( this, false );

      // Update the value property (which is qooxdoo-wise only updated on
      // focus-lost) to be in sync with server-side
      if( this.getFocused() ) {
        this.setValue( this.getComputedValue() );
      }
    },

    _delayedModifyText : function( evt ) {
      // NOTE: this references the text widget (see qx.client.Timer.once above)
      if( org.eclipse.swt.TextUtil._isModified( this ) ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.send();
      }
    },

    _isModified : function( widget ) {
      return widget.getUserData("modified") == true;
    },

    _setModified : function( widget, modified ) {
      return widget.setUserData("modified", modified);
    },

    /**
     * Determines whether the given keyIdentifier potentially modifies the 
     * content of a text widget.
     */
    _isModifyingKey : function( keyIdentifier ) {
      var result = false;
      switch( keyIdentifier ) {
        // Modifier keys
        case "Shift":
        case "Control":
        case "Alt":
        case "Meta":
        case "Win":
        // Navigation keys
        case "Up":
        case "Down":
        case "Left":
        case "Right":
        case "Home":
        case "End":
        case "PageUp":
        case "PageDown":
        case "Tab":
        // Context menu key
        case "Apps":
        //
        case "Escape":
        case "Insert":
        case "Enter":
        //
        case "CapsLock":
        case "NumLock":
        case "Scroll":
        case "PrintScreen":
        // Function keys 1 - 12
        case "F1":
        case "F2":
        case "F3":
        case "F4":
        case "F5":
        case "F6":
        case "F7":
        case "F8":
        case "F9":
        case "F10":
        case "F11":
        case "F12":
          break;
        default:
          result = true;
      }
      return result;
    },

    ///////////////////////////////////////////////////////////////////
    // Functions to maintain the selection-start and -length properties
    
    onMouseUp : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        org.eclipse.swt.TextUtil.updateSelection( evt.getTarget() );
      }
    },

    updateSelection : function( text ) {
      // TODO [rh] executing the code below for a TextArea leads to Illegal Argument
      if( text.classname != "qx.ui.form.TextArea" ) {
        var start = text.getSelectionStart();
        var length = text.getSelectionLength();
        if( text.getUserData( "selectionStart" ) != start ) {
          text.setUserData( "selectionStart", start );
          org.eclipse.swt.TextUtil._setPropertyParam( text, "selectionStart", start );
        }
        if( text.getUserData ("selectionLength" ) != length ) {
          text.setUserData( "selectionLength", length );
          org.eclipse.swt.TextUtil._setPropertyParam( text, "selectionCount", length );
        }
      }
    },

    _setPropertyParam : function( widget, name, value ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( widget );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + "." + name, value );
    },

    setSelection : function( text, start, length ) {
      if( text.isCreated() && !text.getUserData( "pooled" ) ) {
        org.eclipse.swt.TextUtil._doSetSelection( text, start, length );
      }
      else {
        text.setUserData( "onAppear.selectionStart", start );
        text.setUserData( "onAppear.selectionLength", length );
        text.addEventListener( "appear", 
                               org.eclipse.swt.TextUtil._onAppearSetSelection );
      }
    },

    _onAppearSetSelection : function( evt ) {
      var text = evt.getTarget();
      var start = text.getUserData( "onAppear.selectionStart" );
      var length = text.getUserData( "onAppear.selectionLength" );
      org.eclipse.swt.TextUtil._doSetSelection( text, start, length );
      text.removeEventListener( "appear", 
                                org.eclipse.swt.TextUtil._onAppearSetSelection );
    },

    _doSetSelection : function( text, start, length ) {
      text.setUserData( "selectionStart", start );
      text.setSelectionStart( start );
      text.setUserData( "selectionLength", length );
      text.setSelectionLength( length );
    },
    
    // TODO [rst] Workaround for pooling problems with wrap property in IE.
    //            These methods can probably be dropped once qx bug 300 is fixed.
    setWrap : function( text, wrap ) {
      if( text.isCreated() && !text.getUserData( "pooled" ) ) {
        text.setWrap( wrap );
      } else {
        text.setUserData( "onAppear.wrap", wrap );
        text.addEventListener( "appear",
                               org.eclipse.swt.TextUtil._onAppearSetWrap );
      }
    },

    _onAppearSetWrap : function( evt ) {
      var text = evt.getTarget();
      var wrap = text.getUserData( "onAppear.wrap" );
      text.setUserData( "onAppear.wrap", undefined );
      text.setWrap( wrap );
      text.removeEventListener( "appear",
                                org.eclipse.swt.TextUtil._onAppearSetWrap );
    },
    
    ////////////////////////////
    // SelectionListener support
    
    /**
     * This funciton is registered server-side if a SelectionListener should
     * be notified about hte widgetDefaultSelection event that ovvurs when
     * Enter was pressed.
     */
    widgetDefaultSelected : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if(    evt.getKeyIdentifier() == "Enter" 
            && !evt.isShiftPressed()
            && !evt.isAltPressed() 
            && !evt.isCtrlPressed() 
            && !evt.isMetaPressed() ) 
        {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( evt.getTarget() );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          req.send();
        }
      }
    }

  }
});
