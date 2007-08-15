/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.RWTFixture;



public class Image_Test extends TestCase {

  public void testImageFinder() {
    IResourceManager manager = ResourceManager.getInstance();
    // only if you comment initial registration in
    // org.eclipse.swt.internal.widgets.displaykit.QooxdooResourcesUtil
    assertFalse( manager.isRegistered( RWTFixture.IMAGE1 ) );
    assertEquals( 0, Image.size() );
    Image image1 = Image.find( RWTFixture.IMAGE1 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE1 ) );
    String contextPath = Fixture.CONTEXT_DIR.getPath();
    assertTrue( new File( contextPath + "/" + RWTFixture.IMAGE1 ).exists() );
    assertEquals( 1, Image.size() );
    Image image2 = Image.find( RWTFixture.IMAGE1 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE1 ) );
    assertEquals( 1, Image.size() );
    assertSame( image1, image2 );
    assertEquals( Image.getPath( image1 ), Image.getPath( image2 ) );
    // another picture
    Image.find( RWTFixture.IMAGE2 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE2 ) );
    assertTrue( new File( contextPath + "/" + RWTFixture.IMAGE2 ).exists() );
    assertEquals( 2, Image.size() );
    // clear cache
    Image.clear();
    // works only, if deregistration in ressourceManager is implemented
    // assertFalse( manager.isRegistered( "resource/icon/nuvola/16/down.png" ));
    assertEquals( 0, Image.size() );
    // ... and do it again...
    image1 = Image.find( RWTFixture.IMAGE1 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE1 ) );
    assertEquals( 1, Image.size() );
  }
  
  public void testImageFinderWithClassLoader() throws IOException {
    File testGif = new File( Fixture.CONTEXT_DIR, "test.gif" );
    Fixture.copyTestResource( RWTFixture.IMAGE3, testGif );
    URL[] urls = new URL[] { Fixture.CONTEXT_DIR.toURL() };
    URLClassLoader classLoader = new URLClassLoader( urls, null );
    
    IResourceManager manager = ResourceManager.getInstance();
    assertFalse( manager.isRegistered( RWTFixture.IMAGE3 ) );
    assertEquals( 0, Image.size() );
    try {
      Image.find( "test.gif" );
      fail( "Image not available on the classpath." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    Image image = Image.find( "test.gif", classLoader );
    assertNotNull( image );
  }
  
  public void testImageFinderWithInputStream() throws IOException {
    String imgName = "testIS.gif";
    File testGif = new File( Fixture.CONTEXT_DIR, imgName );
    Fixture.copyTestResource( RWTFixture.IMAGE3, testGif );
    URL[] urls = new URL[] { Fixture.CONTEXT_DIR.toURL() };
    URLClassLoader classLoader = new URLClassLoader( urls, null );
    
    IResourceManager manager = ResourceManager.getInstance();
    assertFalse( manager.isRegistered( RWTFixture.IMAGE3 ) );
    assertEquals( 0, Image.size() );
    try {
      Image.find( imgName );
      fail( "Image not available on the classpath." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    InputStream is = classLoader.getResourceAsStream( imgName );
    Image image = Image.find( "test.gif", is );
    assertNotNull( image );
  }
  
  public void testFindWithIllegalArguments() {
    try {
      Image.find( null );
      fail( "Image#find must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Image.find( "" );
      fail( "Image#find must not allow empty string argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Image.find( "", new ByteArrayInputStream( new byte[ 1 ] ) );
      fail( "Image#find must not allow empty string argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testImageBounds() {
    IResourceManager manager = ResourceManager.getInstance();
    // 100 x 50
    assertFalse( manager.isRegistered( RWTFixture.IMAGE_100x50 ) );
    Image image_100x50 = Image.find( RWTFixture.IMAGE_100x50 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE_100x50 ) );
    assertEquals( new Rectangle( 0, 0, 100, 50 ), image_100x50.getBounds() );
    // 50 x 100
    assertFalse( manager.isRegistered( RWTFixture.IMAGE_50x100 ) );
    Image image_50x100 = Image.find( RWTFixture.IMAGE_50x100 );
    assertTrue( manager.isRegistered( RWTFixture.IMAGE_50x100 ) );
    assertEquals( new Rectangle( 0, 0, 50, 100 ), image_50x100.getBounds() );
  }
  
  protected void setUp() throws Exception {
    // we do need the ressource manager for this test
    Fixture.setUp();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Image.clear();
  }
}
