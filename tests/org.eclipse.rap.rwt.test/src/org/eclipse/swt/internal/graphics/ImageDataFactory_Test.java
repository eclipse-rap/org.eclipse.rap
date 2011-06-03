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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;


public class ImageDataFactory_Test extends TestCase {
  private static final ClassLoader CLASS_LOADER = ImageDataFactory_Test.class.getClassLoader();
  
  private ImageDataFactory imageDataFactory;

  public void testFindImageData() {
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, CLASS_LOADER );
    IResourceManager resourceManager = RWT.getResourceManager();
    assertTrue( resourceManager.isRegistered( image.internalImage.getResourceName() ) );
    ImageData imageData = imageDataFactory.findImageData( image.internalImage );
    assertNotNull( imageData );
    assertEquals( 50, imageData.width );
    assertEquals( 100, imageData.height );
  }
  
  public void testFindImageDataUsesCachedImage() {
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, CLASS_LOADER );
    ImageData imageData1 = imageDataFactory.findImageData( image.internalImage );
    ImageData imageData2 = imageDataFactory.findImageData( image.internalImage );
    assertNotSame( imageData1, imageData2 );
    assertEquals( imageData1.data.length, imageData2.data.length );
  }

  public void testFindImageDataWithBlankImage() {
    Image blankImage = Graphics.getImage( "resources/images/blank.gif", CLASS_LOADER );
    ImageData blankData = imageDataFactory.findImageData( blankImage.internalImage );
    assertNotNull( blankData );
    assertEquals( 1, blankData.width );
    assertEquals( 1, blankData.height );
  }

  public void testFindImageDataWithNull() {
    try {
      imageDataFactory.findImageData( null );
      fail( "Must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  protected void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.useDefaultResourceManager();
    imageDataFactory = new ImageDataFactory( RWTFactory.getResourceManager() );
  }

  protected void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }
}
