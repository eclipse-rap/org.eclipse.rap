/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var Processor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ServerPushTest", {

  extend : rwt.qx.Object,

  members : {

    testServerPushInstance : function() {
      var serverPush = this._createServerPush();
      assertTrue( serverPush instanceof rwt.client.ServerPush );
      assertIdentical( serverPush, rwt.client.ServerPush.getInstance() );
    },

    testSetActiveByProtocol : function() {
      var serverPush = this._createServerPush();
      Processor.processOperation( {
        "target" : "rwt.client.ServerPush",
        "action" : "set",
        "properties" : {
          "active" : true
        }
      } );
      assertTrue( serverPush._active );
    },

    testSendUIRequestByProtocol : function() {
      TestUtil.initRequestLog();
      var serverPush = this._createServerPush();
      serverPush.setActive( true );

      serverPush.sendUICallBackRequest();
      TestUtil.forceInterval( serverPush._requestTimer );

      assertEquals( 2, TestUtil.getRequestsSend() );
    },

    testCreateRequest : function() {
      var server = rwt.remote.Server.getInstance();
      var serverPush = this._createServerPush();

      var request = serverPush._createRequest();

      var expected = "servicehandler=org.eclipse.rap.serverpush&cid=" + server.getConnectionId();
      assertEquals( expected, request.getData() );
      assertEquals( "GET", request._method );
      assertEquals( "application/javascript", request._responseType );
    },

    _createServerPush : function() {
      return ObjectManager.getObject( "rwt.client.ServerPush" );
    }

  }

} );

}());
