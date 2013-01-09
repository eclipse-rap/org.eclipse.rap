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

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;
var remoteObject;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.RemoteObjectTest", {

  extend : rwt.qx.Object,

  members : {

    testSetProperty : function() {
      remoteObject.set( "key", "value" );
      remoteObject.set( "key2", 2 );
      remoteObject.set( "key3", 3.5 );
      remoteObject.set( "key4", true );
      remoteObject.set( "key5", "aString" );
      rwt.remote.Server.getInstance().send();

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "set", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "value", operation.properties[ "key" ] );
      assertEquals( 2, operation.properties[ "key2" ] );
      assertEquals( 3.5, operation.properties[ "key3" ] );
      assertEquals( true, operation.properties[ "key4" ] );
      assertEquals( "aString", operation.properties[ "key5" ] );
    },

    testNotifyWithoutListen : function() {
      remoteObject.notify( "method", { "key" : "a" } );

      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testNotify : function() {
      remoteObject._.listen[ "method" ] = true;
      remoteObject.notify( "method", { "key" : "a" } );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.eventType );
      assertEquals( "a", operation.properties[ "key" ] );
    },

    testNotifyWithoutProperties : function() {
      remoteObject._.listen[ "method" ] = true;
      remoteObject.notify( "method" );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.eventType );
      assertEquals( {}, operation.properties );
    },

    testCall : function() {
      remoteObject.call( "method", { "key" : "a" } );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "call", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.method );
      assertEquals( "a", operation.properties[ "key" ] );
    },

    testCallWithoutProperties : function() {
      remoteObject.call( "method" );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "call", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.method );
      assertEquals( {}, operation.properties );
    },

    testIsListening_InitialValueIsFalse : function() {
      assertFalse( remoteObject.isListening( "Modify" ) );
    },

    testListen_SetToTrue : function() {
      remoteObject._.listen[ "Modify" ] = true;

      assertTrue( remoteObject.isListening( "Modify" ) );
    },

    testListen_SetToFalse : function() {
      remoteObject._.listen[ "Modify" ] = true;
      remoteObject._.listen[ "Modify" ] = false;

      assertFalse( remoteObject.isListening( "Modify" ) );
    },

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      remoteObject = rwt.remote.RemoteObjectFactory.getRemoteObject( shell );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    }

  }

} );

}());