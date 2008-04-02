/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import java.io.InputStream;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.graphics.Image;

import junit.framework.TestCase;


public class ImageDataCache_Test extends TestCase {

  private ImageDataCache cache;

  protected void setUp() throws Exception {
    // we do need the resource manager for this test
    Fixture.setUp();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
    cache = new ImageDataCache();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testSmallImage() throws Exception {
    ImageData imageData = getImageData( RWTFixture.IMAGE1 ); // 129 bytes
    Image image = ResourceFactory.findImage( imageData );
    assertNull( cache.getImageData( image ) );
    cache.putImageData( image, imageData );
//    assertEquals( imageData, cache.getImageData( image ) );
  }
  
  public void testTooBig() throws Exception {
    ImageData imageData = getImageData( RWTFixture.IMAGE_100x50 ); // 1281 bytes
    Image image = ResourceFactory.findImage( imageData );
    assertNull( cache.getImageData( image ) );
    cache.putImageData( image, imageData );
    assertNull( cache.getImageData( image ) );
  }

  private ImageData getImageData( final String resource ) {
    IResourceManager manager = ResourceManager.getInstance();
    InputStream inputStream = manager.getResourceAsStream( resource );
    assertNotNull( inputStream );
    ImageData[] datas = ImageDataLoader.load( inputStream );
    assertNotNull( datas );
    assertEquals( 1, datas.length );
    ImageData imageData = datas[ 0 ];
    return imageData;
  }
}
