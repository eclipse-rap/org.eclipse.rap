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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextLCA;
import org.junit.*;


public class FormText_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite shell;
  private FormToolkit toolkit;
  private Form form;
  private FormText formText;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    toolkit = new FormToolkit( shell.getDisplay() );
    form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    formText = toolkit.createFormText( form.getBody(), true );
  }

  @Test
  public void testCreated() {
    assertNotNull( formText );
    assertEquals( toolkit.getHyperlinkGroup(), formText.getHyperlinkSettings() );
  }

  @Test
  public void testHyperlinkSettings() {
    HyperlinkSettings settings = new HyperlinkSettings( display );

    formText.setHyperlinkSettings( settings );

    assertEquals( settings, formText.getHyperlinkSettings() );
  }

  @Test
  public void testComputeSize() {
    String text = "<form>"
      + "<p>First paragraph</p>"
      + "<li>First bullet</li>"
      + "<li>Second bullet</li>"
      + "<li>Third bullet</li>"
      + "<p>Second paragraph</p>"
      + "</form>";
    formText.setText( text, true, false );

    assertEquals( new Point( 110, 120 ), formText.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    assertEquals( new Point( 50, 50 ), formText.computeSize( 50, 50 ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( formText.getAdapter( WidgetLCA.class ) instanceof FormTextLCA );
    assertSame( formText.getAdapter( WidgetLCA.class ), formText.getAdapter( WidgetLCA.class ) );
  }

}

