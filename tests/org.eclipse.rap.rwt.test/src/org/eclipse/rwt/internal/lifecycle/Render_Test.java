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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;


public class Render_Test extends TestCase {

  private Render render;

  public void testGetPhaseId() {
    assertEquals( PhaseId.RENDER, render.getPhaseId() );
  }
  
  public void testExecute() throws IOException {
    Display display = new Display();
    PhaseId nextPhase = render.execute( display );
    assertNull( nextPhase );
  }
  
  public void testExecuteWithNullDisplay() throws IOException {
    PhaseId nextPhase = render.execute( null );
    assertNull( nextPhase );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    render = new Render();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
