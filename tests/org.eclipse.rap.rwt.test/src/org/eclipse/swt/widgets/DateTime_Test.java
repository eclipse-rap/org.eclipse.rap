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
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DateTime_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInvalidValues() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );
    dateTime.setDay( 1 );
    dateTime.setMonth( 0 );
    dateTime.setYear( 2008 );
    dateTime.setHours( 0 );
    dateTime.setMinutes( 0 );
    dateTime.setSeconds( 0 );
    assertEquals( 1, dateTime.getDay() );
    assertEquals( 0, dateTime.getMonth() );
    assertEquals( 2008, dateTime.getYear() );
    assertEquals( 0, dateTime.getHours() );
    assertEquals( 0, dateTime.getMinutes() );
    assertEquals( 0, dateTime.getSeconds() );
    // Test day
    dateTime.setDay( 61 );
    assertEquals( 1, dateTime.getDay() );
    dateTime.setDay( 0 );
    assertEquals( 1, dateTime.getDay() );
    dateTime.setDay( -5 );
    assertEquals( 1, dateTime.getDay() );
    dateTime.setMonth( 1 );
    dateTime.setDay( 29 );
    assertEquals( 29, dateTime.getDay() );
    dateTime.setDay( 30 );
    assertEquals( 29, dateTime.getDay() );
    // Test month
    dateTime.setMonth( 12 );
    assertEquals( 1, dateTime.getMonth() );
    dateTime.setMonth( -5 );
    assertEquals( 1, dateTime.getMonth() );
    dateTime.setMonth( 0 );
    dateTime.setDay( 31 );
    dateTime.setMonth( 1 );
    assertEquals( 0, dateTime.getMonth() );
    // Test year
    dateTime.setYear( 12345 );
    assertEquals( 2008, dateTime.getYear() );
    dateTime.setYear( 123 );
    assertEquals( 2008, dateTime.getYear() );
    dateTime.setDay( 29 );
    dateTime.setMonth( 1 );
    dateTime.setYear( 2007 );
    assertEquals( 2008, dateTime.getYear() );
    // Test hours
    dateTime.setHours( 24 );
    assertEquals( 0, dateTime.getHours() );
    dateTime.setHours( -3 );
    assertEquals( 0, dateTime.getHours() );
    // Test minutes
    dateTime.setMinutes( 65 );
    assertEquals( 0, dateTime.getMinutes() );
    dateTime.setMinutes( -7 );
    assertEquals( 0, dateTime.getMinutes() );
    // Test seconds
    dateTime.setSeconds( 89 );
    assertEquals( 0, dateTime.getSeconds() );
    dateTime.setSeconds( -1 );
    assertEquals( 0, dateTime.getSeconds() );
    // Test date
    dateTime.setDate( 2009, 5, 6 );
    dateTime.setDate( 2008, 1, 30 );
    assertEquals( 6, dateTime.getDay() );
    assertEquals( 5, dateTime.getMonth() );
    assertEquals( 2009, dateTime.getYear() );
    // Test time
    dateTime.setTime( 12, 14, 16 );
    dateTime.setTime( 23, 76, 15 );
    assertEquals( 12, dateTime.getHours() );
    assertEquals( 14, dateTime.getMinutes() );
    assertEquals( 16, dateTime.getSeconds() );
  }

  @Test
  public void testSetDate() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    dateTime.setDate( 1985, 10, 29 );
    assertEquals( 29, dateTime.getDay() );
    assertEquals( 10, dateTime.getMonth() );
    assertEquals( 1985, dateTime.getYear() );

    dateTime.setDate( 2008, 1, 29 );
    assertEquals( 29, dateTime.getDay() );
    assertEquals( 1, dateTime.getMonth() );
    assertEquals( 2008, dateTime.getYear() );
  }

  @Test
  public void testSetTime() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    dateTime.setTime(2, 10, 30);
    assertEquals( 2, dateTime.getHours() );
    assertEquals( 10, dateTime.getMinutes() );
    assertEquals( 30, dateTime.getSeconds() );
  }

  @Test
  public void testStyle() {
    // Test SWT.NONE
    DateTime dateTime = new DateTime( shell, SWT.NONE );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    // Test SWT.BORDER
    dateTime = new DateTime( shell, SWT.BORDER );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.BORDER ) != 0 );
    // Test combination of SWT.DATE | SWT.TIME | SWT.CALENDAR
    dateTime = new DateTime( shell, SWT.DATE | SWT.TIME | SWT.CALENDAR );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.TIME ) == 0 );
    assertTrue( ( dateTime.getStyle() & SWT.CALENDAR ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE | SWT.TIME );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.TIME ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE | SWT.CALENDAR );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.CALENDAR ) == 0 );
    dateTime = new DateTime( shell, SWT.TIME | SWT.CALENDAR );
    assertTrue( ( dateTime.getStyle() & SWT.TIME ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.CALENDAR ) == 0 );
    dateTime = new DateTime( shell, SWT.CALENDAR );
    assertTrue( ( dateTime.getStyle() & SWT.CALENDAR ) != 0 );
    // Test combination of SWT.MEDIUM | SWT.SHORT | SWT.LONG
    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.SHORT | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) == 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.SHORT );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
    dateTime = new DateTime( shell, SWT.TIME | SWT.SHORT | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.TIME ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
    // Test SWT.DROP_DOWN
    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN );
    assertTrue( ( dateTime.getStyle() & SWT.DROP_DOWN ) != 0 );
    dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM | SWT.DROP_DOWN );
    assertTrue( ( dateTime.getStyle() & SWT.DROP_DOWN ) == 0 );
    dateTime = new DateTime( shell, SWT.CALENDAR | SWT.MEDIUM | SWT.DROP_DOWN );
    assertTrue( ( dateTime.getStyle() & SWT.DROP_DOWN ) == 0 );
  }

  @Test
  public void testDispose() {
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    dateTime.dispose();
    assertTrue( dateTime.isDisposed() );
  }

  @Test
  public void testComputeSize() {
    // The component computeSize depends on day/months names
    // which are locale dependent
    RWT.setLocale( Locale.US );

    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.SHORT );
    Point expected = new Point( 145, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.SHORT | SWT.BORDER );
    expected = new Point( 147, 30 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    expected = new Point( 122, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.LONG );
    expected = new Point( 239, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.SHORT );
    expected = new Point( 82, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    expected = new Point( 108, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.LONG );
    expected = new Point( 108, 28 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.CALENDAR );
    expected = new Point( 192, 150 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, dateTime.computeSize( 100, 100 ) );
  }

  @Test
  public void testDateIsSerializable() throws Exception {
    DateTime dateTime = new DateTime( shell, SWT.DATE );
    dateTime.setDate( 2000, 1, 1 );

    DateTime deserializedDateTime = Fixture.serializeAndDeserialize( dateTime );

    assertEquals( 1, deserializedDateTime.getDay() );
    assertEquals( 1, deserializedDateTime.getMonth() );
    assertEquals( 2000, deserializedDateTime.getYear() );
  }

  @Test
  public void testTimeIsSerializable() throws Exception {
    DateTime dateTime = new DateTime( shell, SWT.TIME );
    dateTime.setTime( 12, 12, 12 );

    DateTime deserializedDateTime = Fixture.serializeAndDeserialize( dateTime );

    assertEquals( 12, deserializedDateTime.getHours() );
    assertEquals( 12, deserializedDateTime.getMinutes() );
    assertEquals( 12, deserializedDateTime.getSeconds() );
  }

  @Test
  public void testAddSelectionListener() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    dateTime.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( dateTime.isListening( SWT.Selection ) );
    assertTrue( dateTime.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    dateTime.addSelectionListener( listener );

    dateTime.removeSelectionListener( listener );

    assertFalse( dateTime.isListening( SWT.Selection ) );
    assertFalse( dateTime.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    try {
      dateTime.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    try {
      dateTime.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
}
