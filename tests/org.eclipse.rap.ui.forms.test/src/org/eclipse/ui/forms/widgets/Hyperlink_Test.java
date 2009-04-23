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

public class Hyperlink_Test extends TestCase {

  public void testText() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    String text = "This is a hyperlink!";
    Hyperlink hyperlink
      = toolkit.createHyperlink( form.getBody(), text, SWT.NONE );
    assertNotNull( hyperlink );
    assertEquals( text, hyperlink.getText() );
    hyperlink.setText( null );
    assertEquals( "", hyperlink.getText() );
    text = "Click me!";
    hyperlink.setText( text );
    assertEquals( text, hyperlink.getText() );
  }

  public void testUnderline() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    String text = "This is a hyperlink!";
    Hyperlink hyperlink
      = toolkit.createHyperlink( form.getBody(), text, SWT.NONE );
    assertNotNull( hyperlink );
    assertTrue( hyperlink.isUnderlined() );
    hyperlink.setUnderlined( false );
    assertFalse( hyperlink.isUnderlined() );
  }

  public void testToolTipText() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    String text = "This is a hyperlink!";
    Hyperlink hyperlink
      = toolkit.createHyperlink( form.getBody(), text, SWT.NONE );
    assertNotNull( hyperlink );
    assertNull( hyperlink.getToolTipText() );
    String toolTip = "Click me!";
    hyperlink.setToolTipText( toolTip );
    assertEquals( toolTip, hyperlink.getToolTipText() );
  }

  public void testHref() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    String text = "This is a hyperlink!";
    Hyperlink hyperlink
      = toolkit.createHyperlink( form.getBody(), text, SWT.NONE );
    assertNotNull( hyperlink );
    assertNull( hyperlink.getHref() );
    String href = "http://www.eclipse.org";
    hyperlink.setHref( href );
    assertEquals( href, hyperlink.getHref() );
  }

  public void testComputeSize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    String text = "This is a hyperlink!";
    Hyperlink hyperlink
      = toolkit.createHyperlink( form.getBody(), text, SWT.NONE );
    assertNotNull( hyperlink );
    Point expected = new Point( 108, 15 );
    assertEquals( expected, hyperlink.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 50, 15 );
    assertEquals( expected, hyperlink.computeSize( 50, 50 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
