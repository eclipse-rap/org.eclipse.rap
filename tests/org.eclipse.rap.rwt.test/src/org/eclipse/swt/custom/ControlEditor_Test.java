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
package org.eclipse.swt.custom;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ControlEditor_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testIsSerializable() throws Exception {
    ControlEditor controlEditor = new ControlEditor( new CLabel( shell, SWT.NONE ) );
    controlEditor.minimumHeight = 6;
    
    ControlEditor deserialized = Fixture.serializeAndDeserialize( controlEditor );
    
    assertNotNull( deserialized.parent );
    assertEquals( 6, deserialized.minimumHeight );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
