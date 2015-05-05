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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Layout_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @Test
  public void testLayoutCall() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    Rectangle empty = new Rectangle( 0, 0, 0, 0 );
    assertFalse( empty.equals( shell.getBounds() ) );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    Rectangle shellBounds = new Rectangle( 40, 50, 100, 70 );
    shell.setBounds( shellBounds );
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.layout();
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.setLayout( new FillLayout() );
    composite.setLayout( new FillLayout() );
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.layout();
    assertEquals( shellBounds, shell.getBounds() );
    Rectangle clientArea = shell.getClientArea();
    assertEquals( clientArea, composite.getBounds() );
    Rectangle expected = new Rectangle( 0, 0, clientArea.width, clientArea.height );
    assertEquals( expected, control.getBounds() );
  }

  @Test
  public void testClientArea() {
    Composite comp1 = new Composite( shell, SWT.NONE );
    comp1.setBounds( 0, 0, 50, 100 );
    assertEquals( 0, comp1.getBorderWidth() );
    assertEquals( new Rectangle( 0, 0, 50, 100 ), comp1.getClientArea() );
    Composite comp2 = new Composite( shell, SWT.BORDER );
    comp2.setBounds( 0, 0, 50, 100 );
    assertEquals( 1, comp2.getBorderWidth() );
    assertEquals( new Rectangle( 0, 0, 48, 98 ), comp2.getClientArea() );
  }

  @Test
  public void testClientArea_withDifferentBorderWidths() {
    Composite composite = spy( new Composite( shell, SWT.BORDER ) );
    when( composite.getBorder() ).thenReturn( new BoxDimensions( 2, 3, 4, 1 ) );
    composite.setBounds( 0, 0, 50, 100 );

    assertEquals( new Rectangle( 0, 0, 46, 94 ), composite.getClientArea() );
  }

  @Test
  public void testComputeTrim_withDifferentBorderWidths() {
    Composite composite = spy( new Composite( shell, SWT.BORDER ) );
    when( composite.getBorder() ).thenReturn( new BoxDimensions( 2, 3, 4, 1 ) );

    assertEquals( new Rectangle( -1, -2, 54, 106 ), composite.computeTrim( 0, 0, 50, 100 ) );
  }

  @Test
  public void testComputeSize() {
    Button control1 = new Button( shell, SWT.PUSH );
    assertEquals( 1, control1.getBorderWidth() );
    assertEquals( new Point( 52, 102 ), control1.computeSize( 50, 100 ) );
    Button control2 = new Button( shell, SWT.PUSH | SWT.BORDER );
    assertEquals( 1, control2.getBorderWidth() );
    assertEquals( new Point( 52, 102 ), control2.computeSize( 50, 100 ) );
  }

  @Test
  public void testComputeSize_withDifferentBorderWidths() {
    Button button = spy( new Button( shell, SWT.PUSH | SWT.BORDER ) );
    when( button.getBorder() ).thenReturn( new BoxDimensions( 2, 3, 4, 1 ) );

    assertEquals( new Point( 54, 106 ), button.computeSize( 50, 100 ) );
  }

}
