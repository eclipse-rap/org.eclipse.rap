/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import com.w4t.engine.lifecycle.PhaseId;


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
    assertEquals( 2, spinner.getBorderWidth() );

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
  
  public void testModifyEvent() {
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
}
