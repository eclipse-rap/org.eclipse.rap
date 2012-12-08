/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class BrowserUtil_Test extends TestCase {

  private Display display;
  private Browser browser;
  private BrowserCallback browserCallback;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    Shell shell = new Shell( display );
    browser = new Browser( shell, SWT.NONE );
    browserCallback = new BrowserCallback() {
      public void evaluationSucceeded( Object result ) {
      }
      public void evaluationFailed( Exception exception ) {
      }
    };
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testExecuteWithNullBrowser() {
    try {
      BrowserUtil.evaluate( null, "return true;", browserCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testExecuteWithNullCallback() {
    try {
      BrowserUtil.evaluate( browser, "return true;", null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testExecuteWithNullScript() {
    try {
      BrowserUtil.evaluate( browser, null, browserCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testExecute() {
    BrowserUtil.evaluate( browser, "return true;", browserCallback );

    String expected = "(function(){return true;})();";
    assertEquals( expected, browser.getAdapter( IBrowserAdapter.class ).getExecuteScript() );
  }

  public void testExecuteTwice() {
    BrowserUtil.evaluate( browser, "return true;", browserCallback );
    BrowserUtil.evaluate( browser, "return false;", browserCallback );

    String expected = "(function(){return true;})();";
    assertEquals( expected, browser.getAdapter( IBrowserAdapter.class ).getExecuteScript() );
  }

  public void testExecuteWithDisposedBrowser() {
    browser.dispose();

    try {
      BrowserUtil.evaluate( browser, "return true;", browserCallback );
      fail();
    } catch( Exception expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testCallCallback_Succeeded() {
    final Object[] log = new Object[ 1 ];
    browserCallback = new BrowserCallback() {
      public void evaluationSucceeded( Object result ) {
        log[ 0 ] = result;
      }
      public void evaluationFailed( Exception exception ) {
        log[ 0 ] = exception.getMessage();
      }
    };
    BrowserUtil.evaluate( browser, "return 5;", browserCallback );
    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( browser ), "executeResult", Boolean.TRUE );
    Fixture.fakeSetParameter( getId( browser ), "evaluateResult", new Double[]{ Double.valueOf( 5 ) } );

    Fixture.readDataAndProcessAction( browser );

    assertEquals( Integer.valueOf( 5 ), log[ 0 ] );
  }

  public void testCallCallback_Failed() {
    final Object[] log = new Object[ 1 ];
    browserCallback = new BrowserCallback() {
      public void evaluationSucceeded( Object result ) {
        log[ 0 ] = result;
      }
      public void evaluationFailed( Exception exception ) {
        log[ 0 ] = exception.getMessage();
      }
    };
    BrowserUtil.evaluate( browser, "return 5/0;", browserCallback );
    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( browser ), "executeResult", Boolean.FALSE );
    Fixture.fakeSetParameter( getId( browser ), "evaluateResult", "devide by zero" );

    Fixture.readDataAndProcessAction( browser );

    assertEquals( "Failed to evaluate Javascript expression", log[ 0 ] );
  }
}
