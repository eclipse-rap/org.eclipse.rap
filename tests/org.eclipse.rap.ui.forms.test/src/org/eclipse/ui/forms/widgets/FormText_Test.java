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

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.HyperlinkSettings;

public class FormText_Test extends TestCase {

  public void testHyperlinkSettings() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    FormText formText = toolkit.createFormText( form.getBody(), true );
    assertNotNull( formText );
    assertEquals( toolkit.getHyperlinkGroup(), formText.getHyperlinkSettings() );
    HyperlinkSettings settings = new HyperlinkSettings( display );
    formText.setHyperlinkSettings( settings );
    assertEquals( settings, formText.getHyperlinkSettings() );
  }

  public void testComputeSize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    FormText formText = toolkit.createFormText( form.getBody(), true );
    assertNotNull( formText );
    String text = "<form>"
      + "<p>First paragraph</p>"
      + "<li>First bullet</li>"
      + "<li>Second bullet</li>"
      + "<li>Third bullet</li>"
      + "<p>Second paragraph</p>"
      + "</form>";
    formText.setText( text, true, false );
    Point expected = new Point( 86, 85 );
    assertEquals( expected, formText.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 50, 50 );
    assertEquals( expected, formText.computeSize( 50, 50 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
