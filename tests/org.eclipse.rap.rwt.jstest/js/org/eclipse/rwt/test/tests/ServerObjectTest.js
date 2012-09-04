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

var shell;
var serverObject;

qx.Class.define( "org.eclipse.rwt.test.tests.ServerObjectTest", {

  extend : qx.core.Object,

  members : {

    testSetProperty : function() {
      serverObject.set( "key", "value" );
      serverObject.set( "key2", 2 );
      serverObject.set( "key3", 3.5 );
      serverObject.set( "key4", true );
      serverObject.set( "key5", "aString" );
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

    testNotify : function() {
      serverObject.notify( "method", { "key" : "a" } );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.eventType );
      assertEquals( "a", operation.properties[ "key" ] );
    },

    testNotifyWithoutProperties : function() {
      serverObject.notify( "method" );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.eventType );
      assertEquals( {}, operation.properties );
    },

    testCall : function() {
      serverObject.call( "method", { "key" : "a" } );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "call", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.method );
      assertEquals( "a", operation.properties[ "key" ] );
    },

    testCallWithoutProperties : function() {
      serverObject.call( "method" );

      var operation = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "call", operation.type );
      assertEquals( "w2", operation.target );
      assertEquals( "method", operation.method );
      assertEquals( {}, operation.properties );
    },

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      serverObject = rwt.protocol.ServerObjectFactory.getServerObject( shell );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    }

  }

} );

}());