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
    org.eclipse.swt.Request.getInstance()._sendImmediate( true );
  },

  sendUICallBackRequest : function() {
    if( this._active && !this.running ) {
      this._running = true;
      var url = org.eclipse.swt.Request.getInstance().getUrl();
      var request = new qx.io.remote.Request( url, "GET", "application/javascript" );
      request.addEventListener( "completed", this._handleFinished, this );
      request.addEventListener( "failed", this._handleFailed, this );
      request.setParameter(
        "custom_service_handler",
        "org.eclipse.rwt.internal.uicallback.UICallBackServiceHandler" );
      org.eclipse.swt.Request.getInstance()._sendStandalone( request );
    }
  },

  _handleFinished : function( event ) {
    this._running = false;
    if( event.getType() === "completed" ) {
      // NOTE: this was originally done almost exactly like this in 
      // XmlHttpTransport.getResponseContent, but is now done here for
      // better overview
      var text = event.getContent();
      try {
        if( text && text.length > 0 ) {
          var messageObject = JSON.parse( text );
          org.eclipse.rwt.protocol.Processor.processMessage( messageObject );
        }
      } catch( ex ) {
        throw new Error( "Could not process UICallBack response: [" + text + "]: " + ex );
      }
      this._retryInterval = 0;
    }
    // Transport is normally disposed of in RequestQueue but UICallBackReuests
    // bypass the queue 
    var transport = event.getTarget();
    var request = transport.getRequest();
    transport.dispose();
    request.dispose();
  },

  _handleFailed : function( event ) {
    this._running = false;
    if( org.eclipse.swt.Request.getInstance()._isConnectionError( event.getStatusCode() ) ) {
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