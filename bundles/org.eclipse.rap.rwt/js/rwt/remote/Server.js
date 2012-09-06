/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

(function(){

var Client = rwt.client.Client;
var Timer = rwt.client.Timer;
var Processor = rwt.protocol.MessageProcessor;
var ErrorHandler = org.eclipse.rwt.ErrorHandler;
var EventUtil = org.eclipse.swt.EventUtil;
var UICallBack = rwt.client.UICallBack;
var ClientDocument = rwt.widgets.base.ClientDocument;
var Widget = rwt.widgets.base.Widget;

qx.Class.define( "rwt.remote.Server", {
  type : "singleton",
  extend : qx.core.Target,

  construct : function() {
    this.base( arguments );
    this._url = "";
    this._parameters = {};
    this._writer = null;
    this._event = null;
    this._uiRootId = "";
    this._requestCounter = null;
    this._sendTimer = new Timer( 60 );
    this._sendTimer.addEventListener( "interval", function() {
      this.sendImmediate( true );
     }, this );
    this._waitHintTimer = new Timer( 500 );
    this._waitHintTimer.addEventListener( "interval", this._showWaitHint, this );
    this._retryHandler = null;
  },

  destruct : function() {
    this._retryHandler = null;
    this._sendTimer.dispose();
    this._sendTimer = null;
    this._waitHintTimer.dispose();
    this._waitHintTimer = null;
  },

  members : {

    //////
    // API

    setUrl : function( url ) {
      this._url = url;
    },

    getUrl : function() {
      return this._url;
    },

    setUIRootId : function( uiRootId ) {
      this._uiRootId = uiRootId;
    },

    getUIRootId : function() {
      return this._uiRootId;
    },

    setRequestCounter : function( requestCounter ) {
      this._requestCounter = requestCounter;
    },

    getRequestCounter : function() {
      return this._requestCounter;
    },

    /**
     * Adds a request parameter to the next request with the given name and value
     */
    addParameter : function( name, value ) {
      this._parameters[ name ] = value;
      var nameArr = typeof name === "string" ? name.split( "." ) : null;
      if( nameArr ) {
        if( this._event && name.indexOf( this._event[ 1 ] ) === 0 ) {
          this._event[ 2 ][ nameArr.pop() ] = value;
        } else {
          this._flushEvent();
          var id = nameArr.shift();
          var property = nameArr.join( "." );// there are cases of event-types with "."
          this.getMessageWriter().appendSet( id, property, value );
        }
      } else {
        // Currently results of TSD are not compatible with protocol
        //console.trace();
      }
    },

    /**
     * Removes the parameter denoted by name from the next request
     */
    removeParameter : function( name ) {
      // TODO [tb] : Can not be supported in JSON protocol
      delete this._parameters[ name ];
    },

    /**
     * Returns the parameter value for the given name or null if no parameter
     * with such a name exists.
     */
    getParameter : function( name ) {
      // TODO [tb] : Can not be supported in JSON protocol
      var result = this._parameters[ name ];
      if( result === undefined ) {
        result = null;
      }
      return result;
    },

    /**
     * Adds the given eventType to this request. The sourceId denotes the id of
     * the widget that caused the event.
     */
    addEvent : function( eventType, sourceId ) {
      this._parameters[ eventType ] = sourceId;
      this._flushEvent();
      this._event = [ sourceId, eventType, {} ];
    },

    _flushEvent : function() {
      if( this._event ) {
        var writer = this.getMessageWriter();
        this._event[ 1 ] = this._event[ 1 ].split( "." ).pop();
        writer.appendNotify.apply( writer, this._event );
        this._event = null;
      }
    },

    /**
     * Sends an asynchronous request within 60 milliseconds
     */
    send : function() {
      this._sendTimer.start();
    },

    /**
     * Sends an synchronous or asynchronous request immediately. All parameters that were added
     * since the last request will be sent.
     */
    sendImmediate : function( async ) {
      this._sendTimer.stop();
      if( this._requestCounter === -1 ) {
        // NOTE: Delay sending the request until requestCounter is set
        // TOOD [tb] : This would not work with synchronous requests - bug?
        this.send();
      } else {
        this.dispatchSimpleEvent( "send" );
        this._parameters[ "uiRoot" ] = this._uiRootId;
        if( this._requestCounter != null ) {
          this._parameters[ "requestCounter" ] = this._requestCounter;
          this.getMessageWriter().appendMeta( "requestCounter", this._requestCounter );
          this._requestCounter = -1;
        }
        var request = this._createRequest();
        request.setAsynchronous( async );
        this._attachParameters( request );
        this._waitHintTimer.start();
        this._parameters = {};
        request.send();
      }
    },

    getMessageWriter : function() {
      if( this._writer === null ) {
        this._writer = new rwt.protocol.MessageWriter();
      }
      return this._writer;
    },

    getServerObject : function( target ) {
      return rwt.protocol.ServerObjectFactory.getServerObject( target );
    },

    ////////////
    // Internals

    _attachParameters : function( request ) {
      var data = [];
      for( var key in this._parameters ) {
        data.push(
          encodeURIComponent( key ) + "=" + encodeURIComponent( this._parameters[ key ] )
        );
      }
      this._flushEvent();
      data.push( "message=" + encodeURIComponent( this.getMessageWriter().createMessage() ) );
      this._writer.dispose();
      this._writer = null;
      request.setData( data.join( "&" ) );
    },

    _createRequest : function() {
      var result = new rwt.remote.Request( this._url, "POST", "application/javascript" );
      result.setSuccessHandler( this._handleSuccess, this );
      result.setErrorHandler( this._handleError, this );
      return result;
    },

    ////////////////////////
    // Handle request events

    _handleError : function( event ) {
      if( this._isConnectionError( event.status ) ) {
        this._handleConnectionError( event );
      } else {
        var text = event.responseText;
        if( text && text.length > 0 ) {
          if( this._isJsonResponse( event ) ) {
            var messageObject = JSON.parse( text );
            ErrorHandler.showErrorBox( messageObject.meta.message, true );
          } else {
            ErrorHandler.showErrorPage( text );
          }
        } else {
          var msg = "<p>Request failed.</p><pre>HTTP Status Code: " + event.status + "</pre>";
          ErrorHandler.showErrorPage( msg );
        }
      }
      this._hideWaitHint();
    },

    _handleSuccess : function( event ) {
      try {
        var messageObject = JSON.parse( event.responseText );
        org.eclipse.swt.EventUtil.setSuspended( true );
        Processor.processMessage( messageObject );
        Widget.flushGlobalQueues();
        EventUtil.setSuspended( false );
        UICallBack.getInstance().sendUICallBackRequest();
        this.dispatchSimpleEvent( "received" );
      } catch( ex ) {
        ErrorHandler.processJavaScriptErrorInResponse( event.responseText, ex, event.target );
      }
      this._hideWaitHint();
    },

    ///////////////////////////////
    // Handling connection problems

    _handleConnectionError : function( event ) {
      var msg
        = "<p>The server seems to be temporarily unavailable</p>"
        + "<p><a href=\"javascript:rwt.remote.Server.getInstance()._retry();\">Retry</a></p>";
      ClientDocument.getInstance().setGlobalCursor( null );
      org.eclipse.rwt.ErrorHandler.showErrorBox( msg, false );
      this._retryHandler = function() {
        var request = this._createRequest();
        var failedRequest = event.target;
        request.setAsynchronous( failedRequest.getAsynchronous() );
        request.setData( failedRequest.getData() );
        request.send();
      };
    },

    _retry : function() {
      try {
        org.eclipse.rwt.ErrorHandler.hideErrorBox();
        this._showWaitHint();
        this._retryHandler();
      } catch( ex ) {
        org.eclipse.rwt.ErrorHandler.processJavaScriptError( ex );
      }
    },

    _isConnectionError : qx.core.Variant.select( "qx.client", {
      "mshtml|newmshtml" : function( statusCode ) {
        // for a description of the IE status codes, see
        // http://support.microsoft.com/kb/193625
        var result = (    statusCode === 12007    // ERROR_INTERNET_NAME_NOT_RESOLVED
                       || statusCode === 12029    // ERROR_INTERNET_CANNOT_CONNECT
                       || statusCode === 12030    // ERROR_INTERNET_CONNECTION_ABORTED
                       || statusCode === 12031    // ERROR_INTERNET_CONNECTION_RESET
                       || statusCode === 12152 ); // ERROR_HTTP_INVALID_SERVER_RESPONSE
        return result;
      },
      "gecko" : function( statusCode ) {
        // Firefox 3 reports other statusCode than oder versions (bug #249814)
        var result;
        // Check if Gecko > 1.9 is running (used in FF 3)
        // Gecko/app integration overview: http://developer.mozilla.org/en/Gecko
        if( Client.getMajor() * 10 + Client.getMinor() >= 19 ) {
          result = ( statusCode === 0 );
        } else {
          result = ( statusCode === -1 );
        }
        return result;
      },
      "default" : function( statusCode ) {
        return statusCode === 0;
      }
    } ),

    _isJsonResponse : function( event ) {
      var contentType = event.responseHeaders[ "Content-Type" ];
      return contentType.indexOf( qx.util.Mime.JSON ) !== -1;
    },

    ///////////////////////////////////////////////////
    // Wait hint - UI feedback while request is running

    _showWaitHint : function() {
      this._waitHintTimer.stop();
      ClientDocument.getInstance().setGlobalCursor( qx.constant.Style.CURSOR_PROGRESS );
    },

    _hideWaitHint : function() {
      this._waitHintTimer.stop();
      ClientDocument.getInstance().setGlobalCursor( null );
    }

  }
} );

}());
