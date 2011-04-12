/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageFactory_Test extends TestCase {

  private static final String TEST_PATH = "testpath";

  private static final ClassLoader CLASS_LOADER = ImageFactory_Test.class.getClassLoader();

  private Display display;
  private ImageFactory imageFactory;

  public void testFindImageByPathRegistersResource() {
    Image image1 = imageFactory.findImage( Fixture.IMAGE1, CLASS_LOADER );
    String registerPath = getRegisterPath( image1 );
    assertTrue( RWT.getResourceManager().isRegistered( registerPath ) );
  }

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

  public void testCreateImage() {
    InputStream stream1 = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image = imageFactory.createImage( display, TEST_PATH, stream1 );
    assertNotNull( image );
    assertNotNull( image.internalImage );
  }
  
  public void testCreateImageReturnsDistinctInstancesForSameStream() {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image1 = imageFactory.createImage( display, TEST_PATH, stream );
    Image image2 = imageFactory.createImage( display, TEST_PATH, stream );
    assertNotSame( image1, image2 );
    assertSame( image1.internalImage, image2.internalImage );
  }
  
  public void testCreateImageReturnsDisposableImage() {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image1 = imageFactory.createImage( display, TEST_PATH, stream );
    // image must be disposable, i.e. dispose must not throw an ISE
    image1.dispose();
  }

  public void testGetImagePath() {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    Image image = imageFactory.createImage( display, TEST_PATH, stream );
    String imagePath = ImageFactory.getImagePath( image );
    assertNotNull( imagePath );
    assertTrue( imagePath.length() > 0 );
  }

  public void testGetImagePathForNullImage() {
    String imagePath = ImageFactory.getImagePath( null );
    assertNull( imagePath );
  }

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    display = new Display();
    imageFactory = new ImageFactory();
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  private static String getRegisterPath( Image image ) {
    String imagePath = ImageFactory.getImagePath( image );
    int prefixLength = ResourceManagerImpl.RESOURCES.length() + 1;
    return imagePath.substring( prefixLength );
  }
}
