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
var ObjectManager = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

var monthNames = [ "January",
                   "February",
                   "March",
                   "April",
                   "May",
                   "June",
                   "July",
                   "August",
                   "September",
                   "October",
                   "November",
                   "December",
                   "" ];
var weekdayNames = [ "",
                     "Sunday",
                     "Monday",
                     "Tuesday",
                     "Wednesday",
                     "Thursday",
                     "Friday",
                     "Saturday" ];
var weekdayShortNames = [ "",
                          "Sun",
                          "Mon",
                          "Tue",
                          "Wed",
                          "Thu",
                          "Fri",
                          "Sat" ];
var dateSeparator = "/";
var datePattern = "MDY";

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeDateTest", {

  extend : rwt.qx.Object,

  members : {


    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
    },

    tearDown : function() {
      shell.destroy();
    },

    testCreateDateTimeDateByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      assertTrue( widget instanceof rwt.widgets.DateTimeDate );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-date", widget.getAppearance() );
      assertTrue( widget._medium );
      assertFalse( widget._short );
      assertFalse( widget._long );
      assertEquals( 34, rwt.widgets.base.Calendar.CELL_WIDTH );
      assertEquals( 19, rwt.widgets.base.Calendar.CELL_HEIGHT );
      assertEquals( monthNames, widget._monthname );
      assertEquals( weekdayNames, widget._weekday );
      assertEquals( weekdayShortNames, rwt.widgets.base.Calendar.WEEKDAY_NAMES );
      assertEquals( dateSeparator, widget._separator1.getText() );
      assertEquals( datePattern, widget._datePattern );
      widget.destroy();
    },

    testSetYearByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "year" : 2000 } );
      assertEquals( "2000", widget._yearTextField.getText() );
      widget.destroy();
    },

    testSetMonthByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "month" : 6 } );
      assertEquals( "07", widget._monthTextField.getText() );
      widget.destroy();
    },

    testSetDayByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "day" : 10 } );
      assertEquals( "10", widget._dayTextField.getText() );
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      var date = new Date( 2000, 0, 1, 0, 0, 0, 0 );
      TestUtil.protocolSet( "w3", { "minimum" : date.getTime() } );
      assertEquals( date.getTime(), widget._minimum.getTime() );
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      var date = new Date( 2090, 0, 1, 0, 0, 0, 0 );
      TestUtil.protocolSet( "w3", { "maximum" : date.getTime() } );
      assertEquals( date.getTime(), widget._maximum.getTime() );
      widget.destroy();
    },

    testSetSubWidgetsBoundsByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "subWidgetsBounds" : [ [ 0, 3, 5, 0, 18 ],
                                                           [ 4, 3, 5, 0, 18 ] ] } );
      assertEquals( 3, widget._weekdayTextField.getLeft() );
      assertEquals( 5, widget._weekdayTextField.getTop() );
      assertEquals( 0, widget._weekdayTextField.getWidth() );
      assertEquals( 18, widget._weekdayTextField.getHeight() );
      assertEquals( 3, widget._separator0.getLeft() );
      assertEquals( 5, widget._separator0.getTop() );
      assertEquals( 0, widget._separator0.getWidth() );
      assertEquals( 18, widget._separator0.getHeight() );
      widget.destroy();
    },

    testCreateDateTimeDate : function() {
      var dateTime = this._createDefaultDateTime();
      assertTrue( dateTime instanceof rwt.widgets.DateTimeDate );
      assertEquals( "datetime-date", dateTime.getAppearance() );
      assertFalse( dateTime._spinner.getSelectTextOnInteract() );
      dateTime.destroy();
    },

    testSendAllFieldsTogether : function() {
      var dateTime = this._createDefaultDateTime();
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Up" );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 10, message.findSetProperty( "w3", "day" ) );
      assertEquals( 11, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testSendSelectionEvent : function() {
      var dateTime = this._createDefaultDateTime();
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
      var dateTime = this._createDefaultDateTime();
      TestUtil.fakeListener( dateTime, "DefaultSelection", true );
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      dateTime.destroy();
    },

    testSendDefaultSelectionEvent_withOpenCalendar : function() {
      TestUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTime( true );
      TestUtil.fakeListener( dateTime, "DefaultSelection", true );
      dateTime._showCalendar();
      TestUtil.clearRequestLog();

      TestUtil.press( dateTime, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      dateTime.destroy();
    },

    testDropDownCalendar : function() {
      var dateTime = this._createDefaultDateTime( true );
      TestUtil.click( dateTime._dropDownButton );
      TestUtil.flush();
      var calendar = dateTime._calendar;
      assertTrue( dateTime._calendar.isSeeable() );
      assertEquals( 3, calendar.getLeft() );
      assertEquals( "May fail if browser-window not high enough", 23, calendar.getTop() );
      dateTime.destroy();
    },

    testDropDownCalendarNotEnoughSpace : function() {
      var dateTime = this._createDefaultDateTime( true );
      dateTime.setTop( window.innerHeight - 40 );
      TestUtil.flush();
      TestUtil.click( dateTime._dropDownButton );
      TestUtil.flush();
      var calendar = dateTime._calendar;
      assertTrue( dateTime._calendar.isSeeable() );
      assertEquals( 3, calendar.getLeft() );
      var expectedTop = dateTime.getTop() - calendar.getHeightValue();
      assertEquals( expectedTop, calendar.getTop() );
      dateTime.destroy();
    },

    testPressAltUp_showsCalendar : function() {
      var dateTime = this._createDefaultDateTime( true );

      TestUtil.press( dateTime, "Up", false, rwt.event.DomEvent.ALT_MASK );
      TestUtil.flush();

      assertTrue( dateTime._calendar.isSeeable() );
      dateTime.destroy();
    },

    testPressAltDown_showsCalendar : function() {
      var dateTime = this._createDefaultDateTime( true );

      TestUtil.press( dateTime, "Down", false, rwt.event.DomEvent.ALT_MASK );
      TestUtil.flush();

      assertTrue( dateTime._calendar.isSeeable() );
      dateTime.destroy();
    },

    // see bug 358531
    testEditingWithKeyboard_InitialEditing : function() {
      var dateTime = this._createDefaultDateTime();
      dateTime._setDate( new Date( 2011, 8, 2 ) );
      TestUtil.click( dateTime._dayTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "13", dateTime._dayTextField.getText() );
      dateTime.destroy();
    },

    testEditingWithKeyboard_ContinualEditing : function() {
      var dateTime = this._createDefaultDateTime();
      dateTime._setDate( new Date( 2011, 8, 2 ) );
      TestUtil.click( dateTime._dayTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "2" );
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "03", dateTime._dayTextField.getText() );
      dateTime.destroy();
    },

    testSetCustomVariant : function() {
      var dateTime = this._createDefaultDateTime();

      dateTime.setCustomVariant( "foo" );

      assertEquals( "foo", dateTime._weekdayTextField._customVariant );
      assertEquals( "foo", dateTime._monthTextField._customVariant );
      assertEquals( "foo", dateTime._dayTextField._customVariant );
      assertEquals( "foo", dateTime._yearTextField._customVariant );
      assertEquals( "foo", dateTime._spinner._customVariant );
      assertEquals( "foo", dateTime._separator0._customVariant );
      assertEquals( "foo", dateTime._separator1._customVariant );
      assertEquals( "foo", dateTime._separator2._customVariant );
      dateTime.destroy();
    },

    testSetCustomVariant_withDropDown : function() {
      var dateTime = this._createDefaultDateTime( true );

      dateTime.setCustomVariant( "foo" );

      assertEquals( "foo", dateTime._dropDownButton._customVariant );
      assertEquals( "foo", dateTime._calendar._customVariant );
      dateTime.destroy();
    },

    testSetDirection : function() {
      var dateTime = this._createDefaultDateTime( true );

      dateTime.setDirection( "rtl" );

      assertEquals( "rtl", dateTime.getDirection() );
      assertEquals( "rtl", dateTime._spinner._upbutton.getDirection() );
      assertEquals( "rtl", dateTime._spinner._downbutton.getDirection() );
      assertEquals( "rtl", dateTime._dropDownButton.getDirection() );
      assertEquals( "rtl", dateTime._calendar.getDirection() );
      dateTime.destroy();
    },

    testLayout_onSpinnerWidthChange : function() {
      var dateTime = this._createDefaultDateTime();

      dateTime._spinner.setWidth( 30 );

      assertEquals( 85, dateTime._datePane.getWidth() );
      dateTime.destroy();
    },

    // See bug: 483833: Weekday on 29th February of leapyears shown wrong
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=483833
    testWeekday_onLeapYear : function() {
      var dateTime = this._createDefaultDateTime();
      dateTime.setYear( 2016 );
      dateTime.setMonth( 1 );
      dateTime.setDay( 29 );

      assertEquals( "Monday", dateTime._weekdayTextField.getText() );
      dateTime.destroy();
    },

    //////////
    // Helpers

    _createDefaultDateTimeByProtocol : function( id, parentId, dropdown ) {
      var styles =  [ "DATE", "MEDIUM" ];
      if( dropdown ) {
        styles[ 2 ] = "DROP_DOWN";
      }
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "cellSize" : [ 34, 19 ],
          "monthNames" : monthNames,
          "weekdayNames" : weekdayNames,
          "weekdayShortNames" : weekdayShortNames,
          "dateSeparator" : dateSeparator,
          "datePattern" : datePattern
        }
      } );
      return ObjectManager.getObject( "w3" );
    },

    _createDefaultDateTime : function( dropdown ) {
      var style = "medium";
      if( dropdown ) {
        style +=  "|drop_down";
      }
      var dateTime = new rwt.widgets.DateTimeDate( style,
                                                   monthNames,
                                                   weekdayNames,
                                                   weekdayShortNames,
                                                   dateSeparator,
                                                   datePattern);
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DateTime" );
      ObjectManager.add( "w3", dateTime, handler );
      dateTime.setSpace( 3, 115, 3, 20 );
      dateTime.addToDocument();
      TestUtil.flush();
      return dateTime;
    }

  }

} );

}() );
