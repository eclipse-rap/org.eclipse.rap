/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.SyncKeyEventUtil",
{
  type : "singleton",
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._cancelEvent = false;
  },
  
  members : {
    intercept : function( eventType, keyCode, charCode, domEvent ) {
      // [if] Fix for bug 319159
      var realKeyCode = this._getRealKeyCode( keyCode, domEvent );
      var relevantEvent = this._isRelevantEvent( eventType, realKeyCode );
      if( !org_eclipse_rap_rwt_EventUtil_suspend && relevantEvent ) {
        var control = this._getTargetControl();
        var hasKeyListener = this._hasKeyListener( control );
        var hasTraverseListener = this._hasTraverseListener( control );
        var isTraverseKey = false;
        if( hasTraverseListener ) {
          isTraverseKey = this._isTraverseKey( realKeyCode );
        }
        if( hasKeyListener || ( hasTraverseListener && isTraverseKey ) ) {
          // [if] Don't keep and modify the pending event object outside the
          // "intercept" method. Such approach does not work in IE.
          this._cancelEvent = false;
          this._sendKeyDown( control, realKeyCode, charCode, domEvent );
          if( this._cancelEvent ) {
            this._cancelDomEvent( domEvent );
          }
        } 
      }
      return this._cancelEvent;
    },
    
    cancelEvent : function() {
      this._cancelEvent = true;
    },
    
    allowEvent : function() {
      // do nothing
    },
    
    _isRelevantEvent : function( eventType, keyCode ) {
      var result;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        var keyEventHandler = org.eclipse.rwt.KeyEventHandler;
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

    _getRealKeyCode : function( keyCode, domEvent ) {
      var result = keyCode;
      if( qx.core.Variant.isSet( "qx.client", "opera" ) ) {
        result = domEvent.keyCode;
      }
      return result;
    },

    _getTargetControl : function() {
      var result = org.eclipse.rwt.EventHandler.getCaptureWidget();
      if( !result ) {
        var focusRoot = org.eclipse.rwt.EventHandler.getFocusRoot();
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
    
    _sendKeyDown : function( widget, keyCode, charCode, domEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      req.addEvent( "org.eclipse.swt.events.keyDown", id );
      req.addParameter( "org.eclipse.swt.events.keyDown.keyCode", keyCode );
      req.addParameter( "org.eclipse.swt.events.keyDown.charCode", charCode );
      var modifier = "";
      var commandKey = qx.core.Client.runsOnMacintosh() && domEvent.metaKey;
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
      req.sendSyncronous();
    }
    
  }
} );

