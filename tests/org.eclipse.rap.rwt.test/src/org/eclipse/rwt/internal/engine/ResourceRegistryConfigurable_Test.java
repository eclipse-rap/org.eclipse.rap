/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class ResourceRegistryConfigurable_Test extends TestCase {
  private ResourceRegistryConfigurable configurable;
  private ApplicationContext applicationContext;
  
  public static class TestResource implements IResource {

    public ClassLoader getLoader() {
      return null;
    }

    public String getLocation() {
      return null;
    }

    public String getCharset() {
      return null;
    }

    public RegisterOptions getOptions() {
      return null;
    }

    public boolean isJSLibrary() {
      return false;
    }

    public boolean isExternal() {
      return false;
    }
  }

  public void testConfigure() {
    setInitParameter( TestResource.class.getName() );
    
    configurable.configure( applicationContext );
    
    assertEquals( 1, getResourceRegistry().get().length );
    assertTrue( getResourceRegistry().get()[ 0 ] instanceof TestResource );
  }
  
  public void testConfigureWithUnknownResourceClass() {
    setInitParameter( "unknown" );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setInitParameter( TestResource.class.getName() );
    configurable.configure( applicationContext );
    ResourceRegistry resourceRegistry = getResourceRegistry();

    configurable.reset( applicationContext );
    
    assertEquals( 0, resourceRegistry.get().length );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new ResourceRegistryConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    setInitParameter( null );
    Fixture.disposeOfServletContext();
  }

  private void setInitParameter( String value ) {
    Fixture.setInitParameter( RWTServletContextListener.RESOURCES_PARAM, value );
  }
  
  private ResourceRegistry getResourceRegistry() {
    return applicationContext.getResourceRegistry();
  }
}