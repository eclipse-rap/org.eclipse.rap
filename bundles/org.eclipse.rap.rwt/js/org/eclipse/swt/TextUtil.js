/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

/**
 * This class contains static functions for the Text and Combo widget. Note that
 * the Combo widget also calls the "protected" (_xxx) methods in this class.
 */
qx.Class.define( "org.eclipse.swt.TextUtil", {

  statics : {

    // This factor must be in sync with server side Text#getLineHeight()
    // and CCombo#getTextHeight()
    LINE_HEIGT_FACTOR : 1.2,

    // === Public methods ===

    /*
     * To be called right after widget creation.
     */
    initialize : function( text ) {
      if( text.isCreated() ) {
        org.eclipse.swt.TextUtil._doInitialize( text );
      } else {
        text.addEventListener( "appear", org.eclipse.swt.TextUtil._onAppearInitialize, text );
      }
      text.setLiveUpdate( true );
      // [if] Prevent selection of all text on tab focus
      text._ontabfocus = function() {
        text.setSelectionLength( 0 );
      };
      // [if] Workaround for bug 312215
      text.setUserData( "selectionStart", 0 );
      text.setUserData( "selectionLength", 0 );
    },

    hasVerifyOrModifyListener : function( text ) {
      return text.hasModifyListener() || text.hasVerifyListener();
    },

    /*
     * Sets the selected text range of the given text widget.
     */
    setSelection : function( text, start, length ) {
      // [if] The selection is applied on the TextField when it gains the focus.
      text.setUserData( "selectionStart", start );
      text.setUserData( "selectionLength", length );
      if( text.getFocused() && text.isCreated() ) {
        org.eclipse.swt.TextUtil._doSetSelection( text );
      }
    },

    _doSetSelection : function( text ) {
      var start = text.getUserData( "selectionStart" );
      var length = text.getUserData( "selectionLength" );
      if( start != null && length != null ) {
        text.setSelectionStart( start );
        text.setSelectionLength( length );
      }
    },

    // === Private methods ===

    _onAppearInitialize : function() {
      // TODO [rst] Optimize: add/remove listener on change of
      //            hasVerifyOrModifyListener property
      var text = this;
      text.removeEventListener( "appear", org.eclipse.swt.TextUtil._onAppearInitialize );
      org.eclipse.swt.TextUtil._doInitialize( text );
    },

    _doInitialize : function( text ) {
      text.addEventListener( "mousedown", org.eclipse.swt.TextUtil._onMouseDownUp );
      text.addEventListener( "mouseup", org.eclipse.swt.TextUtil._onMouseDownUp );
      text.addEventListener( "keyup", org.eclipse.swt.TextUtil._onKeyUp );
      text.addEventListener( "keydown", org.eclipse.swt.TextUtil._onKeyDown );
      text.addEventListener( "keypress", org.eclipse.swt.TextUtil._onKeyPress );
      text.addEventListener( "changeValue", org.eclipse.swt.TextUtil._onTextChange );
      text.addEventListener( "changeFont", org.eclipse.swt.TextUtil._onFontChange, text );
      text.addEventListener( "focus", org.eclipse.swt.TextUtil._onFocus, text );
      org.eclipse.swt.TextUtil._updateLineHeight( text );
      var length = text.getUserData( "selectionLength" );
      if( length !== null && length !== 0 ) {
        this._doSetSelection( text );
      }
    },

    // === Event listener ===

    _onMouseDownUp : function( event ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onKeyDown : function( event ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
        if(    event.getKeyIdentifier() == "Enter"
            && !event.isShiftPressed()
            && !event.isAltPressed()
            && !event.isCtrlPressed()
            && !event.isMetaPressed() )
        {
          if( text.hasState( "rwt_MULTI" ) ) {
            event.stopPropagation();
          }
          if( text.hasSelectionListener() ) {
            org.eclipse.swt.TextUtil._sendWidgetDefaultSelected( text );
          }
        }
      }
    },

    _onKeyPress : function( event ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var text = event.getTarget();
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onKeyUp : function( event ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var text = event.getTarget();
        if( event.getKeyIdentifier() == "Tab" ) {
          org.eclipse.swt.TextUtil._doSetSelection( text );
        } else {
          org.eclipse.swt.TextUtil._handleSelectionChange( text );
        }
      }
    },

    _onTextChange : function( event ) {
      var text = event.getTarget();
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        org.eclipse.swt.TextUtil._handleModification( text );
        org.eclipse.swt.TextUtil._handleSelectionChange( text );
      }
    },

    _onFontChange : function( event ) {
      var text = event.getTarget();
      org.eclipse.swt.TextUtil._updateLineHeight( text );
    },

    _onFocus : function( event ) {
      var text = event.getTarget();
      if( !qx.event.handler.FocusHandler.mouseFocus ) {
        org.eclipse.swt.TextUtil._doSetSelection( text );
      }
    },
    
    // this function is also used by Combo.js
    _updateLineHeight : function( text ) {
      // TODO [rst] _inputElement can be undefined when text created invisible
      if( text._inputElement != null ) {
        var font = text.getFont();
        var height = Math.floor( font.getSize() * org.eclipse.swt.TextUtil.LINE_HEIGT_FACTOR );
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
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.send();
    },

    // === Auxiliary ===

    /*
     * Returns modified property of the given text widget. If true, a request
     * send listener is already attached.
     */
    _isModified : function( widget ) {
      return widget.getUserData( "modified" ) === true;
    },

    _setModified : function( widget, modified ) {
      widget.setUserData( "modified", modified );
    },

    ///////////////////////////////////////////////////////////////////
    // Functions to maintain the selection-start and -length properties

    /*
     * Checks for a selection change and updates the request parameter if
     * necessary.
     */
    _handleSelectionChange : function( text, enclosingWidget ) {
      // [if] Workaround for bug 261611
      // [Text] Javascript error "text.getSelectionStart is not a function"
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=261611
      if( text instanceof org.eclipse.rwt.BasicText ) {
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
          length = 0;
        }
        if(    text.getUserData( "selectionStart" ) != start
            || text.getUserData( "selectionLength" ) != length )
        {
          text.setUserData( "selectionStart", start );
          org.eclipse.swt.WidgetUtil.setPropertyParam( widget, "selectionStart", start );
          text.setUserData( "selectionLength", length );
          org.eclipse.swt.WidgetUtil.setPropertyParam( widget, "selectionLength", length );
        }
      }
    }

  }
} );
