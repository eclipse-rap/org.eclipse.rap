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

//var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Message = org.eclipse.rwt.test.fixture.Message;

var writer;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MessageTest", {

  extend : rwt.qx.Object,

  members : {


    testConstructWithNull : function() {
      try {
        new Message( null );
        fail();
      } catch( expected ) {
      }
    },

    testConstructWithEmptyString : function() {
      try {
        new Message( "" );
        fail();
      } catch( expected ) {
        assertTrue( expected.message.indexOf( "Could not parse json" ) !== -1 );
      }
    },

    testConstructWithInvalidJson : function() {
      try {
        new Message( "{" );
        fail();
      } catch( expected ) {
        assertTrue( expected.message.indexOf( "Could not parse json" ) !== -1 );
      }
    },

    testConstructWithoutOperations : function() {
      try {
        new Message( "{ \"foo\": 23 }" );
        fail();
      } catch( expected ) {
        assertTrue( expected.message.indexOf( "Missing operations array" ) !== -1 );
      }
    },

    testConstructWithInvalidOperations : function() {
      try {
        new Message( "{ \"operations\": 23 }" );
        fail();
      } catch( expected ) {
        assertTrue( expected.message.indexOf( "Missing operations array" ) !== -1 );
      }
    },

    testGetOperationCountWhenEmpty : function() {
      assertEquals( 0, getMessage().getOperationCount() );
    },

    testGetOperationCount : function() {
      writer.appendNotify( "w1", "method1", null );
      writer.appendNotify( "w2", "method2", null );

      assertEquals( 2, getMessage().getOperationCount() );
    },

    testGetHead : function() {
      writer.appendHead( "headStuff", 31 );

      assertEquals( 31, getMessage().getHead()[ "headStuff" ] );
    },

    testGetOperation : function() {
      writer.appendNotify( "w2", "method", null );

      assertNotNull( getMessage().getOperation( 0 ) );
    },

    testSetOperation : function() {
      writer.appendSet( "w1", "key", true );
      writer.appendSet( "w1", "key2", "value" );

      var operation = getMessage().getOperation( 0 );
      assertEquals( "set", operation.type );
      assertEquals( "w1", operation.target );
      assertTrue( operation.properties[ "key" ] );
      assertEquals( "value", operation.properties[ "key2" ] );
    },

    testNotifyOperation : function() {
      var properties = {};
      properties[ "key1" ] = "a";
      properties[ "key2" ] = 2;
      writer.appendNotify( "w2", "someevent", properties );

      var operation = getMessage().getOperation( 0 );
      assertEquals( "notify", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "someevent", operation.eventType );
      assertEquals( "a", operation.properties[ "key1" ] );
      assertEquals( 2, operation.properties[ "key2" ] );
    },

    testFindSetOperation : function() {
      writer.appendSet( "w1", "key", true );

      var message = getMessage();

      var operation = message.findSetOperation( "w1", "key" );
      assertTrue( operation.properties[ "key" ] );
    },

    testFindSetOperationFailed : function() {
      writer.appendSet( "w1", "key1", true );

      var message = getMessage();

      assertNull( message.findSetOperation( "w1", "key2" ) );
      assertNull( message.findSetOperation( "w2", "key1" ) );
    },

    testFindSetProperty : function() {
      writer.appendSet( "w1", "key", true );

      var message = getMessage();

      assertTrue( message.findSetProperty( "w1", "key" ) );
    },

    testFindSetPropertyFailed : function() {
      writer.appendSet( "w1", "key1", true );

      var message = getMessage();

      try {
        message.findSetProperty( "w1", "key2" );
        fail();
      } catch( exception ) {
        //expected
      }
      try {
        message.findSetProperty( "w2", "key1" );
        fail();
      } catch( exception ) {
        //expected
      }
    },

    testFindNotifyOperation : function() {
      writer.appendNotify( "w1", "method", null );

      var message = getMessage();

      var operation = message.findNotifyOperation( "w1", "method" );
      assertEquals( "notify", operation.type );
      assertEquals( "w1", operation.target );
      assertEquals( "method", operation.eventType );
    },

    testFindNotifyProperty : function() {
      writer.appendNotify( "w1", "method", { "foo" : "bar" } );

      var message = getMessage();

      assertEquals( "bar", message.findNotifyProperty( "w1", "method", "foo" ) );
    },

    testFindNotifyOperationFailed : function() {
      writer.appendNotify( "w2", "method1", null );
      writer.appendNotify( "w1", "method2", null );

      var message = getMessage();

      assertNull( message.findNotifyOperation( "w1", "method1" ) );
    },

    testFindCallOperation : function() {
      writer.appendCall( "w1", "method", null );

      var message = getMessage();

      var operation = message.findCallOperation( "w1", "method" );
      assertEquals( "call", operation.type );
      assertEquals( "w1", operation.target );
      assertEquals( "method", operation.method );
    },

    testFindCallProperty : function() {
      writer.appendCall( "w1", "method", { "foo" : "bar" } );

      var message = getMessage();

      assertEquals( "bar", message.findCallProperty( "w1", "method", "foo" ) );
    },

    testFindCallOperationFailed : function() {
      writer.appendCall( "w2", "method1", null );
      writer.appendCall( "w1", "method2", null );

      var message = getMessage();

      assertNull( message.findCallOperation( "w1", "method1" ) );
    },

    //////////
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


var getMessage = function() {
  return new Message( writer.createMessage() );
};

}());