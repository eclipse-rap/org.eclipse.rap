/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BrowserUtil_Test {

  private Display display;
  private Browser browser;
  private BrowserCallback browserCallback;

  @Before
  public void setUp() {
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

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExecuteWithNullBrowser() {
    try {
      BrowserUtil.evaluate( null, "return true;", browserCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testExecuteWithNullCallback() {
    try {
      BrowserUtil.evaluate( browser, "return true;", null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testExecuteWithNullScript() {
    try {
      BrowserUtil.evaluate( browser, null, browserCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testExecute() {
    BrowserUtil.evaluate( browser, "return true;", browserCallback );

    String expected = "(function(){return true;})();";
    assertEquals( expected, browser.getAdapter( IBrowserAdapter.class ).getExecuteScript() );
  }

  @Test
  public void testExecuteTwice() {
    BrowserUtil.evaluate( browser, "return true;", browserCallback );
    BrowserUtil.evaluate( browser, "return false;", browserCallback );

    String expected = "(function(){return true;})();";
    assertEquals( expected, browser.getAdapter( IBrowserAdapter.class ).getExecuteScript() );
  }

  @Test
  public void testExecuteWithDisposedBrowser() {
    browser.dispose();

    try {
      BrowserUtil.evaluate( browser, "return true;", browserCallback );
      fail();
    } catch( Exception expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  @Test
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
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( browser ), "executeResult", true );
    Fixture.fakeSetProperty( getId( browser ), "evaluateResult", new JsonArray().add( 5 ) );

    Fixture.readDataAndProcessAction( browser );

    assertEquals( Integer.valueOf( 5 ), log[ 0 ] );
  }

  @Test
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
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( browser ), "executeResult", false );
    Fixture.fakeSetProperty( getId( browser ), "evaluateResult", "devide by zero" );

    Fixture.readDataAndProcessAction( browser );

    assertEquals( "Failed to evaluate Javascript expression", log[ 0 ] );
  }

}
