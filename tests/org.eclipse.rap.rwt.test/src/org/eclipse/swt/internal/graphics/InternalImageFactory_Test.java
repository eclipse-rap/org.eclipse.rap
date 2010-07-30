/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.swt.graphics.*;


public class InternalImageFactory_Test extends TestCase {

  public void testRegisterResource() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream inputStream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    String name = "testName";
    InternalImageFactory.registerResource( name, inputStream );
    assertTrue( ResourceManager.getInstance().isRegistered( name ) );
  }

  public void testReadImageData() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream inputStream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData data
      = InternalImageFactory.readImageData( new BufferedInputStream( inputStream ) );
    assertEquals( 100, data.width );
    assertEquals( 50, data.height );
  }

  public void testImageWithUndefinedType() {
    PaletteData paletteData = new PaletteData( new RGB[]{
      new RGB( 255, 0, 0 ), new RGB( 0, 255, 0 )
    } );
    ImageData imageData = new ImageData( 48, 48, 1, paletteData );
    for( int x = 11; x < 35; x++ ) {
      for( int y = 11; y < 35; y++ ) {
        imageData.setPixel( x, y, 1 );
      }
    }
    // imageData without type field should not throw SWT exception 
    assertNotNull( InternalImageFactory.createInputStream( imageData ) );
  }

  public void testInternalImagesFromInputStreamAreCached() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream1 = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    InternalImage internalImage1 = InternalImageFactory.findInternalImage( stream1 );
    InputStream stream2 = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    InternalImage internalImage2 = InternalImageFactory.findInternalImage( stream2 );
    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  public void testInternalImagesFromFilenameAreCached() throws IOException {
    File imageFile = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, imageFile );
    String path = imageFile.getAbsolutePath();
    InternalImage internalImage1 = InternalImageFactory.findInternalImage( path );
    InternalImage internalImage2 = InternalImageFactory.findInternalImage( path );
    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  public void testInternalImagesFromImageDataAreCached() {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    ImageData imageData1 = image.getImageData();
    ImageData imageData2 = image.getImageData();
    assertNotSame( imageData1, imageData2 );
    InternalImage internalImage1
      = InternalImageFactory.findInternalImage( imageData1 );
    InternalImage internalImage2
      = InternalImageFactory.findInternalImage( imageData2 );
    assertNotNull( internalImage1 );
    assertSame( internalImage1, internalImage2 );
  }

  public void testFindInternalImageWithPath() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream1 = loader.getResourceAsStream( Fixture.IMAGE1 );
    String key = "testkey";
    InternalImage internalImage1
      = InternalImageFactory.findInternalImage( key, stream1 );
    assertNotNull( internalImage1 );
    // second stream is not read
    InputStream stream2 = new ByteArrayInputStream( new byte[ 0 ] );
    InternalImage internalImage2
      = InternalImageFactory.findInternalImage( key, stream2 );
    assertSame( internalImage1, internalImage2 );
  }

  protected void setUp() throws Exception {
    Fixture.setUpWithoutResourceManager();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
    ResourceFactory.clear();
  }
}
