/*******************************************************************************
 * Copyright (c) 2004, 2014 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define( "rwt.runtime.System", {

  extend : rwt.qx.Target,

  statics : {

    getInstance : function() {
      return rwt.runtime.Singletons.get( rwt.runtime.System );
    }

  },

  construct : function() {
    if( this.isSupported() ) {
      this.base( arguments );
      this._startupTime = new Date().getTime();
      // Attach load/unload events
      this._onloadWrapped = rwt.util.Functions.bind( this._onload, this );
      this._onbeforeunloadWrapped = rwt.util.Functions.bind( this._onbeforeunload, this );
      this._onunloadWrapped = rwt.util.Functions.bind( this._onunload, this );
      window.addEventListener( "load", this._onloadWrapped, false );
      window.addEventListener( "beforeunload", this._onbeforeunloadWrapped, false );
      window.addEventListener( "unload", this._onunloadWrapped, false );
      rwt.event.EventHandler.setAllowContextMenu( rwt.widgets.Menu.getAllowContextMenu );
      rwt.event.EventHandler.setMenuManager( rwt.widgets.util.MenuManager.getInstance() );
    }
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

    getStartupTime : function() {
      return this._startupTime;
    },

    _onload : function( event ) {
      try {
        if( !this._onloadDone ) {
          this._onloadDone = true;
          rwt.widgets.base.ClientDocument.getInstance();
          rwt.runtime.MobileWebkitSupport.init();
          rwt.client.Timer.once( this._preload, this, 0 );
        }
      } catch( ex ) {
        rwt.runtime.ErrorHandler.processJavaScriptError( ex );
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

    _onbeforeunload : function( event ) {
      try {
        var domEvent = new rwt.event.DomEvent( "beforeunload", event, window, this );
        this.dispatchEvent( domEvent, false );
        var msg = domEvent.getUserData( "returnValue" );
        domEvent.dispose();
        return msg !== null ? msg : undefined;
      } catch( ex ) {
        rwt.runtime.ErrorHandler.processJavaScriptError( ex );
      }
    },

    _onunload : function( event ) {
      try {
        this.createDispatchEvent( "unload" );
        rwt.event.EventHandler.detachEvents();
        rwt.event.EventHandler.cleanUp();
        rwt.qx.Object.dispose( true );
      } catch( ex ) {
        rwt.runtime.ErrorHandler.processJavaScriptError( ex );
      }
    },

    _isBrowserSupported : function() {
      var result = true;
      var engine = rwt.client.Client.getEngine();
      var version = rwt.client.Client.getMajor();
      if( engine === "trident" && version < 9 ) {
        result = false;
      }
      return result;
    },

    _isModeSupported : function() {
      var result = true;
      var engine = rwt.client.Client.getEngine();
      if( engine === "trident" && document.documentMode < 9 ) {
        result = false;
      }
      return result;
    },

    _isXHRSupported : function() {
      return typeof window.XMLHttpRequest !== "undefined";
    }

  },

  destruct : function() {
    window.removeEventListener( "load", this._onloadWrapped, false );
    window.removeEventListener( "beforeunload", this._onbeforeunloadWrapped, false );
    window.removeEventListener( "unload", this._onunloadWrapped, false );
  },

  defer : function( statics, proto, properties )  {
    // Force direct creation
    statics.getInstance();
  }

} );
