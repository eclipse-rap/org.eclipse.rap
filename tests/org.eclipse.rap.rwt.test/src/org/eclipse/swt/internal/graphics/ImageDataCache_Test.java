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
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.graphics.ImageData;


public class ImageDataCache_Test extends TestCase {

  public void testSmallImageIsCached() {
    ImageDataCache cache = new ImageDataCache();
    ImageData imageData = getImageData( Fixture.IMAGE1 ); // 129 bytes
    InternalImage internalImage
      = new InternalImage( "testpath", imageData.width, imageData.height );
    cache.putImageData( internalImage, imageData );
    assertEqualsImageData( imageData, cache.getImageData( internalImage ) );
  }

  public void testBigImageIsNotCached() {
    ImageDataCache cache = new ImageDataCache();
    ImageData imageData = getImageData( Fixture.IMAGE_100x50 ); // 1281 bytes
    InternalImage internalImage
      = new InternalImage( "testpath", imageData.width, imageData.height );
    cache.putImageData( internalImage, imageData );
    assertNull( cache.getImageData( internalImage ) );
  }

  public void testSafeCopiesReturned() {
    ImageDataCache cache = new ImageDataCache();
    ImageData originalData = getImageData( Fixture.IMAGE1 );
    InternalImage internalImage
      = new InternalImage( "testpath", originalData.width, originalData.height );
    cache.putImageData( internalImage, originalData );
    ImageData copyData = cache.getImageData( internalImage );
    assertNotSame( originalData, copyData );
    assertEqualsImageData( originalData, copyData );
  }

  public void testSafeCopiesStored() {
    ImageDataCache cache = new ImageDataCache();
    ImageData originalData = getImageData( Fixture.IMAGE1 );
    InternalImage internalImage
      = new InternalImage( "testpath", originalData.width, originalData.height );
    cache.putImageData( internalImage, originalData );
    ImageData copyData1 = cache.getImageData( internalImage );
    // modify original data
    originalData.setPixel( 0, 0, 23 );
    ImageData copyData2 = cache.getImageData( internalImage );
    assertEqualsImageData( copyData1, copyData2 );
  }

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  private static ImageData getImageData( String resource ) {
    InputStream inputStream = RWT.getResourceManager().getResourceAsStream( resource );
    return new ImageData( inputStream );
  }

  private static void assertEqualsImageData( ImageData imageData1, ImageData imageData2 ) {
    assertEquals( imageData1.width, imageData2.width );
    assertEquals( imageData1.height, imageData2.height );
    assertTrue( Arrays.equals( imageData1.data, imageData2.data ) );
  }
}
