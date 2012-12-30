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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Render_Test {

  private Render render;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    render = new Render();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPhaseId() {
    assertEquals( PhaseId.RENDER, render.getPhaseId() );
  }

  @Test
  public void testExecute() throws IOException {
    Display display = new Display();
    PhaseId nextPhase = render.execute( display );
    assertNull( nextPhase );
  }

  @Test
  public void testExecuteWithNullDisplay() throws IOException {
    PhaseId nextPhase = render.execute( null );
    assertNull( nextPhase );
  }

}
