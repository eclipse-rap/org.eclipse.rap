/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeCalendarTest", {

  extend : qx.core.Object,

  members : {

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      org.eclipse.swt.widgets.Calendar.CELL_WIDTH = 24;
      org.eclipse.swt.widgets.Calendar.CELL_HEIGHT = 16;
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
      var calendar = new org.eclipse.swt.widgets.DateTimeCalendar( "medium",
                                                                   months,
                                                                   days );
      calendar.addToDocument();
      calendar.setSpace( 3, 194, 3, 138 );
      calendar.setZIndex( 300 );
      calendar.setYear( 2010 );
      calendar.setMonth( 9 );
      calendar.setDay( 27 );
      testUtil.flush();
      assertTrue( calendar.isSeeable() );
      calendar.destroy();
      testUtil.flush();
      assertTrue( calendar.isDisposed() );
    }

  }

} );