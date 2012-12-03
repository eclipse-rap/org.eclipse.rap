/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;


public class PrepareUIRoot_Test extends TestCase {

  private IPhase phase;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    phase = new PrepareUIRoot( ApplicationContextUtil.getInstance() );
    TestEntryPoint.wasInvoked = false;
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetPhaseId() {
    assertEquals( PhaseId.PREPARE_UI_ROOT, phase.getPhaseId() );
  }

  public void testExecuteInSubsequentRequests() throws IOException {
    Display display = new Display();

    PhaseId phaseId = phase.execute( display );

    assertEquals( PhaseId.READ_DATA, phaseId );
  }

  public void testExecuteInFirstRequests() throws IOException {
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
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
