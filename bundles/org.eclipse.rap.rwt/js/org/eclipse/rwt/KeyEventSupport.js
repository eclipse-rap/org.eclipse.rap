/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventSupport", {
  type : "singleton",
  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.EventHandler.setKeyEventFilter( this._onKeyEvent, this );
    this._keyBindings = {};
    this._cancelKeys = {};
    this._currentKeyCode = -1;
    this._bufferedEvents = [];
    this._keyEventRequestRunning = false;
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "received", this._onRequestReceived, this );
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "received", this._onRequestReceived, this );
  },

  members : {
    
    //////
    // API

    setKeyBindings : function( value ) {
      this._keyBindings = value;
    },

    setCancelKeys : function( value ) {
      this._cancelKeys = value;
    },
    
    ////////////
    // Internals

    _onKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      var result = true;
      if( eventType === "keydown" ) {
        this._currentKeyCode = keyCode;
      }
      var control = this._getTargetControl();
      if( this._shouldSend( eventType, this._currentKeyCode, charCode, domEvent, control ) ) {
        this._sendKeyEvent( control, this._currentKeyCode, charCode, domEvent );
      }
      if( this._shouldCancel( eventType, this._currentKeyCode, charCode, domEvent, control ) ) {
        this._cancelDomEvent( domEvent );
        result = false;
      }
      return result;
    },
    
    /////////////
    // send event

    _shouldSend : function( eventType, keyCode, charCode, domEvent, control ) {
      var result = false;
      if( this._isRelevant( keyCode, eventType ) ) {
        if( this._hasTraverseListener( control ) && this._isTraverseKey( keyCode ) ) {
          result = true;
        } 
        if( !result && this._hasKeyListener( control ) ) {
          var activeKeys = control.getUserData( "activeKeys" );
          if( activeKeys ) {
            result = this._isActive( activeKeys, domEvent, keyCode, charCode );
          } else {
            result = true;
          }
        }
        if( !result ) {
          result = this._isActive( this._keyBindings, domEvent, keyCode, charCode );
        }
      }
      return result;
    },
    
    _isRelevant : function( keyCode, eventType ) {
      var result;
      if( this._isModifier( keyCode ) ) {
        // NOTE : because modifier don't repeat
        result = eventType === "keydown";
      } else {
        result = eventType === "keypress";
      }
      return result;
    },

    _onRequestReceived : function( evt ) {
      if( this._keyEventRequestRunning ) {
        this._keyEventRequestRunning = false;
        this._checkBufferedEvents();
      }
    },

    _checkBufferedEvents : function() {
      while( this._bufferedEvents.length > 0 && !this._keyEventRequestRunning ) {
        var size = this._bufferedEvents.length;
        var oldEvent = this._bufferedEvents.shift();
        this._sendKeyEvent.apply( this, oldEvent );
      }
    },

    _sendKeyEvent : function( widget, keyCode, charCode, domEvent ) {
      if( this._keyEventRequestRunning ) {
        this._bufferedEvents.push( [ widget, keyCode, charCode, domEvent ] );
      } else {
        var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
        keyUtil._attachKeyEvent( widget, keyCode, charCode, domEvent );
        this._keyEventRequestRunning = true;
        if( keyCode === 27 ) {
          try{ 
            domEvent.preventDefault(); // otherwise the request would be canceled
          } catch( ex ) {
            // do nothing
          }
        }
        org.eclipse.swt.Request.getInstance()._sendImmediate( true );
      }
    },

    _attachKeyEvent : function( widget, keyCode, charCode, domEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id;
      if( widget === null ) {
        id = "w1";
      } else {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        id = wm.findIdByWidget( widget );
      }
      req.addEvent( "org.eclipse.swt.events.keyDown", id );
      req.addParameter( "org.eclipse.swt.events.keyDown.keyCode", keyCode );
      req.addParameter( "org.eclipse.swt.events.keyDown.charCode", charCode );
      var modifier = "";
      var commandKey = org.eclipse.rwt.Client.getPlatform() === "mac" && domEvent.metaKey;
      if( domEvent.shiftKey ) {
        modifier += "shift,";
      }
      if( domEvent.ctrlKey || commandKey ) {
        modifier += "ctrl,";
      }
      if( domEvent.altKey ) {
        modifier += "alt,";
      }
      req.addParameter( "org.eclipse.swt.events.keyDown.modifier", modifier );
    },
    
    ///////////////
    // cancel event

    _shouldCancel : function( eventType, keyCode, charCode, domEvent, control ) {
      var result = this._isActive( this._cancelKeys, domEvent, keyCode, charCode );
      if( !result ) { 
        var cancelKeys = control ? control.getUserData( "cancelKeys" ) : null;
        if( cancelKeys ) {
          result = this._isActive( cancelKeys, domEvent, keyCode, charCode );
        }
      }
      return result;
    },

    _cancelDomEvent : qx.core.Variant.select( "qx.client", {
      "mshtml|newmshtml" : function( event ) {
        if( event.type !== "keydown" && event.preventDefault ) {
          // preventDefault on keydown would prevent following keypress events 
          event.preventDefault();
        }
        try {
          event.keyCode = 0;
        } catch( ex ) {
        }
        event.returnValue = false;
      },
      "default" : org.eclipse.rwt.EventHandlerUtil.stopDomEvent
    } ),


    /////////
    // helper

    _getTargetControl : function() {
      var result = org.eclipse.rwt.EventHandler.getCaptureWidget();
      if( !result ) {
        var focusRoot = org.eclipse.rwt.EventHandler.getFocusRoot();
        result = focusRoot === null ? null : focusRoot.getActiveChild();
      }
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      while( result !== null && !widgetManager.isControl( result ) ) {
        result = result.getParent ? result.getParent() : null;
      }
      return result;
    },

    _isActive : function( activeKeys, domEvent, keyCode, charCode ) {
      var result = false;
      var identifier = this._getKeyBindingIdentifier( domEvent, "keydown", keyCode, charCode );
      result = activeKeys[ identifier ] === true;
      if( !result ) {
        identifier = this._getKeyBindingIdentifier( domEvent, "keypress", keyCode, charCode );
        result = activeKeys[ identifier ] === true;
      }
      return result;
    },
    
    _getKeyBindingIdentifier : function( domEvent, eventType, keyCode, charCode ) {
      var result = [];
      if( eventType === "keydown" && !isNaN( keyCode ) && keyCode > 0 ) {
        if( domEvent.altKey ) {
          result.push( "ALT" );
        }
        if( domEvent.ctrlKey ) {
          result.push( "CTRL" ); //TODO Command @ apple?
        }
        if( domEvent.shiftKey ) {
          result.push( "SHIFT" );
        }
        result.push( "#" + keyCode.toString() );
      } else if( eventType === "keypress" && !isNaN( charCode ) && charCode > 0 ) {
        result.push( String.fromCharCode( charCode ) );
      }
      return result.join( "+" );
    },

    
    _isModifier : function( keyCode ) {
      return keyCode >= 16 && keyCode <= 20 && keyCode !== 19;
    },

    _hasKeyListener : function( widget ) {
      return widget !== null && widget.getUserData( "keyListener" ) === true;
    },

    _hasTraverseListener : function( widget ) {
      return widget !== null && widget.getUserData( "traverseListener" ) === true;
    },

    _isTraverseKey : function( keyCode ) {
      var result = false;
      if( keyCode === 27 || keyCode === 13 || keyCode === 9 ) {
        result = true;
      }
      return result;
    }

  }
} );

// force instance:
org.eclipse.rwt.KeyEventSupport.getInstance();