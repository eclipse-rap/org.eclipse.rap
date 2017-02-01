/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
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

var shell;

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
var weekdayShortNames = [ "",
                          "Sun",
                          "Mon",
                          "Tue",
                          "Wed",
                          "Thu",
                          "Fri",
                          "Sat" ];

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeCalendarTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
    },

    tearDown : function() {
      shell.destroy();
    },

    testCreateDateTimeCalendarByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      assertTrue( widget instanceof rwt.widgets.DateTimeCalendar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-calendar", widget.getAppearance() );
      assertEquals( 34, rwt.widgets.base.Calendar.CELL_WIDTH );
      assertEquals( 19, rwt.widgets.base.Calendar.CELL_HEIGHT );
      assertEquals( monthNames, rwt.widgets.base.Calendar.MONTH_NAMES );
      assertEquals( weekdayShortNames, rwt.widgets.base.Calendar.WEEKDAY_NAMES );
      widget.destroy();
    },

    testSetYearByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "year" : 2000 } );
      assertEquals( 2000, widget._calendar.getDate().getFullYear() );
      widget.destroy();
    },

    testSetMonthByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "month" : 6 } );
      assertEquals( 6, widget._calendar.getDate().getMonth() );
      widget.destroy();
    },

    testSetDayByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "day" : 10 } );
      assertEquals( 10, widget._calendar.getDate().getDate() );
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      var date = new Date( 1999, 0, 1, 0, 0, 0, 0 );
      TestUtil.protocolSet( "w3", { "minimum" : date.getTime() } );
      assertEquals( date.getTime(), widget._calendar.getMinimum().getTime() );
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      var date = new Date( 2050, 0, 1, 0, 0, 0, 0 );
      TestUtil.protocolSet( "w3", { "maximum" : date.getTime() } );
      assertEquals( date.getTime(), widget._calendar.getMaximum().getTime() );
      widget.destroy();
    },

    // see bug 401780
    testKeypressEscape : function() {
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.flush();
      widget.setFocused( true );

      TestUtil.pressOnce( widget.getElement(), "Escape", 0 );

      widget.destroy();
    },

    testSendDate_allFieldsTogether : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.press( dateTime, "Right" );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( 11, message.findSetProperty( "w3", "day" ) );
      assertEquals( 10, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testDateChange_withKeyboard_right : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.press( dateTime, "Right" );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( 11, message.findSetProperty( "w3", "day" ) );
      assertEquals( 10, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testDateChange_withKeyboard_left : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.press( dateTime, "Left" );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( 9, message.findSetProperty( "w3", "day" ) );
      assertEquals( 10, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testDateChange_withKeyboard_right_RTL : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDirection( "rtl" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.press( dateTime, "Right" );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( 9, message.findSetProperty( "w3", "day" ) );
      assertEquals( 10, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testDateChange_withKeyboard_left_RTL : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDirection( "rtl" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.press( dateTime, "Left" );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertEquals( 11, message.findSetProperty( "w3", "day" ) );
      assertEquals( 10, message.findSetProperty( "w3", "month" ) );
      assertEquals( 2010, message.findSetProperty( "w3", "year" ) );
      dateTime.destroy();
    },

    testDateChanged_onNextMonthButtonClick : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.click( dateTime._calendar._nextMonthBt );

      var date = dateTime._calendar.getDate();
      assertEquals( 1, date.getDate() );
      assertEquals( 11, date.getMonth() );
      assertEquals( 2010, date.getFullYear() );
      dateTime.destroy();
    },

    testDateChanged_onLastMonthButtonClick : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.click( dateTime._calendar._lastMonthBt );

      var date = dateTime._calendar.getDate();
      assertEquals( 1, date.getDate() );
      assertEquals( 9, date.getMonth() );
      assertEquals( 2010, date.getFullYear() );
      dateTime.destroy();
    },

    testDateChanged_onNextYearButtonClick : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.click( dateTime._calendar._nextYearBt );

      var date = dateTime._calendar.getDate();
      assertEquals( 1, date.getDate() );
      assertEquals( 10, date.getMonth() );
      assertEquals( 2011, date.getFullYear() );
      dateTime.destroy();
    },

    testDateChanged_onLastYearButtonClick : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.flush();

      TestUtil.click( dateTime._calendar._lastYearBt );

      var date = dateTime._calendar.getDate();
      assertEquals( 1, date.getDate() );
      assertEquals( 10, date.getMonth() );
      assertEquals( 2009, date.getFullYear() );
      dateTime.destroy();
    },

    testSendSelectionEvent : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.fakeListener( dateTime, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.flush();

      TestUtil.press( dateTime, "Up" );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
      dateTime.destroy();
    },

    testSendSelectionEvent_onNavButtonClick : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.fakeListener( dateTime, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.flush();

      TestUtil.click( dateTime._calendar._nextMonthBt );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
      dateTime.destroy();
    },

    testSendDefaultSelectionEvent : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.fakeListener( dateTime, "DefaultSelection", true );
      TestUtil.clearRequestLog();
      TestUtil.flush();

      TestUtil.press( dateTime, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      dateTime.destroy();
    },

    testSetCustomVariant : function() {
      var dateTime = this._createDefaultDateTimeByProtocol( "w3", "w2" );

      dateTime.setCustomVariant( "foo" );

      assertEquals( "foo", dateTime._calendar._customVariant );
      dateTime.destroy();
    },

    testCreateDispose : function() {
      rwt.widgets.base.Calendar.CELL_WIDTH = 24;
      rwt.widgets.base.Calendar.CELL_HEIGHT = 16;
      var calendar = new rwt.widgets.DateTimeCalendar( "medium", monthNames, weekdayShortNames );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DateTime" );
      ObjectManager.add( "w3", calendar, handler );
      calendar.addToDocument();
      calendar.setSpace( 3, 194, 3, 138 );
      calendar.setZIndex( 300 );
      calendar.setYear( 2010 );
      calendar.setMonth( 9 );
      calendar.setDay( 27 );
      TestUtil.flush();
      assertTrue( calendar.isSeeable() );
      calendar.destroy();
      TestUtil.flush();
      assertTrue( calendar.isDisposed() );
    },

    testSetDirection : function() {
      var dateTime = new rwt.widgets.DateTimeCalendar( "medium", monthNames, weekdayShortNames );

      dateTime.setDirection( "rtl" );
      TestUtil.flush();

      assertEquals( "rtl", dateTime._calendar.getDirection() );
      assertEquals( "right", dateTime._calendar._navBar.getHorizontalChildrenAlign() );
      assertTrue( dateTime._calendar._navBar.getReverseChildrenOrder() );
      assertEquals( "rtl", dateTime._calendar._lastYearBt.getDirection() );
      assertEquals( "rtl", dateTime._calendar._lastMonthBt.getDirection() );
      assertEquals( "rtl", dateTime._calendar._nextMonthBt.getDirection() );
      assertEquals( "rtl", dateTime._calendar._nextYearBt.getDirection() );
      dateTime.destroy();
    },

    //////////
    // Helpers

    _createDefaultDateTimeByProtocol : function( id, parentId ) {
      var styles =  [ "CALENDAR", "MEDIUM" ];
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "cellSize" : [ 34, 19 ],
          "monthNames" : monthNames,
          "weekdayShortNames" : weekdayShortNames
        }
      } );
      return ObjectManager.getObject( "w3" );
    }

  }

} );

}() );
