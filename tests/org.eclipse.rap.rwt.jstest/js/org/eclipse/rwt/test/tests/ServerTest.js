/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var server = rwt.remote.Server.getInstance();
var ClientDocument = rwt.widgets.base.ClientDocument;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ServerTest", {

  extend : rwt.qx.Object,

  members : {

    testSendRequestCounter : function() {
      server.send();

      assertEquals( "number", typeof TestUtil.getMessageObject().getHead()[ "requestCounter" ] );
    },

    testGetServerObject : function() {
      rwt.remote.ObjectRegistry.add( "w1", rwt.widgets.Display.getCurrent() );
      var remoteObject = server.getRemoteObject( rwt.widgets.Display.getCurrent() );

      assertTrue( remoteObject instanceof rwt.remote.RemoteObject );
    },

    // See Bug 391393 - Invalid request counter on session restart
    testSendTwoInitialRequests: function() {
      var fakeServer = org.eclipse.rwt.test.fixture.FakeServer.getInstance();
      fakeServer.setUseAsync( true );
      server.setRequestCounter( null );

      server.sendImmediate( true );
      // NOTE [tb] : can not test sending second request since fixture for Server.js
      //             does not support the requestCounter -1 case

      assertEquals( -1, server.getRequestCounter() );
      TestUtil.forceInterval( fakeServer._timer );
      assertEquals( 1, TestUtil.getRequestsSend() );
      org.eclipse.rwt.test.fixture.FakeServer.getInstance().setUseAsync( false );
    },

    testOnNextSend : function() {
      var logger = TestUtil.getLogger();
      server.onNextSend( logger.log, logger );

      server.send();

      assertEquals( 1, logger.getLog().length );
    },

    testOnNextSendAttachTwice : function() {
      var logger = TestUtil.getLogger();
      server.onNextSend( logger.log, logger );
      server.onNextSend( logger.log, logger );

      server.send();

      assertEquals( 1, logger.getLog().length );
    },

    testOnNextSendSendTwice : function() {
      var logger = TestUtil.getLogger();
      server.onNextSend( logger.log, logger );

      server.send();
      server.send();

      assertEquals( 1, logger.getLog().length );
    },

    testDelayedSend : function() {
      var logger = TestUtil.getLogger();
      server.addEventListener( "send", logger.log, logger );
      server.sendDelayed( 500 );

      assertEquals( 500, server._delayTimer.getInterval() );
      TestUtil.forceInterval( server._delayTimer );

      assertEquals( 1, logger.getLog().length );
    },

    testDelayedSendAborted : function() {
      var logger = TestUtil.getLogger();
      server.addEventListener( "send", logger.log, logger );

      server.sendDelayed( 500 );
      server.send();
      try {
        TestUtil.forceInterval( server._delayTimer );
      } catch( ex ) {
        // expected
      }

      assertEquals( 1, logger.getLog().length );
    },

    testWaitHintChangesCursor : function() {
      server.getWaitHintTimer().setEnabled( true );

      TestUtil.forceInterval( server.getWaitHintTimer() );

      assertEquals( "progress", ClientDocument.getInstance().getGlobalCursor() );
      server._hideWaitHint();
      assertNull( ClientDocument.getInstance().getGlobalCursor() );
    },

    testWaitHintShowsErrorOverlay : function() {
      server.getWaitHintTimer().setEnabled( true );

      TestUtil.forceInterval( server.getWaitHintTimer() );

      assertIdentical( document.body, rwt.runtime.ErrorHandler._overlay.parentNode );
      var overlay = rwt.runtime.ErrorHandler._overlay;
      server._hideWaitHint();
      assertTrue( overlay.parentNode != document.body );
      assertNull( rwt.runtime.ErrorHandler._overlay );
    },

    testSetWaitHintTimeoutByProtocol : function() {
      TestUtil.protocolSet( "rwt.client.ConnectionMessages", { "waitHintTimeout" : 1999 } );

      assertEquals( 1999, server.getWaitHintTimer().getInterval() );
    },

    testRequestUrl : function() {
      server.setUrl( "foo" );
      var request = server._createRequest();

      var expected = "foo?cid=" + server.getConnectionId();
      assertEquals( expected, request._url );
    },

    testRequestUrlWithParameters : function() {
      server.setUrl( "foo?bar=23" );
      var request = server._createRequest();

      var expected = "foo?bar=23&cid=" + server.getConnectionId();
      assertEquals( expected, request._url );
    }

  }

} );

}());
