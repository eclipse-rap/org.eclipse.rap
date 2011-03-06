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

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageFactory_Test extends TestCase {

  public void testFindImageByPath_registersResource() {
    ClassLoader classLoader = ImageFactory_Test.class.getClassLoader();
    String path = Fixture.IMAGE1;
    Image image1 = ImageFactory.findImage( path, classLoader );
    String registerPath = getRegisterPath( image1 );
    assertTrue( ResourceManager.getInstance().isRegistered( registerPath ) );
  }

  public void testFindImageByPath_returnsSharedImage() {
    ClassLoader classLoader = ImageFactory_Test.class.getClassLoader();
    Image image1 = ImageFactory.findImage( Fixture.IMAGE1, classLoader );
    Image image1a = ImageFactory.findImage( Fixture.IMAGE1, classLoader );
    assertNotNull( image1 );
    assertSame( image1, image1a );
    Image image2 = ImageFactory.findImage( Fixture.IMAGE2, classLoader );
    Image image2a = ImageFactory.findImage( Fixture.IMAGE2, classLoader );
    assertNotNull( image2 );
    assertSame( image2, image2a );
  }

  public void testCreateImage() {
    Display display = new Display();
    String path = "testpath";
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream1 = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image1 = ImageFactory.createImage( display , path, stream1 );
    assertNotNull( image1 );
    InputStream stream2 = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image2 = ImageFactory.createImage( display, path, stream2 );
    assertNotSame( image1, image2 );
    assertSame( image1.internalImage, image2.internalImage );
    // image must be disposable, i.e. dispose must not throw an ISE
    image1.dispose();
  }

  private static String getRegisterPath( final Image image ) {
    String imagePath = ResourceFactory.getImagePath( image );
    int prefixLength = ResourceManagerImpl.RESOURCES.length() + 1;
    return imagePath.substring( prefixLength );
  }

  protected void setUp() throws Exception {
    Fixture.createRWTContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfRWTContext();
  }
}
