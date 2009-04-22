/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class contains static functions for the Text and Combo widget. Note that
 * the Combo widget also calls the "protected" (_xxx) methods in this class.
 */
qx.Class.define( "org.eclipse.swt.TextUtil", {

  statics : {

    // This factor must be in sync with server side Text#getLineHeight()
    LINE_HEIGT_FACTOR : 1.2,

    // === Public methods ===

    /*
     * To be called right after widget creation.
     */
    initialize : function( text ) {
      if( text.isCreated() ) {
        org.eclipse.swt.TextUtil._doInitialize( text );
      } else {
        text.addEventListener( "appear",
                               org.eclipse.swt.TextUtil._onAppearInitialize );
      }
      text.setLiveUpdate( true );
      text.setSpellCheck( false );
    },

    /*
     * Sets the hasSelectionListerner property on the given text widget.
     * This property tracks whether the text widget has a selection listener
     * attached.
     */
    setHasSelectionListener : function( text, newValue ) {
      var oldValue = text.getUserData( "hasSelectionListener" );
      if( newValue != oldValue ) {
        text.setUserData( "hasSelectionListener", newValue );
        org.eclipse.swt.TextUtil._updateSelectionListener( text, newValue );
      }
    },

    hasSelectionListener : function( text ) {
      return text.getUserData( "hasSelectionListener" ) == true;
    },

    /*
     * Sets the hasVerifyOrModifyListener property on the given widget.
     * This property tracks whether the text widget has a verify listener or a
     * modify listener attached.
     */
    setHasVerifyOrModifyListener : function( text, newValue ) {
      var oldValue = text.getUserData( "hasVerifyOrModifyListener" );
      if( newValue != oldValue ) {
        text.setUserData( "hasVerifyOrModifyListener", newValue );
        org.eclipse.swt.TextUtil._updateVerifyOrModifyListener( text, newValue );
      }
    },

    hasVerifyOrModifyListener : function( text ) {
      return text.getUserData( "hasVerifyOrModifyListener" ) == true;
    },

    /*
     * Sets the selected text range of the given text widget.
     */
    setSelection : function( text, start, length ) {
      if( text.isCreated() && !text.getUserData( "pooled" ) ) {
        org.eclipse.swt.TextUtil._doSetSelection( text, start, length );
      } else {
        text.setUserData( "onAppear.selectionStart", start );
        text.setUserData( "onAppear.selectionLength", length );
        text.addEventListener( "appear",
                               org.eclipse.swt.TextUtil._onAppearSetSelection );
      }
    },

    // === Private methods ===

    _onAppearInitialize : function( event ) {
      // TODO [rst] Optimize: add/remove listener on change of
      //            hasVerifyOrModifyListener property
      var text = event.getTarget();
      text.removeEventListener( "appear",
                                org.eclipse.swt.TextUtil._onAppearInitialize );
      org.eclipse.swt.TextUtil._doInitialize( text );
    },

    _doInitialize : function( text ) {
      text.addEventListener( "mouseup", org.eclipse.swt.TextUtil._onMouseUp );
      text.addEventListener( "keyup", org.eclipse.swt.TextUtil._onKeyUp );
      text.addEventListener( "keydown", org.eclipse.swt.TextUtil._onKeyDown );
      text.addEventListener( "keypress", org.eclipse.swt.TextUtil._onKeyPress );
      text.addEventListener( "changeValue", org.eclipse.swt.TextUtil._onTextChange );
      text.addEventListener( "changeFont", org.eclipse.swt.TextUtil._onFontChange, text );
      org.eclipse.swt.TextUtil._updateLineHeight( text );
    },

    // === Event listener ===

    _onMouseUp : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onKeyDown : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
        if(    event.getKeyIdentifier() == "Enter"
            && !event.isShiftPressed()
            && !event.isAltPressed()
            && !event.isCtrlPressed()
            && !event.isMetaPressed()
            && org.eclipse.swt.TextUtil.hasSelectionListener( text ) )
        {
          event.setPropagationStopped( true );
          org.eclipse.swt.TextUtil._sendWidgetDefaultSelected( text );
        }
      }
    },

    _onKeyPress : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onKeyUp : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onTextChange : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleModification( text );
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onFontChange : function( event ) {
      org.eclipse.swt.TextUtil._updateLineHeight( this );
    },

    _updateLineHeight : function( text ) {
      // TODO [rst] _inputElement can be undefined when text created invisible
      if( text._inputElement !== undefined ) {
        var font = text.getFont();
        var height = Math.floor( font.getSize()
                                 * org.eclipse.swt.TextUtil.LINE_HEIGT_FACTOR );
        text._inputElement.style.lineHeight = height + "px";
      }
    },

    // === Request related ===

    _handleModification : function( text ) {
      // if not yet done, register an event listener that adds a request param
      // with the text widgets' content just before the request is sent
      if( !org.eclipse.swt.TextUtil._isModified( text ) ) {
        org.eclipse.swt.TextUtil._setModified( text, true );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", org.eclipse.swt.TextUtil._onSend, text );
        if( org.eclipse.swt.TextUtil.hasVerifyOrModifyListener( text ) ) {
          // add modifyText-event with sender-id to request parameters
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( text );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.modifyText", id );
          // register listener that is notified when a request is sent
          qx.client.Timer.once( org.eclipse.swt.TextUtil._delayedSend, text, 500 );
        }
      }
    },

    _updateSelectionListener : function( text, newValue ) {
//      text.debug( "_____ update selection listener", text, newValue );
    },

    _updateVerifyOrModifyListener : function( text, newValue ) {
//      text.debug( "_____ update ver/mod listener", text, newValue );
    },

    _onSend : function( event ) {
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

    _delayedSend : function( event ) {
      // NOTE: "this" references the text widget (see qx.client.Timer.once above)
      if( org.eclipse.swt.TextUtil._isModified( this ) ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.send();
      }
    },

    /*
     * Sends a widget default selected event to the server.
     */
    _sendWidgetDefaultSelected : function( text ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( text );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      req.send();
    },

    // === Auxiliary ===

    /*
     * Returns modified property of the given text widget. If true, a request
     * send listener is already attached.
     */
    _isModified : function( widget ) {
      return widget.getUserData( "modified" ) == true;
    },

    _setModified : function( widget, modified ) {
      widget.setUserData( "modified", modified );
    },

    /**
     * Determines whether the given keyIdentifier potentially modifies the
     * content of a text widget.
     * TODO [rst] Obsolete but still used by ComboUtil
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

    /*
     * Checks for a selection change and updates the request parameter if
     * necessary.
     */
    _handleSelectionChange : function( text, enclosingWidget ) {
      var widget = enclosingWidget != null ? enclosingWidget : text;
      var start = text.getSelectionStart();
      // TODO [rst] Quick fix for bug 258632
      //            https://bugs.eclipse.org/bugs/show_bug.cgi?id=258632
      if( start === undefined ) {
        start = 0;
      }
      var length = text.getSelectionLength();
      // TODO [rst] Workaround for qx bug 521. Might be redundant as the
      // bug is marked as (partly) fixed.
      // See http://bugzilla.qooxdoo.org/show_bug.cgi?id=521
      if( typeof length == "undefined" ) {
        text.debug( "___ qx bug 521 still exists" );
        length = 0;
      }
      if(    text.getUserData( "selectionStart" ) != start
          || text.getUserData( "selectionLength" ) != length )
      {
        text.setUserData( "selectionStart", start );
        org.eclipse.swt.WidgetUtil.setPropertyParam( widget,
                                                     "selectionStart",
                                                     start );
        text.setUserData( "selectionLength", length );
        org.eclipse.swt.WidgetUtil.setPropertyParam( widget,
                                                     "selectionLength",
                                                     length );
      }
    },

    _onAppearSetSelection : function( event ) {
      var text = event.getTarget();
      var start = text.getUserData( "onAppear.selectionStart" );
      var length = text.getUserData( "onAppear.selectionLength" );
      org.eclipse.swt.TextUtil._doSetSelection( text, start, length );
      text.removeEventListener( "appear",
                                org.eclipse.swt.TextUtil._onAppearSetSelection );
    },

    _doSetSelection : function( text, start, length ) {
      text.setUserData( "selectionStart", start );
      text.setUserData( "selectionLength", length );
      // [if] Workaround for bug
      // 262908: Focus jump when setting text in focusLost event
      if( start == 0 && length == 0 ) {
        // [if] Clear the selection by setting the text again. 
        // This way the text field does not gain the focus.
        if( text._inputElement !== undefined ) {
          var value = text.getValue();
          text._inputElement.value = "";
          text._inputElement.value = value;
        }
      } else {  
        text.setSelectionStart( start );
        text.setSelectionLength( length );
      }
    }

  }
});
