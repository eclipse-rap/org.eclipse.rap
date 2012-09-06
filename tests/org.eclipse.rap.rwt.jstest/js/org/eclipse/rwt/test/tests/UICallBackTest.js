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
var ObjectManager = rwt.protocol.ObjectManager;

qx.Class.define( "org.eclipse.rwt.test.tests.UICallBackTest", {

  extend : qx.core.Object,

  members : {

    testCreateUICallBackByProtocol : function() {
      var uiCallBack = this._createUICallBack();
      assertTrue( uiCallBack instanceof rwt.client.UICallBack );
      assertIdentical( uiCallBack, rwt.client.UICallBack.getInstance() );
    },

    testSetActiveByProtocol : function() {
      var uiCallBack = this._createUICallBack();
      Processor.processOperation( {
        "target" : "uicb",
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
      rwt.protocol.MessageProcessor.processOperation( {
        "target" : "uicb",
        "action" : "call",
        "method" : "sendUIRequest",
        "properties" : null
      } );
      assertEquals( 1, TestUtil.getRequestsSend() );
    },

    _createUICallBack : function() {
      Processor.processOperation( {
        "target" : "uicb",
        "action" : "create",
        "type" : "rwt.UICallBack"
      } );
      return ObjectManager.getObject( "uicb" );
    }

  }
  
} );

}());