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

namespace( "org.eclipse.rwt" );

org.eclipse.rwt.UICallBack = function() {
  if( org.eclipse.rwt.UICallBack._instance !== undefined ) {
    throw new Error( "UICallBack can not be created twice" );
  } else {
    org.eclipse.rwt.UICallBack._instance = this;
  }
  this._retryInterval = 0;
  this._active = false;
  this._running = false;
  this._requestTimer = new qx.client.Timer( 0 );
  this._requestTimer.addEventListener( "interval", this._doSendUICallBackRequest, this );
};

org.eclipse.rwt.UICallBack.getInstance = function() {
  if( org.eclipse.rwt.UICallBack._instance === undefined ) {
    new org.eclipse.rwt.UICallBack();
  }
  return org.eclipse.rwt.UICallBack._instance;
};

org.eclipse.rwt.UICallBack.prototype = {

  setActive : function( active ) {
    this._active = active;
  },

  sendUIRequest : function() {
    org.eclipse.swt.Server.getInstance()._sendImmediate( true );
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
    var url = org.eclipse.swt.Server.getInstance().getUrl();
    var request = new org.eclipse.rwt.Request( url, "GET", "application/javascript" );
    request.setSuccessHandler( this._handleSuccess, this );
    request.setErrorHandler( this._handleError, this );
    request.setData( "custom_service_handler=org.eclipse.rap.uicallback" );
    request.send();
  },

  _handleSuccess : function( event ) {
    this._running = false;
    var text = event.responseText;
    try {
      var messageObject = JSON.parse( text );
      org.eclipse.rwt.protocol.Processor.processMessage( messageObject );
    } catch( ex ) {
      var escapedText = org.eclipse.rwt.protocol.EncodingUtil.escapeText( text, true );
      var msg = "Could not process UICallBack response: [" + escapedText + "]: " + ex;
      org.eclipse.rwt.ErrorHandler.showErrorPage( msg );
    }
    this._retryInterval = 0;
  },

  _handleError : function( event ) {
    this._running = false;
    if( org.eclipse.swt.Server.getInstance()._isConnectionError( event.status ) ) {
      qx.client.Timer.once( this.sendUICallBackRequest, this, this._retryInterval );
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
