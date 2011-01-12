/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * Modified qx.io.remote.Request
 */
qx.Class.define( "org.eclipse.rwt.test.fixture.DummyRequest", {
  extend : qx.core.Target,
  
  construct : function() {
    this.base( arguments );  
    this._requestHeaders = {};
    this._parameters = {};
    this._formFields = {};  
    this.setProhibitCaching( true );  
  },
  
  properties : {
    
    asynchronous : {
      check : "Boolean",
      init : true
    },
  
    data : {
      check : "String",
      nullable : true
    },
    
    state : {
      check : [ "configured",
                "queued",
                "sending",
                "receiving",
                "completed",
                "aborted",
                "timeout",
                "failed" ],
      init  : "configured",
      apply : "_applyState",
      event : "changeState"
    },
    
    timeout : {
      check : "Integer",
      nullable : true
    },
    
    prohibitCaching : {
      check : "Boolean",
        init : true
    }
    
  },
  
  members : {
    responseText : "",
  
    setResponse : function( response ) {
      this.responseText = response;
    },
    
    send : function() {
      org.eclipse.rwt.test.fixture.DummyRequest.addRequest( this );
    },
    
    abort : function() {
      this.error( "abort not supported!" );
    },
    
    reset : function() {
      switch( this.getState() ) {
        case "sending":
        case "receiving":
          this.error( "Aborting already sent request!" );
    
          // no break
    
        case "queued":
          this.abort();
          break;
      }
    },
    
    /**
     * needed for org.eclipse.swt.Request
     * 
     * NOTE:
     * currently, the dummy does not actually use a response object
     * might be implemented in the future if needed 
     */
    getImplementation : function() {
      return this;
    },
    
    getRequest : function() {
      return this;
    },
    
    /*
    ---------------------------------------------------------------------------
      STATE ALIASES
    ---------------------------------------------------------------------------
     */
    
    isConfigured : function() {
      return this.getState() === "configured";
    },
    
    isQueued : function() {
      return this.getState() === "queued";
    },
    
    isSending : function() {
      return this.getState() === "sending";
    },
    
    isReceiving : function() {
      return this.getState() === "receiving";
    },
    
    isCompleted : function() {
      return this.getState() === "completed";
    },
    
    isAborted : function() {
      return this.getState() === "aborted";
    },
    
    isTimeout : function() {
      return this.getState() === "timeout";
    },
    
    isFailed : function() {
      return this.getState() === "failed";
    },
    
    /*
    ---------------------------------------------------------------------------
      EVENT HANDLER
    ---------------------------------------------------------------------------
     */
    
    _onqueued : function( e ) {
      this.setState( "queued" );
      this.dispatchEvent( e );
    },
    
    _onsending : function( e ) {
      this.setState( "sending" );
      this.dispatchEvent( e );
    },
    
    _onreceiving : function( e ) {
      this.setState( "receiving" );
      this.dispatchEvent( e );
    },
    
    _oncompleted : function( e ) {
      this.setState( "completed" );
      this.dispatchEvent( e );
      this.dispose();
    },
    
    _onaborted : function( e ) {
      this.setState( "aborted" );
      this.dispatchEvent( e );
      this.dispose();
    },
    
    _ontimeout : function( e ) {
      this.setState( "timeout" );
      this.dispatchEvent( e );
      this.dispose();
    },
    
    _onfailed : function( e ) {
      this.setState( "failed" );
      this.dispatchEvent( e );
      this.dispose();
    },
    
    /*
    ---------------------------------------------------------------------------
      APPLY ROUTINES
    ---------------------------------------------------------------------------
     */
    
    _applyState : function( value, old ) {
      if( qx.core.Variant.isSet( "qx.debug", "on" ) ) {
        if( qx.core.Setting.get( "qx.ioRemoteDebug" ) ) {
          this.debug( "State: " + value );
        }
      }
    },
    
    /*
    ---------------------------------------------------------------------------
      PARAMETERS
    ---------------------------------------------------------------------------
     */
    
    setParameter : function( vId, vValue ) {
      this._parameters[ vId ] = vValue;
    },
    
    removeParameter : function( vId ) {
      delete this._parameters[ vId ];
    },
    
    getParameter : function( vId ) {
      return this._parameters[ vId ] || null;
    },
    
    getParameters : function() {
      return this._parameters;
    },
  
  /*
  ---------------------------------------------------------------------------
    FORM FIELDS
  ---------------------------------------------------------------------------
   */
  
    // Neither supported nor needed for the dummy
    setFormField : function( vId, vValue ) {
      this._formFields[ vId ] = vValue;
    },
  
    removeFormField : function( vId ) {
      delete this._formFields[ vId ];
    },
  
    getFormField : function( vId ) {
      return this._formFields[ vId ] || null;
    },
  
    getFormFields : function() {
      return this._formFields;
    },
  
    getSequenceNumber : function() {
      return null;
    }
  },
  
  events : {
    "created" : "qx.event.type.Event",
    "configured" : "qx.event.type.Event",
    "sending" : "qx.event.type.Event",
    "receiving" : "qx.event.type.Event",
    "completed" : "qx.io.remote.Response",
    "aborted" : "qx.io.remote.Response",
    "failed" : "qx.io.remote.Response",
    "timeout" : "qx.io.remote.Response"
  },
  
  /**
  * Statics are used to communicate with the "RAPServer"-Singleton
  */
  statics : {
    _queue : [],
  
    addRequest : function( request ) {
      if( request.getAsynchronous() ) {
        org.eclipse.rwt.test.fixture.DummyRequest._queue.push( request );
        request._onqueued( new qx.event.type.Event( "queued" ) );
        org.eclipse.rwt.test.fixture.DummyRequest._sendAsync();
      } else {
        org.eclipse.rwt.test.fixture.DummyRequest._sendSync( request );
      }
    },
    
    // TODO [tb] : For practicability-reasons, this actually works synchronous.
    //             That may lead to some difficulies when there is code that
    //             is assumed to have been executed between the "send"-call
    //             and the response. If this is the case, the
    //             "Could not execute javascript:"-error from 
    //             from "org.eclipse.swt.Request" will occur.
    //             Asynchronous communication could be simulated with  
    //             some time and effort.
  
    _sendAsync : function() {
      if( org.eclipse.rwt.test.fixture.DummyRequest._queue.length == 1 ) {
        // prevent the flush of Request.js
        org.eclipse.rwt.test.fixture.TestUtil.preventFlushs( true );
        var request = org.eclipse.rwt.test.fixture.DummyRequest._queue[ 0 ];
        request._onsending( new qx.event.type.Event( "sending" ) );
        org.eclipse.rwt.test.fixture.RAPServer.getInstance().receive( request );
        org.eclipse.rwt.test.fixture.TestUtil.preventFlushs( false );
      }
    },
    
    receiveAsync : function( request ) {
      request._onreceiving( new qx.event.type.Event( "receiving" ) );
      request._oncompleted( new qx.event.type.Event( "completed" ) );
      org.eclipse.rwt.test.fixture.DummyRequest._queue.shift();
      org.eclipse.rwt.test.fixture.DummyRequest._sendAsync();
    },
    
    _sendSync : function( request ) {
      request._onsending( new qx.event.type.Event( "sending" ) );
      org.eclipse.rwt.test.fixture.RAPServer.getInstance().receive( request );
    },
    
    receiveSync : function( request ) {
      request._onreceiving( new qx.event.type.Event( "receiving" ) );
      request._oncompleted( new qx.event.type.Event( "completed" ) );
    }  
  },
  
  destruct : function() {
    this._disposeFields( "_requestHeaders", "_parameters", "_formFields" );
  }
} );
