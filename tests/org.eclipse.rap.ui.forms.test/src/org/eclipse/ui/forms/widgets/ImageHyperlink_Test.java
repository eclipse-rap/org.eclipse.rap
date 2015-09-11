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

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit.ImageHyperlinkLCA;
import org.junit.*;


@SuppressWarnings( "restriction" )
public class ImageHyperlink_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite shell;
  private ImageHyperlink hyperlink;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( shell.getDisplay() );
    Form form = toolkit.createForm( shell );
    form.getBody().setLayout( new TableWrapLayout() );
    hyperlink = toolkit.createImageHyperlink( form.getBody(), SWT.NONE );
  }

  @Test
  public void testCreated() throws IOException {
    assertNotNull( hyperlink );
    assertEquals( null, hyperlink.getImage() );
  }

  @Test
  public void testImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    hyperlink.setImage( image );

    assertEquals( image, hyperlink.getImage() );
  }

  @Test
  public void testComputeSize() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    hyperlink.setImage( image );

    assertEquals( new Point( 109, 52 ), hyperlink.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    assertEquals( new Point( 50, 52 ), hyperlink.computeSize( 50, 50 ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( hyperlink.getAdapter( WidgetLCA.class ) instanceof ImageHyperlinkLCA );
    assertSame( hyperlink.getAdapter( WidgetLCA.class ), hyperlink.getAdapter( WidgetLCA.class ) );
  }

}
