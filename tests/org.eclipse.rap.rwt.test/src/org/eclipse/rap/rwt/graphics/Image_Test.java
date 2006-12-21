/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.graphics;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.resources.DefaultResourceManagerFactory;
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.Fixture;
import com.w4t.IResourceManager;
import com.w4t.engine.util.ResourceRegistrationException;


public class Image_Test extends TestCase {

  public void testImageFinder() {
    IResourceManager manager = ResourceManager.getInstance();
    // only if you comment initial registration in
    // org.eclipse.rap.rwt.internal.widgets.displaykit.QooxdooResourcesUtil
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
    Image.clear();
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
    } catch( final ResourceRegistrationException rre ) {
      // expected
    }
    Image image = Image.find( "test.gif", classLoader );
    assertNotNull( image );
    
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
  }
}
