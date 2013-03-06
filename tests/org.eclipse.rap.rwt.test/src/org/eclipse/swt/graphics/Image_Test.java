/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class Image_Test {

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    device = new Display();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  //////////////////////////
  // InputStream constructor

  private Display device;

  @Test
  public void testStreamConstructorWithNullDevice() {
    device.dispose();
    try {
      new Image( null, new ByteArrayInputStream( new byte[ 0 ] ) );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testStreamConstructorWithNullInputStream() {
    try {
      new Image( device, ( InputStream )null );
      fail( "Must provide input stream for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testStreamConstructorUsesDefaultDisplay() throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( null, stream );
    stream.close();
    assertSame( Display.getCurrent(), image.getDevice() );
  }

  @Test
  public void testStreamConstructor() throws IOException {
    Image image = createImage( device, Fixture.IMAGE1 );

    assertEquals( new Rectangle( 0, 0, 58, 12 ), image.getBounds() );
  }

  @Test
  public void testStreamConstructorWithIllegalImage() {
    try {
      new Image( device, new ByteArrayInputStream( new byte[ 12 ] ) );
      fail( "Must throw exception when passing in invalid image data" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_UNSUPPORTED_FORMAT, e.code );
    }
  }

  ///////////////////////
  // Filename constructor

  @Test
  public void testFileConstructorWithNullDevice() {
    device.dispose();
    try {
      new Image( null, "" );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testFileConstructorWithNullFileName() {
    try {
      new Image( device, ( String )null );
      fail( "Must provide filename for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testFileConstructorUsesDefaultDisplay() throws IOException {
    File imageFile = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, imageFile );
    Image image = new Image( null, imageFile.getAbsolutePath() );
    assertSame( device, image.getDevice() );
    imageFile.delete();
  }

  @Test
  public void testFileConstructor() throws IOException {
    File testImage = new File( Fixture.TEMP_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE1, testImage );
    Image image = new Image( device, testImage.getAbsolutePath() );
    assertEquals( new Rectangle( 0, 0, 58, 12 ), image.getBounds() );
    testImage.delete();
  }

  @Test
  public void testFileConstructorWithMissingImage() {
    File missingImage = new File( Fixture.TEMP_DIR, "not-existing.gif" );
    try {
      new Image( device, missingImage.getAbsolutePath() );
      fail( "Image file must exist" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_IO, e.code );
    }
  }

  ////////////////////
  // Image constructor

  @Test
  public void testImageConstructorWithNullImage() {
    try {
      new Image( device, ( Image )null, SWT.IMAGE_COPY );
      fail( "Must provide image for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testImageConstructorWithIllegalFlag() throws IOException {
    Image image = createImage( device, Fixture.IMAGE1 );

    try {
      new Image( device, image, SWT.PUSH );
      fail( "Must not allow invalid flag" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testImageConstructor() throws IOException {
    Image image = createImage( device, Fixture.IMAGE1 );
    Image copiedImage = new Image( device, image, SWT.IMAGE_COPY );
    assertEquals( image.getBounds(), copiedImage.getBounds() );
    assertSame( image.internalImage, copiedImage.internalImage );
    image.dispose();
    assertFalse( copiedImage.isDisposed() );
  }

  ////////////////////////
  // ImageData constructor

  @Test
  public void testImageDataConstructor() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    stream.close();
    Image image = new Image( device, imageData );
    assertEquals( 100, image.getBounds().width );
    assertEquals( 50, image.getBounds().height );
  }

  @Test
  public void testImageDataConstructorWithNullDevice() throws IOException {
    device.dispose();
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    ImageData imageData = new ImageData( stream );
    stream.close();
    try {
      new Image( null, imageData );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  @Test
  public void testImageDataConstructorWithNullImageData() {
    try {
      new Image( device, ( ImageData )null );
      fail( "Must provide image data for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  ///////////////////////////
  // Width/Height constructor

  @Test
  public void testWidthHeightConstructor() {
    Fixture.useDefaultResourceManager();
    Image image = new Image( device, 1, 1 );
    ImageData imageData = image.getImageData();
    RGB[] rgbs = imageData.getRGBs();
    assertEquals( new RGB( 255, 255, 255 ), rgbs[ 0 ] );
    assertEquals( new Rectangle( 0, 0, 1, 1 ), image.getBounds() );
  }

  @Test
  public void testWidthHeightConstructorWithNullDevice() {
    device.dispose();
    try {
      new Image( null, 1, 1 );
      fail( "Must provide device for constructor" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testWidthHeightConstructorWithZeroWidth() {
    try {
      new Image( null, 0, 1 );
      fail( "Width must be a positive value" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testWidthHeightConstructorWithZeroHeight() {
    try {
      new Image( null, 1, 0 );
      fail( "Height must be a positive value" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  ////////////////
  // Image methods

  @Test
  public void testGetBounds() throws IOException {
    Image image1 = createImage( device, Fixture.IMAGE_100x50 );
    assertEquals( new Rectangle( 0, 0, 100, 50 ), image1.getBounds() );

    Image image2 = createImage( device, Fixture.IMAGE_100x50 );
    assertEquals( new Rectangle( 0, 0, 100, 50 ), image2.getBounds() );
  }

  @Test
  public void testGetBoundsWhenDisposed() throws IOException {
    Image image = createImage( device, Fixture.IMAGE1 );
    image.dispose();

    try {
      image.getBounds();
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, expected.code );
    }
  }

  @Test
  public void testGetImageData() throws IOException {
    Fixture.useDefaultResourceManager();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    stream.close();
    Image image = new Image( device, imageData );
    ImageData imageDataFromImage = image.getImageData();
    assertEquals( 100, imageDataFromImage.width );
    assertEquals( 50, imageDataFromImage.height );
  }

  @Test
  public void testGetImageDataWhenDisposed() throws IOException {
    Image image = createImage( device, Fixture.IMAGE1 );
    image.dispose();
    try {
      image.getImageData();
      fail();
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
  }

  @Test
  public void testSetBackgroundWhenDisposed() throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    Image image = new Image( device, stream );
    stream.close();
    image.dispose();
    try {
      image.setBackground( new Color( device, 0, 0, 0 ) );
      fail( "setBackground cannot be called on disposed image" );
    } catch( SWTException expected ) {
    }
  }

  @Test
  public void testSetBackgroundWithDisposedColor() throws IOException {
    Image image = createImage( device, Fixture.IMAGE_100x50 );
    Color disposedColor = new Color( device, 0, 0, 0 );
    disposedColor.dispose();
    try {
      image.setBackground( disposedColor );
      fail( "setBackground must not accept disposed color" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetBackgroundWithNullColor() throws IOException {
    Image image = createImage( device, Fixture.IMAGE_100x50 );
    try {
      image.setBackground( null );
      fail( "setBackground must not accept null-color" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testGetBackground() throws IOException {
    Image image = createImage( device, Fixture.IMAGE_100x50 );

    assertNull( image.getBackground() );
  }

  @Test
  public void testGetBackgroundWhenDisposed() throws IOException {
    Image image = createImage( device, Fixture.IMAGE_100x50 );
    image.dispose();

    try {
      image.getBackground();
      fail( "setBackground cannot be called on disposed image" );
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, expected.code );
    }
  }

  @Test
  public void testDispose() throws IOException {
    Image image = createImage( device, Fixture.IMAGE_100x50 );
    image.dispose();

    assertTrue( image.isDisposed() );
  }

  @Test
  public void testDisposeFactoryCreated() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    try {
      image.dispose();
      fail( "It is not allowed to dispose of a factory-created image" );
    } catch( IllegalStateException e ) {
      assertFalse( image.isDisposed() );
    }
  }

  @Test
  public void testEquality() throws IOException {
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE1 );
    Image anotherImage = Graphics.getImage( Fixture.IMAGE2 );
    assertTrue( image1.equals( image2 ) );
    assertFalse( image1.equals( anotherImage ) );

    image1 = createImage( device, Fixture.IMAGE1 );
    image2 = createImage( device, Fixture.IMAGE1 );
    assertFalse( image1.equals( image2 ) );

    image1 = createImage( device, Fixture.IMAGE1 );
    image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertFalse( image1.equals( image2 ) );
  }

  @Test
  public void testIdentity() throws IOException {
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertSame( image1, image2 );
    image1 = createImage( device, Fixture.IMAGE1 );
    image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertNotSame( image1, image2 );
  }

}
