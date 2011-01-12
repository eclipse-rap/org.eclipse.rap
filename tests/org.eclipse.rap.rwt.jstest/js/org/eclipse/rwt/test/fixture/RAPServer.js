/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.fixture.RAPServer", {
  extend : qx.core.Target,
  type : "singleton",

  construct : function() {
    this.base( arguments );    
    this._timer = new qx.client.Timer(0);
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
      if( this.getUseAsync() && request.getAsynchronous() ) {
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
    
    respond : function(request) {
      var response = this.handleMessage( request.getData() );
      request.setResponse( response );
      if( request.getAsynchronous() ){
        org.eclipse.rwt.test.fixture.DummyRequest.receiveAsync( request );
      }else{
        org.eclipse.rwt.test.fixture.DummyRequest.receiveSync( request );
      }
    },
    
    handleMessage : function(message) {
      this._requestCounter++;
      var response = "";
      if( this.getRequestHandler() ) {
        response += this.getRequestHandler()( message );
      }
      response += "org.eclipse.swt.Request.getInstance()." + 
                  "setRequestCounter("+this._requestCounter+");";
      return response;        
    }
   
  }
} );