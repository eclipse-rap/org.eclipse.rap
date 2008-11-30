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

qx.Class.define( "org.eclipse.rwt.SyncKeyEventUtil",
{
  type : "singleton",
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._pendingEvent = null;
  },
  
  members : {
    intercept : function( eventType, keyCode, charCode, domEvent ) {
      var result = false;
      var relevantEvent = this._isRelevantEvent( eventType, keyCode );
      if( !org_eclipse_rap_rwt_EventUtil_suspend && relevantEvent ) {
        var control = this._getTargetControl();
        var hasKeyListener = this._hasKeyListener( control );
        var hasTraverseListener = this._hasTraverseListener( control );
        if( hasKeyListener || ( hasTraverseListener && this._isTraverseKey( keyCode ) ) ) {
          var key = charCode == 0 ? keyCode : charCode;
          this._pendingEvent = domEvent;
          this._sendKeyDown( control, key, domEvent );
          result = this._isDomEventCanceled( domEvent );
        } 
      }
      return result;
    },
    
    cancelEvent : function() {
      this._cancelDomEvent( this._pendingEvent );
    },
    
    allowEvent : function() {
      // do nothing
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
          || keyCode == 27 // escape
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
      return widget !== null && widget.getUserData( "keyListener" ) === true;
    },
    
    _hasTraverseListener : function( widget ) {
      return widget !== null && widget.getUserData( "traverseListener" ) === true;
    },
    
    _isTraverseKey : function( keyCode ) {
      var result = false;
      if(    keyCode === 27 
          || keyCode === 13 
          || keyCode === 9 )
      {
        result = true;
      }
      return result;
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
    
    _sendKeyDown : function( widget, keyCode, domEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      req.addEvent( "org.eclipse.swt.events.keyDown", id );
      req.addParameter( "org.eclipse.swt.events.keyDown.keyCode", keyCode );
      var modifier = "";
      if( domEvent.shiftKey ) {
        modifier += "shift,";
      }
      if( domEvent.ctrlKey ) {
        modifier += "ctrl,";
      }
      if( domEvent.altKey ) {
        modifier += "alt,";
      }
      req.addParameter( "org.eclipse.swt.events.keyDown.modifier", modifier );
      req.sendSyncronous();
    }
    
  }
} );

