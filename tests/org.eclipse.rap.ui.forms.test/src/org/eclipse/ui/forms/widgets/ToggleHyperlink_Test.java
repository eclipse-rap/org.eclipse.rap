/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.widgets;

import static org.junit.Assert.*;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkLCA;
import org.junit.*;


public class ToggleHyperlink_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite shell;
  private Twistie twistie;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    twistie = new Twistie( shell, SWT.NONE );
  }

  @Test
  public void testColors() {
    Color decorationColor =new Color( display, 255, 0, 0 );
    twistie.setDecorationColor( decorationColor );

    assertEquals( decorationColor, twistie.getDecorationColor() );

    Color hoverColor =new Color( display, 0, 255, 0 );
    twistie.setHoverDecorationColor( hoverColor );

    assertEquals( hoverColor, twistie.getHoverDecorationColor() );
  }

  @Test
  public void testExpanded() {
    twistie.setExpanded( true );

    assertTrue( twistie.isExpanded() );
  }

  @Test
  public void testComputeSize() {
    assertEquals( new Point( 11, 11 ), twistie.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    assertEquals( new Point( 11, 11 ), twistie.computeSize( 50, 50 ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( twistie.getAdapter( WidgetLCA.class ) instanceof ToggleHyperlinkLCA );
    assertSame( twistie.getAdapter( WidgetLCA.class ), twistie.getAdapter( WidgetLCA.class ) );
  }

}
