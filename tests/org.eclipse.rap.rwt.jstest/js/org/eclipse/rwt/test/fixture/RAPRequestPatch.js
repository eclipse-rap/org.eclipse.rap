/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Mixin.define( "org.eclipse.rwt.test.fixture.RAPRequestPatch", {
  
  "members": {
    
    send : function() {
      if( !this._inDelayedSend ) {
        this._inDelayedSend = true;
        this._sendImmediate( true );
      }
    },    
    
    _sendStandalone : function(){
	    var message = "_sendStandalone/enableUICallBack not (yet) supported!";
	    this.error( message );
      throw( message );
    },

    _sendImmediate : function( async ) {
      this._dispatchSendEvent();
      this._parameters[ "uiRoot" ] = this._uiRootId;
      if( this._requestCounter == -1 ) {
        this._inDelayedSend = false;
        this.send();
      } else {
        if( this._requestCounter != null ) {
          this._parameters[ "requestCounter" ] = this._requestCounter;
          this._requestCounter = -1;
        }
        var request = this._createRequest();
        request.setAsynchronous( async );
        this._inDelayedSend = false;
        this._copyParameters( request );
        this._logSend();
        this._runningRequestCount++;
        if( this._runningRequestCount === 1 ) {
          // Removed: is distracting in debugging and useless unless tested 
          //qx.client.Timer.once( this._showWaitHint, this, 500 );
        }
        this._parameters = {};
        request.send();
      }
    },    
    
    _createRequest : function() {
      var result = new org.eclipse.rwt.test.fixture.DummyRequest();
      result.addEventListener( "sending", this._handleSending, this );
      result.addEventListener( "completed", this._handleCompleted, this );
      result.addEventListener( "failed", this._handleFailed, this );
      return result;
    },
    
    _writeErrorPage : function( content ) {
      throw( content );
    }
        
  }
});