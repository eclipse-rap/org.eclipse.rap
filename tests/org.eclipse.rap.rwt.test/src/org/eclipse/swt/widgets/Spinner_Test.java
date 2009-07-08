/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class Spinner_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

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

    // ignore negative min or max values (behave like SWT)
    spinner.setMinimum( 1 );
    spinner.setMaximum( 100 );
    spinner.setMinimum( -1 );
    assertEquals( 1, spinner.getMinimum() );
    spinner.setMinimum( 1 );
    spinner.setMaximum( 100 );
    spinner.setMaximum( -1 );
    assertEquals( 100, spinner.getMaximum() );
  }

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

  public void testModifyAndSelectionEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Spinner spinner = new Spinner( shell, SWT.NONE );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        assertSame( spinner, event.getSource() );
        log.append( "modifyEvent" );
      }
    } );
    // Changing the selection programmatically never triggers a selection event
    spinner.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
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

  public void testComputeSize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Point expected = new Point( 52, 18 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner.setMaximum( 1000000 );
    expected = new Point( 73, 18 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    spinner = new Spinner( shell, SWT.BORDER );
    expected = new Point( 54, 20 );
    assertEquals( expected, spinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 117, 102 );
    assertEquals( expected, spinner.computeSize( 100, 100 ) );
  }

  public void testComputeTrim() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Rectangle expected = new Rectangle( 0, 0, 115, 100 );
    assertEquals( expected, spinner.computeTrim( 0, 0, 100, 100 ) );

    spinner = new Spinner( shell, SWT.BORDER );
    expected = new Rectangle( -1, -1, 117, 102 );
    assertEquals( expected, spinner.computeTrim( 0, 0, 100, 100 ) );
  }
  
  public void testGetText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    spinner.setSelection( 5 );
    assertEquals( "5", spinner.getText() );
  }
  
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
}
