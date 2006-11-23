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
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.resources.DefaultResourceManagerFactory;
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.Fixture;
import com.w4t.IResourceManager;


public class Image_Test extends TestCase {

  // 2 images from org.eclipse.rap.w4t.test-project
  public static final String IMAGE2 
    = "resources/images/generated/a2fb9a01c602ae.gif";
  public static final String IMAGE1 
    = "resources/images/generated/82f7c683860a85c182.gif";

  public void testGetItemsAndGetItemCount() {
    IResourceManager manager = ResourceManager.getInstance();
    // only if you comment initial registration in
    // org.eclipse.rap.rwt.internal.widgets.displaykit.QooxdooResourcesUtil
    assertFalse( manager.isRegistered( IMAGE1 ) );
    assertEquals( 0, Image.size() );
    Image image1 = Image.find( IMAGE1 );
    assertTrue( manager.isRegistered( IMAGE1 ) );
    String contextPath = Fixture.CONTEXT_DIR.getPath();
    assertTrue( new File( contextPath + "/" + IMAGE1 ).exists() );
    assertEquals( 1, Image.size() );
    Image image2 = Image.find( IMAGE1 );
    assertTrue( manager.isRegistered( IMAGE1 ) );
    assertEquals( 1, Image.size() );
    assertSame( image1, image2 );
    assertEquals( Image.getPath( image1 ), Image.getPath( image2 ) );
    // another picture
    Image.find( IMAGE2 );
    assertTrue( manager.isRegistered( IMAGE2 ) );
    assertTrue( new File( contextPath + "/" + IMAGE2 ).exists() );
    assertEquals( 2, Image.size() );
    // clear cache
    Image.clear();
    // works only, if deregistration in ressourceManager is implemented
    // assertFalse( manager.isRegistered( "resource/icon/nuvola/16/down.png" ));
    assertEquals( 0, Image.size() );
    // ... and do it again...
    image1 = Image.find( IMAGE1 );
    assertTrue( manager.isRegistered( IMAGE1 ) );
    assertEquals( 1, Image.size() );
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
