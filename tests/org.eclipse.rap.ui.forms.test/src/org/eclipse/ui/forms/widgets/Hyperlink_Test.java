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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkLCA;
import org.junit.*;


public class Hyperlink_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite parent;
  private FormToolkit toolkit;
  private Form form;
  private Hyperlink hyperlink;

  @Before
  public void setUp() {
    display = new Display();
    parent = new Shell( display, SWT.NONE );
    parent.setLayout( new FillLayout() );
    toolkit = new FormToolkit( display );
    form = toolkit.createForm( parent );
    form.getBody().setLayout( new TableWrapLayout() );
    hyperlink = toolkit.createHyperlink( form.getBody(), "hyperlink text", SWT.NONE );
  }

  @Test
  public void testCreated() {
    assertNotNull( hyperlink );
    assertEquals( "hyperlink text", hyperlink.getText() );
    assertTrue( hyperlink.isUnderlined() );
    assertNull( hyperlink.getToolTipText() );
    assertNull( hyperlink.getHref() );
  }

  @Test
  public void testText() {
    hyperlink.setText( "bar" );

    assertEquals( "bar", hyperlink.getText() );
  }

  @Test
  public void testText_null() {
    hyperlink.setText( null );

    assertEquals( "", hyperlink.getText() );
  }

  @Test
  public void testUnderline() {
    hyperlink.setUnderlined( false );

    assertFalse( hyperlink.isUnderlined() );
  }

  @Test
  public void testToolTipText() {
    hyperlink.setToolTipText( "foo" );

    assertEquals( "foo", hyperlink.getToolTipText() );
  }

  @Test
  public void testHref() {
    String href = "http://www.eclipse.org";

    hyperlink.setHref( href );

    assertEquals( href, hyperlink.getHref() );
  }

  @Test
  public void testComputeSize() {
    assertEquals( new Point( 96, 20 ), hyperlink.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    assertEquals( new Point( 50, 20 ), hyperlink.computeSize( 50, 50 ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( hyperlink.getAdapter( WidgetLCA.class ) instanceof HyperlinkLCA );
    assertSame( hyperlink.getAdapter( WidgetLCA.class ), hyperlink.getAdapter( WidgetLCA.class ) );
  }

}
