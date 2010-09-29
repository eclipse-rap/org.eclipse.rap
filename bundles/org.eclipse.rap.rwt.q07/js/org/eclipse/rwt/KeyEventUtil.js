/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
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
  },
    
  members : {

    cancelEvent : function() {
      this._getInstance().cancelEvent();
    },

    allowEvent : function() {
      this._getInstance().allowEvent();
    },
    
    _onKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      var util = this._getInstance();
      return !util.intercept( eventType, keyCode, charCode, domEvent );
    },

    _getInstance : function() {
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
      var id
        = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
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
    }

  }
} );

