/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var Processor = rwt.protocol.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.protocol.ObjectRegistry;

qx.Class.define( "org.eclipse.rwt.test.tests.ServerPushTest", {

  extend : qx.core.Object,

  members : {

    testCreateServerPushByProtocol : function() {
      var uiCallBack = this._createUICallBack();
      assertTrue( uiCallBack instanceof rwt.client.ServerPush );
      assertIdentical( uiCallBack, rwt.client.ServerPush.getInstance() );
    },

    testSetActiveByProtocol : function() {
      var uiCallBack = this._createUICallBack();
      Processor.processOperation( {
        "target" : "rwt.client.ServerPush",
        "action" : "set",
        "properties" : {
          "active" : true
        }
      } );
      assertTrue( uiCallBack._active );
    },

    testSendUIRequestByProtocol : function() {
      TestUtil.initRequestLog();
      var uiCallBack = this._createUICallBack();
      uiCallBack.setActive( true );

      uiCallBack.sendUICallBackRequest();
      TestUtil.forceInterval( uiCallBack._requestTimer );

      assertEquals( 2, TestUtil.getRequestsSend() );
    },

    _createUICallBack : function() {
      return ObjectManager.getObject( "rwt.client.ServerPush" );
    }

  }

} );

}());
