/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;


public class Layout_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testLayoutCall() {
    RWTFixture.fakePhase( PhaseId.PREPARE_UI_ROOT );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    Rectangle empty = new Rectangle( 0, 0, 0, 0 );
    assertFalse( empty.equals( shell.getBounds() ) );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    Rectangle shellBounds = new Rectangle( 40, 50, 60, 70 );
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
    Rectangle expected = new Rectangle( 0,
                                        0,
                                        clientArea.width,
                                        clientArea.height );
    assertEquals( expected, control.getBounds() );
  }

  public void testClientArea() throws Exception {
    RWTFixture.fakePhase( PhaseId.PREPARE_UI_ROOT );
    Display display = new Display();
    Shell shell = new Shell( display );
    Composite comp1 = new Composite( shell, SWT.NONE );
    comp1.setBounds( 0, 0, 50, 100 );
    assertEquals( 0, comp1.getBorderWidth() );
    assertEquals( new Rectangle( 0, 0, 50, 100 ), comp1.getClientArea() );
    Composite comp2 = new Composite( shell, SWT.BORDER );
    comp2.setBounds( 0, 0, 50, 100 );
    assertEquals( 2, comp2.getBorderWidth() );
    assertEquals( new Rectangle( 0, 0, 46, 96 ), comp2.getClientArea() );
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PREPARE_UI_ROOT );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button control1 = new Button( shell, SWT.PUSH );
    assertEquals( 1, control1.getBorderWidth() );
    assertEquals( new Point( 52, 102 ), control1.computeSize( 50, 100 ) );
    Button control2 = new Button( shell, SWT.PUSH | SWT.BORDER );
    assertEquals( 2, control2.getBorderWidth() );
    assertEquals( new Point( 54, 104 ), control2.computeSize( 50, 100 ) );
  }
}
