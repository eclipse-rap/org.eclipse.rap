/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.ExecuteScriptOperation;
import org.eclipse.swt.widgets.Display;


public class JSExecutor_Test extends TestCase {

  private static final String EXECUTE_1 = "execute_1";
  private static final String EXECUTE_2 = "execute_2";

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testExecuteJSOnce() {
    JSExecutor.executeJS( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "text/javascript", operation.getScriptType() );
    assertEquals( EXECUTE_1, operation.getScript() );
  }

  public void testExecuteJSTwice() {
    JSExecutor.executeJS( EXECUTE_1 );
    JSExecutor.executeJS( EXECUTE_2 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "text/javascript", operation.getScriptType() );
    assertEquals( EXECUTE_1 + EXECUTE_2, operation.getScript() );
  }

  public void testExecuteJSIsClearedAfterRender() {
    JSExecutor.executeJS( EXECUTE_1 );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.executeLifeCycleFromServerThread();

    assertFalse( Fixture.getAllMarkup().contains( EXECUTE_1 ) );
  }

  public void testExecuteJSWithDifferentDisplay() {
    JSExecutor.executeJS( EXECUTE_1 );

    simulateDifferentDisplay();
    Fixture.executeLifeCycleFromServerThread();

    assertFalse( Fixture.getAllMarkup().contains( EXECUTE_1 ) );
  }

  private static void simulateDifferentDisplay() {
    Display.getCurrent().dispose();
    new Display();
  }
}
