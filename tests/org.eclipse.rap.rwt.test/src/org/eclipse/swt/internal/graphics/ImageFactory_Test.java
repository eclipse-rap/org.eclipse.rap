/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ImageFactory_Test {

  private static final String TEST_PATH = "testpath";

  private static final ClassLoader CLASS_LOADER = ImageFactory_Test.class.getClassLoader();

  private Display display;
  private ImageFactory imageFactory;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    display = new Display();
    imageFactory = new ImageFactory();
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testFindImageByPathRegistersResource() {
    Image image1 = imageFactory.findImage( Fixture.IMAGE1, CLASS_LOADER );
    String registerPath = getRegisterPath( image1 );
    assertTrue( RWT.getResourceManager().isRegistered( registerPath ) );
  }

  @Test
  public void testFindImageByPathReturnsSharedImage() {
    Image image1 = imageFactory.findImage( Fixture.IMAGE1, CLASS_LOADER );
    Image image1a = imageFactory.findImage( Fixture.IMAGE1, CLASS_LOADER );
    assertNotNull( image1 );
    assertSame( image1, image1a );
    Image image2 = imageFactory.findImage( Fixture.IMAGE2, CLASS_LOADER );
    Image image2a = imageFactory.findImage( Fixture.IMAGE2, CLASS_LOADER );
    assertNotNull( image2 );
    assertSame( image2, image2a );
  }

  @Test
  public void testCreateImage() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image = imageFactory.createImage( display, TEST_PATH, stream );
    stream.close();
    assertNotNull( image );
    assertNotNull( image.internalImage );
  }

  @Test
  public void testCreateImageReturnsDistinctInstancesForSameStream() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image1 = imageFactory.createImage( display, TEST_PATH, stream );
    Image image2 = imageFactory.createImage( display, TEST_PATH, stream );
    stream.close();
    assertNotSame( image1, image2 );
    assertSame( image1.internalImage, image2.internalImage );
  }

  @Test
  public void testCreateImageReturnsDisposableImage() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image1 = imageFactory.createImage( display, TEST_PATH, stream );
    stream.close();
    // image must be disposable, i.e. dispose must not throw an ISE
    image1.dispose();
  }

  @Test
  public void testGetImagePath() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image = imageFactory.createImage( display, TEST_PATH, stream );
    stream.close();
    String imagePath = ImageFactory.getImagePath( image );
    assertNotNull( imagePath );
    assertTrue( imagePath.length() > 0 );
  }

  @Test
  public void testGetImagePathForNullImage() {
    String imagePath = ImageFactory.getImagePath( null );
    assertNull( imagePath );
  }

  private static String getRegisterPath( Image image ) {
    String imagePath = ImageFactory.getImagePath( image );
    int prefixLength = ResourceDirectory.DIRNAME.length() + 1;
    return imagePath.substring( prefixLength );
  }

}
