/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class DateTimeLCA_Test {

  private Display display;
  private Shell shell;
  private DateTimeLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new DateTimeLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( dateTime );
    ControlLCATestUtil.testFocusListener( dateTime );
    ControlLCATestUtil.testMouseListener( dateTime );
    ControlLCATestUtil.testKeyListener( dateTime );
    ControlLCATestUtil.testTraverseListener( dateTime );
    ControlLCATestUtil.testMenuDetectListener( dateTime );
    ControlLCATestUtil.testHelpListener( dateTime );
  }

  @Test
  public void testDateTimeDatePreserveValues() {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN );
    dateTime.setDay( 1 );
    dateTime.setMonth( 1 );
    dateTime.setYear( 2008 );
    Fixture.markInitialized( display );
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
  }

  @Test
  public void testDateTimeTimePreserveValues() {
    DateTime dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    dateTime.setHours( 1 );
    dateTime.setMinutes( 2 );
    dateTime.setSeconds( 3 );
    Fixture.markInitialized( display );
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
  }

  @Test
  public void testDateTimeCalendarPreserveValues() {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );
    dateTime.setDay( 1 );
    dateTime.setMonth( 1 );
    dateTime.setYear( 2008 );
    Fixture.markInitialized( display );
    // Test preserved control properties
    testPreserveControlProperties( dateTime );
  }

  @Test
  public void testSelectionEvent_Date() {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    fakeAndCheckSelectionEvent( dateTime );
  }

  @Test
  public void testSelectionEvent_Time() {
    DateTime dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    fakeAndCheckSelectionEvent( dateTime );
  }

  // 315950: [DateTime] method getDay() return wrong day in particular
  // circumstances
  @Test
  public void testDateTimeDate_Bug315950() {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN );
    dateTime.setDay( 15 );
    dateTime.setMonth( 5 );
    dateTime.setYear( 2010 );
    Fixture.markInitialized( display );
    // Test preserved day, month, year
    Fixture.preserveWidgets();

    Fixture.fakeNotifyOperation( getId( dateTime ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.fakeSetParameter( getId( dateTime ), "day", Integer.valueOf( 31 ) );
    Fixture.fakeSetParameter( getId( dateTime ), "month", Integer.valueOf( 4 ) );
    Fixture.fakeSetParameter( getId( dateTime ), "year", Integer.valueOf( 2010 ) );
    Fixture.readDataAndProcessAction( dateTime );

    assertEquals( 31, dateTime.getDay() );
    assertEquals( 4, dateTime.getMonth() );
    assertEquals( 2010, dateTime.getYear() );
  }

  private void testPreserveControlProperties( DateTime dateTime ) {
    // control: enabled
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
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

  private void fakeAndCheckSelectionEvent( DateTime dateTime ) {
    SelectionListener listener = mock( SelectionListener.class );
    dateTime.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( dateTime ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( dateTime );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( dateTime, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
  }

  @Test
  public void testRenderCreateDate() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.SHORT | SWT.DROP_DOWN );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertEquals( "rwt.widgets.DateTime", operation.getType() );
    java.util.List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "DATE" ) );
    assertTrue( styles.contains( "SHORT" ) );
    assertTrue( styles.contains( "DROP_DOWN" ) );
  }

  @Test
  public void testRenderCreateDate_InitalParameters() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    java.util.List<String> propertyNames = operation.getPropertyNames();
    assertTrue( propertyNames.contains( "cellSize" ) );
    assertTrue( propertyNames.contains( "monthNames" ) );
    assertTrue( propertyNames.contains( "weekdayNames" ) );
    assertTrue( propertyNames.contains( "weekdayShortNames" ) );
    assertTrue( propertyNames.contains( "dateSeparator" ) );
    assertTrue( propertyNames.contains( "datePattern" ) );
  }

  @Test
  public void testRenderCreateTime() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertEquals( "rwt.widgets.DateTime", operation.getType() );
    java.util.List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "TIME" ) );
    assertTrue( styles.contains( "MEDIUM" ) );
  }

  @Test
  public void testRenderCreateCalendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR | SWT.LONG );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertEquals( "rwt.widgets.DateTime", operation.getType() );
    java.util.List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "CALENDAR" ) );
    assertTrue( styles.contains( "LONG" ) );
  }

  @Test
  public void testRenderCreateCalendar_InitalParameters() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    java.util.List<String> propertyNames = operation.getPropertyNames();
    assertTrue( propertyNames.contains( "cellSize" ) );
    assertTrue( propertyNames.contains( "monthNames" ) );
    assertTrue( propertyNames.contains( "weekdayShortNames" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );

    lca.renderInitialization( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertEquals( WidgetUtil.getId( dateTime.getParent() ), operation.getParent() );
  }


  @Test
  public void testRenderAddSelectionListener() throws Exception {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );
    Fixture.preserveWidgets();

    dateTime.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( dateTime, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    SelectionListener listener = new SelectionAdapter() { };
    dateTime.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );
    Fixture.preserveWidgets();

    dateTime.removeSelectionListener( listener );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( dateTime, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );
    Fixture.preserveWidgets();

    dateTime.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( dateTime, "selection" ) );
  }

  @Test
  public void testRenderInitialYear() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "year" ) );
  }

  @Test
  public void testRenderYear() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    dateTime.setYear( 2000 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2000 ), message.findSetProperty( dateTime, "year" ) );
  }

  @Test
  public void testRenderYearUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setYear( 2000 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "year" ) );
  }

  @Test
  public void testRenderInitialMonth() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "month" ) );
  }

  @Test
  public void testRenderMonth() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    // Note: we have to use a month with 31 days, otherwise it is rejected on a 31th
    dateTime.setMonth( 2 ); // 2 == March!
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( dateTime, "month" ) );
  }

  @Test
  public void testRenderMonthUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    // Note: we have to use a month with 31 days, otherwise it is rejected on a 31th
    dateTime.setMonth( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "month" ) );
  }

  @Test
  public void testRenderInitialDay() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "day" ) );
  }

  @Test
  public void testRenderDay() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    dateTime.setDay( 3 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 3 ), message.findSetProperty( dateTime, "day" ) );
  }

  @Test
  public void testRenderDayUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setDay( 3 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "day" ) );
  }

  @Test
  public void testRenderInitialSubWidgetsBounds_Date() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "subWidgetsBounds" ) );
  }

  @Test
  public void testRenderSubWidgetsBounds_Date() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );

    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( dateTime, "subWidgetsBounds" );
    assertEquals( 9, actual.length() );
  }

  @Test
  public void testRenderSubWidgetsBoundsUnchanged_Date() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.DATE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "subWidgetsBounds" ) );
  }

  @Test
  public void testRenderInitialHours() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "hours" ) );
  }

  @Test
  public void testRenderHours() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    dateTime.setHours( 10 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( dateTime, "hours" ) );
  }

  @Test
  public void testRenderHoursUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setHours( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "hours" ) );
  }

  @Test
  public void testRenderInitialMinutes() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "minutes" ) );
  }

  @Test
  public void testRenderMinutes() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    dateTime.setMinutes( 10 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( dateTime, "minutes" ) );
  }

  @Test
  public void testRenderMinutesUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setMinutes( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "minutes" ) );
  }

  @Test
  public void testRenderInitialSeconds() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "seconds" ) );
  }

  @Test
  public void testRenderSeconds() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    dateTime.setSeconds( 10 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( dateTime, "seconds" ) );
  }

  @Test
  public void testRenderSecondsUnchanged() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setSeconds( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "seconds" ) );
  }

  @Test
  public void testRenderInitialSubWidgetsBounds_Time() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "subWidgetsBounds" ) );
  }

  @Test
  public void testRenderSubWidgetsBounds_Time() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );

    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( dateTime, "subWidgetsBounds" );
    assertEquals( 6, actual.length() );
  }

  @Test
  public void testRenderSubWidgetsBoundsUnchanged_Time() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.TIME );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "subWidgetsBounds" ) );
  }

  @Test
  public void testRenderInitialYear_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "year" ) );
  }

  @Test
  public void testRenderYear_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    dateTime.setYear( 2000 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2000 ), message.findSetProperty( dateTime, "year" ) );
  }

  @Test
  public void testRenderYearUnchanged_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setYear( 2000 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "year" ) );
  }

  @Test
  public void testRenderInitialMonth_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "month" ) );
  }

  @Test
  public void testRenderMonth_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    // Note: we have to use a month with 31 days, otherwise it is rejected on a 31th
    dateTime.setMonth( 2 ); // 2 == March!
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( dateTime, "month" ) );
  }

  @Test
  public void testRenderMonthUnchanged_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    // Note: we have to use a month with 31 days, otherwise it is rejected on a 31th
    dateTime.setMonth( 2 ); // 2 == March!
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "month" ) );
  }

  @Test
  public void testRenderInitialDay_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    lca.render( dateTime );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( dateTime );
    assertTrue( operation.getPropertyNames().contains( "day" ) );
  }

  @Test
  public void testRenderDay_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );

    dateTime.setDay( 3 );
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 3 ), message.findSetProperty( dateTime, "day" ) );
  }

  @Test
  public void testRenderDayUnchanged_Calendar() throws IOException {
    DateTime dateTime = new DateTime( shell, SWT.CALENDAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( dateTime );

    dateTime.setDay( 3 );
    Fixture.preserveWidgets();
    lca.renderChanges( dateTime );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dateTime, "day" ) );
  }
}
