
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.Request", {
  type : "singleton",
  extend : qx.core.Target,

  construct : function() {
    this.base( arguments );
    // the URL to which the requests are sent
    this._url = "";
    // the map of parameters that will be posted with the next call to 'send()'
    this._parameters = {};
    // instance variables that hold the essential request parameters
    this._uiRootId = "";
    this._requestCounter = 0;
    // Flag that is set to true if send() was called but the delay timeout
    // has not yet timed out
    this._inDelayedSend = false;
    // Initialize the request queue to allow only one request at a time
    // and set the timout to 5 min (eases debuging)
    var requestQueue = qx.io.remote.RequestQueue.getInstance();
    // As the CallBackRequests get blocked at the server to wait for
    // background activity I choose a large timeout...
    requestQueue.setDefaultTimeout( 60000 * 60 * 24 ); // 24h
    // Standard UI-Requests and CallBackRequests
    requestQueue.setMaxConcurrentRequests( 1 );
    // References the currently running request or null if no request is active
    this._currentRequest = null;
  },
  
  destruct : function() {
    this._currentRequest = null;
  },
  
  events : {
    "send" : "qx.event.type.DataEvent"
  },

  members : {
    setUrl : function( url ) {
      this._url = url;
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

    /**
     * Adds a request parameter to this request with the given name and value
     */
    addParameter : function( name, value ) {
      this._parameters[ name ] = value;
    },

    /**
     * Removes the parameter denoted by name from this request.
     */
    removeParameter : function( name ) {
      delete this._parameters[ name ];
    },
    
    /**
     * Returns the parameter value for the given name or null if no parameter
     * with such a name exists. 
     */
    getParameter : function( name ) {
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
    },


    /**
     * Sends this request. All parameters that were added since the last 'send()'
     * will now be sent.
     */
    send : function() {
      if( !this._inDelayedSend ) {
        this._inDelayedSend = true;
        // Wait and then actually send the request
        // TODO [rh] optimize wait interval (below 60ms seems to not work 
        //      reliable)
        qx.client.Timer.once( this._sendImmediate, this, 60 );
      }
    },

    /**
     * To enable server callbacks to the UI this method sends a request
     * that will be blocked by the server till background activities 
     * require UI updates.
     */
    enableUICallBack : function( url, service_param, service_id ) {
      var request = new qx.io.remote.Request( url, 
                                              qx.net.Http.METHOD_GET, 
                                              qx.util.Mime.JAVASCRIPT );
      request.setParameter( service_param, service_id );
      request.setAsynchronous( true );
//      request.send();
      // TODO [rh] WORKAROUND
      //       we would need two requestQueues (one for 'normal' requests that 
      //       is limited to 1 concurrent request and one for the UI callback
      //       requests (probably without limit)
      //       Until qooxdoo supports multiple requestQueues we send the UI 
      //       callback request without letting the requestQueue know
      var vRequest = request;
      var vTransport = new qx.io.remote.Exchange(vRequest);
      // Establish event connection between qx.io.remote.Exchange instance and
      // qx.io.remote.Request
      vTransport.addEventListener("sending", vRequest._onsending, vRequest);
      vTransport.addEventListener("receiving", vRequest._onreceiving, vRequest);
      vTransport.addEventListener("completed", vRequest._oncompleted, vRequest);
      vTransport.addEventListener("aborted", vRequest._onaborted, vRequest);
      vTransport.addEventListener("timeout", vRequest._ontimeout, vRequest);
      vTransport.addEventListener("failed", vRequest._onfailed, vRequest);
      vTransport._start = (new Date).valueOf();
      vTransport.send();
      // END WORKAROUND
    },

    _sendImmediate : function() {
      this._dispatchSendEvent();
      // set mandatory parameters; do this after regular params to override them
      // in case of conflict
      this._parameters[ "uiRoot" ] = this._uiRootId;
      this._parameters[ "requestCounter" ] = this._requestCounter;

      // create and configure request object
      var request = new qx.io.remote.Request( this._url, 
                                              qx.net.Http.METHOD_POST, 
                                              qx.util.Mime.TEXT );
      // copy the _parameters map which was filled during client interaction to
      // the request
      this._inDelayedSend = false;
      this._copyParameters( request );
      // notify user when request takes longer than 500 ms
      qx.client.Timer.once( this._showWaitHint, this, 500 );
      request.addEventListener( "completed", this._handleCompleted, this );
      request.addEventListener( "failed", this._handleFailed, this );

      this._logSend();
      // queue request to be sent
      // TODO [rh] check that there is no currently active request
      this._currentRequest = request;
      request.send();
      // clear the parameter list
      this._parameters = {};
    },
    
    _copyParameters : function( request ) {
      var data = new Array();
      for( var parameterName in this._parameters ) {
        data.push(   encodeURIComponent( parameterName ) 
                   + "=" 
                   + encodeURIComponent( this._parameters[ parameterName ] ) );
      }
      request.setData( data.join( "&" ) );
    },

    _logSend : function() {
      if( qx.core.Variant.isSet( "qx.debug", "on" ) ) {
        var msg = "sending request [ "; 
        for( var parameterName in this._parameters ) {
          msg += parameterName + "=" + this._parameters[ parameterName ] + "; ";
        }
        msg += " ]";
        this.debug( msg );
      }
    },

    _showWaitHint : function() {
      if( this._currentRequest != null ) {
        if( qx.core.Variant.isSet( "qx.debug", "on" ) ) {
          this.debug( "showWaitHint" );
        }
        var doc = qx.ui.core.ClientDocument.getInstance();
        doc.setGlobalCursor( qx.constant.Style.CURSOR_PROGRESS );
      }
    },
    
    _handleFailed : function( evt ) {
      var text = evt.getTarget().getImplementation().getRequest().responseText;
      document.open( "text/html", true );
      if( text == "" || text == null ) {
        document.write( "<html><head><title>Error Page</title></head><body>" );
        document.write( "<p>Request failed:</p><pre>" );
        document.write( "HTTP Status Code: " );
        document.write( evt.getStatusCode() );
        document.write( "</pre></body></html>" );
      } else {
        document.write( text );
      }
      document.close();
    },
    
    _handleCompleted : function( evt ) {
      var text = evt.getTarget().getImplementation().getRequest().responseText;
      if( text && text.indexOf( "<!DOCTYPE" ) === 0 ) {
        // Handle request to timed out session: write info page and offer
        // link to restart application. This way was chosen for two reasons: 
        // - with rendering an anchor tag we can restart the same entry point as 
        //   is currently used
        // - as clicking the link issues a regular request, we can be sure that 
        //   the stale application will be cleaned up properly by the browser
        var location = window.location;
        document.open( "text/html", true );
        document.write( "<html><head><title>Session timed out</title></head>" );
        document.write( "<body><p>The server session timed out.</p>" );
        document.write( "<p>Please click <a href=\"" );
        document.write( location ); 
        document.write( "\">here</a> to restart the session.</p>" );
        document.write( "</body></html>" );
        document.close();
      } else {
        try {
          if( text && text.length > 0 ) {
            window.eval( text );
          }
          this._hideWaitHint( evt );      
        } catch( ex ) {
          this.error( "Could not execute javascript: [" + text + "]", ex );
          document.open( "text/html", true );
          document.write( "<html><head><title>Error Page</title></head><body>" );
          document.write( "<p>Could not evaluate javascript response:</p><pre>" );
          document.write( ex );
          document.write( "\n\n" );
          document.write( text );
          document.write( "</pre></body></html>" );
          document.close();
        }
      }
    },

    _hideWaitHint : function( evt ) {
      this._currentRequest = null;
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.setGlobalCursor( null );
      if( qx.core.Variant.isSet( "qx.debug", "on" ) ) {
        this.debug( "hideWaitHint" );
      }
    },

    _dispatchSendEvent : function() {
      if( this.hasEventListeners( "send" ) ) {
        var event = new qx.event.type.DataEvent( "send", this );
        this.dispatchEvent( event, true );
      }
    }
  }
});
