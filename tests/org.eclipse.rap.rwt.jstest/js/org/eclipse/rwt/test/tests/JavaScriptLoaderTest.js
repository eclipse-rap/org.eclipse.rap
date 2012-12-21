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
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var FakeServer = org.eclipse.rwt.test.fixture.FakeServer;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.JavaScriptLoaderTest", {

  extend : rwt.qx.Object,

  members : {

    testLoadSendsRequest : function() {
      scheduleResponse();

      load( [ "rwt-resource/myJS" ] );

      assertNotNull( getRequestArguments( "send" ) );
    },

    testRequestOpenParams : function() {
      scheduleResponse();

      load( [ "rwt-resource/myJS" ] );

      var open = getRequestArguments( "open" );
      assertEquals( "GET", open[ 0 ] );
      assertEquals( "rwt-resource/myJS", open[ 1 ] );
      assertFalse( open[ 2 ] );
    },

    testParseScript : function() {
      scheduleResponse( "rwt.remote.ObjectRegistry.add( \"testObj\", { \"value\" : 42 } );" );

      load( [ "rwt-resource/myJS" ] );

      var result = ObjectRegistry.getObject( "testObj" );
      assertEquals( 42, result.value );
    },

    testLoadMultipleSendsMultipleRequest : function() {
      scheduleResponse();

      load( [ "rwt-resource/myJS", "rwt-resource/myOtherJS" ] );

      assertNotNull( getRequestArguments( "send", 0 ) );
      assertNotNull( getRequestArguments( "send", 1 ) );
    },

    testMultipleRequestOpenParams : function() {
      scheduleResponse();

      load( [ "rwt-resource/myJS", "rwt-resource/myOtherJS" ] );

      assertEquals( "rwt-resource/myJS", getRequestArguments( "open", 0 )[ 1 ] );
      assertEquals( "rwt-resource/myOtherJS", getRequestArguments( "open", 1 )[ 1 ] );
    },

    testParseMultipleScriptsInOrder : function() {
      FakeServer.getInstance().setRequestHandler( function( message, url ) {
        var response;
        if( url === "rwt-resource/myJS" ) {
          response = "rwt.remote.ObjectRegistry.add( \"testObj\", { \"value\" : 42 } );";
        } else if( url === "rwt-resource/myOtherJS" ) {
          response = "rwt.remote.ObjectRegistry.getObject( \"testObj\" ).value++;";
        }
        return response;
      } );

      load( [ "rwt-resource/myJS", "rwt-resource/myOtherJS" ] );

      var result = ObjectRegistry.getObject( "testObj" );
      assertEquals( 43, result.value );
    }

  }

} );

function load( files ) {
  TestUtil.protocolCall( "rwt.client.JavaScriptLoader", "load", { "files" : files } );
}

function scheduleResponse( response ) {
  FakeServer.getInstance().setRequestHandler( function() {
    return response;
  } );
}

function getRequestArguments( type, position ) {
  var log = TestUtil.getXMLHttpRequests()[ position ? position : 0 ].getLog();
  var result = null;
  for( var i = 0; i < log.length; i++ ) {
    if( log[ i ][ 0 ] === type ) {
      result = log[ i ][ 1 ];
    }
  }
  return result;
}

}());
