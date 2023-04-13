/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProcessAction_Test {

  private ProcessAction processAction;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    processAction = new ProcessAction();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPhaseId() {
    assertEquals( PhaseId.PROCESS_ACTION, processAction.getPhaseId() );
  }

  @Test
  public void testExecute() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );

    shell.notifyListeners( SWT.Close, null );
    PhaseId phaseId = processAction.execute( display );

    assertEquals( PhaseId.RENDER, phaseId );
    verify( listener ).shellClosed( any( ShellEvent.class ) );
  }

}
