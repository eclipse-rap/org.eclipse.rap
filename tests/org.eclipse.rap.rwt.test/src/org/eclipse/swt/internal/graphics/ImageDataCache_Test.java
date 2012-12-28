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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.ImageData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ImageDataCache_Test {

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testSmallImageIsCached() throws IOException {
    ImageDataCache cache = new ImageDataCache();
    ImageData imageData = getImageData( Fixture.IMAGE1 ); // 129 bytes
    InternalImage internalImage
      = new InternalImage( "testpath", imageData.width, imageData.height );
    cache.putImageData( internalImage, imageData );
    assertEqualsImageData( imageData, cache.getImageData( internalImage ) );
  }

  @Test
  public void testBigImageIsNotCached() throws IOException {
    ImageDataCache cache = new ImageDataCache();
    ImageData imageData = getImageData( Fixture.IMAGE_100x50 ); // 1281 bytes
    InternalImage internalImage
      = new InternalImage( "testpath", imageData.width, imageData.height );
    cache.putImageData( internalImage, imageData );
    assertNull( cache.getImageData( internalImage ) );
  }

  @Test
  public void testSafeCopiesReturned() throws IOException {
    ImageDataCache cache = new ImageDataCache();
    ImageData originalData = getImageData( Fixture.IMAGE1 );
    InternalImage internalImage
      = new InternalImage( "testpath", originalData.width, originalData.height );
    cache.putImageData( internalImage, originalData );
    ImageData copyData = cache.getImageData( internalImage );
    assertNotSame( originalData, copyData );
    assertEqualsImageData( originalData, copyData );
  }

  @Test
  public void testSafeCopiesStored() throws IOException {
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

  private ImageData getImageData( String resource ) throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( resource );
    try {
      return new ImageData( inputStream );
    } finally {
      inputStream.close();
    }
  }

  private static void assertEqualsImageData( ImageData imageData1, ImageData imageData2 ) {
    assertEquals( imageData1.width, imageData2.width );
    assertEquals( imageData1.height, imageData2.height );
    assertTrue( Arrays.equals( imageData1.data, imageData2.data ) );
  }

}
