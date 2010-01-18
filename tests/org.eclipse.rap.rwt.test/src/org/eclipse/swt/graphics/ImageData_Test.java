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

package org.eclipse.swt.graphics;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataLoader;


public class ImageData_Test extends TestCase {
  
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

  public void testImageData() {
    IResourceManager manager = ResourceManager.getInstance();
    InputStream inputStream
      = manager.getResourceAsStream( Fixture.IMAGE_100x50 );
    assertNotNull( inputStream );
    ImageData[] datas = ImageDataLoader.load( inputStream );
    assertNotNull( datas );
    assertEquals( 1, datas.length );
    ImageData data = datas[ 0 ];
    assertNotNull( data );
    assertEquals( 100, data.width );
    assertEquals( 50, data.height );
    assertEquals( SWT.IMAGE_PNG, data.type );
  }
}
