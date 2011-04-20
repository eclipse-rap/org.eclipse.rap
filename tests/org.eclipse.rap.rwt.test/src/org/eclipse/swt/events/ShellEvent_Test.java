/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ShellEvent_Test extends TestCase {
  
  private static final String SHELL_CLOSED = "shellClosed|";
  
  private String log;
  private Shell shell;
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    log = "";
    Display display = new Display();
    shell = new Shell( display );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testInitialValues() {
    ShellEvent shellEvent = new ShellEvent( shell, ShellEvent.SHELL_CLOSED );
    assertNull( shellEvent.data );
    assertSame( shell.getDisplay(), shellEvent.display );
    assertSame( shell, shellEvent.widget );
    assertSame( shell, shellEvent.getSource() );
    assertTrue( shellEvent.doit );
    assertEquals( ShellEvent.SHELL_CLOSED, shellEvent.getID() );
  }
  
  public void testAddRemoveClosedListener() {
    ShellListener listener = new ShellAdapter() {
      public void shellClosed( ShellEvent event ) {
        log += SHELL_CLOSED;
      }
    };
    shell.addShellListener( listener );
    
    shell.close();
    
    assertEquals( SHELL_CLOSED, log );
  }
  
  public void testRemoveCloseListener() {
    ShellListener listener = new ShellAdapter() {
      public void shellClosed( ShellEvent event ) {
        log += SHELL_CLOSED;
      }
    };
    shell.addShellListener( listener );
    shell.removeShellListener( listener );

    shell.close();
    
    assertEquals( "", log );
  }
  
  public void testDenyClose() {
    ShellListener listener = new ShellAdapter() {
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
