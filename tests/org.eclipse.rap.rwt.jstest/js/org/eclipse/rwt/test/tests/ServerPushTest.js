/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var Processor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;
var ServerPush = rwt.client.ServerPush;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ServerPushTest", {

  extend : rwt.qx.Object,

  members : {

    tearDown : function() {
      rwt.runtime.Singletons.clear( rwt.client.ServerPush );
    },

    testServerPushInstance : function() {
      var serverPush1 = ServerPush.getInstance();
      var serverPush2 = ServerPush.getInstance();

      assertTrue( serverPush1 instanceof rwt.client.ServerPush );
      assertIdentical( serverPush2, serverPush2 );
    },

    testSetActive : function() {
      var serverPush = ServerPush.getInstance();

      serverPush.setActive( true );

      assertTrue( serverPush._active );
    },

    // TODO [rst] Move to ServerPushHandler test or an integration test suite
    testSetActiveByProtocol : function() {
      var serverPush = ServerPush.getInstance();

      Processor.processOperation( {
        "target" : "rwt.client.ServerPush",
        "action" : "set",
        "properties" : {
          "active" : true
        }
      } );

      assertTrue( serverPush._active );
    },

    testSendServerPushRequest_doesNotRunWhenInactive : function() {
      var serverPush = ServerPush.getInstance();

      serverPush.sendServerPushRequest();

      assertFalse( serverPush._requestTimer.getEnabled() );
    },

    testSendServerPushRequest_sendsUIRequest : function() {
      TestUtil.initRequestLog();
      var serverPush = ServerPush.getInstance();
      serverPush.setActive( true );

      serverPush.sendServerPushRequest();
      TestUtil.forceInterval( serverPush._requestTimer );

      assertEquals( 2, TestUtil.getRequestsSend() );
    },

    testCreateRequest : function() {
      var connection = rwt.remote.Connection.getInstance();
      var serverPush = ServerPush.getInstance();

      var request = serverPush._createRequest();

      var expected = "servicehandler=org.eclipse.rap.serverpush&cid=" + connection.getConnectionId();
      assertEquals( expected, request.getData() );
      assertEquals( "GET", request._method );
      assertEquals( "application/javascript", request._responseType );
    }

  }

} );

}() );
