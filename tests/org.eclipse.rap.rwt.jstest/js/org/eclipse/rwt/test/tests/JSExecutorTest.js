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

qx.Class.define( "org.eclipse.rwt.test.tests.JSExecutorTest", {

  extend : qx.core.Object,

  members : {

    testJSExecutorExists : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var externalBrowser = ObjectManager.getObject( "jsex" );
      assertTrue( externalBrowser instanceof org.eclipse.rwt.JSExecutor );
    },

    testCreateJSExecutorByProtocol : function() {
      var jsExecutor = org.eclipse.rwt.JSExecutor.getInstance();
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "jsex",
        "action" : "create",
        "type" : "rwt.JSExecutor",
        "properties" : {}
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      assertIdentical( jsExecutor, ObjectManager.getObject( "jsex" ) );
    },

    testExecuteByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var uiCallBack = ObjectManager.getObject( "jsex" );
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "jsex",
        "action" : "call",
        "method" : "execute",
        "properties" : {
          "content" : "window.foo = 33;"
        }
      } );
      assertEquals( 33, window.foo );
      foo = undefined; // IE doesnt like delete window.foo or delete foo;
    }

  }

} );