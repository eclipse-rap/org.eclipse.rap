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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ImageHyperlink_Test extends TestCase {

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    ImageHyperlink hyperlink
      = toolkit.createImageHyperlink( form.getBody(), SWT.NONE );
    assertNotNull( hyperlink );
    assertEquals( null, hyperlink.getImage() );
    Image image = Graphics.getImage( RWTFixture.IMAGE_100x50 );
    hyperlink.setImage( image );
    assertEquals( image, hyperlink.getImage() );
  }

  public void testComputeSize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    ImageHyperlink hyperlink
      = toolkit.createImageHyperlink( form.getBody(), SWT.NONE );
    assertNotNull( hyperlink );
    assertEquals( null, hyperlink.getImage() );
    Image image = Graphics.getImage( RWTFixture.IMAGE_100x50 );
    hyperlink.setImage( image );
    Point expected = new Point( 109, 52 );
    assertEquals( expected, hyperlink.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 50, 52 );
    assertEquals( expected, hyperlink.computeSize( 50, 50 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
