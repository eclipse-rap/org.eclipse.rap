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
 
qx.Class.define( "org.eclipse.rwt.ErrorHandler", {

  statics : {

    showError : function( content ) {
      this._enableTextSelection();
      this._freezeApplication();
      document.title = "Error Page";
      this._createErrorArea().innerHTML = content;
    },

    showTimeout : function( content ) {
      var location = String( window.location );
      var index = location.indexOf( "#" );
      if( index != -1 ) {
        location = location.substring( 0, index );
      }
      var hrefAttr = "href=\"" + location + "\"";
      var html = content.replace( /{HREF_URL}/, hrefAttr );
      this._freezeApplication();
      this._createOverlay();
      this._createTimeoutArea( 400, 100 ).innerHTML = html;
    },

    processJavaScriptErrorInResponse : function( script, error, currentRequest ) {
      var content = "<p>Could not evaluate javascript response:</p><pre>";
      content += this._gatherErrorInfo( error, script, currentRequest );
      content += "</pre>";
      this.showError( content );
    },

    processJavaScriptError : function( error ) {
      var content = "<p>Javascript error occurred:</p><pre>";
      content += this._gatherErrorInfo( error );
      content += "</pre>";
      this.showError( content );
      throw error;
    },

    _gatherErrorInfo : function( error, script, currentRequest ) {
      var info = [];
      try {
	      info.push( "Error: " + error + "\n" );
	      if( script ) {
		      info.push( "Script: " + script );
	      }
        if( error instanceof Error ) {
          for( var key in error ) {
            info.push( key + ": " + error[ key ] );
          }
        }
        info.push( "Debug: " + qx.core.Variant.get( "qx.debug" ) );
        if( currentRequest ) {
	        info.push( "Request: " + currentRequest.getData() );
        }
        var inFlush = qx.ui.core.Widget._inFlushGlobalQueues;
        if( inFlush ) {
          info.push( "Phase: " + qx.ui.core.Widget._flushGlobalQueuesPhase );
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

    _createErrorArea : function() {
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

    _createTimeoutArea : function( width, height ) {
      var element = document.createElement( "div" );
      var style = element.style;
      style.position = "absolute";
      style.width = width + "px";
      style.height = height + "px";
      var doc = qx.ui.core.ClientDocument.getInstance();
      var left = ( doc.getClientWidth() - width ) / 2;
      var top = ( doc.getClientHeight() - height ) / 2;
      style.left = left < 0 ? 0 : left;
      style.top = top < 0 ? 0 : top;
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
      var app = qx.core.Init.getInstance().getApplication();
      app.setExitConfirmation( null );
      qx.io.remote.RequestQueue.getInstance().setEnabled( false );
      org.eclipse.rwt.EventHandler.detachEvents();
      qx.core.Target.prototype.dispatchEvent = function() {};
      org.eclipse.rwt.Animation._stopLoop();
    },

    _enableTextSelection : function() {
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.setSelectable( true );
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        var app = qx.core.Init.getInstance().getApplication();
        qx.html.EventRegistration.removeEventListener( document.documentElement,
                                                       "mousedown",
                                                       app._onFFMouseDown );
      }
    }

  }

} );