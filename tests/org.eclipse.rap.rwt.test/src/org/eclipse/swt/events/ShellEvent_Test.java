/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ShellEvent_Test {

  private static final String SHELL_CLOSED = "shellClosed|";

  private String log;
  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    log = "";
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = display;
    event.widget = shell;
    event.doit = false;
    event.data = new Object();

    ShellEvent shellEvent = new ShellEvent( event );

    EventTestHelper.assertFieldsEqual( shellEvent, event );
  }

  @Test
  public void testAddRemoveClosedListener() {
    ShellListener listener = new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent event ) {
        log += SHELL_CLOSED;
      }
    };
    shell.addShellListener( listener );

    shell.close();

    assertEquals( SHELL_CLOSED, log );
  }

  @Test
  public void testRemoveCloseListener() {
    ShellListener listener = new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent event ) {
        log += SHELL_CLOSED;
      }
    };
    shell.addShellListener( listener );
    shell.removeShellListener( listener );

    shell.close();

    assertEquals( "", log );
  }

  @Test
  public void testDenyClose() {
    ShellListener listener = new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent event ) {
        // Test initial value of doit flag on ShellEvent
        assertTrue( event.doit );
        event.doit = false;
        log += SHELL_CLOSED;
      }
    };
    shell.addShellListener( listener );
    shell.open();
    shell.close();
    assertFalse( shell.isDisposed() );
    assertEquals( SHELL_CLOSED, log );
    assertTrue( shell.getVisible() );
  }

  @Test
  public void testDenyCloseWithUntypedListener() {
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        // Test initial value of doit flag on ShellEvent
        assertTrue( event.doit );
        event.doit = false;
        log += SHELL_CLOSED;
      }
    };
    shell.addListener( SWT.Close, listener );
    shell.open();
    shell.close();
    assertFalse( shell.isDisposed() );
    assertEquals( SHELL_CLOSED, log );
    assertTrue( shell.getVisible() );
  }

}
