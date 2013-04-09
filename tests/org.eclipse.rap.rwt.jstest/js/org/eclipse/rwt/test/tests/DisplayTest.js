/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
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
var MessageProcessor = rwt.remote.MessageProcessor;

var display;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DisplayTest", {

  extend : rwt.qx.Object,

  members : {

    testSetFocusControlByProtocol : function() {
      var button = new rwt.widgets.Button( "push" );
      rwt.remote.WidgetManager.getInstance().add( button, "btn1" );
      button.addToDocument();
      TestUtil.flush();
      assertFalse( button.getFocused() );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "focusControl" : "btn1"
        }
      } );
      assertTrue( button.getFocused() );
      button.destroy();
    },

    testSetMnemonicActivatorByProtocol : function() {
      TestUtil.protocolSet( "w1", { "mnemonicActivator" : "CTRL" } );

      var handler = rwt.widgets.util.MnemonicHandler.getInstance();
      assertTrue( handler._activator.ctrlKey );
      assertFalse( handler._activator.altKey );
      assertFalse( handler._activator.shiftKey );
      handler.setActivator( null );
    },

    testSendFocusControlByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.open();
      var button = new rwt.widgets.Button( "push" );
      rwt.remote.WidgetManager.getInstance().add( button, "btn1" );
      button.setParent( shell );
      TestUtil.flush();
      shell.setActive( true );

      TestUtil.click( button );
      rwt.remote.Server.getInstance().send();

      assertTrue( shell.getActive() );
      var message = TestUtil.getLastMessage();
      assertEquals( "btn1", message.findSetProperty( "w1", "focusControl" ) );
      shell.setActive( false );
      shell.destroy();
    },

    testSetHasResizeListenerByProtocol : function() {
      TestUtil.protocolListen( "w1", { "Resize" : true } );

      assertTrue( display._hasResizeListener );
    },

    testSendResizeEventByProtocol_WithoutListener : function() {
      rwt.widgets.base.ClientDocument.getInstance().createDispatchEvent( "windowresize" );
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertNull( message.findNotifyOperation( "w1", "Resize" ) );
    },

    testSendResizeEventByProtocol_WithListener : function() {
      TestUtil.protocolListen( "w1", { "Resize" : true } );

      rwt.widgets.base.ClientDocument.getInstance().createDispatchEvent( "windowresize" );

      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w1", "Resize" ) );
    },

    testSendWindowSize : function() {
      var width = rwt.html.Window.getInnerWidth( window );
      var height = rwt.html.Window.getInnerHeight( window );

      rwt.widgets.base.ClientDocument.getInstance().createDispatchEvent( "windowresize" );
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( [ 0, 0, width, height ], message.findSetProperty( "w1", "bounds" ) );
    },

    testSetEnableUiTests : function() {
      display.setEnableUiTests( true );

      assertIdentical( true, rwt.widgets.base.Widget._renderHtmlIds );
      display.setEnableUiTests( false );
    },

    testSendCursorLocation : function() {
      TestUtil.clickDOM( document.body, 10, 20 );
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( [ 10, 20 ], message.findSetProperty( "w1", "cursorLocation" ) );
    },

    testSendDPI : function() {
      var dpi = display.getDPI();
      display._appendSystemDPI();
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( dpi, message.findSetProperty( "w1", "dpi" ) );
    },


    testSendTimezoneOffset : function() {
      display._appendTimezoneOffset();
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      var expected = ( new Date() ).getTimezoneOffset();
      var actual = message.findSetProperty( "rwt.client.ClientInfo", "timezoneOffset" );
      assertEquals( expected, actual );
    },

    testSendColorDepth : function() {
      display._appendColorDepth();
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertTrue( typeof message.findSetProperty( "w1", "colorDepth" ) === "number" );
    },

    setUp : function() {
      display = rwt.widgets.Display.getCurrent();
      var adapter = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Display" );
      rwt.remote.ObjectRegistry.add( "w1", display, adapter );
      display.setHasResizeListener( false );
    }

  }

} );

}());
