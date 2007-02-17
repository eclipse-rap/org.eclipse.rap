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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import junit.framework.TestCase;


public class Spinner_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Spinner spinner = new Spinner( shell, RWT.NONE );
    assertEquals( 0, spinner.getSelection() );
    assertEquals( 0, spinner.getMinimum() );
    assertEquals( 100, spinner.getMaximum() );
    assertEquals( 1, spinner.getIncrement() );
    assertEquals( 10, spinner.getPageIncrement() );
    assertEquals( 0, spinner.getDigits() );
    assertEquals( 0, spinner.getBorderWidth() );

    spinner = new Spinner( shell, RWT.BORDER );
    assertTrue( ( spinner.getStyle() & RWT.BORDER ) != 0 );
    assertEquals( 2, spinner.getBorderWidth() );

    spinner = new Spinner( shell, RWT.READ_ONLY );
    assertTrue( ( spinner.getStyle() & RWT.READ_ONLY ) != 0 );
  }
  
  public void testMinMax() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Spinner spinner = new Spinner( shell, RWT.NONE );
    
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
    Shell shell = new Shell( display, RWT.NONE );
    Spinner spinner = new Spinner( shell, RWT.NONE );
    
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
}
