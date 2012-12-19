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

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Client = rwt.client.Client;

var request;

var URL = "http://127.0.0.1/";

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.RequestTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateNativeRequest : function() {
      request.send();

      assertEquals( 1, TestUtil.getXMLHttpRequests().length );
    },

    testCallOpen : function() {
      request.send();

      var open = findNativeCall( "open" );
      assertEquals( "POST", open[ 0 ] );
      assertTrue( open[ 1 ].indexOf( URL + "?nocache=" ) === -1 );
      assertTrue( open[ 2 ] ); // async
    },

    testCallOpenSync : function() {
      request.setAsynchronous( false );
      request.send();

      var open = findNativeCall( "open" );
      assertFalse( open[ 2 ] );
    },

    testSetRequestHeader : function() {
      request.send();

      var contentType = "application/json; charset=UTF-8";
      if( !Client.isWebkit() ) {
        assertNotNull( findNativeCall( "setRequestHeader", [ "Referer", window.location.href ] ) );
      }
      assertNotNull( findNativeCall( "setRequestHeader", [ "Content-Type", contentType ] ) );
      assertNull( findNativeCall( "setRequestHeader", [ "Pragma", "no-cache" ] ) );
      assertNull( findNativeCall( "setRequestHeader", [ "Cache-Control", "no-cache" ] ) );
    },

    testCompleted : function() {
      var log = createRequestLogger( request );
      request.send();

      recieve( getNative(), "" );

      assertEquals( 2 , log.length );
      assertEquals( "success" , log[ 0 ] );
      assertEquals( 200 , log[ 1 ].status );
    },

    testSendData : function() {
      request.setData( "foobar" );
      request.send();

      assertNotNull( findNativeCall( "send", [ "foobar" ] ) );
    },

    testRecieveData : function() {
      request.send();
      var log = createRequestLogger( request );

      recieve( getNative(), "foobar" );

      assertEquals( "foobar", log[ 1 ].responseText );
    },

    testDisposeWhileSending : function() {
      request.send();

      request.dispose();

      assertNotNull( findNativeCall( "abort" ) );
    },

    testDisposeAfterComplete : function() {
      request.send();

      recieve( getNative(), "" );

      assertTrue( TestUtil.hasNoObjects( request, true ) );
      assertTrue( getNative().onreadystatechange == null );
    },

    testRequestError : function() {
      request.send();
      var log = createRequestLogger( request );

      getNative().status = 404;
      getNative().readyState = 4;
      getNative().responseText = "foobar";
      getNative().onreadystatechange();

      assertEquals( "error", log[ 0 ] );
      assertEquals( "foobar", log[ 1 ].responseText );
      assertEquals( 404, log[ 1 ].status );
    },

    testResponseHeader : function() {
      var log = createRequestLogger( request );
      request.send();

      recieve( getNative(), "", "mykey: myvalue" );

      assertEquals( { "mykey" : "myvalue" } , log[ 1 ].responseHeaders );
    },

    testGetter : function() {
      request.setAsynchronous( false );
      request.setData( "foo" );

      assertFalse( request.getAsynchronous() );
      assertEquals( "foo", request.getData() );
    },

    setUp : function() {
      org.eclipse.rwt.test.fixture.NativeRequestMock.useFakeServer = false;
      org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests();
      request = new rwt.remote.Request( URL, "POST", "application/json" );
    },

    tearDown : function() {
      org.eclipse.rwt.test.fixture.NativeRequestMock.useFakeServer = true;
      request.dispose();
      request = null;
    }

  }

} );

var getNative = function() {
  return TestUtil.getXMLHttpRequests()[ 0 ];
};

var findNativeCall = function( method, targetArgs ) {
  var log = getNative().getLog();
  var result = null;
  for( var i = 0; i < log.length && result == null; i++ ) {
    if( log[ i ][ 0 ] === method ) {
      var args = log[ i ][ 1 ];
      if( targetArgs == null ) {
        result = args;
      } else if( targetArgs.length === args.length ){
        var match = true;
        for( var j = 0; j < args.length; j++ ) {
          if( args[ j ] !== targetArgs[ j ] ) {
            match = false;
          }
        }
        if( match ) {
          result = args;
        }
      }
    }
  }
  return result;
};

var createRequestLogger = function( request ) {
  var log = [];
  request.setSuccessHandler( function() {
    this.push( "success", arguments[ 0 ] );
  }, log );
  request.setErrorHandler( function() {
    this.push( "error", arguments[ 0 ] );
  }, log );
  return log;
};

var recieve = function( nativeRequest, data, headers ) {
  nativeRequest.readyState = 2;
  nativeRequest.onreadystatechange();
  nativeRequest.readyState = 3;
  nativeRequest.onreadystatechange();
  nativeRequest.status = 200;
  nativeRequest.readyState = 4;
  nativeRequest.responseText = data;
  if( headers ) {
    nativeRequest.getAllResponseHeaders = function() {
      return headers;
    };
  }
  nativeRequest.onreadystatechange();
};

}());