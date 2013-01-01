/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PrepareUIRoot_Test {

  private IPhase phase;

  @Before
  public void setUp() {
    Fixture.setUp();
    phase = new PrepareUIRoot( ApplicationContextUtil.getInstance() );
    TestEntryPoint.wasInvoked = false;
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPhaseId() {
    assertEquals( PhaseId.PREPARE_UI_ROOT, phase.getPhaseId() );
  }

  @Test
  public void testExecuteInSubsequentRequests() throws IOException {
    Display display = new Display();

    PhaseId phaseId = phase.execute( display );

    assertEquals( PhaseId.READ_DATA, phaseId );
  }

  @Test
  public void testExecuteInFirstRequests() throws IOException {
    EntryPointManager entryPointManager = getApplicationContext().getEntryPointManager();
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, TestEntryPoint.class, null );

    PhaseId phaseId = phase.execute( null );

    assertEquals( PhaseId.RENDER, phaseId );
    assertTrue( TestEntryPoint.wasInvoked );
  }

  private static class TestEntryPoint implements EntryPoint {
    static boolean wasInvoked;
    public int createUI() {
      wasInvoked = true;
      return 0;
    }
  }
}
