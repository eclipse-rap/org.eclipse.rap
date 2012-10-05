/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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
var MessageProcessor = rwt.protocol.MessageProcessor;

var display;

qx.Class.define( "org.eclipse.rwt.test.tests.DisplayTest", {

  extend : qx.core.Object,

  members : {

    testCallProbeByProtocol : function() {
      var text
        = "!#$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxy";
      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
      TestUtil.initRequestLog();

      MessageProcessor.processOperation( {
        "target" : "w1",
        "action" : "call",
        "method" : "probe",
        "properties" : {
          "fonts" : [
             [ -785380229, text, fontName, 11, false, false ],
             [ -785380485, text, fontName, 12, false, false ]
          ]
        }
      } );

      assertEquals( 1, TestUtil.getRequestsSend() ); // because timer is skipped in tests
      var op = TestUtil.getMessageObject().findCallOperation( "w1", "storeProbes" );
      assertEquals( 2, op.properties.results[ "-785380229" ].length );
      assertEquals( 2, op.properties.results[ "-785380485" ].length );
    },

    testCallMeasureStringsByProtocol : function() {
      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
      TestUtil.initRequestLog();

      MessageProcessor.processOperation( {
        "target" : "w1",
        "action" : "call",
        "method" : "measureStrings",
        "properties" : {
          "strings" : [
             [ -1114032847, "Check", fontName, 12, false, false, -1 ],
             [ 1767849485, "  Push &&\n Button ", fontName, 12, false, false, -1 ]
          ]
        }
      } );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var op = TestUtil.getMessageObject().findCallOperation( "w1", "storeMeasurements" );
      assertEquals( 2, op.properties.results[ "-1114032847" ].length );
      assertEquals( 2, op.properties.results[ "1767849485" ].length );
    },

    testSetFocusControlByProtocol : function() {
      var button = new rwt.widgets.Button( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( button, "btn1" );
      button.addToDocument();
      TestUtil.flush();
      assertFalse( button.getFocused() );
      var processor = rwt.protocol.MessageProcessor;
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

    testSendFocusControlByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.open();
      var button = new rwt.widgets.Button( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( button, "btn1" );
      button.setParent( shell );
      TestUtil.flush();
      shell.setActive( true );

      TestUtil.click( button );
      rwt.remote.Server.getInstance().send();

      assertTrue( shell.getActive() );
      var message = TestUtil.getLastMessage();
      assertEquals( "btn1", message.findSetProperty( "w1", "focusControl" ) );
      shell.destroy();
    },

    testSetCurrentThemeByProtocol : function() {
      MessageProcessor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "currentTheme" : "myTheme"
        }
      } );
      assertEquals( "myTheme", rwt.theme.ThemeStore.getInstance()._currentTheme );
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

    testSendWindowSize : function() {
      var width = qx.html.Window.getInnerWidth( window );
      var height = qx.html.Window.getInnerHeight( window );
      rwt.widgets.base.ClientDocument.getInstance().createDispatchEvent( "windowresize" );

      var message = TestUtil.getMessageObject();
      assertEquals( [ 0, 0, width, height ], message.findSetProperty( "w1", "bounds" ) );
    },

    testSendDPI : function() {
      var dpi = display.getDPI();
      display._appendSystemDPI();
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( dpi, message.findSetProperty( "w1", "dpi" ) );
    },

    testSendColorDepth : function() {
      display._appendColorDepth();
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertTrue( typeof message.findSetProperty( "w1", "colorDepth" ) === "number" );
    },

    setUp : function() {
      display = rwt.widgets.Display.getCurrent();
      var adapter = rwt.protocol.AdapterRegistry.getAdapter( "rwt.Display" );
      rwt.protocol.ObjectRegistry.add( "w1", display, adapter );
    }

  }

} );

}());
