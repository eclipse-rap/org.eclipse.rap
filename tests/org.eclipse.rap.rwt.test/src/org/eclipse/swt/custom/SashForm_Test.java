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
package org.eclipse.swt.custom;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class SashForm_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private SashForm sashForm;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    sashForm = new SashForm( shell, SWT.HORIZONTAL );
  }

  @Test
  public void testSetOrientation_initial() {
    sashForm = new SashForm( shell, SWT.RIGHT_TO_LEFT );

    int actual = sashForm.getStyle() & ( SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT );
    assertEquals( SWT.RIGHT_TO_LEFT, actual );
  }

  @Test
  public void testSetOrientation() {
    sashForm.setOrientation( SWT.RIGHT_TO_LEFT );

    int actual = sashForm.getStyle() & ( SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT );
    assertEquals( SWT.RIGHT_TO_LEFT, actual );
  }

  @Test
  public void testIsSerializable() throws Exception {
    new Label( sashForm, SWT.NONE );
    new Label( sashForm, SWT.NONE );
    sashForm.setWeights( new int[]{ 30, 70 } );

    SashForm deserializedSashForm = serializeAndDeserialize( sashForm );

    assertEquals( sashForm.getWeights()[ 0 ], deserializedSashForm.getWeights()[ 0 ] );
    assertEquals( sashForm.getWeights()[ 1 ], deserializedSashForm.getWeights()[ 1 ] );
  }

}
