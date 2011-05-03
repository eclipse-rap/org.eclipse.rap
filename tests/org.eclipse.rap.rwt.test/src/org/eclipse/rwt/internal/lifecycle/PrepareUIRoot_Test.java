/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;


public class PrepareUIRoot_Test extends TestCase {
  
  private IPhase phase;

  private static class TestEntryPoint implements IEntryPoint {
    static boolean wasInvoked;
    public int createUI() {
      wasInvoked = true;
      return 0;
    }
  }
  
  public void testGetPhaseId() {
    assertEquals( PhaseId.PREPARE_UI_ROOT, phase.getPhaseId() );
  }

  public void testExecuteInSubsequentRequests() throws IOException {
    Display display = new Display();
    PhaseId phaseId = phase.execute( display );
    assertEquals( PhaseId.READ_DATA, phaseId );
  }
  
  public void testExecuteInFirstRequestsWithNoStartupParameter() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, TestEntryPoint.class );
    PhaseId phaseId = phase.execute( null );
    assertEquals( PhaseId.RENDER, phaseId );
    assertTrue( TestEntryPoint.wasInvoked );
  }
  
  public void testExecuteInFirstRequestsWithStartupParameter() throws IOException {
    String entryPointName = "myEntryPoint";
    RWTFactory.getEntryPointManager().register( entryPointName, TestEntryPoint.class );
    Fixture.fakeRequestParam( RequestParams.STARTUP, entryPointName );
    PhaseId phaseId = phase.execute( null );
    assertEquals( PhaseId.RENDER, phaseId );
    assertTrue( TestEntryPoint.wasInvoked );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    phase = new PrepareUIRoot();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
