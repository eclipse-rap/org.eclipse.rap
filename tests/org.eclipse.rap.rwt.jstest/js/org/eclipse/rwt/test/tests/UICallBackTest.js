/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.UICallBackTest", {

  extend : qx.core.Object,

  members : {

    testUICallBackExists : function() {
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var uiCallBack = objectManager.getObject( "uicb" );
      assertTrue( uiCallBack instanceof org.eclipse.rwt.UICallBack );
    },

    testSetActiveByProtocol : function() {
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var uiCallBack = objectManager.getObject( "uicb" );
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "uicb",
        "action" : "set",
        "properties" : {
          "active" : true
        }
      } );
      assertTrue( uiCallBack._active );
    },

    testSendUIRequestByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var uiCallBack = objectManager.getObject( "uicb" );
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "uicb",
        "action" : "call",
        "method" : "sendUIRequest",
        "properties" : null
      } );
      assertEquals( 1, testUtil.getRequestsSend() );
    }

  }
  
} );