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

var RemoteObjectFactory = rwt.remote.RemoteObjectFactory;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.RemoteObjectFactoryTest", {

  extend : rwt.qx.Object,

  members : {

    testCreate : function() {
      var remoteObject = RemoteObjectFactory.getRemoteObject( shell );

      assertNotNull( remoteObject );
    },

    testCreateWithNullParameter : function() {
      try {
        RemoteObjectFactory.getRemoteObject( null );
        fail();
      } catch( expected ) {
      }
    },

    testCreateWithInvalidTarget : function() {
      var target = {};
      try {
        RemoteObjectFactory.getRemoteObject( target );
        fail();
      } catch( expected ) {
      }
    },

    testSameInstance : function() {
      var remoteObject = RemoteObjectFactory.getRemoteObject( shell );

      assertIdentical( remoteObject, RemoteObjectFactory.getRemoteObject( shell ) );
    },

    testGetServerObjectForDisplay: function() {
      var remoteObject = RemoteObjectFactory.getRemoteObject( rwt.widgets.Display.getCurrent() );

      assertNotNull( remoteObject );
    },

    testDisposeWithTarget : function() {
      var remoteObject = RemoteObjectFactory.getRemoteObject( shell );

      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "destroy"
      } );

      shell = TestUtil.createShellByProtocol( "w2" );
      assertTrue( remoteObject !== RemoteObjectFactory.getRemoteObject( shell ) );
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