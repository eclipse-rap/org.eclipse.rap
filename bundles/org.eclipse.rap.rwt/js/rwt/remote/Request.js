/*******************************************************************************
 * Copyright (c) 2012, 2023 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.remote" );

(function(){

rwt.remote.Request = function( url, method, responseType ) {
  this._url = url;
  this._method = method;
  this._async = true;
  this._success = null;
  this._redirect = null;
  this._error = null;
  this._data = null;
  this._responseType = responseType;
  this._request = rwt.remote.Request.createXHR();
};

rwt.remote.Request.createXHR = function() {
  return new XMLHttpRequest();
};

rwt.remote.Request.prototype = {

  dispose : function() {
    if( this._request != null ) {
      this._request.onreadystatechange = null;
      this._request.abort();
      this._success = null;
      this._redirect = null;
      this._error = null;
      this._request = null;
    }
  },

  send : function() {
    if( this._isFetchSupported() && this._async ) {
      this._sendWithFetch();
    } else {
      this._sendWithXHR();
    }
  },

  setAsynchronous : function( value ) {
    this._async = value;
  },

  getAsynchronous : function() {
    return this._async;
  },

  setSuccessHandler : function( handler, context ) {
    this._success = function(){ handler.apply( context, arguments ); };
  },

  setErrorHandler : function( handler, context ) {
    this._error = function(){ handler.apply( context, arguments ); };
  },

  setRedirectHandler : function( handler, context ) {
    this._redirect = function(){ handler.apply( context, arguments ); };
  },

  setData : function( value ) {
    this._data = value;
  },

  getData : function() {
    return this._data;
  },

  _sendWithXHR : function() {
    this._request.open( this._method, this._createUrl(), this._async );
    this._request.setRequestHeader( "Content-Type", "application/json; charset=UTF-8" );
    this._request.onreadystatechange = rwt.util.Functions.bind( this._onReadyStateChange, this );
    this._request.send( this._method === "POST" ? this._data : undefined );
    if( !this._async ) {
      this.dispose();
    }
  },

  _sendWithFetch : function() {
    var event = {
      "target" : this
    };
    fetch( this._createUrl(), {
      method: this._method,
      redirect: "manual",
      headers: {
        "Content-Type": "application/json; charset=UTF-8"
      },
      credentials: 'same-origin',
      body: this._method === "POST" ? this._data : undefined
    } ).then( function( response ) {
      event.status = response.status;
      event.responseHeaders = event.target._getFetchHeaders( response.headers );
      if( response.type === "opaqueredirect" ) {
        event.responseHeaders.location = response.url;
        if( event.target._redirect ) {
          event.target._redirect( event );
        }
      } else {
        response.text().then( function( text ) {
          event.responseText = text;
          if( response.status === 200 ) {
            if( event.target._success ) {
              event.target._success( event );
            }
          } else {
            if( event.target._error ) {
              event.target._error( event );
            }
          }
          event.target.dispose();
        } );
      }
    } ).catch( function() {
      event.status = 0;
      if( event.target._error ) {
        event.target._error( event );
      }
    } );
  },

  _createUrl : function() {
    var urlpar = null;
    var post = this._method === "POST";
    if( !post && this._data ) {
      urlpar = this._data;
    }
    var url = this._url;
    if( urlpar ) {
      url += ( url.indexOf( "?" ) >= 0 ? "&" : "?" ) + urlpar;
    }
    return url;
  },

  _onReadyStateChange : function() {
    if( this._request.readyState === 4 ) {
      var text;
      // [if] typeof(..) == "unknown" is IE specific. Used to prevent error:
      // "The data necessary to complete this operation is not yet available"
      if( typeof this._request.responseText !== "unknown" ) {
        text = this._request.responseText;
      }
      var event = {
        "responseText" : text,
        "status" : this._request.status,
        "responseHeaders" : this._getXHRHeaders( this._request.getAllResponseHeaders() ),
        "target" : this
      };
      if( this._request.status === 200 ) {
        if( this._success ) {
          this._success( event );
        }
      } else {
        if( this._error ) {
          this._error( event );
        }
      }
      if( this._async ) {
        this.dispose();
      }
    }
  },

  _getXHRHeaders : function( headers ) {
    var values = headers.split( /[\r\n]+/g );
    var result = {};
    for( var i = 0; i < values.length; i++ ) {
      var pair = values[ i ].match( /^([^:]+)\s*:\s*(.+)$/i );
      if( pair ) {
        // Note: According to HTTP/2 spec all response headers are now lower-case
        result[ pair[ 1 ].toLowerCase() ] = pair[ 2 ];
      }
    }
    return result;
  },

  _getFetchHeaders : function( headers ) {
    var result = {};
    headers.forEach( function( value, name ) {
      result[ name ] = value;
    } );
    return result;
  },

  _isFetchSupported : function() {
    return typeof fetch === "function";
  }

};

}());
