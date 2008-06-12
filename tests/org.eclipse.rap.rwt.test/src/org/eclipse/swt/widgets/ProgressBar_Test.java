/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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

public class ProgressBar_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testRangeOperations() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    assertEquals( 0, progressBar.getMinimum() );
    assertEquals( 100, progressBar.getMaximum() );
    
    progressBar.setMinimum( 10 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( -1 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( 100 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( 101 );
    assertEquals( 10, progressBar.getMinimum() );
    
    progressBar.setMaximum( 20 );
    assertEquals( 20, progressBar.getMaximum() );
    progressBar.setMaximum( progressBar.getMinimum() - 1 );
    assertEquals( 20, progressBar.getMaximum() );
    progressBar.setMaximum( progressBar.getMinimum() );
    assertEquals( 20, progressBar.getMaximum() );
  }
  
  public void testSelection() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    assertEquals( 0, progressBar.getSelection() );

    progressBar.setMinimum( 10 );
    assertEquals( 10, progressBar.getSelection() );
    
    progressBar.setSelection( 20 );
    progressBar.setMaximum( 15 );
    assertEquals( 15, progressBar.getSelection() );
    
    progressBar.setSelection( 20 );
    assertEquals( 15, progressBar.getSelection() );
    progressBar.setSelection( 0 );
    assertEquals( 10, progressBar.getSelection() );
  }
}
