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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public class ResourceManager_Test extends TestCase {
  private final class EmptyInitializer implements Runnable {
    public void run() {
    }
  }

  public static class TestResourceManagerFactory
    implements IResourceManagerFactory
  {
    public IResourceManager create() {
      return new TestResourceManager();
    }
  }

  public void testResourceManagerFactoryRegistration() {
    ResourceManager.register( new TestResourceManagerFactory() );
    IResourceManager manager1 = ResourceManager.getInstance();
    IResourceManager manager2 = ResourceManager.getInstance();

    assertTrue( manager1 instanceof TestResourceManager );
    assertSame( manager1, manager2 );
  }
  
  public void testUniqueResourceManagerFactoryRegistration() {
    ResourceManager.register( new TestResourceManagerFactory() );
    try {
      ResourceManager.register( new TestResourceManagerFactory() );
      fail( "Only one factory at a time could be registered." );
    } catch( final IllegalStateException ise ) {
      // expected
    }
  }
  
  public void testRegistrationAfterDisposalOfResourceManagerFactory() {
    ResourceManager.register( new TestResourceManagerFactory() );
    IResourceManager manager1 = ResourceManager.getInstance();
    
    ResourceManager.disposeOfResourceManagerFactory();
    ResourceManager.register( new TestResourceManagerFactory() );
    IResourceManager manager2 = ResourceManager.getInstance();

    assertNotSame( manager1, manager2 );
  }
  
  protected void setUp() {
    Fixture.createRWTContext( new EmptyInitializer() );
    Fixture.createServiceContext();
  }
  
  protected void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfRWTContext();
  }
}
