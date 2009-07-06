/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

public class DateTime_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInvalidValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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

  public void testSetDate() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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

  public void testSetTime() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    DateTime dateTime = new DateTime( shell, SWT.NONE );

    dateTime.setTime(2, 10, 30);
    assertEquals( 2, dateTime.getHours() );
    assertEquals( 10, dateTime.getMinutes() );
    assertEquals( 30, dateTime.getSeconds() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    dateTime = new DateTime( shell, SWT.DATE
                                  | SWT.MEDIUM
                                  | SWT.SHORT
                                  | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) == 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE
                                  | SWT.MEDIUM
                                  | SWT.SHORT );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) == 0 );
    dateTime = new DateTime( shell, SWT.DATE
                                  | SWT.MEDIUM
                                  | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.DATE ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
    dateTime = new DateTime( shell, SWT.TIME
                                  | SWT.SHORT
                                  | SWT.LONG );
    assertTrue( ( dateTime.getStyle() & SWT.TIME ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.SHORT ) != 0 );
    assertTrue( ( dateTime.getStyle() & SWT.LONG ) == 0 );
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display );
    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    dateTime.dispose();
    assertTrue( dateTime.isDisposed() );
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    // The component computeSize depends on day/months names
    // which are locale dependent
    RWT.setLocale( Locale.US );

    DateTime dateTime = new DateTime( shell, SWT.DATE | SWT.SHORT );
    Point expected = new Point( 107, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.SHORT | SWT.BORDER );
    expected = new Point( 109, 23 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.MEDIUM );
    expected = new Point( 90, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.DATE | SWT.LONG );
    expected = new Point( 185, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.SHORT );
    expected = new Point( 58, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.MEDIUM );
    expected = new Point( 80, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.TIME | SWT.LONG );
    expected = new Point( 80, 21 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    dateTime = new DateTime( shell, SWT.CALENDAR );
    expected = new Point( 192, 136 );
    assertEquals( expected, dateTime.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, dateTime.computeSize( 100, 100 ) );
  }
}
