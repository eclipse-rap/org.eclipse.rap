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
package org.eclipse.swt.graphics;

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


public class Image_Test extends TestCase {

  //////////////////////////
  // InputStream constructor

  public void testStreamConstructorWithNullDevice() {
    try {
      new Image( null, new ByteArrayInputStream( new byte[ 0 ] ) );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testStreamConstructorWithNullInputStream() {
    try {
      new Image( new Display(), ( InputStream )null );
      fail( "Must provide input stream for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testStreamConstructorUsesDefaultDisplay() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    new Display();
    Image image = new Image( null, stream );
    assertSame( Display.getCurrent(), image.getDevice() );
  }

  public void testStreamConstructor() throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    assertEquals( new Rectangle( 0, 0, 58, 12 ), image.getBounds() );
    stream.close();
  }

  public void testStreamConstructorWithIllegalImage() {
    Display display = new Display();
    try {
      new Image( display, new ByteArrayInputStream( new byte[ 12 ] ) );
      fail( "Must throw exception when passing in invalid image data" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_UNSUPPORTED_FORMAT, e.code );
    }
  }

  ///////////////////////
  // Filename constructor

  public void testFileConstructorWithNullDevice() {
    try {
      new Image( null, "" );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testFileConstructorWithNullFileName() {
    try {
      new Image( new Display(), (String)null );
      fail( "Must provide filename for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testFileConstructorUsesDefaultDisplay() throws IOException {
    new Display();
    File imageFile = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, imageFile );
    Image image = new Image( null, imageFile.getAbsolutePath() );
    assertSame( Display.getCurrent(), image.getDevice() );
    imageFile.delete();
  }

  public void testFileConstructor() throws IOException {
    File testImage = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, testImage );
    Display display = new Display();
    Image image = new Image( display, testImage.getAbsolutePath() );
    assertEquals( new Rectangle( 0, 0, 58, 12 ), image.getBounds() );
    testImage.delete();
  }

  public void testFileConstructorWithMissingImage() {
    Display display = new Display();
    File missingImage = new File( Fixture.TEMP_DIR, "not-existing.gif" );
    try {
      new Image( display, missingImage.getAbsolutePath() );
      fail( "Image file must exist" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_IO, e.code );
    }
  }

  ////////////////////
  // Image constructor

  public void testImageConstructorWithNullImage() {
    try {
      new Image( new Display(), (Image)null, SWT.IMAGE_COPY );
      fail( "Must provide image for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testImageConstructorWithIllegalFlag() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    try {
      new Image( display, image, SWT.PUSH );
      fail( "Must not allow invalid flag" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testImageConstructor() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    Image copiedImage = new Image( display, image, SWT.IMAGE_COPY );
    assertEquals( image.getBounds(), copiedImage.getBounds() );
    assertSame( image.internalImage, copiedImage.internalImage );
    image.dispose();
    assertFalse( copiedImage.isDisposed() );
  }

  ////////////////////////
  // ImageData constructor
  
  public void testImageDataConstructor() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    Image image = new Image( display, imageData );
    assertEquals( 100, image.getBounds().width );
    assertEquals( 50, image.getBounds().height );
  }

  public void testImageDataConstructorWithNullDevice() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    ImageData imageData = new ImageData( stream );
    try {
      new Image( null, imageData );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testImageDataConstructorWithNullImageData() {
    try {
      new Image( new Display(), ( ImageData )null );
      fail( "Must provide image data for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }
  
  ///////////////////////////
  // Width/Height constructor
  
  public void testWidthHeightConstructor() {
    Display device = new Display();
    Image image = new Image( device, 1, 1 );
    ImageData imageData = image.getImageData();
    RGB[] rgbs = imageData.getRGBs();
    assertEquals( new RGB( 255, 255, 255 ), rgbs[ 0 ] );
    assertEquals( new Rectangle( 0, 0, 1, 1 ), image.getBounds() );
  }

  public void testWidthHeightConstructorWithNullDevice() {
    try {
      new Image( null, 1, 1 );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testWidthHeightConstructorWithZeroWidth() {
    try {
      new Image( null, 0, 1 );
      fail( "Width must be a positive value" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testWidthHeightConstructorWithZeroHeight() {
    try {
      new Image( null, 1, 0 );
      fail( "Height must be a positive value" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  ////////////////
  // Image methods

  public void testGetBounds() {
    ClassLoader loader = Fixture.class.getClassLoader();
    Display display = new Display();
    InputStream stream1 = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image1 = new Image( display, stream1 );
    assertEquals( new Rectangle( 0, 0, 100, 50 ), image1.getBounds() );
    InputStream stream2 = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image2 = new Image( display, stream2 );
    assertEquals( new Rectangle( 0, 0, 100, 50 ), image2.getBounds() );
  }

  public void testGetBoundsWhenDisposed() {
    ClassLoader loader = Fixture.class.getClassLoader();
    Display display = new Display();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      image.getBounds();
      fail();
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
  }

  public void testGetImageData() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    Device device = new Display();
    Image image = new Image( device, imageData );
    ImageData imageDataFromImage = image.getImageData();
    assertEquals( 100, imageDataFromImage.width );
    assertEquals( 50, imageDataFromImage.height );
  }

  public void testGetImageDataWhenDisposed() {
    ClassLoader loader = Fixture.class.getClassLoader();
    Display display = new Display();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      image.getImageData();
      fail();
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
  }

  public void testSetBackgroundWhenDisposed() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      image.setBackground( new Color( display, 0, 0, 0 ) );
      fail( "setBackground cannot be called on disposed image" );
    } catch( SWTException expected ) {
    }
  }

  public void testSetBackgroundWithDisposedColor() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( display, stream );
    Color disposedColor = new Color( display, 0, 0, 0 );
    disposedColor.dispose();
    try {
      image.setBackground( disposedColor );
      fail( "setBackground must not accept disposed color" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetBackgroundWithNullColor() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( display, stream );
    try {
      image.setBackground( null );
      fail( "setBackground must not accept null-color" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testGetBackground() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( display, stream );
    assertNull( image.getBackground() );
  }

  public void testGetBackgroundWhenDisposed() {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      image.getBackground();
      fail( "setBackground cannot be called on disposed image" );
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, expected.code );
    }
  }

  public void testDispose() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    image.dispose();
    assertTrue( image.isDisposed() );
    try {
      stream.close();
    } catch( IOException e ) {
      fail( "Unable to close input stream." );
    }
  }

  public void testDisposeFactoryCreated() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    try {
      image.dispose();
      fail( "It is not allowed to dispose of a factory-created image" );
    } catch( IllegalStateException e ) {
      assertFalse( image.isDisposed() );
    }
  }

  public void testEquality() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream;
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE1 );
    Image anotherImage = Graphics.getImage( Fixture.IMAGE2 );
    assertTrue( image1.equals( image2 ) );
    assertFalse( image1.equals( anotherImage ) );
    Device device = new Display();
    stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    image1 = new Image( device, stream );
    stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    image2 = new Image( device, stream );
    assertFalse( image1.equals( image2 ) );
    stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    image1 = new Image( device, stream );
    image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertFalse( image1.equals( image2 ) );
  }

  public void testIdentity() {
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertSame( image1, image2 );
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Device device = new Display();
    image1 = new Image( device, stream );
    image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertNotSame( image1, image2 );
  }

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }
}
