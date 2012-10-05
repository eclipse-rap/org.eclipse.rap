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

qx.Class.define( "org.eclipse.rwt.test.tests.ServerTest", {

  extend : qx.core.Object,

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
      server.addEvent( "org.eclipse.swt.events.widgetSelected", "w3" );

      server.send();

      var op = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", op.type );
      assertEquals( "w3", op.target );
      assertEquals( "widgetSelected", op.eventType );
    },

    testSendEventWithParam : function() {
      server.addEvent( "org.eclipse.swt.events.widgetSelected", "w3" );
      server.addParameter( "org.eclipse.swt.events.widgetSelected.text", "foo" );

      server.send();

      var op = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "w3", op.target );
      assertEquals( "foo", op.properties[ "text" ] );
    },

    testSendEventWithParamAndSet : function() {
      server.addEvent( "org.eclipse.swt.events.widgetSelected", "w3" );
      server.addParameter( "org.eclipse.swt.events.widgetSelected.text", "foo" );
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
      server.addEvent( "org.eclipse.swt.events.widgetSelected", "w3" );
      server.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", "w3" );

      server.send();

      var op1 = TestUtil.getMessageObject().getOperation( 0 );
      assertEquals( "notify", op1.type );
      assertEquals( "w3", op1.target );
      assertEquals( "widgetSelected", op1.eventType );
      var op2 = TestUtil.getMessageObject().getOperation( 1 );
      assertEquals( "notify", op2.type );
      assertEquals( "w3", op2.target );
      assertEquals( "widgetDefaultSelected", op2.eventType );
    },

    testGetServerObject : function() {
      rwt.protocol.ObjectRegistry.add( "w1", rwt.widgets.Display.getCurrent() );
      var serverObject = server.getServerObject( rwt.widgets.Display.getCurrent() );

      assertTrue( serverObject instanceof rwt.protocol.ServerObject );
    }

  }

} );

}());