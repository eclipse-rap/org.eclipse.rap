/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public class ResourceFactory_Test extends TestCase {

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
    Image image1 = Graphics.getImage( RWTFixture.IMAGE_50x100,
                                      ResourceFactory_Test.class.getClassLoader() );
    assertNotNull( image1 );
    assertEquals( 1, ResourceFactory.imagesCount() );
    Image image1a = Graphics.getImage( RWTFixture.IMAGE_50x100,
                                       ResourceFactory_Test.class.getClassLoader() );
    assertSame( image1, image1a );
    assertEquals( 1, ResourceFactory.imagesCount() );
    Image image2 = Graphics.getImage( RWTFixture.IMAGE_100x50,
                                      ResourceFactory_Test.class.getClassLoader() );
    assertNotNull( image2 );
    assertEquals( 2, ResourceFactory.imagesCount() );
  }
  
  public void testGetImageData() throws Exception {
    Image image = Graphics.getImage( RWTFixture.IMAGE1,
                                     ResourceFactory_Test.class.getClassLoader() );
    ImageData imageData = ResourceFactory.getImageData( image );
    assertNotNull( imageData );
    assertTrue( imageData.width > 0 );
    assertTrue( imageData.height > 0 );
    ImageData imageData2 = ResourceFactory.getImageData( image );
    assertNotNull( imageData2 );
    assertEquals( imageData.data.length, imageData2.data.length );
    assertNotSame( imageData, imageData2 );
  }

  protected void setUp() throws Exception {
    // we do need the resource manager for this test
    Fixture.setUp();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    ResourceFactory.clear();
  }
}
