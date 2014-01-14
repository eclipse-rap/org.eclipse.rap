/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
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

var connection = rwt.remote.Connection.getInstance();
var ClientDocument = rwt.widgets.base.ClientDocument;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ConnectionTest", {

  extend : rwt.qx.Object,

  members : {

    testSendRequestCounter : function() {
      connection.send();

      assertEquals( "number", typeof TestUtil.getMessageObject().getHead()[ "requestCounter" ] );
    },

    testGetServerObject : function() {
      rwt.remote.ObjectRegistry.add( "w1", rwt.widgets.Display.getCurrent() );
      var remoteObject = connection.getRemoteObject( rwt.widgets.Display.getCurrent() );

      assertTrue( remoteObject instanceof rwt.remote.RemoteObject );
    },

    // See Bug 391393 - Invalid request counter on session restart
    testSendTwoInitialRequests: function() {
      var fakeServer = org.eclipse.rwt.test.fixture.FakeServer.getInstance();
      fakeServer.setUseAsync( true );
      connection.setRequestCounter( null );

      connection.sendImmediate( true );
      // NOTE [tb] : can not test sending second request since fixture for Connection.js
      //             does not support the pending request case

      assertTrue( connection._requestPending );
      TestUtil.forceInterval( fakeServer._timer );
      assertEquals( 1, TestUtil.getRequestsSend() );
      org.eclipse.rwt.test.fixture.FakeServer.getInstance().setUseAsync( false );
    },

    testOnNextSend : function() {
      var logger = TestUtil.getLogger();
      connection.onNextSend( logger.log, logger );

      connection.send();

      assertEquals( 1, logger.getLog().length );
    },

    testOnNextSendAttachTwice : function() {
      var logger = TestUtil.getLogger();
      connection.onNextSend( logger.log, logger );
      connection.onNextSend( logger.log, logger );

      connection.send();

      assertEquals( 1, logger.getLog().length );
    },

    testOnNextSendSendTwice : function() {
      var logger = TestUtil.getLogger();
      connection.onNextSend( logger.log, logger );

      connection.send();
      connection.send();

      assertEquals( 1, logger.getLog().length );
    },

    testDelayedSend : function() {
      var logger = TestUtil.getLogger();
      connection.addEventListener( "send", logger.log, logger );
      connection.sendDelayed( 500 );

      assertEquals( 500, connection._delayTimer.getInterval() );
      TestUtil.forceInterval( connection._delayTimer );

      assertEquals( 1, logger.getLog().length );
    },

    testDelayedSendAborted : function() {
      var logger = TestUtil.getLogger();
      connection.addEventListener( "send", logger.log, logger );

      connection.sendDelayed( 500 );
      connection.send();
      try {
        TestUtil.forceInterval( connection._delayTimer );
      } catch( ex ) {
        // expected
      }

      assertEquals( 1, logger.getLog().length );
    },

    testWaitHintChangesCursor : function() {
      connection.getWaitHintTimer().setEnabled( true );

      TestUtil.forceInterval( connection.getWaitHintTimer() );

      assertEquals( "progress", ClientDocument.getInstance().getGlobalCursor() );
      connection._hideWaitHint();
      assertNull( ClientDocument.getInstance().getGlobalCursor() );
    },

    testWaitHintShowsErrorOverlay : function() {
      connection.getWaitHintTimer().setEnabled( true );

      TestUtil.forceInterval( connection.getWaitHintTimer() );

      assertIdentical( document.body, rwt.runtime.ErrorHandler._overlay.parentNode );
      var overlay = rwt.runtime.ErrorHandler._overlay;
      connection._hideWaitHint();
      assertTrue( overlay.parentNode != document.body );
      assertNull( rwt.runtime.ErrorHandler._overlay );
    },

    testSetWaitHintTimeoutByProtocol : function() {
      TestUtil.protocolSet( "rwt.client.ConnectionMessages", { "waitHintTimeout" : 1999 } );

      assertEquals( 1999, connection.getWaitHintTimer().getInterval() );
    },

    testRequestUrl : function() {
      connection.setUrl( "foo" );
      var request = connection._createRequest();

      var expected = "foo?cid=" + connection.getConnectionId();
      assertEquals( expected, request._url );
    },

    testRequestUrlWithParameters : function() {
      connection.setUrl( "foo?bar=23" );
      var request = connection._createRequest();

      var expected = "foo?bar=23&cid=" + connection.getConnectionId();
      assertEquals( expected, request._url );
    },

    testRetry : function() {
      var log = [];
      connection._retryHandler = function() { log.push( "retry" ); };

      connection._retry();

      assertEquals( [ "retry" ], log );
    }

  }

} );

}());
