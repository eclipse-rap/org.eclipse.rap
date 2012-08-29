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

qx.Class.define( "org.eclipse.rwt.test.tests.DisplayTest", {

  extend : qx.core.Object,

  members : {

    testCallProbeByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text
        = "!#$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxy";
      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
      TestUtil.initRequestLog();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
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
      assertEquals( 0, TestUtil.getRequestsSend() );
      var request = rwt.remote.Server.getInstance();
      assertNotNull( request.getParameter( "-785380229" ) );
      assertNotNull( request.getParameter( "-785380485" ) );
    },

    // TODO [tb] : re-enable before commit!
//    testCallMeasureStringsByProtocol : function() {
//      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
//      TestUtil.initRequestLog();
//      var processor = rwt.protocol.MessageProcessor;
//      processor.processOperation( {
//        "target" : "w1",
//        "action" : "call",
//        "method" : "measureStrings",
//        "properties" : {
//          "strings" : [
//             [ -1114032847, "Check", fontName, 12, false, false, -1 ],
//             [ 1767849485, "  Push &&\n Button ", fontName, 12, false, false, -1 ]
//          ]
//        }
//      } );
//      assertEquals( 2, TestUtil.getRequestsSend() );
//      var log = TestUtil.getRequestLog();
//      assertTrue( log[ 0 ].indexOf( "-1114032847=" ) != -1 );
//      assertTrue( log[ 1 ].indexOf( "1767849485=" ) != -1 );
//    },

    testSetFocusControlByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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

    testSetCurrentThemeByProtocol : function() {
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "currentTheme" : "myTheme"
        }
      } );
      assertEquals( "myTheme", org.eclipse.swt.theme.ThemeStore.getInstance()._currentTheme );
    },

    testSetEnableUiTests : function() {
      rwt.widgets.Display._current = undefined;
      var display = new rwt.widgets.Display();

      display.setEnableUiTests( true );

      assertIdentical( true, rwt.widgets.base.Widget._renderHtmlIds );
      display.setEnableUiTests( false );
    }

  }

} );
