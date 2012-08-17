/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*global ActiveXObject: false */

namespace( "org.eclipse.rwt" );

(function(){

var Client = org.eclipse.rwt.Client;

org.eclipse.rwt.Request = function( url, method, responseType ) {
  this._url = url;
  this._method = method;
  this._async = true;
  this._success = null;
  this._error = null;
  this._data = null;
  this._responseType = responseType;
  this._request = org.eclipse.rwt.Request.createXHR();
};

org.eclipse.rwt.Request.createXHR = qx.core.Variant.select( "qx.client", {
  "default" : function() {
    return new XMLHttpRequest();
  },
  "mshtml" : qx.lang.Object.select( window.XMLHttpRequest ? "native" : "activeX", {
    "native" : function() {
      return new XMLHttpRequest();
    },
    "activeX" : function() {
      return new ActiveXObject( this._getMSXML() );
    }
  } )
} );

/**
 * According to information on the Microsoft XML Team's WebLog
 * it is recommended to check for availability of MSXML versions 6.0 and 3.0.
 * Other versions are included for completeness, 5.0 is excluded as it is
 * "off-by-default" in IE7 (which could trigger a goldbar).
 *
 * http://blogs.msdn.com/xmlteam/archive/2006/10/23/using-the-right-version-of-msxml-in-internet-explorer.aspx
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/xmlsdk/html/aabe29a2-bad2-4cea-8387-314174252a74.asp
 *
 * MSXML 3 is preferred over MSXML 6 because the IE7 native XMLHttpRequest returns
 * a MSXML 3 document and so does not properly work with other types of xml documents.
 **/
org.eclipse.rwt.Request._getMSXML = function() {
  if( this.__server == null ) {
    var servers = [
     "MSXML2.XMLHTTP.3.0",
     "MSXML2.XMLHTTP.6.0",
     "MSXML2.XMLHTTP.4.0",
     "MSXML2.XMLHTTP",    // v3.0
     "Microsoft.XMLHTTP"  // v2.x
    ];
    for( var i = 0; i < servers.length && this.__server == null; i++ ) {
      try {
        new ActiveXObject( servers[ i ] );
        this.__server = servers[ i ];
      } catch( ex ) {
        // try next one
      }
    }
  }
  return this.__server;
};

org.eclipse.rwt.Request.prototype = {

    dispose : function() {
      if( this._request != null ) {
        this._request.abort();
        this._success = null;
        this._error = null;
        this._request = null;
      }
    },

    send : function() {
      var urlpar = "nocache=" + ( new Date() ).valueOf();
      var post = this._method === "POST";
      if( !post ) {
        urlpar += "&" + this._data;
      }
      var url = this._url + ( this._url.indexOf( "?" ) >= 0 ? "&" : "?" ) + urlpar;
      this._request.open( this._method, url, this._async );
      this._configRequest();
      this._request.send( post ? this._data : undefined );
    },

    setAsynchronous : function( value ) {
      this._async = value;
    },

    getAsynchronous : function() {
      return this._async;
    },

    setHandleSuccess : function( handler ) {
      this._success = handler;
    },

    setHandleError : function( handler ) {
      this._error = handler;
    },

    setData : function( value ) {
      this._data = value;
    },

    getData : function() {
      return this._data;
    },

    _configRequest : function() {
      if( !Client.isWebkit() ) {
        this._request.setRequestHeader( "Referer", window.location.href );
      }
      var contentType = "application/x-www-form-urlencoded; charset=UTF-8";
      this._request.setRequestHeader( "Content-Type", contentType );
      this._request.setRequestHeader( "Pragma", "no-cache" );
      this._request.setRequestHeader( "Cache-Control", "no-cache" );
      this._request.onreadystatechange = qx.lang.Function.bind( this._onReadyStateChange, this );
    },

    _onReadyStateChange : function() {
      if( this._request.readyState === 4 ) {
        if( this._request.status === 200 ) {
          if( this._success ) {
            this._success( this._request.responseText, this._request.status, this._getHeaders() );
          }
        } else {
          if( this._error ) {
            this._error( this._request.responseText, this._request.status, this._getHeaders() );
          }
        }
        this.dispose();
      }
    },

    _getHeaders : function() {
      var text = this._request.getAllResponseHeaders();
      var values = text.split( /[\r\n]+/g );
      var result = {};
      for( var i=0; i < values.length; i++ ) {
        var pair = values[ i ].match( /^([^:]+)\s*:\s*(.+)$/i );
        if( pair ) {
          result[ pair[ 1 ] ] = pair[ 2 ];
        }
      }
      return result;
    }

};

}());
