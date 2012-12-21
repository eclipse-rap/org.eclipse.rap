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

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var server = rwt.remote.Server.getInstance();

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ServerTest", {

  extend : rwt.qx.Object,

  members : {

    testSendRequestCounter : function() {
      server.send();

      assertEquals( "number", typeof TestUtil.getMessageObject().getHead()[ "requestCounter" ] );
    },

    testSendSetParameter : function() {
      server.addParameter( "w3.myProp", 42 );

      server.send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 2, message.getOperationCount() );
      var op = message.getOperation( 0 );
      assertEquals( "set", op.type );
      assertEquals( "w3", op.target );
      assertEquals( 42, op.properties.myProp );
    },

    testSendSetParameterTwice : function() {
      server.addParameter( "w3.myProp", 42 );
      server.send();
      TestUtil.clearRequestLog();

      server.send();

      var message = TestUtil.getMessageObject();
      assertEquals( 1, message.getOperationCount() );
    },

    testSendSetParameterWithDot : function() {
      server.addParameter( "w3.my.Prop", 42 );

      server.send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 2, message.getOperationCount() );
      var op = message.getOperation( 0 );
      assertEquals( "set", op.type );
      assertEquals( "w3", op.target );
      assertEquals( 42, op.properties[ "my.Prop" ] );
    },

    testSendEvent : function() {
      server.addEvent( "org.eclipse.swt.events.Selection", "w3" );

      server.send();

      var op = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", op.type );
      assertEquals( "w3", op.target );
      assertEquals( "Selection", op.eventType );
    },

    testSendEventWithParam : function() {
      server.addEvent( "org.eclipse.swt.events.Selection", "w3" );
      server.addParameter( "org.eclipse.swt.events.Selection.text", "foo" );

      server.send();

      var op = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "w3", op.target );
      assertEquals( "foo", op.properties[ "text" ] );
    },

    testSendEventWithParamAndSet : function() {
      server.addEvent( "org.eclipse.swt.events.Selection", "w3" );
      server.addParameter( "org.eclipse.swt.events.Selection.text", "foo" );
      server.addParameter( "w3.myProp", 42 );

      server.send();

      var notify = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", notify.type );
      assertEquals( "w3", notify.target );
      assertEquals( "foo", notify.properties[ "text" ] );
      var set = TestUtil.getMessageObject().getOperation( 1 );
      assertEquals( "set", set.type );
      assertEquals( "w3", set.target );
      assertEquals( 42, set.properties.myProp );
    },

    testSendTwoEventsInOneRequest : function() {
      server.addEvent( "org.eclipse.swt.events.Selection", "w3" );
      server.addEvent( "org.eclipse.swt.events.DefaultSelection", "w3" );

      server.send();

      var op1 = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", op1.type );
      assertEquals( "w3", op1.target );
      assertEquals( "Selection", op1.eventType );
      var op2 = TestUtil.getMessageObject().getOperation( 1 );
      assertEquals( "notify", op2.type );
      assertEquals( "w3", op2.target );
      assertEquals( "DefaultSelection", op2.eventType );
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
    }

  }

} );

}());
