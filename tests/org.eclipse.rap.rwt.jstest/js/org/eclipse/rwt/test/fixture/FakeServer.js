/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.fixture.FakeServer", {
  extend : qx.core.Target,
  type : "singleton",

  construct : function() {
    this.base( arguments );
    this._timer = new rwt.client.Timer( 0 );
    this._timer.addEventListener( "interval", this._onTimer, this );
  },

  properties : {
    responseTime : {
      check : "Number",
      nullable : false,
      init : 20
    },

    useAsync : {
      check : "Boolean",
      nullable : false,
      init : false
    },

    requestHandler : {
      check : "Function",
      nullable : true,
      init : null
    }

  },

  members : {
    _requestCounter : 1,
    _timer : null,
    _currentAsyncRequest : null,

    main : function( evt ) {
      this.base( arguments );
    },

    receive : function( request ){
      if( this.getUseAsync() && this._getCall( request, "open" )[ 2 ] ) {
        if( this._timer.isEnabled() ) {
          this.error( "An Asynchronous requests is already processed!" );
        } else {
          this._currentAsyncRequest = request;
          this._timer.setInterval( this.getResponseTime() );
          this._timer.start();
        }
      } else {
        this.respond( request );
      }
    },

    _onTimer : function() {
      this._timer.stop();
      var request = this._currentAsyncRequest;
      this._currentAsyncRequest = null;
      this.respond( request );
    },

    respond : function( request ) {
      var response = this.handleMessage( this._getCall( request, "send" )[ 0 ] );
      request.responseText = response;
      request.status = 200;
      request.readyState = 4;
      request.onreadystatechange();
    },

    handleMessage : function( message ) {
      this._requestCounter++;
      var response = "";
      if( this.getRequestHandler() ) {
        response += this.getRequestHandler()( message );
      }
      response =   "{ \"meta\" : { \"requestCounter\" : "
                 + this._requestCounter
                 + " }, \"operations\" : [] }";
      return response;
    },

    _getCall : function( request, name ) {
      var result;
      for( var i = 0; i < request.getLog().length; i++ ) {
        if( request.getLog()[ i ][ 0 ] === name ) {
          result = request.getLog()[ i ][ 1 ];
        }
      }
      return result;
    }

  }
} );
