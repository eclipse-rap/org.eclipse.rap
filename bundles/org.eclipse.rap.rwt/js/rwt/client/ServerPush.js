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

namespace( "rwt.client" );

rwt.client.ServerPush = function() {
  if( rwt.client.ServerPush._instance !== undefined ) {
    throw new Error( "ServerPush can not be created twice" );
  } else {
    rwt.client.ServerPush._instance = this;
  }
  this._retryInterval = 0;
  this._active = false;
  this._running = false;
  this._requestTimer = new rwt.client.Timer( 0 );
  this._requestTimer.addEventListener( "interval", this._doSendUICallBackRequest, this );
};

rwt.client.ServerPush.getInstance = function() {
  if( rwt.client.ServerPush._instance === undefined ) {
    new rwt.client.ServerPush();
  }
  return rwt.client.ServerPush._instance;
};

rwt.client.ServerPush.prototype = {

  setActive : function( active ) {
    this._active = active;
  },

  sendUIRequest : function() {
    rwt.remote.Server.getInstance().sendImmediate( true );
  },

  sendUICallBackRequest : function() {
    if( this._active && !this._running ) {
      this._running = true;
      this._requestTimer.start();
    }
  },

  // workaround for bug 353819 - send UICallBackRequest with a timer
  _doSendUICallBackRequest : function() {
    this._requestTimer.stop();
    var url = rwt.remote.Server.getInstance().getUrl();
    var request = new rwt.remote.Request( url, "GET", "application/javascript" );
    request.setSuccessHandler( this._handleSuccess, this );
    request.setErrorHandler( this._handleError, this );
    request.setData( "servicehandler=org.eclipse.rap.serverpush" );
    request.send();
  },

  _handleSuccess : function( event ) {
    this._running = false;
    this._retryInterval = 0;
    this.sendUIRequest();
  },

  _handleError : function( event ) {
    this._running = false;
    if( rwt.remote.Server.getInstance()._isConnectionError( event.status ) ) {
      rwt.client.Timer.once( this.sendUICallBackRequest, this, this._retryInterval );
      this._increaseRetryInterval();
    }
  },

  _increaseRetryInterval : function() {
    if( this._retryInterval === 0 ) {
      this._retryInterval = 1000;
    } else if( this._retryInterval < 60 * 1000 ) {
      this._retryInterval *= 2;
    }
  }

};
