/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.progressbarkit.ProgressBarLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ProgressBar_Test {

  @Rule
  public TestContext context = new TestContext();

  private Composite shell;
  private ProgressBar progressBar;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    progressBar = new ProgressBar( shell, SWT.NONE );
  }

  @Test
  public void testRangeOperations() {
    assertEquals( 0, progressBar.getMinimum() );
    assertEquals( 100, progressBar.getMaximum() );

    progressBar.setMinimum( 10 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( -1 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( 100 );
    assertEquals( 10, progressBar.getMinimum() );
    progressBar.setMinimum( 101 );
    assertEquals( 10, progressBar.getMinimum() );

    progressBar.setMaximum( 20 );
    assertEquals( 20, progressBar.getMaximum() );
    progressBar.setMaximum( progressBar.getMinimum() - 1 );
    assertEquals( 20, progressBar.getMaximum() );
    progressBar.setMaximum( progressBar.getMinimum() );
    assertEquals( 20, progressBar.getMaximum() );
  }

  @Test
  public void testSelection() {
    assertEquals( 0, progressBar.getSelection() );

    progressBar.setMinimum( 10 );
    assertEquals( 10, progressBar.getSelection() );

    progressBar.setSelection( 20 );
    progressBar.setMaximum( 15 );
    assertEquals( 15, progressBar.getSelection() );

    progressBar.setSelection( 20 );
    assertEquals( 15, progressBar.getSelection() );
    progressBar.setSelection( 0 );
    assertEquals( 10, progressBar.getSelection() );
  }

  @Test
  public void testState() {
    assertEquals( SWT.NORMAL, progressBar.getState() );
    progressBar.setState( SWT.PAUSED );
    assertEquals( SWT.PAUSED, progressBar.getState() );
    progressBar.setState( SWT.ERROR );
    assertEquals( SWT.ERROR, progressBar.getState() );
    // do not change state if parameter is not allowed
    progressBar.setState( 1 << 3 );
    assertEquals( SWT.ERROR, progressBar.getState() );
  }

  @Test
  public void testComputeSize() {
    ProgressBar bar = new ProgressBar( shell, SWT.HORIZONTAL );
    Point expected = new Point( 162, 18 );
    assertEquals( expected, bar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    bar = new ProgressBar( shell, SWT.VERTICAL );
    expected = new Point( 18, 162 );
    assertEquals( expected, bar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    bar = new ProgressBar( shell, SWT.BORDER );
    expected = new Point( 162, 18 );
    assertEquals( 1, bar.getBorderWidth() );
    assertEquals( expected, bar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 102, 102 );
    assertEquals( expected, bar.computeSize( 100, 100 ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    progressBar.setSelection( 54 );

    ProgressBar deserializedBar = serializeAndDeserialize( progressBar );

    assertEquals( progressBar.getSelection(), deserializedBar.getSelection() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( progressBar.getAdapter( WidgetLCA.class ) instanceof ProgressBarLCA );
    assertSame( progressBar.getAdapter( WidgetLCA.class ), progressBar.getAdapter( WidgetLCA.class ) );
  }

}
