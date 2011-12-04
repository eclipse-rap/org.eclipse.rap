/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt" );

org.eclipse.rwt.Display = function() {
  this._document = qx.ui.core.ClientDocument.getInstance();
  this._request = org.eclipse.swt.Request.getInstance();
  this._exitConfirmation = null;
  if( org.eclipse.rwt.Display._current !== undefined ) {
    throw new Error( "Display can not be created twice" );
  } else {
    org.eclipse.rwt.Display._current = this;
  }
};

org.eclipse.rwt.Display.getCurrent = function() {
  return org.eclipse.rwt.Display._current;
};

org.eclipse.rwt.Display.prototype = {

  init : function( args ) {
    this._request.setUrl( args.url );
    this._request.setUIRootId( args.rootId );
    this._request.addParameter( "startup", args.entrypoint );
    this._request.addParameter( "rwt_initialize", "true" );
    this._appendWindowSize();
    this._appendSystemDPI();      
    this._appendColorDepth();
    this._attachListener();
    this._request._sendImmediate( true );
  },

  probe : function( args ) {
    org.eclipse.swt.FontSizeCalculation.probe( args.fonts );
  },

  measureStrings : function( args ) {
    org.eclipse.swt.FontSizeCalculation.measureStringItems( args.strings );
  },

  allowEvent : function() {
    // NOTE : in the future might need a parameter if there are multiple types of cancelable events 
    org.eclipse.rwt.KeyEventUtil.getInstance().allowEvent();
  },

  cancelEvent : function() {
    org.eclipse.rwt.KeyEventUtil.getInstance().cancelEvent();
  },

  reload : function( args ) {
    if( confirm( args.message ) ) {
      this.setExitConfirmation( null );
      window.location.reload( false );
    }
  },

  /**
   * An exit confirmation dialog will be displayed if the given message is not
   * null. If the message is empty, the dialog will be displayed but without a
   * message.
   */
  setExitConfirmation : function( message ) {
    this._exitConfirmation = message;
  },

  setFocusControl : function( widgetId ) {
    org.eclipse.swt.WidgetManager.getInstance().focus( widgetId );
  },

  ////////////////////////
  // Global Event handling
  
  _attachListener : function() {
    this._document.addEventListener( "windowresize", this._onResize, this );
    this._document.addEventListener( "keypress", this._onKeyPress, this );
    this._request.addEventListener( "send", this._onSend, this );
    org.eclipse.rwt.KeyEventUtil.getInstance(); // adds global KeyListener  
    // Observe browser history
    // TODO [tb] : do this on demand only
    var history = qx.client.History.getInstance();
    history.addEventListener( "request", this._historyNavigated, this );
    org.eclipse.rwt.System.getInstance().addEventListener( "beforeunload", this._onBeforeUnload, this );
    org.eclipse.rwt.System.getInstance().addEventListener( "unload", this._onUnload, this );
  },
  
  _onResize : function( evt ) {
    this._appendWindowSize();
    // Fix for bug 315230
    if( this._request.getRequestCounter() != null ) {
      this._request.send();
    }
  },

  _onKeyPress : function( evt ) {
    if( evt.getKeyIdentifier() == "Escape" ) {
      evt.preventDefault();
    }
  },

  _onSend : function( evt ) {
    var pageX = qx.event.type.MouseEvent.getPageX();
    var pageY = qx.event.type.MouseEvent.getPageY();
    var id = this._request.getUIRootId();
    this._request.addParameter( id + ".cursorLocation.x", String( pageX ) );
    this._request.addParameter( id + ".cursorLocation.y", String( pageY ) );
  },  

  _historyNavigated : function( event ) {
    var entryId = event.getData();
    var req = org.eclipse.swt.Request.getInstance();
    req.addParameter( "org.eclipse.rwt.events.historyNavigated", "true" );
    req.addParameter( "org.eclipse.rwt.events.historyNavigated.entryId", entryId );
    req.send();
  },

  _onBeforeUnload : function( event ) {
    if( this._exitConfirmation !== null && this._exitConfirmation !== "" ) {
      event.getDomEvent().returnValue = this._exitConfirmation;
    }
  },

  _onUnload : function() {
    this._document.removeEventListener( "windowresize", this._onResize, this );
    this._document.removeEventListener( "keypress", this._onKeyPress, this );
    this._request.removeEventListener( "send", this._onSend, this );
  },

  ///////////////////
  // client to server

  _appendWindowSize : function() {
    var width = qx.html.Window.getInnerWidth( window );
    var height = qx.html.Window.getInnerHeight( window );
    // Append document size to request
    var id = this._request.getUIRootId();
    this._request.addParameter( id + ".bounds.width", String( width ) );
    this._request.addParameter( id + ".bounds.height", String( height ) );
  },

  _appendSystemDPI : function() {
    var dpi = [ 0, 0 ];
    if( typeof screen.systemXDPI == "number" ) {
      dpi[ 0 ] = parseInt( screen.systemXDPI );
      dpi[ 1 ] = parseInt( screen.systemYDPI );
    } else {
      var testElement = document.createElement( "div" );
      testElement.style.width = "1in";
      testElement.style.height = "1in";
      testElement.style.padding = 0;
      document.body.appendChild( testElement );
      dpi[ 0 ] = parseInt( testElement.offsetWidth );
      dpi[ 1 ] = parseInt( testElement.offsetHeight );
      document.body.removeChild( testElement );        
    }
    this._request.addParameter( "w1.dpi.x", String( dpi[ 0 ] ) );
    this._request.addParameter( "w1.dpi.y", String( dpi[ 1 ] ) );
  },

  _appendColorDepth : function() {
    var depth = 16;
    if( typeof screen.colorDepth == "number" ) {
      depth = parseInt( screen.colorDepth );
    }
    if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
      // Firefox detects 24bit and 32bit as 24bit, but 32bit is more likely
      depth = depth == 24 ? 32 : depth;
    }
    this._request.addParameter( "w1.colorDepth", String( depth ) );
  }

};