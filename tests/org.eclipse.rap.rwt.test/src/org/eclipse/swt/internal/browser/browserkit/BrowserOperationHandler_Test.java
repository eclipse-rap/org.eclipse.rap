/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public class BrowserOperationHandler_Test {

  private Browser browser;
  private BrowserOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    browser = new Browser( shell, SWT.NONE );
    handler = new BrowserOperationHandler( browser );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifyProgress() {
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );

    handler.handleNotify( "Progress", new JsonObject() );

    InOrder order = inOrder( listener );
    order.verify( listener ).changed( any( ProgressEvent.class ) );
    order.verify( listener ).completed( any( ProgressEvent.class ) );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testHandleNotifyProgress_invisibleBrowser() {
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    browser.setVisible( false );

    handler.handleNotify( "Progress", new JsonObject() );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testHandleNotifyProgress_disabledBrowser() {
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    browser.setEnabled( false );

    handler.handleNotify( "Progress", new JsonObject() );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testHandleCallExecuteFunction() {
    final List<Object> log = new ArrayList<Object>();
    new BrowserFunction( browser, "func" ) {
      @Override
      public Object function( Object[] arguments ) {
        log.addAll( Arrays.asList( arguments ) );
        return new Object[ 0 ];
      }
    };

    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray().add( "eclipse" ).add( 3.6 ) );
    handler.handleCall( "executeFunction", parameters );

    Object[] expected = new Object[] { "eclipse", Double.valueOf( 3.6 ) };
    assertTrue( Arrays.equals( expected, log.toArray() ) );
  }

  @Test
  public void testHandleCallEvaluationSucceeded() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );

    JsonObject parameters = new JsonObject().add( "result", new JsonArray().add( 27 ) );
    handler.handleCall( "evaluationSucceeded", parameters );

    verify( browserCallback ).evaluationSucceeded( Integer.valueOf( 27 ) );
  }

  @Test
  public void testHandleCallEvaluationFailed() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );

    handler.handleCall( "evaluationFailed", new JsonObject() );

    verify( browserCallback ).evaluationFailed( any( Exception.class ) );
  }

}
