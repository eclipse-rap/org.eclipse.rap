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

var ServerObjectFactory = rwt.protocol.ServerObjectFactory;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ServerObjectFactoryTest", {

  extend : rwt.qx.Object,

  members : {

    testCreate : function() {
      var serverObject = ServerObjectFactory.getServerObject( shell );

      assertNotNull( serverObject );
    },

    testCreateWithNullParameter : function() {
      try {
        ServerObjectFactory.getServerObject( null );
        fail();
      } catch( expected ) {
      }
    },

    testCreateWithInvalidTarget : function() {
      var target = {};
      try {
        ServerObjectFactory.getServerObject( target );
        fail();
      } catch( expected ) {
      }
    },

    testSameInstance : function() {
      var serverObject = ServerObjectFactory.getServerObject( shell );

      assertIdentical( serverObject, ServerObjectFactory.getServerObject( shell ) );
    },

    testGetServerObjectForDisplay: function() {
      var serverObject = ServerObjectFactory.getServerObject( rwt.widgets.Display.getCurrent() );

      assertNotNull( serverObject );
    },

    testDisposeWithTarget : function() {
      var serverObject = ServerObjectFactory.getServerObject( shell );

      rwt.protocol.MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "destroy"
      } );

      shell = TestUtil.createShellByProtocol( "w2" );
      assertTrue( serverObject !== ServerObjectFactory.getServerObject( shell ) );
    },



    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    }

  }

} );

}());