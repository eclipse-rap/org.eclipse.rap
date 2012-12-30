/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Spinner_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 0, spinner.getBorderWidth() );

    spinner = new Spinner( shell, SWT.BORDER );
    assertTrue( ( spinner.getStyle() & SWT.BORDER ) != 0 );
    assertEquals( 1, spinner.getBorderWidth() );

    spinner = new Spinner( shell, SWT.READ_ONLY );
    assertTrue( ( spinner.getStyle() & SWT.READ_ONLY ) != 0 );
  }

  @Test
  public void testMinMax() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    // it is allowed to set min and max to the same value
    spinner.setMinimum( 1 );
    spinner.setMaximum( 1 );
    assertEquals( spinner.getMinimum(), spinner.getMaximum() );
    assertEquals( 1, spinner.getSelection() );

    // ignore when min is set to a value greater than max
    spinner.setMinimum( 1 );
    spinner.setMaximum( 100 );
    spinner.setMinimum( 2000 );
    assertEquals( 1, spinner.getMinimum() );

    // ignore when max is set to a value less than min
    spinner.setMinimum( 1 );
    spinner.setMaximum( 100 );
    spinner.setMaximum( -200 );
    assertEquals( 100, spinner.getMaximum() );

    // it is allowed to set min and max to negative values
    spinner.setMinimum( -100 );
    spinner.setMaximum( -50 );
    assertEquals( -100, spinner.getMinimum() );
    assertEquals( -50, spinner.getMaximum() );
  }

  @Test
  public void testIncrementAndPageIncrement() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    // ignore illegal values
    spinner.setIncrement( 0 );
    assertEquals( 1, spinner.getIncrement() );
    spinner.setIncrement( -1 );
    assertEquals( 1, spinner.getIncrement() );
    spinner.setPageIncrement( 0 );
    assertEquals( 10, spinner.getPageIncrement() );
    spinner.setIncrement( -1 );
    assertEquals( 10, spinner.getPageIncrement() );
  }

  @Test
  public void testModifyAndSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuilder log = new StringBuilder();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Spinner spinner = new Spinner( shell, SWT.NONE );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        assertSame( spinner, event.getSource() );
        log.append( "modifyEvent" );
      }
    } );
    // Changing the selection programmatically never triggers a selection event
    spinner.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        assertSame( spinner, event.getSource() );
        log.append( "selectionEvent" );
      }
    } );
    // Changing the selection causes a modifyEvent
    spinner.setSelection( spinner.getSelection() + 1 );
    assertEquals( "modifyEvent", log.toString() );
    // Setting the selection to its current value also causes a modifyEvent
    log.setLength( 0 );
    spinner.setSelection( spinner.getSelection() );
    assertEquals( "modifyEvent", log.toString() );
    // setValues which indirectly changes the selection also causes a
    // modifyEvent
    log.setLength( 0 );
    spinner.setValues( 1, 0, 100, 0, 1, 10 );
    assertEquals( "modifyEvent", log.toString() );
  }

  @Test
  public void testAddModifyListener() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.addModifyListener( mock( ModifyListener.class ) );

    assertTrue( spinner.isListening( SWT.Modify ) );
  }

  @Test
  public void testRemoveModifyListener() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    ModifyListener listener = mock( ModifyListener.class );
    spinner.addModifyListener( listener );

    spinner.removeModifyListener( listener );

    assertFalse( spinner.isListening( SWT.Modify ) );
  }

  @Test
  public void testAddModifyListenerWithNullArgument() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    try {
      spinner.addModifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveModifyListenerWithNullArgument() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    try {
      spinner.removeModifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testComputeSize() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Point expected = new Point( 100, 28 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner.setMaximum( 1000000 );
    expected = new Point( 127, 28 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner.setMinimum( -1000000 );
    spinner.setMaximum( 100 );
    expected = new Point( 134, 28 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner = new Spinner( shell, SWT.BORDER );
    expected = new Point( 102, 30 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner.setDigits( 5 );
    expected = new Point( 129, 30 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 132, 102 );
    assertEquals( expected, spinner.computeSize( 100, 100 ) );
  }

  @Test
  public void testComputeTrim() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Rectangle expected = new Rectangle( 0, 0, 130, 100 );
    assertEquals( expected, spinner.computeTrim( 0, 0, 100, 100 ) );

    spinner = new Spinner( shell, SWT.BORDER );
    expected = new Rectangle( -1, -1, 132, 102 );
    assertEquals( expected, spinner.computeTrim( 0, 0, 100, 100 ) );
  }

  @Test
  public void testGetText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    spinner.setSelection( 5 );
    assertEquals( "5", spinner.getText() );

    spinner.setDigits( 2 );
    RWT.setLocale( Locale.US );
    assertEquals( "0.05", spinner.getText() );

    RWT.setLocale( Locale.GERMANY );
    assertEquals( "0,05", spinner.getText() );
  }

  @Test
  public void testTextLimit() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    assertEquals( Spinner.LIMIT, spinner.getTextLimit() );
    spinner.setTextLimit( 1 );
    assertEquals( 1, spinner.getTextLimit() );
    try {
      spinner.setTextLimit( 0 );
      fail( "Must not allow zero" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDigits() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    assertEquals( 0, spinner.getDigits() );
    spinner.setDigits( 1 );
    assertEquals( 1, spinner.getDigits() );
    try {
      spinner.setDigits( -1 );
      fail( "Must not allow negative values" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetValuesValid() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setValues( 50, 40, 60, 2, 5, 10 );
    assertEquals( 50, spinner.getSelection() );
    assertEquals( 40, spinner.getMinimum() );
    assertEquals( 60, spinner.getMaximum() );
    assertEquals( 2, spinner.getDigits() );
    assertEquals( 5, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );
  }

  @Test
  public void testSetValuesInvalid() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setValues( 5, 6, 4, 5, 1, 2 );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );

    spinner.setValues( 5, 4, 6, -5, 1, 2 );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );

    spinner.setValues( 5, 4, 6, 5, -1, 2 );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );

    spinner.setValues( 5, 4, 6, 5, 1, -2 );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );
  }

  @Test
  public void testSetValuesWithNonCrossedRanges() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setValues( 500, 400, 600, 2, 5, 10 );
    assertEquals( 500, spinner.getSelection() );
    assertEquals( 400, spinner.getMinimum() );
    assertEquals( 600, spinner.getMaximum() );
    assertEquals( 2, spinner.getDigits() );
    assertEquals( 5, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );

    spinner.setValues( 50, 40, 60, 2, 5, 10 );
    assertEquals( 50, spinner.getSelection() );
    assertEquals( 40, spinner.getMinimum() );
    assertEquals( 60, spinner.getMaximum() );
    assertEquals( 2, spinner.getDigits() );
    assertEquals( 5, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.HORIZONTAL );
    spinner.setSelection( 2 );

    Spinner deserializedSpinner = Fixture.serializeAndDeserialize( spinner );

    assertEquals( spinner.getSelection(), deserializedSpinner.getSelection() );
  }

  @Test
  public void testAddSelectionListener() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( spinner.isListening( SWT.Selection ) );
    assertTrue( spinner.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    spinner.addSelectionListener( listener );

    spinner.removeSelectionListener( listener );

    assertFalse( spinner.isListening( SWT.Selection ) );
    assertFalse( spinner.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    try {
      spinner.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Spinner spinner = new Spinner( shell, SWT.NONE );

    try {
      spinner.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
