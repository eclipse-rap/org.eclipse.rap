/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExternalImageDescriptor_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    display = new Display();
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test( expected = NullPointerException.class )
  public void testConstructor_withNullUrl() {
    new ExternalImageDescriptor( null, 1, 2 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithZeroWidth() {
    new ExternalImageDescriptor( "url", 0, 1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithNegativeWidth() {
    new ExternalImageDescriptor( "url", -1, 1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithZeroHeight() {
    new ExternalImageDescriptor( "url", 1, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithNegativeHeight() {
    new ExternalImageDescriptor( "url", 1, 0 );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateImage_nullDevice() {
    new ExternalImageDescriptor( "url", 1, 2 ).createImage( null );
  }

  @Test
  public void testCreateImage() {
    String url = "http://foo.org/bar.png";
    ExternalImageDescriptor imageDescriptor = new ExternalImageDescriptor( url, 1, 2 );
    Image image = imageDescriptor.createImage( display );

    assertNotNull( image );
    assertEquals( new Rectangle( 0, 0, 1, 2 ), image.getBounds() );
    assertNull( image.getImageData() );
    assertEquals( "http://foo.org/bar.png", image.internalImage.getResourceName() );
    assertSame( display, image.getDevice() );
  }

  @Test
  public void testCreateImage_usesSameInternalImage() {
    String url = "http://foo.org/bar.png";
    ExternalImageDescriptor imageDescriptor = new ExternalImageDescriptor( url, 1, 2 );

    Image image1 = imageDescriptor.createImage( display );
    Image image2 = imageDescriptor.createImage( display );

    assertSame( image1.internalImage, image2.internalImage );
  }

}
