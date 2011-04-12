/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import junit.framework.TestCase;

import org.eclipse.rwt.TestResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public class ResourceManagerProvider_Test extends TestCase {
  private static class TestResourceManagerFactory implements IResourceManagerFactory {
    public IResourceManager create() {
      return new TestResourceManager();
    }
  }

  private ResourceManagerProvider resourceManagerProvider;

  public void testRegisterFactoryWithNullArgument() {
    try {
      resourceManagerProvider.registerFactory( null );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterFactoryAndGetInstance() {
    resourceManagerProvider.registerFactory( new TestResourceManagerFactory() );
    IResourceManager manager1 = resourceManagerProvider.getResourceManager();
    IResourceManager manager2 = resourceManagerProvider.getResourceManager();

    assertTrue( manager1 instanceof TestResourceManager );
    assertSame( manager1, manager2 );
  }
  
  public void testRegisterFactoryAcceptsOnlySingleFactory() {
    resourceManagerProvider.registerFactory( new TestResourceManagerFactory() );
    try {
      resourceManagerProvider.registerFactory( new TestResourceManagerFactory() );
      fail( "Only one factory at a time could be registered." );
    } catch( IllegalStateException expected ) {
    }
  }
  
  protected void setUp() {
    resourceManagerProvider = new ResourceManagerProvider();
  }
}
