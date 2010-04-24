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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public class ResourceFactory_Test extends TestCase {

  public static final String IMAGE_BLANK_PIXEL = "resources/images/blank.gif";

  public void testGetColor() throws Exception {
    assertEquals( 0, ResourceFactory.colorsCount() );
    Color color = Graphics.getColor( 15, 127, 255 );
    assertEquals( 15, color.getRed() );
    assertEquals( 127, color.getGreen() );
    assertEquals( 255, color.getBlue() );
    assertEquals( 1, ResourceFactory.colorsCount() );
    Color red = Graphics.getColor( 255, 0, 0 );
    assertEquals( 2, ResourceFactory.colorsCount() );
    Color red2 = Graphics.getColor( 255, 0, 0 );
    assertEquals( 2, ResourceFactory.colorsCount() );
    assertSame( red, red2 );
  }

  public void testGetFont() throws Exception {
    assertEquals( 0, ResourceFactory.fontsCount() );
    Font font1 = Graphics.getFont( "Times", 12, SWT.BOLD );
    assertEquals( "Times", font1.getFontData()[ 0 ].getName() );
    assertEquals( 12, font1.getFontData()[ 0 ].getHeight() );
    assertEquals( SWT.BOLD, font1.getFontData()[ 0 ].getStyle() );
    assertNotNull( font1 );
    assertEquals( 1, ResourceFactory.fontsCount() );
    Font font1a = Graphics.getFont( "Times", 12, SWT.BOLD );
    assertSame( font1, font1a );
    assertEquals( 1, ResourceFactory.fontsCount() );
  }

  public void testGetImage() throws Exception {
    assertEquals( 0, ResourceFactory.imagesCount() );
    ClassLoader classLoader = ResourceFactory_Test.class.getClassLoader();
    Image image1 = Graphics.getImage( Fixture.IMAGE_50x100, classLoader );
    assertNotNull( image1 );
    assertEquals( 1, ResourceFactory.imagesCount() );
    Image image1a = Graphics.getImage( Fixture.IMAGE_50x100, classLoader );
    assertSame( image1, image1a );
    assertEquals( 1, ResourceFactory.imagesCount() );
    Image image2 = Graphics.getImage( Fixture.IMAGE_100x50, classLoader );
    assertNotNull( image2 );
    assertEquals( 2, ResourceFactory.imagesCount() );
  }

  public void testGetImageData() throws Exception {
    ClassLoader classLoader = ResourceFactory_Test.class.getClassLoader();
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, classLoader );
    ImageData imageData = ResourceFactory.getImageData( image );
    assertNotNull( imageData );
    assertEquals( 50, imageData.width );
    assertEquals( 100, imageData.height );
    ImageData imageData2 = ResourceFactory.getImageData( image );
    assertNotNull( imageData2 );
    assertEquals( imageData.data.length, imageData2.data.length );
    assertNotSame( imageData, imageData2 );
    Image blankImage = Graphics.getImage( "resources/images/blank.gif",
                                          classLoader );
    ImageData blankData = ResourceFactory.getImageData( blankImage );
    assertNotNull( blankData );
    assertEquals( 1, blankData.width );
    assertEquals( 1, blankData.height );

    try {
      ResourceFactory.getImageData( null );
      fail( "Must not allow null-argument" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testRegisterImage() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream inputStream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    String name = "testName";
    Point size = ResourceFactory.registerImage( name, inputStream );
    assertEquals( 100, size.x );
    assertEquals( 50, size.y );
    assertTrue( ResourceManager.getInstance().isRegistered( name ) );
  }

  public void testRegisterImageWithInvalidInput() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream( new byte[ 16 ] );
    String name = "testName";
    Point size = ResourceFactory.registerImage( name, inputStream );
    assertNull( size );
    // TODO [rst] Does it make sense to register invalid images?
    assertTrue( ResourceManager.getInstance().isRegistered( name ) );
  }

  protected void setUp() throws Exception {
    // we do need the resource manager for this test
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
