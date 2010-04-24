/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.widgets.Display;


public class Image_Test extends TestCase {

  public void testImageFinder() {
    IResourceManager manager = ResourceManager.getInstance();
    // only if you comment initial registration in
    // org.eclipse.swt.internal.widgets.displaykit.QooxdooResourcesUtil
    assertFalse( manager.isRegistered( Fixture.IMAGE1 ) );
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    assertTrue( manager.isRegistered( Fixture.IMAGE1 ) );
    File contextDir = new File( Fixture.CONTEXT_DIR,
                                ResourceManagerImpl.RESOURCES );
    assertTrue( new File( contextDir, Fixture.IMAGE1 ).exists() );
    Image image2 = Graphics.getImage( Fixture.IMAGE1 );
    assertTrue( manager.isRegistered( Fixture.IMAGE1 ) );
    assertSame( image1, image2 );
    assertEquals( ResourceFactory.getImagePath( image1 ),
                  ResourceFactory.getImagePath( image2 ) );
    // another picture
    Graphics.getImage( Fixture.IMAGE2 );
    assertTrue( manager.isRegistered( Fixture.IMAGE2 ) );
    assertTrue( new File( contextDir, Fixture.IMAGE2 ).exists() );
    // ... and do it again...
    image1 = Graphics.getImage( Fixture.IMAGE1 );
    assertTrue( manager.isRegistered( Fixture.IMAGE1 ) );
  }

  public void testImageFinderWithClassLoader() throws IOException {
    File testGif = new File( Fixture.CONTEXT_DIR, "test.gif" );
    Fixture.copyTestResource( Fixture.IMAGE3, testGif );
    URL[] urls = new URL[] { Fixture.CONTEXT_DIR.toURI().toURL() };
    URLClassLoader classLoader = new URLClassLoader( urls, null );

    IResourceManager manager = ResourceManager.getInstance();
    assertFalse( manager.isRegistered( Fixture.IMAGE3 ) );
    try {
      Graphics.getImage( "test.gif" );
      fail( "Image not available on the classpath." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    Image image = Graphics.getImage( "test.gif", classLoader );
    assertNotNull( image );
  }

  public void testImageFinderWithInputStream() throws IOException {
    String imageName = "testIS.gif";
    File testGif = new File( Fixture.CONTEXT_DIR, imageName );
    Fixture.copyTestResource( Fixture.IMAGE3, testGif );
    URL[] urls = new URL[] { Fixture.CONTEXT_DIR.toURI().toURL() };
    URLClassLoader classLoader = new URLClassLoader( urls, null );

    IResourceManager manager = ResourceManager.getInstance();
    assertFalse( manager.isRegistered( Fixture.IMAGE3 ) );
    try {
      Graphics.getImage( imageName );
      fail( "Image not available on the classpath." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    InputStream is = classLoader.getResourceAsStream( imageName );
    Image image = Graphics.getImage( "test.gif", is );
    assertNotNull( image );
  }

  public void testFindWithIllegalArguments() {
    try {
      Graphics.getImage( null );
      fail( "Image#find must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Graphics.getImage( "" );
      fail( "Image#find must not allow empty string argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Graphics.getImage( "", new ByteArrayInputStream( new byte[ 1 ] ) );
      fail( "Image#find must not allow empty string argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

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
      new Image( new Display(), (InputStream)null );
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

  public void testImageConstructorWithIllegalFlag() throws Exception {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    try {
      new Image( display, image, SWT.PUSH );
      fail( "Must not allow invalid flag" );
    } catch( Exception e ) {
      // expected
    }
  }

  public void testImageConstructor() throws Exception {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Display display = new Display();
    Image image = new Image( display, stream );
    Image copiedImage = new Image( display, image, SWT.IMAGE_COPY );
    assertEquals( image.getBounds(), copiedImage.getBounds() );
    assertFalse( image.resourceName.equals( copiedImage.resourceName ) );
    image.dispose();
    assertFalse( copiedImage.isDisposed() );
  }

  ////////////////////////
  // ImageData constructor

  public void testDataConstructorWithNullDevice() {
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

  public void testDataConstructorWithNullImageData() {
    try {
      new Image( new Display(), (ImageData)null );
      fail( "Must provide image data for constructor" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "Argument cannot be null", e.getMessage() );
    }
  }

  public void testImageDataConstructor() throws Exception {
    Display display = new Display();
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    Image image = new Image( display, imageData );
    assertEquals( 100, image.getBounds().width );
    assertEquals( 50, image.getBounds().height );
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

  public void testGetImageData() throws Exception {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    ImageData imageData = new ImageData( stream );
    Device device = new Display();
    Image image = new Image( device , imageData );
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
    // we do need the resource manager for this test
    Fixture.setUpWithoutResourceManager();
    Fixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
