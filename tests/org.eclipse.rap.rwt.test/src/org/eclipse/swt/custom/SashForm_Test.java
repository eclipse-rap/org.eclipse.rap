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

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class SashForm_Test extends TestCase {
  
  private Display display;
  private Shell shell;

  public void testIsSerializable() throws Exception {
    SashForm sashForm = new SashForm( shell, SWT.HORIZONTAL );
    new Label( sashForm, SWT.NONE );
    new Label( sashForm, SWT.NONE );
    sashForm.setWeights( new int[]{ 30, 70 } );
    
    SashForm deserializedSashForm = Fixture.serializeAndDeserialize( sashForm );
    
    assertEquals( sashForm.getWeights()[ 0 ], deserializedSashForm.getWeights()[ 0 ] );
    assertEquals( sashForm.getWeights()[ 1 ], deserializedSashForm.getWeights()[ 1 ] );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
