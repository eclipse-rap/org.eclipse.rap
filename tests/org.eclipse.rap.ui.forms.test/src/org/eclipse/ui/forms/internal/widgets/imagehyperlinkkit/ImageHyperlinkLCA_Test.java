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
package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.junit.*;


@SuppressWarnings( "restriction" )
public class ImageHyperlinkLCA_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ImageHyperlinkLCA lca;

  private ImageHyperlink hyperlink;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ImageHyperlinkLCA();
    hyperlink = new ImageHyperlink( shell, SWT.NONE );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    hyperlink.setImage( image );

    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "image" );
    assertNotNull( actual.get( 0 ) );
    assertEquals( 100, actual.get( 1 ).asInt() );
    assertEquals( 50, actual.get( 2 ).asInt() );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    hyperlink.setImage( image );
    Fixture.preserveWidgets();

    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    hyperlink.setImage( image );

    Fixture.preserveWidgets();
    hyperlink.setImage( null );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( hyperlink, "image" ) );
  }

}
