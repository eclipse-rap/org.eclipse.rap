/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeTimeTest", {
  extend : qx.core.Object,

  members : {

    testCreateDateTimeDate : function() {
      var dateTime = this._createDefaultDateTimeTime();
      assertTrue( dateTime instanceof org.eclipse.swt.widgets.DateTimeTime );
      assertEquals( "datetime-time", dateTime.getAppearance() );
      dateTime.destroy();
    },

    testSendAllFieldsTogether : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHours( 4 );
      dateTime.setMinutes( 34 );
      dateTime.setSeconds( 55 );
      testUtil.clearRequestLog();
      dateTime._sendChanges();
      assertEquals( 0, testUtil.getRequestsSend() );
      var req = org.eclipse.swt.Request.getInstance();
      assertEquals( 4, req._parameters[ "w3.hours" ] );
      assertEquals( 34, req._parameters[ "w3.minutes" ] );
      assertEquals( 55, req._parameters[ "w3.seconds" ] );
    },

    testSendEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTimeTime();
      dateTime.setHasSelectionListener( true );
      dateTime.setHours( 4 );
      dateTime.setMinutes( 34 );
      dateTime.setSeconds( 55 );
      testUtil.clearRequestLog();
      dateTime._sendChanges();
      assertEquals( 1, testUtil.getRequestsSend() );
    },
    

    //////////
    // Helpers

    _createDefaultDateTimeTime : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = "medium";
      var dateTime = new org.eclipse.swt.widgets.DateTimeTime( style );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( dateTime, "w3", true );
      dateTime.setSpace( 3, 115, 3, 20 );
      dateTime.addToDocument();
      testUtil.flush();
      return dateTime;
    }

  }

} );
