/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.client.JavaScriptExecutorImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JavaScriptExecutorImpl_Test {

  private static final String EXECUTE_1 = "execute_1";
  private static final String EXECUTE_2 = "execute_2";

  private JavaScriptExecutorImpl executor;

  @Before
  public void setUp() {
    Fixture.setUp();
    new Display();
    executor = new JavaScriptExecutorImpl();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExecuteJSOnce() {
    executor.execute( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertTrue( indexOfCallOperation( message, "execute", EXECUTE_1 ) != -1 );
  }

  @Test
  public void testExecuteJSTwice() {
    executor.execute( EXECUTE_1 );
    executor.execute( EXECUTE_2 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertTrue( indexOfCallOperation( message, "execute", EXECUTE_1 + EXECUTE_2 ) != -1 );
  }

  @Test
  public void testExecuteJSIsClearedAfterRender() {
    executor.execute( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();
    executor.execute( EXECUTE_2 );
    Fixture.executeLifeCycleFromServerThread();

    String script = getMessageScript();
    assertFalse( script.contains( EXECUTE_1 ) );
    assertTrue( script.contains( EXECUTE_2 ) );
  }

  @Test
  public void testExecuteJSWithDifferentDisplay() {
    executor.execute( EXECUTE_1 );

    simulateDifferentDisplay();
    Fixture.executeLifeCycleFromServerThread();

    assertFalse( getMessageScript().contains( EXECUTE_1 ) );
  }

  @Test
  public void testEmptyScriptIsNotRendered() {
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( "rwt.client.JavaScriptExecutor", "execute" ) );
  }

  private int indexOfCallOperation( Message message, String method, String contentProperty ) {
    int result = -1;
    int operationCount = message.getOperationCount();
    for( int position = 0; position < operationCount; position++ ) {
      Operation operation = message.getOperation( position );
      if( operation instanceof CallOperation ) {
        CallOperation callOperation = ( CallOperation )operation;
        if(    method.equals( callOperation.getMethodName() )
            && contentProperty.equals( callOperation.getProperty( "content" ) ) )
        {
          result = position;
        }
      }
    }
    return result;
  }

  private static void simulateDifferentDisplay() {
    Display.getCurrent().dispose();
    new Display();
  }

  private static String getMessageScript() {
    String result = "";
    Message message = Fixture.getProtocolMessage();
    if( message.getOperationCount() > 0 ) {
      CallOperation operation
        = message.findCallOperation( "rwt.client.JavaScriptExecutor", "execute" );
      if( operation != null ) {
        result = ( String )operation.getProperty( "content" );
      }
    }
    return result;
  }

}
