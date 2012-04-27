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
package org.eclipse.rwt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.swt.widgets.Display;


public class JSExecutor_Test extends TestCase {

  private static final String EXECUTE_1 = "execute_1";
  private static final String EXECUTE_2 = "execute_2";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testExecuteJSOnce() {
    JSExecutor.executeJS( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    int createIndex = indexOfCreateOperation( message );
    int execute1Index = indexOfCallOperation( message, "execute", EXECUTE_1 );
    assertTrue( createIndex != -1 );
    assertTrue( execute1Index != -1 );
    assertTrue( createIndex < execute1Index );
  }

  public void testExecuteJSTwice() {
    JSExecutor.executeJS( EXECUTE_1 );
    JSExecutor.executeJS( EXECUTE_2 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    int createIndex = indexOfCreateOperation( message );
    int execute1Index = indexOfCallOperation( message, "execute", EXECUTE_1 + EXECUTE_2 );
    assertTrue( createIndex != -1 );
    assertTrue( execute1Index != -1 );
    assertTrue( createIndex < execute1Index );
  }

  public void testExecuteJSIsClearedAfterRender() {
    JSExecutor.executeJS( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.executeLifeCycleFromServerThread();

    assertFalse( ProtocolTestUtil.getMessageScript().contains( EXECUTE_1 ) );
  }

  public void testExecuteJSWithDifferentDisplay() {
    JSExecutor.executeJS( EXECUTE_1 );

    simulateDifferentDisplay();
    Fixture.executeLifeCycleFromServerThread();

    assertFalse( ProtocolTestUtil.getMessageScript().contains( EXECUTE_1 ) );
  }

  private int indexOfCreateOperation( Message message ) {
    int result = -1;
    int operationCount = message.getOperationCount();
    for( int position = 0; position < operationCount; position++ ) {
      Operation operation = message.getOperation( position );
      if( operation instanceof CreateOperation ) {
        CreateOperation createOperation = ( CreateOperation )operation;
        if( createOperation.getTarget().equals( "jsex" ) ) {
          result = position;
        }
      }
    }
    return result;
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
}
