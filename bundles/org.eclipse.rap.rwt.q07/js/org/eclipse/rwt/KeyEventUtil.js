/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventUtil",
{
  type : "singleton",
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._lastKeyCode = 0;
    this._pendingEvent = null;
    this._bufferedEvents = new Array();
    this._keyEventRequestRunning = false;
  },
  
  members : {
    intercept : function( eventType, keyCode, charCode, domEvent ) {
      var result = false;
      var relevantEvent = this._isRelevantEvent( eventType, keyCode );
      if( !org_eclipse_rap_rwt_EventUtil_suspend && relevantEvent ) {
        var control = this._getTargetControl();
        var hasKeyListener = false;
        if( control !== null && this._hasKeyListener( control ) ) {
          hasKeyListener = true;
        }
        if( eventType === "keydown" ) {
          this._lastKeyCode = keyCode;
        }
        if( hasKeyListener ) {
          if( this._keyEventRequestRunning ) {
            this._bufferedEvents.push( domEvent );
            this._cancelDomEvent( domEvent );
          } else {
            var key = charCode == 0 ? keyCode : charCode;
            this._pendingEvent = domEvent;
            this._sendKeyDown( control, key );
            result = this._isDomEventCanceled();
            this._checkBufferedEvents();
          }
        } 
      }
      return result;
    },
    
    cancelEvent : function() {
      this._cancelDomEvent( this._pendingEvent );
    },
    
    _isDomEventCanceled : function( domEvent ) {
      var result;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        result = domEvent.returnValue === false;
      } else {
        result = domEvent.__isCanceled && domEvent.__isCanceled === true;
      }
      return result; 
    },
    
    _isRelevantEvent : function( eventType, keyCode ) {
      var result;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        var keyEventHandler = qx.event.handler.KeyEventHandler.getInstance();
        var nonPrintable
          =  keyEventHandler._isNonPrintableKeyCode( keyCode ) 
          || keyCode == 8  // backspace
          || keyCode == 9; // tab
        if( nonPrintable ) {
          result = eventType === "keydown";
        } else {
          result= eventType === "keypress";
        }
      } else {
        result = eventType === "keypress";
      }
      return result;      
    },
    
    _getTargetControl : function() {
      var result = qx.event.handler.EventHandler.getInstance().getCaptureWidget();
      if( !result ) {
        var focusRoot = qx.event.handler.EventHandler.getInstance().getFocusRoot();
        result = focusRoot == null ? null : focusRoot.getActiveChild();  
      }
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      while( result !== null && !widgetManager.isControl( result ) ) {
        result = result.getParent ? result.getParent() : null;
      }
      return result;
    },
        
    _hasKeyListener : function( widget ) {
      return widget.getUserData( "keyListener" ) === true;
    },
    
    _cancelDomEvent : function( domEvent ) {
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        domEvent.returnValue = false;
        domEvent.cancelBubble = true;
      } else {
        domEvent.__isCanceled = true;
        domEvent.preventDefault();
        domEvent.stopPropagation();
      }
    },
    
    _sendKeyDown : function( widget, keyCode ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      req.addEvent( "org.eclipse.swt.events.keyDown", id );
      req.addParameter( "org.eclipse.swt.events.keyDown.keyCode", keyCode );
      req.addParameter( "org.eclipse.swt.events.keyDown.character", "9" );
      this._keyEventRequestRunning = true;
      req.sendSyncronous();
      this._keyEventRequestRunning = false;
    },
    
    _checkBufferedEvents : function() {
// TODO [rh] Firefox does suppress input events during sync XmlHttpRequests
//      Find a way to re-throw key events         
      if( this._bufferedEvents.length > 0 ) {
        if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
//          var oldEvent = this._bufferedEvents.shift();
//          var newEvent = document.createEvent( "KeyboardEvent" );
//          newEvent.initKeyEvent( "keydown",
//                                 oldEvent.bubbles,
//                                 true, // cancelable
//                                 oldEvent.view,
//                                 oldEvent.ctrlKey,
//                                 oldEvent.altKey,
//                                 oldEvent.shiftKey,
//                                 oldEvent.metaKey,
//                                 this._lastKeyEvent,
//                                 oldEvent.charCode );
//          oldEvent.target.dispatchEvent( newEvent );
//          newEvent = document.createEvent( "KeyboardEvent" );
//          newEvent.initKeyEvent( "keypress",
//                                 oldEvent.bubbles,
//                                 true, // cancelable
//                                 oldEvent.view,
//                                 oldEvent.ctrlKey,
//                                 oldEvent.altKey,
//                                 oldEvent.shiftKey,
//                                 oldEvent.metaKey,
//                                 oldEvent.keyCode,
//                                 oldEvent.charCode );
//          oldEvent.target.dispatchEvent( newEvent );
//          newEvent = document.createEvent( "KeyboardEvent" );
//          newEvent.initKeyEvent( "keyup",
//                                 oldEvent.bubbles,
//                                 true, // cancelable
//                                 oldEvent.view,
//                                 oldEvent.ctrlKey,
//                                 oldEvent.altKey,
//                                 oldEvent.shiftKey,
//                                 oldEvent.metaKey,
//                                 this._lastKeyEvent,
//                                 oldEvent.charCode );
//          oldEvent.target.dispatchEvent( newEvent );
        }
      }
    }
    
  }
} );

