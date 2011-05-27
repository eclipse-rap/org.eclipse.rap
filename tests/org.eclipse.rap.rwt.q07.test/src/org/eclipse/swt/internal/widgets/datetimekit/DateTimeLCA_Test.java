/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class DateTimeLCA_Test extends TestCase {

  public void testDateTimeDatePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.DATE
                                           | SWT.MEDIUM
                                           | SWT.DROP_DOWN );
    dateTime.setDay( 1 );
    dateTime.setMonth( 1 );
    dateTime.setYear( 2008 );
    Fixture.markInitialized( display );
    // Test preserved day, month, year
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    Integer day = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_DAY );
    assertEquals( 1, day.intValue() );
    Integer month = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_MONTH );
    assertEquals( 1, month.intValue() );
    Integer year = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_YEAR );
    assertEquals( 2008, year.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
    // Test preserved sub widgets bounds
    IDateTimeAdapter dtAdapter
      = DateTimeLCAUtil.getDateTimeAdapter( dateTime );
    Fixture.preserveWidgets();
    String propName = IDateTimeAdapter.WEEKDAY_TEXTFIELD + "_BOUNDS";
    Rectangle bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.WEEKDAY_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.DAY_TEXTFIELD + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.DAY_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.MONTH_TEXTFIELD + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.MONTH_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.YEAR_TEXTFIELD + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.YEAR_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.WEEKDAY_MONTH_SEPARATOR + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.WEEKDAY_MONTH_SEPARATOR ),
                  bounds );
    propName = IDateTimeAdapter.MONTH_DAY_SEPARATOR + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.MONTH_DAY_SEPARATOR ),
                  bounds );
    propName = IDateTimeAdapter.DAY_YEAR_SEPARATOR + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.DAY_YEAR_SEPARATOR ),
                  bounds );
    propName = IDateTimeAdapter.SPINNER + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.SPINNER ),
                  bounds );
    propName = IDateTimeAdapter.DROP_DOWN_BUTTON + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.DROP_DOWN_BUTTON ),
                  bounds );
    Fixture.clearPreserved();
    // Test preserved selection listeners
    testPreserveSelectionListener( dateTime );
  }

  public void testDateTimeTimePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    dateTime.setHours( 1 );
    dateTime.setMinutes( 2 );
    dateTime.setSeconds( 3 );
    Fixture.markInitialized( display );
    // Test preserved hours, minutes, seconds
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    Integer hours
      = ( Integer )adapter.getPreserved( DateTimeTimeLCA.PROP_HOURS );
    assertEquals( 1, hours.intValue() );
    Integer minutes
      = ( Integer )adapter.getPreserved( DateTimeTimeLCA.PROP_MINUTES );
    assertEquals( 2, minutes.intValue() );
    Integer seconds
      = ( Integer )adapter.getPreserved( DateTimeTimeLCA.PROP_SECONDS );
    assertEquals( 3, seconds.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
    // Test preserved sub widgets bounds
    IDateTimeAdapter dtAdapter = DateTimeLCAUtil.getDateTimeAdapter( dateTime );
    Fixture.preserveWidgets();
    String propName = IDateTimeAdapter.HOURS_TEXTFIELD + "_BOUNDS";
    Rectangle bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.HOURS_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.MINUTES_TEXTFIELD + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.MINUTES_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.SECONDS_TEXTFIELD + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.SECONDS_TEXTFIELD ),
                  bounds );
    propName = IDateTimeAdapter.HOURS_MINUTES_SEPARATOR + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.HOURS_MINUTES_SEPARATOR ),
                  bounds );
    propName = IDateTimeAdapter.MINUTES_SECONDS_SEPARATOR + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.MINUTES_SECONDS_SEPARATOR ),
                  bounds );
    propName = IDateTimeAdapter.SPINNER + "_BOUNDS";
    bounds = ( Rectangle )adapter.getPreserved( propName );
    assertEquals( dtAdapter.getBounds( IDateTimeAdapter.SPINNER ), bounds );
    Fixture.clearPreserved();
    // Test preserved selection listeners
    testPreserveSelectionListener( dateTime );
  }

  public void testDateTimeCalendarPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );
    dateTime.setDay( 1 );
    dateTime.setMonth( 1 );
    dateTime.setYear( 2008 );
    Fixture.markInitialized( display );
    // Test preserved day, month, year
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    Integer day = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_DAY );
    assertEquals( 1, day.intValue() );
    Integer month = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_MONTH );
    assertEquals( 1, month.intValue() );
    Integer year = ( Integer )adapter.getPreserved( DateTimeDateLCA.PROP_YEAR );
    assertEquals( 2008, year.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
    // Test preserved selection listeners
    testPreserveSelectionListener( dateTime );
  }

  public void testSelectionEvent() {
    // Date
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    testSelectionEvent( dateTime );
    // Time
    dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    testSelectionEvent( dateTime );
  }
  
  // 315950: [DateTime] method getDay() return wrong day in particular
  // circumstances
  public void testDateTimeDate_Bug315950() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.DATE
                                           | SWT.MEDIUM
                                           | SWT.DROP_DOWN );
    dateTime.setDay( 15 );
    dateTime.setMonth( 5 );
    dateTime.setYear( 2010 );
    Fixture.markInitialized( display );
    // Test preserved day, month, year
    Fixture.preserveWidgets();
    String dateTimeId = WidgetUtil.getId( dateTime );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, dateTimeId );
    Fixture.fakeRequestParam( dateTimeId + ".day", "31" );
    Fixture.fakeRequestParam( dateTimeId + ".month", "4" );
    Fixture.fakeRequestParam( dateTimeId + ".year", "2010" );
    Fixture.readDataAndProcessAction( dateTime );
    assertEquals( 31, dateTime.getDay() );
    assertEquals( 4, dateTime.getMonth() );
    assertEquals( 2010, dateTime.getYear() );
  }

  private void testPreserveControlProperties( final DateTime dateTime ) {
    // control: enabled
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    dateTime.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    dateTime.setEnabled( true );
    // visible
    dateTime.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    dateTime.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( dateTime );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    dateTime.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    dateTime.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    dateTime.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    dateTime.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
  }

  private void testPreserveSelectionListener( final DateTime dateTime ) {
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    Boolean hasListeners
      = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    dateTime.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( dateTime );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  private void testSelectionEvent( final DateTime dateTime ) {
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        assertEquals( dateTime, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    };
    dateTime.addSelectionListener( selectionListener );
    String dateTimeId = WidgetUtil.getId( dateTime );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, dateTimeId );
    Fixture.readDataAndProcessAction( dateTime );
    assertEquals( "widgetSelected", log.toString() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
