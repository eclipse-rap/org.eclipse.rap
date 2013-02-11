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
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define( "rwt.runtime.System", {

  type : "singleton",

  extend : rwt.qx.Target,

  construct : function() {
    if( this.isSupported() ) {
      this.base( arguments );
      this._startupTime = new Date().getTime();
      // Attach load/unload events
      this._onloadWrapped = rwt.util.Functions.bind( this._onload, this );
      this._onbeforeunloadWrapped = rwt.util.Functions.bind( this._onbeforeunload, this );
      this._onunloadWrapped = rwt.util.Functions.bind( this._onunload, this );
      rwt.html.EventRegistration.addEventListener( window, "load", this._onloadWrapped );
      rwt.html.EventRegistration.addEventListener( window, "beforeunload", this._onbeforeunloadWrapped );
      rwt.html.EventRegistration.addEventListener( window, "unload", this._onunloadWrapped );
      this._applyPatches();
      rwt.graphics.GraphicsUtil.init();
      var eventHandler = rwt.event.EventHandler;
      eventHandler.setAllowContextMenu( rwt.widgets.Menu.getAllowContextMenu );
      eventHandler.setMenuManager( rwt.widgets.util.MenuManager.getInstance() );
    }
  },

  events : {
    "beforeunload" : "rwt.event.DomEvent",
    "unload" : "rwt.event.Event",
    "uiready" : "rwt.event.Event"
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
      return this._isBrowserSupported() && this._isModeSupported() && this._isXHRSupported();
    },

    _applyPatches : function() {
      if( !rwt.client.Client.supportsCss3() ) {
        rwt.qx.Class.patch( rwt.widgets.base.Parent, rwt.widgets.util.GraphicsMixin );
        rwt.qx.Class.patch( rwt.widgets.base.BasicText, rwt.widgets.util.GraphicsMixin );
        rwt.qx.Class.patch( rwt.widgets.base.GridRow, rwt.widgets.util.GraphicsMixin );
        rwt.qx.Class.patch( rwt.widgets.base.MultiCellWidget, rwt.widgets.util.GraphicsMixin );
      } else {
        rwt.qx.Class.patch( rwt.widgets.ProgressBar, rwt.widgets.util.GraphicsMixin );
      }
      rwt.qx.Class.patch( rwt.event.DomEvent, rwt.event.DomEventPatch );
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
      var visibleImages = rwt.html.ImageManager.getInstance().getVisibleImages();
      this.__preloader = new rwt.html.ImagePreloaderSystem( visibleImages, this._preloaderDone, this );
      this.__preloader.start();
    },

    _preloaderDone : function() {
      this.__preloader.dispose();
      this.__preloader = null;
      rwt.event.EventHandler.init();
      rwt.event.EventHandler.attachEvents();
      this.setUiReady( true );
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.client.Timer.once( this._postload, this, 100 );
    },

    _postload : function() {
      var hiddenImages = rwt.html.ImageManager.getInstance().getHiddenImages();
      this.__postloader = new rwt.html.ImagePreloaderSystem( hiddenImages, this._postloaderDone, this );
      this.__postloader.start();
    },

    _postloaderDone : function() {
      this.__postloader.dispose();
      this.__postloader = null;
    },

    _onbeforeunload : function( e ) {
      var event = new rwt.event.DomEvent( "beforeunload", e, window, this );
      this.dispatchEvent( event, false );
      var msg = event.getUserData( "returnValue" );
      event.dispose();
      return msg !== null ? msg : undefined;
    },

    _onunload : function( e ) {
      this.createDispatchEvent( "unload" );
      rwt.event.EventHandler.detachEvents();
      rwt.event.EventHandler.cleanUp();
      rwt.qx.Object.dispose( true );
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

    _isXHRSupported : function() {
      return typeof window.XMLHttpRequest !== "undefined";
    }

  },

  destruct : function() {
    rwt.html.EventRegistration.removeEventListener( window, "load", this._onloadWrapped );
    rwt.html.EventRegistration.removeEventListener( window, "beforeunload", this._onbeforeunloadWrapped );
    rwt.html.EventRegistration.removeEventListener( window, "unload", this._onunloadWrapped );
  },

  defer : function( statics, proto, properties )  {
    // Force direct creation
    statics.getInstance();
  }

} );
