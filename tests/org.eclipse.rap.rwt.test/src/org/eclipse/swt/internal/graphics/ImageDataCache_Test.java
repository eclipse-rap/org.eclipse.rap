/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.graphics.Image;


public class ImageDataCache_Test extends TestCase {

  private ImageDataCache cache;

  protected void setUp() throws Exception {
    // we do need the resource manager for this test
    Fixture.setUp();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
    cache = new ImageDataCache();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testSmallImage() throws Exception {
    ImageData imageData = getImageData( RWTFixture.IMAGE1 ); // 129 bytes
    Image image = ResourceFactory.findImage( imageData );
    assertNull( cache.getImageData( image ) );
    cache.putImageData( image, imageData );
    assertEqualsImageData( imageData, cache.getImageData( image ) );
  }

  public void testTooBig() throws Exception {
    ImageData imageData = getImageData( RWTFixture.IMAGE_100x50 ); // 1281 bytes
    Image image = ResourceFactory.findImage( imageData );
    assertNull( cache.getImageData( image ) );
    cache.putImageData( image, imageData );
    assertNull( cache.getImageData( image ) );
  }

  public void testModifyData() throws Exception {
    ImageData origData = getImageData( RWTFixture.IMAGE1 );
    Image image = ResourceFactory.findImage( origData );
    cache.putImageData( image, origData );
    ImageData copyData1 = cache.getImageData( image );
    assertNotSame( origData, copyData1 );
    assertEqualsImageData( origData, copyData1 );
    // modify original data
    origData.setPixel( 0, 0, 23 );
    ImageData copyData2 = cache.getImageData( image );
    assertNotSame( copyData1, copyData2 );
    assertEqualsImageData( copyData1, copyData2 );
  }

  private static void assertEqualsImageData( final ImageData imageData1,
                                             final ImageData imageData2 )
  {
    assertEquals( imageData1.width, imageData2.width );
    assertEquals( imageData1.height, imageData2.height );
    assertTrue( Arrays.equals( imageData1.data, imageData2.data ) );
  }

  private ImageData getImageData( final String resource ) {
    IResourceManager manager = ResourceManager.getInstance();
    InputStream inputStream = manager.getResourceAsStream( resource );
    assertNotNull( inputStream );
    ImageData[] datas = ImageDataLoader.load( inputStream );
    assertNotNull( datas );
    assertEquals( 1, datas.length );
    ImageData imageData = datas[ 0 ];
    return imageData;
  }
}
