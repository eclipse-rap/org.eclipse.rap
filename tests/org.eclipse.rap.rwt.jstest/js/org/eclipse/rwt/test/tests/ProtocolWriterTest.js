/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var writer;

var getMessage = function() {
  return JSON.parse( writer.createMessage() );
};

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ProtocolWriterTest", {

  extend : rwt.qx.Object,

  members : {

    testDispose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      writer.dispose();

      assertTrue( TestUtil.hasNoObjects( writer, true ) );
    },

    testHasNoOperations : function() {
      assertFalse( writer.hasOperations() );
    },


    testHasOperationsAfterAppend : function() {
      writer.appendSet( "target", "foo", 23 );

      assertTrue( writer.hasOperations() );
    },


    testEmptyMessage : function() {
      var messageString = writer.createMessage();

      var message = JSON.parse( messageString );
      assertEquals( {}, message.head );
      assertEquals( [], message.operations );
    },

    testGetHead : function() {
      writer.appendHead( "requestCounter", 1 );

      assertEquals( 1, writer.getHead( "requestCounter" ) );
    },

    testGetHead_ReturnsNullIfNotSet : function() {
      assertNull( writer.getHead( "requestCounter" ) );
    },

    testMessageWithRequestCounter : function() {
      writer.appendHead( "requestCounter", 1 );

      assertEquals( 1, getMessage().head.requestCounter );
    },

    testWriteMessageAfterDispose : function() {
      writer.dispose();
      try {
        writer.createMessage();
        fail();
      } catch( expected ) {
      }
    },

    testAppendAfterDispose : function() {
      writer.dispose();
      try {
        writer.appendSet( "target", "foo", 23 );
        fail();
      } catch( expected ) {
      }
    },

    testMessageWithNotify : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = "a";
      properties[ "key2"] = "b" ;

      writer.appendNotify( shellId, methodName, properties );

      var operation = getMessage().operations[ 0 ];
      assertEquals( "notify", operation[ 0 ] );
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },


    testMessageWithTwoNotify : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = 5;
      properties[ "key2" ] = "b";
      properties[ "key3" ] = false;

      writer.appendNotify( shellId, methodName, null );
      writer.appendNotify( shellId, methodName, properties );

      var operation = getMessage().operations[ 1 ];
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },

    testMessageWithSet : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "image", "aUrl" );
      writer.appendSet( buttonId, "fake", 1 );

      var message = getMessage();
      assertEquals( 1, message.operations.length );
      var operation = message.operations[ 0 ];
      assertEquals( "set", operation[ 0 ] );
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
      assertEquals( "aUrl", operation[ 2 ][ "image" ] );
      assertEquals( 1, operation[ 2 ][ "fake" ] );
    },

    testMessageWithSetTwice : function() {
      var shellId = "w2";
      var buttonId = "w5";

      writer.appendSet( shellId, "text", "newText2" );
      writer.appendSet( shellId, "image", false );
      writer.appendSet( shellId, "fake", 2 );
      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "image", true );
      writer.appendSet( buttonId, "fake", 1 );

      var message = getMessage();
      assertEquals( 2, message.operations.length );
      var operation = message.operations[ 1 ];
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
      assertEquals( 1, operation[ 2 ][ "fake" ] );
      assertTrue( operation[ 2 ][ "image" ] );
    },

    testMessageWithSetDuplicateProperty : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "text", "newText" );

      var message = getMessage();
      assertEquals( 1, message.operations.length );
      var operation = message.operations[ 0 ];
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
    },

    testMessageWithNotifyBetweenSetDuplicateProperty : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendNotify( buttonId, "methodName", null );
      writer.appendSet( buttonId, "text", "newText" );

      var message = getMessage();
      assertEquals( 3, message.operations.length );
      assertEquals( "newText", message.operations[ 0 ][ 2 ][ "text" ] );
      assertEquals( "methodName", message.operations[ 1 ][ 2 ] );
      assertEquals( "newText", message.operations[ 2 ][ 2 ][ "text" ] );
    },

    testMessageWithCall : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = "a";
      properties[ "key2"] = "b" ;

      writer.appendCall( shellId, methodName, properties );

      var operation = getMessage().operations[ 0 ];
      assertEquals( "call", operation[ 0 ] );
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },


    testMessageWithTwoCall : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = 5;
      properties[ "key2" ] = "b";
      properties[ "key3" ] = false;

      writer.appendCall( shellId, methodName, null );
      writer.appendCall( shellId, methodName, properties );

      var operation = getMessage().operations[ 1 ];
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },



    /////////
    // Helper

    setUp : function() {
      writer = new rwt.remote.MessageWriter();
    },

    tearDown : function() {
      writer.dispose();
      writer = null;
    }

  }

} );

}());