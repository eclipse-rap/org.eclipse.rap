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
    }

  }

} );

}());