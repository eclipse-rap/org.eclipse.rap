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
var MessageProcessor = rwt.protocol.MessageProcessor;
var ObjectRegistry = rwt.protocol.ObjectRegistry;


qx.Class.define( "org.eclipse.rwt.test.tests.JavaScriptLoaderTest", {

  extend : qx.core.Object,

  members : {

    testLoadSendsRequest : function() {
      scheduleResponse();

      load( "rwt-resource/myJS" );

      assertNotNull( getRequestArguments( "send" ) );
    },

    testRequestOpenParams : function() {
      scheduleResponse();

      load( "rwt-resource/myJS" );

      var open = getRequestArguments( "open" );
      assertEquals( "GET", open[ 0 ] );
      assertEquals( "rwt-resource/myJS", open[ 1 ] );
      assertFalse( open[ 2 ] );
    },

    testParseScript : function() {
      scheduleResponse( "rwt.protocol.ObjectRegistry.add( \"testObj\", { \"value\" : 42 } );" );

      load( "rwt-resource/myJS" );

      var result = ObjectRegistry.getObject( "testObj" );
      assertEquals( 42, result.value );
    }

  }

} );

function load( url ) {
  TestUtil.protocolCall( "rwt.client.JavaScriptLoader", "load", { "url" : url } );
}

function scheduleResponse( response ) {
  org.eclipse.rwt.test.fixture.FakeServer.getInstance().setRequestHandler( function() {
    return response;
  } );
}

function getRequestArguments( type ) {
  var log = TestUtil.getXMLHttpRequests()[ 0 ].getLog();
  var result = null;
  for( var i = 0; i < log.length; i++ ) {
    if( log[ i ][ 0 ] === type ) {
      result = log[ i ][ 1 ];
    }
  }
  return result;
}

}());
