/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.eclipse.swt.internal.browser.browserkit.BrowserOperationHandler.javaToJson;
import static org.eclipse.swt.internal.browser.browserkit.BrowserOperationHandler.jsonToJava;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
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
    BrowserFunction browserFunction = mockBrowserFunction();

    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray().add( "eclipse" ).add( 3.6 ) );
    handler.handleCall( "executeFunction", parameters );

    Object[] expected = new Object[] { "eclipse", Double.valueOf( 3.6 ) };
    verify( browserFunction ).function( expected );
  }

  @Test
  public void testHandleCallExecuteFunction_withoutArguments() {
    BrowserFunction browserFunction = mockBrowserFunction();

    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray() );
    handler.handleCall( "executeFunction", parameters );

    verify( browserFunction ).function( new Object[0] );
  }

  @Test
  public void testHandleCallExecuteFunction_withInvalidReturnValue() {
    BrowserFunction browserFunction = mockBrowserFunction();
    when( browserFunction.function( any( Object[].class ) ) )
      .thenReturn( new HashMap<String, Object>() );

    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray() );

    try {
      handler.handleCall( "executeFunction", parameters );
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_INVALID_RETURN_VALUE, error.code );
    }
  }

  @Test
  public void testHandleCallEvaluationSucceeded() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );

    JsonObject parameters = new JsonObject().add( "result", new JsonArray().add( 27 ) );
    handler.handleCall( "evaluationSucceeded", parameters );

    verify( browserCallback ).evaluationSucceeded( Double.valueOf( 27 ) );
  }

  @Test
  public void testHandleCallEvaluationSucceeded_nullResult() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );

    JsonObject parameters = new JsonObject().add( "result", JsonObject.NULL );
    handler.handleCall( "evaluationSucceeded", parameters );

    verify( browserCallback ).evaluationSucceeded( null );
  }

  @Test
  public void testHandleCallEvaluationFailed() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );

    handler.handleCall( "evaluationFailed", new JsonObject() );

    verify( browserCallback ).evaluationFailed( any( Exception.class ) );
  }

  @Test
  public void testJsonToJava_null() {
    assertNull( jsonToJava( JsonObject.NULL ) );
  }

  @Test
  public void testJsonToJava_boolean() {
    assertEquals( Boolean.TRUE, jsonToJava( JsonObject.TRUE ) );
  }

  @Test
  public void testJsonToJava_number() {
    assertEquals( Double.valueOf( 5 ), jsonToJava( JsonValue.valueOf( 5 ) ) );
  }

  @Test
  public void testJsonToJava_string() {
    assertEquals( "foo", jsonToJava( JsonValue.valueOf( "foo" ) ) );
  }

  @Test
  public void testJsonToJava_array() {
    JsonArray array = new JsonArray().add( true ).add( JsonObject.NULL ).add( 5 ).add( "foo" );

    Object[] result = ( Object[] )jsonToJava( array );

    Object[] expected = new Object[] { Boolean.TRUE, null, Double.valueOf( 5 ), "foo" };
    assertArrayEquals( expected, result );
  }

  @Test
  public void testJsonToJava_nestedArray() {
    JsonArray array = new JsonArray().add( true ).add( new JsonArray().add( 5 ).add( "foo" ) );

    Object[] result = ( Object[] )jsonToJava( array );

    Object[] expected = new Object[] { Boolean.TRUE, new Object[] { Double.valueOf( 5 ), "foo" } };
    assertArrayEquals( expected, result );
  }

  @Test( expected = RuntimeException.class )
  public void testJsonToJava_object() {
    jsonToJava( new JsonObject() );
  }

  @Test
  public void testJavaToJson_null() {
    assertEquals( JsonValue.NULL, javaToJson( null ) );
  }

  @Test
  public void testJavaToJson_boolean() {
    assertEquals( JsonValue.TRUE, javaToJson( Boolean.TRUE ) );
  }

  @Test
  public void testJavaToJson_number() {
    // According to the contract of BrowserFunction, all subtypes of number are supported
    assertEquals( JsonValue.valueOf( 23d ), javaToJson( Double.valueOf( 23 ) ) );
    assertEquals( JsonValue.valueOf( 23d ), javaToJson( new AtomicInteger( 23 ) ) );
  }

  @Test
  public void testJavaToJson_string() {
    assertEquals( JsonValue.valueOf( "foo" ), javaToJson( "foo" ) );
  }

  @Test
  public void testJavaToJson_array() {
    Object[] array = new Object[] { Boolean.TRUE, null, Double.valueOf( 5 ), "foo" };

    JsonValue result = javaToJson( array );

    JsonArray expected = new JsonArray().add( true ).add( JsonObject.NULL ).add( 5 ).add( "foo" );
    assertEquals( expected, result );
  }

  @Test
  public void testJavaToJson_nestedArray() {
    Object[] array = new Object[] { Boolean.TRUE, new Object[] { Double.valueOf( 5 ), "foo" } };

    JsonValue result = javaToJson( array );

    JsonArray expected = new JsonArray().add( true ).add( new JsonArray().add( 5 ).add( "foo" ) );
    assertEquals( expected, result );
  }

  @Test
  public void testJavaToJson_unsupportedObject() {
    try {
      javaToJson( new HashMap<String, Object>() );
      fail();
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_INVALID_RETURN_VALUE, error.code );
      assertEquals( "Return value not valid", error.getMessage() );
    }
  }

  private BrowserFunction mockBrowserFunction() {
    final BrowserFunction mock = mock( BrowserFunction.class );
    // Wrap the mock in a delegator BrowserFunction that registers itself in Browser.createFunction
    new BrowserFunction( browser, "func" ) {
      @Override
      public Object function( Object[] arguments ) {
        return mock.function( arguments );
      }
    };
    return mock;
  }

}
