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

qx.OO.defineClass( "org.eclipse.rap.rwt.Request", qx.core.Object,
  function() {
    qx.core.Object.call( this );
		// the map of parameters that will be posted with the next call to 'send()'
		this._parameters = {};
		// instance variables that hold the essential request parameters
		this._uiRootId = "";
		this._requestCounter = 0;
		//
    this._waitHintTimer = null;
    // Initialize the request queue to allow only one request at a time
    // and set the timout to 5 min (eases debuging)
    var requestQueue = qx.io.remote.RemoteRequestQueue.getInstance();
    requestQueue.setDefaultTimeout( 60000 * 5 ); // 5 min
    requestQueue.setMaxConcurrentRequests( 1 );
  }
);

qx.Proto.setUIRootId = function( uiRootId ) {
  this._uiRootId = uiRootId;
};

qx.Proto.setRequestCounter = function( requestCounter ) {
  this._requestCounter = requestCounter;
};

/**
 * Adds a request parameter to this request with the given name and value
 */
qx.Proto.addParameter = function( name, value ) {
  this._parameters[ name ] = value;
};

/**
 * Removes the parameter denoted by name from this request.
 */
qx.Proto.removeParameter = function( name ) {
  delete this._parameters[ name ];
};

/**
 * Sends this request. All parameters that were added since the last 'send()'
 * will now be sent.
 * This operation is asynchronous.
 */
qx.Proto.send = function() {
  // create and configure request object
  var action = org_eclipse_rap_rwt_requesthandler;
  var request = new qx.io.remote.RemoteRequest( action, 
                                                "POST", 
                                                "text/javascript" );
  request.setAsynchronous( true );
  // apply _parameters map which was filled during client interaction
  for( var parameterName in this._parameters ) {
    var value = this._parameters[ parameterName ];
    request.setParameter( parameterName, encodeURIComponent( value ) );
  }
  // set mandatory parameters; do this after regular params to override them
  // in case of conflict
  request.setParameter( "uiRoot", this.uiRootId );
  request.setParameter( "requestCounter", this._requestCounter );
  this._startWaitHintTimer();
  request.addEventListener( "completed", this._hideWaitHint, this );
  this._logSend();  
  // queue request to be sent
  request.send();
  // clear the parameter list
  this._parameters = {};
};

qx.Proto._logSend = function() {
  var msg
    = "sending request [ "
    + "uiRoot=" + this._uiRootId + "; "
    + "requestCounter=" + this._requestCounter + "; ";
  for( var parameterName in this._parameters ) {
    msg += "; " + parameterName + "=" + this._parameters[ parameterName ];
  }
  msg += " ]";
  this.debug( msg );
};

qx.Proto._startWaitHintTimer = function () {
  this.debug( "starting request " );
  // notify user when request takes longer than 500 ms
  this._waitHintTimer = new qx.client.Timer( 500 );
  this._waitHintTimer.addEventListener( "interval", this._showWaitHint, this );
  this._waitHintTimer.start();
};

qx.Proto._showWaitHint = function() {
  // stop timer - we only need this to be triggered *once* 
  if( this._waitHintTimer != null ) {
    this._waitHintTimer.stop();
    this.debug( "showWaitHint" );
  }
};

qx.Proto._hideWaitHint = function() {
  // stop eventually running timer (this happens when request completes before 
  // timer interval 
  if( this._waitHintTimer != null ) {
    this._waitHintTimer.dispose();
    this._waitHintTimer = null;
  }
  this.debug( "hideWaitHint" );
};

/**
 * Returns the sole instance of Request.
 */
qx.Class.getInstance = qx.util.Return.returnInstance;

