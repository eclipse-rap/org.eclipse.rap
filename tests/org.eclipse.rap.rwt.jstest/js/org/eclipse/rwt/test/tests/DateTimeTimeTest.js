/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeTimeTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
    },

    tearDown : function() {
      shell.destroy();
    },

    testDateTimeHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DateTime" );

      assertEquals( [ "Selection", "DefaultSelection" ], handler.events );
    },

    testCreateDateTimeTimeByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.DateTimeTime );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-time", widget.getAppearance() );
      assertTrue( widget._medium );
      assertFalse( widget._short );
      assertFalse( widget._long );
      widget.destroy();
    },

    testSetHoursByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "hours" : 3
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "03", widget._hoursTextField.getText() );
      widget.destroy();
    },

    testSetMinutesByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "minutes" : 33
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "33", widget._minutesTextField.getText() );
      widget.destroy();
    },

    testSetSecondsByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "seconds" : 22
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "22", widget._secondsTextField.getText() );
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var minimum = 1000;
      var minimumDate = new Date( minimum );
      var time = new Date(1970, 0, 1, minimumDate.getHours(), minimumDate.getMinutes(), minimumDate.getSeconds());

      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "minimum" : minimum
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );

      assertEquals( time.getTime(), widget._minimum.getTime() );
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var maximum = 99000;
      var maximumDate = new Date( maximum );
      var time = new Date(1970, 0, 1, maximumDate.getHours(), maximumDate.getMinutes(), maximumDate.getSeconds());

      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "maximum" : maximum
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );

      assertEquals( time.getTime(), widget._maximum.getTime() );
      widget.destroy();
    },

    testSetSubWidgetsBoundsByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : [ "TIME", "MEDIUM" ],
          "parent" : "w2",
          "subWidgetsBounds" : [ [ 8, 3, 5, 24, 18 ], [ 11, 27, 5, 6, 18 ] ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 3, widget._hoursTextField.getLeft() );
      assertEquals( 5, widget._hoursTextField.getTop() );
      assertEquals( 24, widget._hoursTextField.getWidth() );
      assertEquals( 18, widget._hoursTextField.getHeight() );
      assertEquals( 27, widget._separator3.getLeft() );
      assertEquals( 5, widget._separator3.getTop() );
      assertEquals( 6, widget._separator3.getWidth() );
      assertEquals( 18, widget._separator3.getHeight() );
      widget.destroy();
    },

    testCreateDateTimeTime : function() {
      var dateTime = this._createDefaultDateTimeTime();
      assertTrue( dateTime instanceof rwt.widgets.DateTimeTime );
      assertEquals( "datetime-time", dateTime.getAppearance() );
      assertFalse( dateTime._spinner.getSelectTextOnInteract() );
      dateTime.destroy();
    },

    testSendAllFieldsTogether : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 4 );
      dateTime.setMinutes( 34 );
      dateTime.setSeconds( 55 );
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Up" );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 5, message.findSetProperty( "w3", "hours" ) );
      assertEquals( 34, message.findSetProperty( "w3", "minutes" ) );
      assertEquals( 55, message.findSetProperty( "w3", "seconds" ) );
      dateTime.destroy();
    },

    testSendSelectionEvent : function() {
      var dateTime = this._createDefaultDateTimeTime();
      TestUtil.fakeListener( dateTime, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Up" );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
      dateTime.destroy();
    },

    testSendDefaultSelectionEvent : function() {
      var dateTime = this._createDefaultDateTimeTime();
      TestUtil.fakeListener( dateTime, "DefaultSelection", true );
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      dateTime.destroy();
    },

    // see bug 358531
    testEditingWithKeyboard_InitialEditing : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 2 );
      dateTime.setMinutes( 2 );
      dateTime.setSeconds( 2 );
      TestUtil.click( dateTime._minutesTextField );
      TestUtil.flush();
      TestUtil.press( dateTime, "1" );
      TestUtil.press( dateTime, "3" );
      assertEquals( "13", dateTime._minutesTextField.getText() );
      dateTime.destroy();
    },

    testEditingWithKeyboard_ContinualEditing : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 2 );
      dateTime.setMinutes( 2 );
      dateTime.setSeconds( 2 );
      TestUtil.click( dateTime._minutesTextField );
      TestUtil.flush();
      TestUtil.press( dateTime, "2" );
      TestUtil.press( dateTime, "1" );
      TestUtil.press( dateTime, "3" );
      assertEquals( "03", dateTime._minutesTextField.getText() );
      dateTime.destroy();
    },

    testSetCustomVariant : function() {
      var dateTime = this._createDefaultDateTimeTime();

      dateTime.setCustomVariant( "foo" );

      assertEquals( "foo", dateTime._hoursTextField._customVariant );
      assertEquals( "foo", dateTime._minutesTextField._customVariant );
      assertEquals( "foo", dateTime._secondsTextField._customVariant );
      assertEquals( "foo", dateTime._spinner._customVariant );
      assertEquals( "foo", dateTime._separator3._customVariant );
      assertEquals( "foo", dateTime._separator4._customVariant );
      dateTime.destroy();
    },

    testRollLeft : function() {
      var dateTime = this._createDefaultDateTimeTime();

      TestUtil.press( dateTime, "Left" );
      assertTrue( dateTime._secondsTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Left" );
      assertTrue( dateTime._minutesTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Left" );
      assertTrue( dateTime._hoursTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Left" );
      assertTrue( dateTime._secondsTextField.hasState( "selected" ) );

      dateTime.destroy();
    },

    testRollRight : function() {
      var dateTime = this._createDefaultDateTimeTime();

      TestUtil.press( dateTime, "Right" );
      assertTrue( dateTime._minutesTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Right" );
      assertTrue( dateTime._secondsTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Right" );
      assertTrue( dateTime._hoursTextField.hasState( "selected" ) );
      TestUtil.press( dateTime, "Right" );
      assertTrue( dateTime._minutesTextField.hasState( "selected" ) );

      dateTime.destroy();
    },

    testPressHome : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 4 );

      TestUtil.press( dateTime, "Home" );
      assertEquals( "00", dateTime._hoursTextField.getText() );

      dateTime.destroy();
    },

    testPressEnd : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 4 );

      TestUtil.press( dateTime, "End" );

      assertEquals( "23", dateTime._hoursTextField.getText() );
      dateTime.destroy();
    },

    testRollWithMouseWheel : function() {
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setFocused( true );
      dateTime.setHours( 4 );

      TestUtil.fakeWheel( dateTime, -1 );

      assertEquals( "03", dateTime._hoursTextField.getText() );
      dateTime.destroy();
    },

    testSetDirection : function() {
      var dateTime = this._createDefaultDateTimeTime();

      dateTime.setDirection( "rtl" );

      assertEquals( "rtl", dateTime.getDirection() );
      assertEquals( "rtl", dateTime._spinner._upbutton.getDirection() );
      assertEquals( "rtl", dateTime._spinner._downbutton.getDirection() );
      dateTime.destroy();
    },

    testLayout_onSpinnerWidthChange : function() {
      var dateTime = this._createDefaultDateTimeTime();

      dateTime._spinner.setWidth( 30 );

      assertEquals( 85, dateTime._timePane.getWidth() );
      dateTime.destroy();
    },

    //////////
    // Helpers

    _createDefaultDateTimeTime : function() {
      var dateTime = new rwt.widgets.DateTimeTime( "medium" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DateTime" );
      ObjectRegistry.add( "w3", dateTime, handler );
      dateTime.setSpace( 3, 115, 3, 20 );
      dateTime.addToDocument();
      TestUtil.flush();
      return dateTime;
    }

  }

} );

}() );
