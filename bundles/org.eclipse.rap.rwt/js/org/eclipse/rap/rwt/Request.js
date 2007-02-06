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

/**
 * @event send
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.Request", qx.core.Target,
  function() {
    qx.core.Object.call( this );
		// the map of parameters that will be posted with the next call to 'send()'
		this._parameters = {};
		// instance variables that hold the essential request parameters
		this._uiRootId = "";
		this._requestCounter = 0;
		//
    this._waitHintTimer = null;
    // Flag that is set to true if send() was called but the delay timeout
    // has not yet timed out
    this._inDelayedSend = false;
    // Initialize the request queue to allow only one request at a time
    // and set the timout to 5 min (eases debuging)
    var requestQueue = qx.io.remote.RequestQueue.getInstance();
    requestQueue.setDefaultTimeout( 60000 * 5 ); // 5 min
    requestQueue.setMaxConcurrentRequests( 1 );
  }
);

/**
 * Returns the sole instance of Request.
 */
qx.Class.getInstance = qx.lang.Function.returnInstance;

qx.Proto.setUIRootId = function( uiRootId ) {
  this._uiRootId = uiRootId;
}

qx.Proto.getUIRootId = function() {
  return this._uiRootId;  
}

qx.Proto.setRequestCounter = function( requestCounter ) {
  this._requestCounter = requestCounter;
}

/**
 * Adds a request parameter to this request with the given name and value
 */
 // TODO [rh] someone frequently adds an empty parameter, should we ignore
 //      that, or even better treat this as an error?
qx.Proto.addParameter = function( name, value ) {
  this._parameters[ name ] = value;
}

/**
 * Removes the parameter denoted by name from this request.
 */
qx.Proto.removeParameter = function( name ) {
  delete this._parameters[ name ];
}

/**
 * Adds the given eventType to this request. The sourceId denotes the id of
 * the widget that caused the event.
 * In addition, events are given a sequential number when added.
 */
qx.Proto.addEvent = function( eventType, sourceId ) {
  this._parameters[ eventType ] = sourceId;
}

/**
 * Sends this request. All parameters that were added since the last 'send()'
 * will now be sent.
 */
qx.Proto.send = function() {
  if( !this._inDelayedSend ) {
    this._inDelayedSend = true;
    // Wait and then actually send the request
// TODO [rh] optimize wait interval (below 60ms seems to not work reliable)    
    qx.client.Timer.once( this._sendImmediate, this, 60 );
  }
}

qx.Proto._sendImmediate = function() {
  this._dispatchSendEvent();
  // create and configure request object
  var action = org_eclipse_rap_rwt_requesthandler;
  // To solve bugzilla bug entry 165666 we use GET- instead of POST-method
  var request = new qx.io.remote.Request( action, "GET", "text/javascript" );
  request.setAsynchronous( true );
  // apply _parameters map which was filled during client interaction
  for( var parameterName in this._parameters ) {
    var value = this._parameters[ parameterName ];
    request.setParameter( parameterName, value );
  }
  // set mandatory parameters; do this after regular params to override them
  // in case of conflict
  request.setParameter( "uiRoot", this.uiRootId );
  request.setParameter( "requestCounter", this._requestCounter );
  this._inDelayedSend = false;
  this._startWaitHintTimer();
  request.addEventListener( "completed", this._hideWaitHint, this );
  this._logSend();  
  // queue request to be sent
  request.send();
  // clear the parameter list
  this._parameters = {};
}

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
}

qx.Proto._startWaitHintTimer = function () {
  this.debug( "starting request " );
  // notify user when request takes longer than 500 ms
  this._waitHintTimer = new qx.client.Timer( 500 );
  this._waitHintTimer.addEventListener( "interval", this._showWaitHint, this );
  this._waitHintTimer.start();
}

qx.Proto._showWaitHint = function() {
  // stop timer - we only need this to be triggered *once* 
  if( this._waitHintTimer != null ) {
    this._waitHintTimer.stop();
    this.debug( "showWaitHint" );
  }
}

qx.Proto._hideWaitHint = function() {
  // stop eventually running timer (this happens when request completes before 
  // timer interval 
  if( this._waitHintTimer != null ) {
    this._waitHintTimer.dispose();
    this._waitHintTimer = null;
  }
  this.debug( "hideWaitHint" );
}

qx.Proto._dispatchSendEvent = function() {
  if( this.hasEventListeners( "send" ) ) {
    var event = new qx.event.type.DataEvent( "send", this );
    this.dispatchEvent( event, true );
  }
}