/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeCalendarTest", {

  extend : rwt.qx.Object,

  members : {

    monthNames : [ "January",
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
                   "" ],
    weekdayShortNames : [ "",
                          "Sun",
                          "Mon",
                          "Tue",
                          "Wed",
                          "Thu",
                          "Fri",
                          "Sat" ],

    testCreateDateTimeCalendarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      assertTrue( widget instanceof rwt.widgets.DateTimeCalendar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-calendar", widget.getAppearance() );
      assertEquals( 34, rwt.widgets.base.Calendar.CELL_WIDTH );
      assertEquals( 19, rwt.widgets.base.Calendar.CELL_HEIGHT );
      assertEquals( this.monthNames, rwt.widgets.base.Calendar.MONTH_NAMES );
      assertEquals( this.weekdayShortNames, rwt.widgets.base.Calendar.WEEKDAY_NAMES );
      shell.destroy();
      widget.destroy();
    },

    testSetYearByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "year" : 2000 } );
      assertEquals( 2000, widget._calendar.getDate().getFullYear() );
      shell.destroy();
      widget.destroy();
    },

    testSetMonthByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "month" : 6 } );
      assertEquals( 6, widget._calendar.getDate().getMonth() );
      shell.destroy();
      widget.destroy();
    },

    testSetDayByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "day" : 10 } );
      assertEquals( 10, widget._calendar.getDate().getDate() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    // see bug 401780
    testKeypressEscape : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2" );
      TestUtil.flush();
      widget.setFocused( true );

      TestUtil.pressOnce( widget.getElement(), "Escape", 0 );

      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      rwt.widgets.base.Calendar.CELL_WIDTH = 24;
      rwt.widgets.base.Calendar.CELL_HEIGHT = 16;
      var months = [
        "Januar",
        "Februar",
        "März",
        "April",
        "Mai",
        "Juni",
        "Juli",
        "August",
        "September",
        "Oktober",
        "November",
        "Dezember",
        ""
      ];
      var days = [ "", "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa" ];
      var calendar = new rwt.widgets.DateTimeCalendar( "medium", months, days );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DateTime" );
      ObjectRegistry.add( "w3", calendar, handler );
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

    //////////
    // Helpers

    _createDefaultDateTimeByProtocol : function( id, parentId ) {
      var styles =  [ "CALENDAR", "MEDIUM" ];
      MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "cellSize" : [ 34, 19 ],
          "monthNames" : this.monthNames,
          "weekdayShortNames" : this.weekdayShortNames
        }
      } );
      return ObjectRegistry.getObject( "w3" );
    }

  }

} );

}());
