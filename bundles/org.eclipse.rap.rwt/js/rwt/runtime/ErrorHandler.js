/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*global console: false */

qx.Class.define( "rwt.runtime.ErrorHandler", {

  statics : {

    _overlay : null,
    _box : null,

    processJavaScriptErrorInResponse : function( script, error, currentRequest ) {
      var content = "<p>Could not process server response:</p><pre>";
      content += this._gatherErrorInfo( error, script, currentRequest );
      content += "</pre>";
      this.showErrorPage( content );
    },

    processJavaScriptError : function( error ) {
      this.errorObject = error; // for later inspection by developer
      if( typeof console === "object" ) {
        var msg = "Error: " + ( error.message ? error.message : error );
        if( typeof console.error !== "undefined" ) { // IE returns "object" for typeof
          console.error( msg );
        } else if( typeof console.log !== "undefined" ) {
          console.log( msg );
        }
        if( typeof console.log === "function" && error.stack ) {
          console.log( "Error stack:\n" + error.stack );
        } else if( typeof console.trace !== "undefined" ) {
          console.trace();
        }
      }
      var debug = true;
      try {
        debug = rwt.util.Variant.isSet( "qx.debug", "on" );
      } catch( ex ) {
        // ignore: Variant may not be loaded yet
      }
      if( debug ) {
        var content = "<p>Javascript error occurred:</p><pre>";
        content += this._gatherErrorInfo( error );
        content += "</pre>";
        this.showErrorPage( content );
        throw error;
      }
    },

    showErrorPage : function( content ) {
      this._enableTextSelection();
      this._freezeApplication();
      document.title = "Error Page";
      this._createErrorPageArea().innerHTML = content;
    },

    showErrorBox : function( content, freeze ) {
      var location = String( window.location );
      var index = location.indexOf( "#" );
      if( index != -1 ) {
        location = location.substring( 0, index );
      }
      var hrefAttr = "href=\"" + location + "\"";
      var html = content.replace( /\{HREF_URL\}/, hrefAttr );
      html = rwt.protocol.EncodingUtil.replaceNewLines( html, "<br/>" );
      if( freeze ) {
        this._freezeApplication();
      }
      this._overlay = this._createOverlay();
      this._box = this._createErrorBoxArea( 400, 100 );
      this._box.innerHTML = html;
      var hyperlink = this._box.getElementsByTagName( "a" )[ 0 ];
      hyperlink.style.outline = "none";
      hyperlink.focus();
    },

    hideErrorBox : function() {
      if( this._box ) {
        this._box.parentElement.removeChild( this._box );
        this._overlay.parentElement.removeChild( this._overlay );
      }
    },

    _gatherErrorInfo : function( error, script, currentRequest ) {
      var info = [];
      try {
        info.push( "Error: " + error + "\n" );
        if( script ) {
          info.push( "Script: " + script );
        }
        if( error instanceof Error ) {
          for( var key in error ) { // NOTE : does not work in webkit (no iteration)
            info.push( key + ": " + error[ key ] );
          }
          if( error.stack ) { // ensures stack is printed in webkit, might be printed twice in gecko
            info.push( "Stack: " + error.stack );
          }
       }
        info.push( "Debug: " + rwt.util.Variant.get( "qx.debug" ) );
        if( currentRequest ) {
          info.push( "Request: " + currentRequest.getData() );
        }
        var inFlush = rwt.widgets.base.Widget._inFlushGlobalQueues;
        if( inFlush ) {
          info.push( "Phase: " + rwt.widgets.base.Widget._flushGlobalQueuesPhase );
        }
      } catch( ex ) {
        // ensure we get a info no matter what
      }
      return info.join( "\n  " );
    },

    _createOverlay : function() {
      var element = document.createElement( "div" );
      var style = element.style;
      style.position = "absolute";
      style.width = "100%";
      style.height = "100%";
      style.backgroundColor = "#808080";
      org.eclipse.rwt.HtmlUtil.setOpacity( element, 0.2 );
      style.zIndex = 100000000;
      document.body.appendChild( element );
      return element;
    },

    _createErrorPageArea : function() {
      var element = document.createElement( "div" );
      var style = element.style;
      style.position = "absolute";
      style.width = "100%";
      style.height = "100%";
      style.backgroundColor = "#ffffff";
      style.zIndex = 100000001;
      style.overflow = "auto";
      style.padding = "10px";
      document.body.appendChild( element );
      return element;
    },

    _createErrorBoxArea : function( width, height ) {
      var element = document.createElement( "div" );
      var style = element.style;
      style.position = "absolute";
      style.width = width + "px";
      style.height = height + "px";
      var doc = rwt.widgets.base.ClientDocument.getInstance();
      var left = ( doc.getClientWidth() - width ) / 2;
      var top = ( doc.getClientHeight() - height ) / 2;
      style.left = ( left < 0 ? 0 : left ) + "px";
      style.top = ( top < 0 ? 0 : top ) + "px";
      style.backgroundColor = "#dae9f7";
      style.border = "1px solid black";
      style.zIndex = 100000001;
      style.overflow = "auto";
      style.padding = "10px";
      style.textAlign = "center";
      style.fontFamily = 'verdana,"lucida sans",arial,helvetica,sans-serif';
      style.fontSize = "12px";
      style.fontStyle = "normal";
      style.fontWeight = "normal";
      document.body.appendChild( element );
      return element;
    },

    _freezeApplication : function() {
      try {
        var display = rwt.widgets.Display.getCurrent();
        display.setExitConfirmation( null );
        //qx.io.remote.RequestQueue.getInstance().setEnabled( false );
        org.eclipse.rwt.EventHandler.detachEvents();
        qx.core.Target.prototype.dispatchEvent = function() {};
        rwt.animation.Animation._stopLoop();
      } catch( ex ) {
        try {
          console.log( "_freezeApplication exception: " + ex );
        } catch( exTwo ) {
          // ignore
        }
      }
    },

    _enableTextSelection : function() {
      var doc = rwt.widgets.base.ClientDocument.getInstance();
      doc.setSelectable( true );
      if( rwt.client.Client.isGecko() ) {
        var EventHandlerUtil = org.eclipse.rwt.EventHandlerUtil;
        qx.html.EventRegistration.removeEventListener( document.documentElement,
                                                       "mousedown",
                                                       EventHandlerUtil._ffMouseFixListener );
      }
    }

  }

} );
