/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

public class ToggleHyperlink_Test extends TestCase {

  public void testColors() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Color decorationColor =new Color( display, 255, 0, 0 );
    twistie.setDecorationColor( decorationColor );
    assertEquals( decorationColor, twistie.getDecorationColor() );
    Color hoverColor =new Color( display, 0, 255, 0 );
    twistie.setHoverDecorationColor( hoverColor );
    assertEquals( hoverColor, twistie.getHoverDecorationColor() );
  }

  public void testExpanded() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    twistie.setExpanded( true );
    assertTrue( twistie.isExpanded() );
  }

  public void testComputeSize() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Point expected = new Point( 11, 11 );
    assertEquals( expected, twistie.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 11, 11 );
    assertEquals( expected, twistie.computeSize( 50, 50 ) );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
