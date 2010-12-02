/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 *     Ruediger Herrmann - bug 314453: Disable spell checking in Chrome
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.Application", {
  extend : qx.application.Gui,

  construct : function() {
    this.base( arguments );
    this._exitConfirmation = null;
    this._startupTime = new Date().getTime();
    qx.Class.patch( qx.ui.core.Parent, org.eclipse.rwt.GraphicsMixin );
    qx.Class.patch( qx.ui.form.TextField, org.eclipse.rwt.GraphicsMixin );
    qx.Class.patch( org.eclipse.rwt.widgets.MultiCellWidget,
                    org.eclipse.rwt.GraphicsMixin );
    qx.Class.patch( qx.ui.core.ClientDocumentBlocker,
                    org.eclipse.rwt.FadeAnimationMixin );
    qx.Class.patch( qx.event.type.DomEvent,
                    org.eclipse.rwt.DomEventPatch );              
    org.eclipse.rwt.MobileWebkitSupport.init();
    org.eclipse.rwt.KeyEventUtil.getInstance();
    org.eclipse.rwt.GraphicsUtil.init();
    var eventHandler = org.eclipse.rwt.EventHandler;
    eventHandler.setAllowContextMenu(
      org.eclipse.rwt.widgets.Menu.getAllowContextMenu
    );
    eventHandler.setMenuManager( org.eclipse.rwt.MenuManager.getInstance() );
  },
  
  destruct : function() {
    var doc = qx.ui.core.ClientDocument.getInstance();
    doc.removeEventListener( "windowresize", 
                             org.eclipse.swt.Application._onResize );
    doc.removeEventListener( "keypress",
                             org.eclipse.swt.Application._onKeyPress );
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSend, this );
  },

  statics : {
    _onResize : function( evt ) {
      org.eclipse.swt.Application._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      // Fix for bug 315230
      if( req.getRequestCounter() != null ) {
        req.send();
      }
    },

    _onKeyPress : function( evt ) {
      if( evt.getKeyIdentifier() == "Escape" ) {
        evt.preventDefault();
      }
    },
    
    _appendWindowSize : function() {
      var width = qx.html.Window.getInnerWidth( window );
      var height = qx.html.Window.getInnerHeight( window );
      // Append document size to request
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      req.addParameter( id + ".bounds.width", String( width ) );
      req.addParameter( id + ".bounds.height", String( height ) );
    },
    
    _appendScrollBarSize : function() {
      var size = org.eclipse.rwt.widgets.ScrollBar.BAR_WIDTH;
      // Append scrollbar size to request
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      req.addParameter( id + ".scrollbar.size", String( size ) );
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
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( "w1.dpi.x", String( dpi[ 0 ] ) );
      req.addParameter( "w1.dpi.y", String( dpi[ 1 ] ) );
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
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( "w1.colorDepth", String( depth ) );
    }

  },

  members : {
    /**
     * An exit confirmation dialog will be displayed if the given message is not
     * null. If the message is empty, the dialog will be displayed but without a
     * message.
     */
    setExitConfirmation : function( message ) {
      // IE shows exit dialog also on empty string
      if( message == "" ) {
        this._exitConfirmation = " ";
      } else {
        this._exitConfirmation = message;
      }
    },
    
    reload : function( message ) {
      if( confirm( message ) ) {
        this.setExitConfirmation( null );
        window.location.reload( false );
      }
    },
    
    getStartupTime : function() {
      return this._startupTime;
    },
    
    main : function( evt ) {
      this.base( arguments );
      // Reduce scroll-event delay to 80ms (default is 250ms)
      // All scroll events that arrive in shorter time will be merged
      // Overwrite the default mapping for internal images. This is necessary
      // if the application is deployed under a root different from "/".
      qx.io.Alias.getInstance().add( "static", "./rwt-resources/resource/static" );
      qx.io.Alias.getInstance().add( "org.eclipse.swt", "./rwt-resources/resource" );
      // Observe window size
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.addEventListener( "windowresize",
                            org.eclipse.swt.Application._onResize );
      // Install key-listener to prevent certain keys from being processed
      doc.addEventListener( "keypress",
                            org.eclipse.swt.Application._onKeyPress );
      // Disable spell-checking in general
      doc.getElement().setAttribute( "spellcheck", "false" );
      // Gecko-specific settings
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        // Prevent url-dropping in FF as a whole (see bug 304651)
        doc.getElement().setAttribute( "ondrop", "event.preventDefault();" );
        // Fix for bug 193703:
        doc.getElement().style.position = "absolute";      
        doc.setSelectable( true );
        // Fix for bug 295475:
        var docElement = document.documentElement;
        qx.html.EventRegistration.addEventListener( docElement, 
                                                    "mousedown", 
                                                    this._onFFMouseDown );
      }
      // Observe browser history
      var history = qx.client.History.getInstance();
      history.addEventListener( "request", this._historyNavigated, this );
      // Initial request to obtain startup-shell
      org.eclipse.swt.Application._appendWindowSize();
      org.eclipse.swt.Application._appendScrollBarSize();
      org.eclipse.swt.Application._appendSystemDPI();      
      org.eclipse.swt.Application._appendColorDepth();
      var req = org.eclipse.swt.Request.getInstance();
      req.addEventListener( "send", this._onSend, this );
      req.send();
    },
    
    close : function( evt ) {
      this.base( arguments );
      return this._exitConfirmation;
    },

    _historyNavigated : function( event ) {
      var entryId = event.getData();
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( "org.eclipse.rwt.events.historyNavigated", "true" );
      req.addParameter( "org.eclipse.rwt.events.historyNavigated.entryId", 
                        entryId );
      req.send();
    },
    
    _onFFMouseDown : function( event ) {
      var tagName = null;
      try{
        tagName = event.originalTarget.tagName;
      } catch( e ) {
        // Firefox bug: On the very first mousedown, access to the events target 
        // is forbidden and causes an error.
      }
      // NOTE: See also Bug 321372
      if( event.button === 0 && tagName != null && tagName != "INPUT" ) {
        event.preventDefault();
      }
    },
    
    _onSend : function( evt ) {
      var pageX = qx.event.type.MouseEvent.getPageX();
      var pageY = qx.event.type.MouseEvent.getPageY();
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".cursorLocation.x", String( pageX ) );
      req.addParameter( id + ".cursorLocation.y", String( pageY ) );
    }
    
  }
});
