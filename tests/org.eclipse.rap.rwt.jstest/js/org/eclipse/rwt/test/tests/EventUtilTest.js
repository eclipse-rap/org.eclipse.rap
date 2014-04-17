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

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var widget;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.EventUtilTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      TestUtil.createShellByProtocol();
      widget = TestUtil.createWidgetByProtocol( "w11" );
      TestUtil.flush();
    },

    tearDown : function() {
      rwt.remote.MessageProcessor.processOperationArray( [ "destroy", "w2" ] );
      widget = null;
    },

    testMouseDownSendsNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );

      TestUtil.click( widget );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseDown" ) );
    },

    testMouseUpSendsNotify : function() {
      widget.addEventListener( "mouseup", rwt.remote.EventUtil.mouseUp );

      TestUtil.click( widget );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseUp" ) );
    },

    testMouseDoubleClickSendsNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDoubleClick );
      widget.addEventListener( "mouseup", rwt.remote.EventUtil.mouseUpCounter );

      TestUtil.doubleClick( widget );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseDoubleClick" ) );
    },

    testMouseDoubleClickDoesNotSendNotifyIfDeviationIsBigger : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDoubleClick );
      widget.addEventListener( "mouseup", rwt.remote.EventUtil.mouseUpCounter );

      TestUtil.click( widget, 10, 10 );
      TestUtil.click( widget, 20, 20 );

      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testMouseDownOnChildElementSendsNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      var element = document.createElement( "div" );
      widget.getElement().appendChild( element );

      TestUtil.clickDOM( element );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseDown" ) );
    },

    testMouseDownOnSubWidgetSendsNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      var child = new rwt.widgets.base.Terminator();
      child.setParent( widget );
      TestUtil.flush();

      TestUtil.click( child );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseDown" ) );
    },

    testMouseDownOnChildControlSendsNoNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      var child = TestUtil.createWidgetByProtocol( "w12", "w11" );
      TestUtil.flush();

      TestUtil.click( child );

      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testMouseDownOnDisabledControlSendsNoNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      widget.setEnabled( false );

      TestUtil.click( widget );

      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testMouseDownOnDisabledChildControlSendsNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      var child = TestUtil.createWidgetByProtocol( "w12", "w11" );
      child.setEnabled( false );
      TestUtil.flush();

      TestUtil.click( child );

      assertNotNull( TestUtil.getLastMessage().findNotifyOperation( "w11", "MouseDown" ) );
    },

    testMouseDownOnDisabledGrandChildControlSendsNoNotify : function() {
      widget.addEventListener( "mousedown", rwt.remote.EventUtil.mouseDown );
      var child = TestUtil.createWidgetByProtocol( "w12", "w11" );
      var grandchild = TestUtil.createWidgetByProtocol( "w13", "w12" );
      grandchild.setEnabled( false );
      TestUtil.flush();

      TestUtil.click( grandchild );

      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testNotifySelected : function() {
      TestUtil.fakeListener( widget, "Selection", true );
      rwt.remote.EventUtil._button = rwt.event.MouseEvent.C_BUTTON_MIDDLE;
      rwt.remote.EventUtil._ctrlKey = true;

      rwt.remote.EventUtil.notifySelected( widget, 1, 2, 3, 4, "foo" );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w11", "Selection" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "Selection", "button" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w11", "Selection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "altKey" ) );
      assertEquals( 1, message.findNotifyProperty( "w11", "Selection", "x" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "Selection", "y" ) );
      assertEquals( 3, message.findNotifyProperty( "w11", "Selection", "width" ) );
      assertEquals( 4, message.findNotifyProperty( "w11", "Selection", "height" ) );
      assertEquals( "foo", message.findNotifyProperty( "w11", "Selection", "detail" ) );
    },

    testNotifySelected_withSinglePropertiesObject : function() {
      TestUtil.fakeListener( widget, "Selection", true );
      rwt.remote.EventUtil._button = rwt.event.MouseEvent.C_BUTTON_MIDDLE;
      rwt.remote.EventUtil._ctrlKey = true;

      rwt.remote.EventUtil.notifySelected( widget, { "foo" : "bar" } );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w11", "Selection" ) );
      assertEquals( "bar", message.findNotifyProperty( "w11", "Selection", "foo" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "Selection", "button" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w11", "Selection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "altKey" ) );
    },

    testNotifyDefaultSelected : function() {
      TestUtil.fakeListener( widget, "DefaultSelection", true );
      rwt.remote.EventUtil._button = rwt.event.MouseEvent.C_BUTTON_MIDDLE;
      rwt.remote.EventUtil._ctrlKey = true;

      rwt.remote.EventUtil.notifyDefaultSelected( widget, 1, 2, 3, 4, "foo" );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w11", "DefaultSelection" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "DefaultSelection", "button" ) );
      assertFalse( message.findNotifyProperty( "w11", "DefaultSelection", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w11", "DefaultSelection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "DefaultSelection", "altKey" ) );
      assertEquals( 1, message.findNotifyProperty( "w11", "DefaultSelection", "x" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "DefaultSelection", "y" ) );
      assertEquals( 3, message.findNotifyProperty( "w11", "DefaultSelection", "width" ) );
      assertEquals( 4, message.findNotifyProperty( "w11", "DefaultSelection", "height" ) );
      assertEquals( "foo", message.findNotifyProperty( "w11", "DefaultSelection", "detail" ) );
    },

    testNotifyDefaultSelected_withSinglePropertiesObject : function() {
      TestUtil.fakeListener( widget, "DefaultSelection", true );
      rwt.remote.EventUtil._button = rwt.event.MouseEvent.C_BUTTON_MIDDLE;
      rwt.remote.EventUtil._ctrlKey = true;

      rwt.remote.EventUtil.notifyDefaultSelected( widget, { "foo" : "bar" } );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w11", "DefaultSelection" ) );
      assertEquals( "bar", message.findNotifyProperty( "w11", "DefaultSelection", "foo" ) );
      assertEquals( 2, message.findNotifyProperty( "w11", "DefaultSelection", "button" ) );
      assertFalse( message.findNotifyProperty( "w11", "DefaultSelection", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w11", "DefaultSelection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "DefaultSelection", "altKey" ) );
    }

  }

} );

}() );
