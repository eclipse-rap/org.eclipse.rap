/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.client" );

rwt.client.Cookie = {

  get : function( key ) {
    var result = null;
    var start = document.cookie.indexOf( key + "=" );
    if( start >= 0 ) {
      start += key.length + 1;
      var end = document.cookie.indexOf( ";", start );
      if( end == -1 ) {
        end = document.cookie.length;
      }
      try {
        result = decodeURIComponent( document.cookie.substring( start, end ) );
      } catch ( error ) {
        console.error( "Error while decoding URI components", error.message );
      }
    }
    return result;
  },

  set : function( key, value, expires, path, domain, secure, sameSite ) {
    var cookie = [ key, "=", encodeURIComponent( value ) ];
    if( expires ) {
      var today = new Date();
      today.setTime( today.getTime() );
      cookie.push( ";expires=", new Date( today.getTime() + expires * 24 * 60 * 60 * 1000 ).toGMTString() );
    }
    if( path ) {
      cookie.push( ";path=", path );
    }
    if( domain ) {
      cookie.push( ";domain=", domain );
    }
    if( secure ) {
      cookie.push( ";secure" );
    }
    cookie.push( ";sameSite=", sameSite || "Strict" );
    document.cookie = cookie.join( "" );
  },

  del : function( key, path, domain ) {
    var cookie = rwt.client.Cookie.get( key );
    if( cookie ) {
      cookie = [ key, "=" ];
      if( path ) {
        cookie.push( ";path=", path );
      }
      if( domain ) {
        cookie.push( ";domain=", domain );
      }
      cookie.push( ";expires=Thu, 01-Jan-1970 00:00:10 GMT" );
      document.cookie = cookie.join( "" );
    }
  }

};
