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
package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ImageHyperlink;


@SuppressWarnings( "restriction" )
public class ImageHyperlinkLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ImageHyperlinkLCA lca;

  @Override
  protected void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ImageHyperlinkLCA();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRenderInitialImage() throws IOException {
    ImageHyperlink hyperlink = new ImageHyperlink( shell, SWT.NONE );

    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "image" ) );
  }

  public void testRenderImage() throws IOException {
    ImageHyperlink hyperlink = new ImageHyperlink( shell, SWT.NONE );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    hyperlink.setImage( image );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "image" );
    assertNotNull( actual.get( 0 ) );
    assertEquals( 100, actual.get( 1 ).asInt() );
    assertEquals( 50, actual.get( 2 ).asInt() );
  }

  public void testRenderImageUnchanged() throws IOException {
    ImageHyperlink hyperlink = new ImageHyperlink( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    hyperlink.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    ImageHyperlink hyperlink = new ImageHyperlink( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    hyperlink.setImage( image );

    Fixture.preserveWidgets();
    hyperlink.setImage( null );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( hyperlink, "image" ) );
  }
}
