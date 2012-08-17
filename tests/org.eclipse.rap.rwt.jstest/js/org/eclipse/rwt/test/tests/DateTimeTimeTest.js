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
qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeTimeTest", {
  extend : qx.core.Object,

  members : {

    testCreateDateTimeTimeByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.DateTimeTime );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-time", widget.getAppearance() );
      assertTrue( widget._medium );
      assertFalse( widget._short );
      assertFalse( widget._long );
      shell.destroy();
      widget.destroy();
    },

    testSetHoursByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "hours" : 3
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 3, widget._hoursTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinutesByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "minutes" : 33
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 33, widget._minutesTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetSecondsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "seconds" : 22
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 22, widget._secondsTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetSubWidgetsBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "subWidgetsBounds" : [ [ 8, 3, 5, 24, 18 ], [ 11, 27, 5, 6, 18 ] ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 3, widget._hoursTextField.getLeft() );
      assertEquals( 5, widget._hoursTextField.getTop() );
      assertEquals( 24, widget._hoursTextField.getWidth() );
      assertEquals( 18, widget._hoursTextField.getHeight() );
      assertEquals( 27, widget._separator3.getLeft() );
      assertEquals( 5, widget._separator3.getTop() );
      assertEquals( 6, widget._separator3.getWidth() );
      assertEquals( 18, widget._separator3.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },
 
    testCreateDateTimeTime : function() {
      var dateTime = this._createDefaultDateTimeTime();
      assertTrue( dateTime instanceof org.eclipse.swt.widgets.DateTimeTime );
      assertEquals( "datetime-time", dateTime.getAppearance() );
      dateTime.destroy();
    },

    testSendAllFieldsTogether : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 4 );
      dateTime.setMinutes( 34 );
      dateTime.setSeconds( 55 );
      TestUtil.clearRequestLog();
      dateTime._sendChanges();
      assertEquals( 0, TestUtil.getRequestsSend() );
      var req = org.eclipse.swt.Server.getInstance();
      assertEquals( 4, req._parameters[ "w3.hours" ] );
      assertEquals( 34, req._parameters[ "w3.minutes" ] );
      assertEquals( 55, req._parameters[ "w3.seconds" ] );
    },

    testSendEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHasSelectionListener( true );
      dateTime.setHours( 4 );
      dateTime.setMinutes( 34 );
      dateTime.setSeconds( 55 );
      TestUtil.clearRequestLog();
      dateTime._sendChanges(); 
      // this should restart the timer, though there is currently no way to test it:
      dateTime._sendChanges(); 
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.forceInterval( dateTime._requestTimer );
      assertFalse( dateTime._requestTimer.getEnabled() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      dateTime.destroy();

    },

    // see bug 358531
    testEditingWithKeyboard_InitialEditing : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 2 );
      dateTime.setMinutes( 2 );
      dateTime.setSeconds( 2 );
      TestUtil.click( dateTime._minutesTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "13", dateTime._minutesTextField.getText() );
      dateTime.destroy();
    },

    testEditingWithKeyboard_ContinualEditing : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 2 );
      dateTime.setMinutes( 2 );
      dateTime.setSeconds( 2 );
      TestUtil.click( dateTime._minutesTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "2" );
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "03", dateTime._minutesTextField.getText() );
      dateTime.destroy();
    },

    //////////
    // Helpers

    _createDefaultDateTimeTime : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = "medium";
      var dateTime = new org.eclipse.swt.widgets.DateTimeTime( style );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( dateTime, "w3", true );
      dateTime.setSpace( 3, 115, 3, 20 );
      dateTime.addToDocument();
      TestUtil.flush();
      return dateTime;
    }

  }

} );
