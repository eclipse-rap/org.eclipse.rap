/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ToggleHyperlink_Test extends TestCase {

  public void testColors() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Color decorationColor = Graphics.getColor( 255, 0, 0 );
    twistie.setDecorationColor( decorationColor );
    assertEquals( decorationColor, twistie.getDecorationColor() );
    Color hoverColor = Graphics.getColor( 0, 255, 0 );
    twistie.setHoverDecorationColor( hoverColor );
    assertEquals( hoverColor, twistie.getHoverDecorationColor() );
  }

  public void testExpanded() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    twistie.setExpanded( true );
    assertTrue( twistie.isExpanded() );
  }

  public void testComputeSize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Point expected = new Point( 11, 11 );
    assertEquals( expected, twistie.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 11, 11 );
    assertEquals( expected, twistie.computeSize( 50, 50 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
