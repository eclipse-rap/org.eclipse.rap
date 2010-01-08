/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;

public class BrowserFunction_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateDispose() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    BrowserFunction function = new BrowserFunction( browser, "func" );
    assertFalse( function.isDisposed() );
    IBrowserAdapter adapter
      = ( IBrowserAdapter )browser.getAdapter( IBrowserAdapter.class );
    BrowserFunction[] functions = adapter.getBrowserFunctions();
    assertEquals( 1, functions.length );
    assertSame( function, functions[ 0 ] );
    assertFalse( function.isDisposed() );
    assertSame( browser, function.getBrowser() );
    assertEquals( "func", function.getName() );
    function.dispose();
    assertTrue( function.isDisposed() );
    functions = adapter.getBrowserFunctions();
    assertEquals( 0, functions.length );
    function = new BrowserFunction( browser, "func" );
    assertFalse( function.isDisposed() );
    assertSame( browser, function.getBrowser() );
    assertEquals( "func", function.getName() );
    browser.dispose();
    assertTrue( function.isDisposed() );
  }

  public void testIllegalCreate() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    try {
      new BrowserFunction( null, "func" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    Browser browser = new Browser( shell, SWT.NONE );
    try {
      new BrowserFunction( browser, null );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    browser = new Browser( shell, SWT.NONE );
    browser.dispose();
    try {
      new BrowserFunction( browser, "func" );
    } catch( SWTException e ) {
      // expected
    }
  }
  
  public void testDuplicateFunction() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    BrowserFunction function1 = new BrowserFunction( browser, "func" );
    IBrowserAdapter adapter
      = ( IBrowserAdapter )browser.getAdapter( IBrowserAdapter.class );
    BrowserFunction[] functions = adapter.getBrowserFunctions();
    assertEquals( 1, functions.length );
    assertSame( function1, functions[ 0 ] );
    BrowserFunction function2 = new BrowserFunction( browser, "func" );
    functions = adapter.getBrowserFunctions();
    assertEquals( 1, functions.length );
    assertSame( function2, functions[ 0 ] );
  }
}
