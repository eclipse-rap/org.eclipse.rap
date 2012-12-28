/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.internal.resources.ResourceRegistry.ResourceRegistration;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.junit.Before;
import org.junit.Test;


public class ResourceRegistry_Test {

  private ResourceManager resourceManager;
  private ResourceRegistry resourceRegistry;

  @Before
  public void setUp() {
    resourceManager = mock( ResourceManager.class );
    resourceRegistry = new ResourceRegistry( resourceManager );
  }

  @Test
  public void testAdd() {
    String resourceName = "name";
    ResourceLoader resourceLoader = mock( ResourceLoader.class );
    resourceRegistry.add( resourceName, resourceLoader );

    ResourceRegistration resourceRegistration = resourceRegistry.getResourceRegistrations()[ 0 ];

    assertEquals( resourceName, resourceRegistration.getResourceName() );
    assertEquals( resourceLoader, resourceRegistration.getResourceLoader() );
  }

  @Test
  public void testClear() {
    resourceRegistry.add( "name", mock( ResourceLoader.class ) );

    resourceRegistry.clear();

    assertEquals( 0, resourceRegistry.getResourceRegistrations().length );
  }

  @Test
  @SuppressWarnings( "resource" )
  public void testRegisterResources() throws IOException {
    String resourceName = "name";
    InputStream inputStream = mock( InputStream.class );
    ResourceLoader resourceLoader = mock( ResourceLoader.class );
    when( resourceLoader.getResourceAsStream( resourceName ) ).thenReturn( inputStream );
    resourceRegistry.add( resourceName, resourceLoader );

    resourceRegistry.registerResources();

    verify( resourceManager ).register( resourceName, inputStream );
    assertEquals( 0, resourceRegistry.getResourceRegistrations().length );
  }

  @Test
  public void testRegisterResourcesWithCorruptResourceLoader() {
    String resourceName = "resource-name";
    ResourceLoader resourceLoader = mock( ResourceLoader.class );
    resourceRegistry.add( resourceName, resourceLoader );

    try {
      resourceRegistry.registerResources();
      fail();
    } catch( IllegalStateException expected ) {
      assertTrue( expected.getMessage().contains( resourceName ) );
    }
  }

}
