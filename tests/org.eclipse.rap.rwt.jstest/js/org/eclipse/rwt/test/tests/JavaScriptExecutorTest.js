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

var Processor = rwt.remote.MessageProcessor;
var ObjectManager = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.JavaScriptExecutorTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateJavaScriptExecutorByProtocol : function() {
      var jsExecutor = this._createJavaScriptExecutor();
      assertTrue( jsExecutor instanceof rwt.client.JavaScriptExecutor );
      assertIdentical( jsExecutor, rwt.client.JavaScriptExecutor.getInstance() );
    },

    testExecuteByProtocol : function() {
      var jsExecutor = this._createJavaScriptExecutor();
      Processor.processOperation( {
        "target" : "rwt.client.JavaScriptExecutor",
        "action" : "call",
        "method" : "execute",
        "properties" : {
          "content" : "window.foo = 33;"
        }
      } );
      assertEquals( 33, window.foo );
      foo = undefined; // IE doesnt like delete window.foo or delete foo;
    },

    _createJavaScriptExecutor : function() {
      return ObjectManager.getObject( "rwt.client.JavaScriptExecutor" );
    }

  }

} );

}());