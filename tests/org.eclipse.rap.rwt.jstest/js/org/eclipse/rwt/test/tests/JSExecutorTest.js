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

var Processor = org.eclipse.rwt.protocol.Processor;
var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;

qx.Class.define( "org.eclipse.rwt.test.tests.JSExecutorTest", {

  extend : qx.core.Object,

  members : {

    testCreateJSExecutorByProtocol : function() {
      var jsExecutor = this._createJSExecutor();
      assertTrue( jsExecutor instanceof org.eclipse.rwt.JSExecutor );
      assertIdentical( jsExecutor, org.eclipse.rwt.JSExecutor.getInstance() );
    },

    testExecuteByProtocol : function() {
      var jsExecutor = this._createJSExecutor();
      Processor.processOperation( {
        "target" : "jsex",
        "action" : "call",
        "method" : "execute",
        "properties" : {
          "content" : "window.foo = 33;"
        }
      } );
      assertEquals( 33, window.foo );
      foo = undefined; // IE doesnt like delete window.foo or delete foo;
    },

    _createJSExecutor : function() {
      Processor.processOperation( {
        "target" : "jsex",
        "action" : "create",
        "type" : "rwt.JSExecutor"
      } );
      return ObjectManager.getObject( "jsex" );
    }

  }

} );

}());