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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;


public class ImageDataFactory_Test extends TestCase {

  public void testFindImageData() {
    ClassLoader classLoader = InternalImageFactory_Test.class.getClassLoader();
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, classLoader );
    assertTrue( ResourceManager.getInstance().isRegistered( image.internalImage.getResourceName() ) );
    ImageData imageData = ImageDataFactory.findImageData( image.internalImage );
    assertNotNull( imageData );
    assertEquals( 50, imageData.width );
    assertEquals( 100, imageData.height );
    ImageData imageData2 = ImageDataFactory.findImageData( image.internalImage );
    assertNotNull( imageData2 );
    assertNotSame( imageData, imageData2 );
    assertEquals( imageData.data.length, imageData2.data.length );
  }

  public void testFindImageDataWithBlankImage() {
    ClassLoader classLoader = InternalImageFactory_Test.class.getClassLoader();
    Image blankImage = Graphics.getImage( "resources/images/blank.gif",
                                          classLoader );
    ImageData blankData
      = ImageDataFactory.findImageData( blankImage.internalImage );
    assertNotNull( blankData );
    assertEquals( 1, blankData.width );
    assertEquals( 1, blankData.height );
  }

  public void testFindImageDataWithNull() {
    try {
      ImageDataFactory.findImageData( null );
      fail( "Must not allow null-argument" );
    } catch( NullPointerException e ) {
      // expected
    }
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
  }
}
