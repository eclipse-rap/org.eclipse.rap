/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventUtil", {
  type : "singleton",
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.EventHandler.setKeyEventFilter( this._onKeyEvent, this );
    this._keyBindings = {};
  },
    
  members : {

    cancelEvent : function() {
      this._getDelegate().cancelEvent();
    },

    allowEvent : function() {
      this._getDelegate().allowEvent();
    },
    
    setKeyBindings : function( value ) {
      this._keyBindings = value;
    },

    _onKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      var result;
      if( this._isKeyBinding( domEvent, keyCode, charCode ) ) {
        result = false;
        if( eventType === "keydown" ) {
          // TODO [tb] : use keypress (repeats) instead?
          org.eclipse.rwt.EventHandlerUtil.stopDomEvent( domEvent ); 
//          TODO [rst] Pass focused widget instead of null
//          var widget = this._getTargetControl();
          this._attachKeyDown( null, keyCode, charCode, domEvent );
          org.eclipse.swt.Request.getInstance().send();
        }
      } else {
        var util = this._getDelegate();
        result = !util.intercept( eventType, keyCode, charCode, domEvent );
      }
      return result;
    },

    _getDelegate : function() {
      var util;
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        util = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      } else {
        util = org.eclipse.rwt.SyncKeyEventUtil.getInstance();
      }
      return util;
    },
    
    //////////////////////////////////////////////////////////////
    // Helper, also used by AsyncKeyEventUtil and SyncKeyEventUtil
    
    _isKeyBinding : function( domEvent, keyCode, charCode ) {
      var identifier 
        = this._getKeyBindingIdentifier( domEvent, keyCode, charCode );
      var result = this._keyBindings[ identifier ] === true;
      return result;
    },
    
    _getKeyBindingIdentifier : function( domEvent, keyCode, charCode ) {
      var result = [];
      if( domEvent.altKey ) {
        result.push( "ALT" );
      }
      if( domEvent.ctrlKey ) {
        result.push( "CTRL" );//TODO Command @ apple?
      }
      if( domEvent.shiftKey ) {
        result.push( "SHIFT" );
      }
      if( !isNaN( keyCode ) && keyCode > 0 ) {
        result.push( keyCode.toString() ); 
      } else if( !isNaN( charCode ) && charCode > 0 ) {
        // Usually, the keyCode matches the charcode of the upper-case character
        var charStr = String.fromCharCode( charCode );
        result.push( charStr.toUpperCase().charCodeAt( 0 ) );
      }
      return result.join( "+" );
    },
    
    _isRelevantEvent : function( eventType, keyCode ) {
      var result;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        // TODO [tb] : is the browser-switch still relevant?
        var keyEventHandler = org.eclipse.rwt.EventHandlerUtil;
        var nonPrintable  =    keyEventHandler._isNonPrintableKeyCode( keyCode )
                            || keyCode == 27 // escape
                            || keyCode == 8  // backspace
                            || keyCode == 9; // tab
        if( nonPrintable ) {
          result = eventType === "keydown";
        } else {
          result = eventType === "keypress";
        }
      } else {
        result = eventType === "keypress";
      }
      return result;
    },

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

    _hasKeyListener : function( widget ) {
      return widget !== null && widget.getUserData( "keyListener" ) === true;
    },

    _hasTraverseListener : function( widget ) {
      return    widget !== null 
             && widget.getUserData( "traverseListener" ) === true;
    },

    _isTraverseKey : function( keyCode ) {
      var result = false;
      if( keyCode === 27 || keyCode === 13 || keyCode === 9 ) {
        result = true;
      }
      return result;
    },

    _attachKeyDown : function( widget, keyCode, charCode, domEvent ) {
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
    }

  }
} );
