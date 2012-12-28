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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class InternalImageFactory_Test {

  private static final ClassLoader CLASS_LOADER = InternalImageFactory_Test.class.getClassLoader();

  private static final String IMAGE_SAMPLE1 = "resources/images/image-sample1.png";
  private static final String IMAGE_SAMPLE2 = "resources/images/image-sample2.png";
  private static final String IMAGE_OK = "resources/images/ok.png";
  private static final String IMAGE_OK_BLACK = "resources/images/ok-black.png";

  private InternalImageFactory internalImageFactory;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    internalImageFactory = new InternalImageFactory();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testRegisterResource() throws IOException {
    InputStream inputStream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE_100x50 );
    String name = "testName";
    RWT.getResourceManager().register( name, inputStream );
    inputStream.close();

    assertTrue( RWT.getResourceManager().isRegistered( name ) );
  }

  @Test
  public void testReadImageData() throws IOException {
    InputStream inputStream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData data = InternalImageFactory.readImageData( new BufferedInputStream( inputStream ) );
    inputStream.close();

    assertEquals( 100, data.width );
    assertEquals( 50, data.height );
  }

  @Test
  public void testImageWithUndefinedType() {
    // imageData without type field should not throw SWT exception
    assertNotNull( InternalImageFactory.createInputStream( createImageDataWithoutType() ) );
  }

  @Test
  public void testInternalImagesFromInputStreamAreCached() throws IOException {
    InputStream stream1 = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE_100x50 );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( stream1 );
    stream1.close();
    InputStream stream2 = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE_100x50 );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( stream2 );
    stream2.close();

    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  @Test
  public void testInternalImagesFromFilenameAreCached() throws IOException {
    File imageFile = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, imageFile );
    String path = imageFile.getAbsolutePath();
    InternalImage internalImage1 = internalImageFactory.findInternalImage( path );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( path );
    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testInternalImagesFromImageDataAreCached() {
    new Display();
    Fixture.useDefaultResourceManager();
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    ImageData imageData1 = image.getImageData();
    ImageData imageData2 = image.getImageData();
    assertNotSame( imageData1, imageData2 );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( imageData1 );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( imageData2 );
    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  @Test
  public void testInternalImagesDifferForDifferentPalettes() {
    PaletteData palette1 = new PaletteData( new RGB[] { new RGB( 23, 1, 7 ) } );
    PaletteData palette2 = new PaletteData( new RGB[] { new RGB( 3, 5, 42 ) } );
    ImageData imageData1 = new ImageData( 8, 8, 8, palette1  );
    ImageData imageData2 = new ImageData( 8, 8, 8, palette2 );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( imageData1 );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( imageData2 );
    assertNotSame( internalImage1, internalImage2 );
  }

  @Test
  public void testInternalImagesDifferForDifferentPalettes2() {
    PaletteData palette1 = new PaletteData( new RGB[] { new RGB( 1, 2, 3 ) } );
    PaletteData palette2 = new PaletteData( 1, 2, 3 );
    ImageData imageData1 = new ImageData( 8, 8, 8, palette1  );
    ImageData imageData2 = new ImageData( 8, 8, 8, palette2 );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( imageData1 );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( imageData2 );
    assertNotSame( internalImage1, internalImage2 );
  }

  // Regression test for bug 326888
  @Test
  public void testInternalImagesDifferForSimilarImageData() throws IOException {
    InputStream stream1 = CLASS_LOADER.getResourceAsStream( IMAGE_SAMPLE1 );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( stream1 );
    stream1.close();
    InputStream stream2 = CLASS_LOADER.getResourceAsStream( IMAGE_SAMPLE2 );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( stream2 );
    stream2.close();

    assertNotNull( internalImage1 );
    assertNotSame( internalImage1, internalImage2 );
  }

  // Regression test for bug 326888
  @Test
  public void testInternalImagesDifferForDifferentColor() throws IOException {
    InputStream stream1 = CLASS_LOADER.getResourceAsStream( IMAGE_OK );
    InternalImage internalImage1 = internalImageFactory.findInternalImage( stream1 );
    stream1.close();
    InputStream stream2 = CLASS_LOADER.getResourceAsStream( IMAGE_OK_BLACK );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( stream2 );
    stream2.close();

    assertNotNull( internalImage1 );
    assertNotSame( internalImage1, internalImage2 );
  }

  @Test
  public void testFindInternalImageWithPath() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    String key = "testkey";
    InternalImage internalImage1 = internalImageFactory.findInternalImage( key, stream );
    stream.close();
    assertNotNull( internalImage1 );
    // second stream is not read
    InputStream stream2 = new ByteArrayInputStream( new byte[ 0 ] );
    InternalImage internalImage2 = internalImageFactory.findInternalImage( key, stream2 );
    assertSame( internalImage1, internalImage2 );
  }

  @Test
  public void testImageExtension_PNG() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( IMAGE_OK );
    InternalImage internalImage = internalImageFactory.findInternalImage( stream );
    stream.close();

    assertTrue( internalImage.getResourceName().endsWith( ".png" ) );
  }

  @Test
  public void testImageExtension_GIF() throws IOException {
    InputStream stream = CLASS_LOADER.getResourceAsStream( Fixture.IMAGE1 );
    InternalImage internalImage = internalImageFactory.findInternalImage( stream );
    stream.close();

    assertTrue( internalImage.getResourceName().endsWith( ".gif" ) );
  }

  @Test
  public void testImageExtension_UndefinedType() throws IOException {
    InputStream stream = InternalImageFactory.createInputStream( createImageDataWithoutType() );

    InternalImage internalImage = internalImageFactory.findInternalImage( stream );
    stream.close();

    assertTrue( internalImage.getResourceName().endsWith( ".png" ) );
  }

  private ImageData createImageDataWithoutType() {
    PaletteData paletteData = new PaletteData( new RGB[]{
      new RGB( 255, 0, 0 ), new RGB( 0, 255, 0 )
    } );
    ImageData result = new ImageData( 48, 48, 1, paletteData );
    for( int x = 11; x < 35; x++ ) {
      for( int y = 11; y < 35; y++ ) {
        result.setPixel( x, y, 1 );
      }
    }
    return result;
  }

}
