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

qx.Class.define( "rwt.runtime.System", {

  type : "singleton",

  extend : qx.core.Target,

  construct : function() {
    if( this.isSupported() ) {
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
      org.eclipse.rwt.GraphicsUtil.init();
      var eventHandler = org.eclipse.rwt.EventHandler;
      eventHandler.setAllowContextMenu( rwt.widgets.Menu.getAllowContextMenu );
      eventHandler.setMenuManager( org.eclipse.rwt.MenuManager.getInstance() );
    } else {
      this._handleUnsupported();
    }
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

    isSupported : function() {
      return this._isBrowserSupported() && this._isModeSupported();
    },

    _applyPatches : function() {
      if( !rwt.client.Client.supportsCss3() ) {
        qx.Class.patch( rwt.widgets.base.Parent, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( rwt.widgets.base.BasicText, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( rwt.widgets.base.GridRow, org.eclipse.rwt.GraphicsMixin );
        qx.Class.patch( rwt.widgets.base.MultiCellWidget, org.eclipse.rwt.GraphicsMixin );
      } else {
        qx.Class.patch( rwt.widgets.ProgressBar, org.eclipse.rwt.GraphicsMixin );
      }
      qx.Class.patch( qx.event.type.DomEvent, org.eclipse.rwt.DomEventPatch );
    },

    getStartupTime : function() {
      return this._startupTime;
    },

    _onload : function(e) {
      if( !this._onloadDone ) {
        this._onloadDone = true;
        rwt.widgets.base.ClientDocument.getInstance();
        rwt.runtime.MobileWebkitSupport.init();
        rwt.client.Timer.once( this._preload, this, 0 );
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
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.client.Timer.once( this._postload, this, 100 );
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

    _isBrowserSupported : function() {
      var result = true;
      var engine = rwt.client.Client.getEngine();
      var version = rwt.client.Client.getMajor();
      if( engine === "mshtml" && version < 7 ) {
        result = false;
      }
      return result;
    },

    _isModeSupported : function() {
      var result = true;
      var engine = rwt.client.Client.getEngine();
      if( engine === "newmshtml" && document.documentMode < 9 ) {
        result = false;
      }
      return result;
    },

    _handleUnsupported : function() {
      document.write( "<big style='background-color:white;color:black;'>" );
      if( !this._isModeSupported() ) {
        document.write( "Unsupported Browser mode: Your Browser is only supported when " );
        document.write( "running in standard mode, but is running in quirksmode." );
      } else {
        document.write( "Unsupported Browser: You're using an " );
        document.write( "outdated browser version that is not supported anymore." );
      }
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
