/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
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
    assertTrue( Fixture.getAllMarkup().indexOf( EXECUTE_1 ) != -1 );
  }

  public void testExecuteJSTwice() {
    JSExecutor.executeJS( EXECUTE_1 );
    JSExecutor.executeJS( EXECUTE_2 );
    Fixture.executeLifeCycleFromServerThread();
    assertTrue( Fixture.getAllMarkup().indexOf( EXECUTE_1 + EXECUTE_2 ) != -1 );
  }
  
  public void testExecuteJSIsClearedAfterRender() {
    JSExecutor.executeJS( EXECUTE_1 );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.executeLifeCycleFromServerThread();
    assertTrue( Fixture.getAllMarkup().indexOf( EXECUTE_1 ) == -1 );
  }

  public void testExecuteJSWithDifferentDisplay() {
    JSExecutor.executeJS( EXECUTE_1 );
    simulateDifferentDisplay();
    Fixture.executeLifeCycleFromServerThread();
    assertTrue( Fixture.getAllMarkup().indexOf( EXECUTE_1 ) == -1 );
  }

  private static void simulateDifferentDisplay() {
    Display.getCurrent().dispose();
    new Display();
  }
}
