/*******************************************************************************
 * Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.System", {

  type : "singleton",

  extend : qx.core.Target,

  construct : function() {
    this.base( arguments );
    this._startupTime = new Date().getTime();
    // Attach load/unload events
    this._onloadWrapped = qx.lang.Function.bind( this._onload, this );
    this._onbeforeunloadWrapped = qx.lang.Function.bind( this._onbeforeunload, this );
    this._onunloadWrapped = qx.lang.Function.bind( this._onunload, this );
    qx.html.EventRegistration.addEventListener( window, "load", this._onloadWrapped );
    qx.html.EventRegistration.addEventListener( window, "beforeunload", this._onbeforeunloadWrapped );
    qx.html.EventRegistration.addEventListener( window, "unload", this._onunloadWrapped );
    // Overwrite the default mapping for internal images. This is necessary
    // if the application is deployed under a root different from "/".
    qx.io.Alias.getInstance().add( "static", "./rwt-resources/resource/static" );
    qx.io.Alias.getInstance().add( "widget", "./rwt-resources/resource/widget/rap" );
    this._applyPatches();
    org.eclipse.rwt.MobileWebkitSupport.init();
    org.eclipse.rwt.GraphicsUtil.init();
    var eventHandler = org.eclipse.rwt.EventHandler;
    eventHandler.setAllowContextMenu( org.eclipse.rwt.widgets.Menu.getAllowContextMenu );
    eventHandler.setMenuManager( org.eclipse.rwt.MenuManager.getInstance() );
    this._registerSingletons();
  },

  events : {
    "beforeunload" : "qx.event.type.DomEvent",
    "unload" : "qx.event.type.Event",
    "uiready" : "qx.event.type.Event"
  },

  members : {
    
    _autoDispose : false,
    _onloadDone : false,
    _uiReady : false,
    
    setUiReady : function( value ) {
      this._uiReady = value;
      if( value ) {
        this.createDispatchEvent( "uiready" );
      }
    },

    getUiReady : function() {
      return this._uiReady;
    },

    _applyPatches : function() {
      if( !org.eclipse.rwt.Client.supportsCss3() ) {
        qx.Class.patch( qx.ui.core.Parent, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( org.eclipse.rwt.widgets.BasicText, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( org.eclipse.rwt.widgets.TreeRow, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( org.eclipse.rwt.widgets.MultiCellWidget, org.eclipse.rwt.GraphicsMixin );
      } else {
        qx.Class.patch( org.eclipse.swt.widgets.ProgressBar, org.eclipse.rwt.GraphicsMixin );
      }
      qx.Class.patch( qx.event.type.DomEvent, org.eclipse.rwt.DomEventPatch );
    },

    _registerSingletons : function() {
      var AdapterRegistry = org.eclipse.rwt.protocol.AdapterRegistry;
      var uiCallBack = org.eclipse.rwt.UICallBack.getInstance();
      org.eclipse.rwt.protocol.ObjectManager.add( 
        "uicb", 
        uiCallBack, 
        AdapterRegistry.getAdapter( "rwt.UICallBack" ) 
      );
      var jsExecutor = org.eclipse.rwt.JSExecutor.getInstance();
      org.eclipse.rwt.protocol.ObjectManager.add( 
        "jsex", 
        jsExecutor, 
        AdapterRegistry.getAdapter( "rwt.JSExecutor" ) 
      );
      var browser = org.eclipse.rwt.widgets.ExternalBrowser.getInstance();
      org.eclipse.rwt.protocol.ObjectManager.add( 
        "eb", 
        browser, 
        AdapterRegistry.getAdapter( "rwt.widgets.ExternalBrowser" ) 
      );
    },

    getStartupTime : function() {
      return this._startupTime;
    },

    _onload : function(e) {
      if( !this._onloadDone ) {
        if( this._isSupported() ) {
          this._onloadDone = true;
          qx.ui.core.ClientDocument.getInstance();
          qx.client.Timer.once( this._preload, this, 0 );
        } else {
          this._handleUnsupportedBrowser();
        }
      }
    },

    _preload : function() {
      var visibleImages = qx.io.image.Manager.getInstance().getVisibleImages();
      this.__preloader = new qx.io.image.PreloaderSystem( visibleImages, this._preloaderDone, this );
      this.__preloader.start();
    },

    _preloaderDone : function() {
      this.__preloader.dispose();
      this.__preloader = null;
      org.eclipse.rwt.EventHandler.init();
      org.eclipse.rwt.EventHandler.attachEvents();
      this.setUiReady( true );
      qx.ui.core.Widget.flushGlobalQueues();
      qx.client.Timer.once( this._postload, this, 100 );
    },

    _postload : function() {
      var hiddenImages = qx.io.image.Manager.getInstance().getHiddenImages();
      this.__postloader = new qx.io.image.PreloaderSystem( hiddenImages, this._postloaderDone, this );
      this.__postloader.start();
    },

    _postloaderDone : function() {
      this.__postloader.dispose();
      this.__postloader = null;
    },

    _onbeforeunload : function( e ) {
      var event = new qx.event.type.DomEvent( "beforeunload", e, window, this );
      this.dispatchEvent( event, false );
      var msg = event.getUserData( "returnValue" );
      event.dispose();
      return msg !== null ? msg : undefined;
    },

    _onunload : function( e ) {
      this.createDispatchEvent( "unload" );
      org.eclipse.rwt.EventHandler.detachEvents();
      org.eclipse.rwt.EventHandler.cleanUp();
      qx.core.Object.dispose( true );
    },
    
    _isSupported : function() {
      var result = true;
      var engine = org.eclipse.rwt.Client.getEngine();
      var version = org.eclipse.rwt.Client.getMajor();
      if( engine === "mshtml" && version < 7 ) {
        result = false;
      }
      return result;
    },
    
    _handleUnsupportedBrowser : function() {
      document.write( "<big style='background-color:white;color:black;'>" );
      document.write( "Unsupported Browser: You're using an " );
      document.write( "outdated browser version that is not supported anymore." );
      document.write( "</big>" );
    }
    
  },

  destruct : function() {
    qx.html.EventRegistration.removeEventListener( window, "load", this._onloadWrapped );
    qx.html.EventRegistration.removeEventListener( window, "beforeunload", this._onbeforeunloadWrapped );
    qx.html.EventRegistration.removeEventListener( window, "unload", this._onunloadWrapped );
  },

  defer : function( statics, proto, properties )  {
    // Force direct creation
    statics.getInstance();
  }

} );
