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

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManagerProvider;
import org.eclipse.rwt.resources.IResourceManager;


public class ResourceManangerProviderConfigurable_Test extends TestCase {
  private ResourceManagerProviderConfigurable configurable;
  private ApplicationContext applicationContext;
  
  public void testGetFactoryName() {
    setResourceManagerFactory( null );
    String defaultFactoryName = configurable.getFactoryName();
    setTestFactoryNameAsInitParam();
    String testFactoryName = configurable.getFactoryName();
    
    assertEquals( DefaultResourceManagerFactory.class.getName(), defaultFactoryName );
    assertEquals( TestResourceManagerFactory.class.getName(), testFactoryName );
  }
  
  public void testConfigure() {
    setTestFactoryNameAsInitParam();

    configurable.configure( applicationContext );
    
    assertTrue( getResourceManager() instanceof TestResourceManager );
  }
  
  public void testConfigureWithUnknownFactoryName() {
    setResourceManagerFactory( "unkown" );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setTestFactoryNameAsInitParam();
    configurable.configure( applicationContext );
    ResourceManagerProvider provider = applicationContext.getResourceManagerProvider();
    
    configurable.reset( applicationContext );
    
    checkResourceManagerFactoryHasBeenDeregistered( provider );
  }

  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new ResourceManagerProviderConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    setResourceManagerFactory( null );
    Fixture.disposeOfServletContext();
  }
  
  private void checkResourceManagerFactoryHasBeenDeregistered( ResourceManagerProvider provider ) {
    try {
      provider.getResourceManager();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  private IResourceManager getResourceManager() {
    ResourceManagerProvider provider = applicationContext.getResourceManagerProvider();
    return provider.getResourceManager();
  }
  
  private void setTestFactoryNameAsInitParam() {
    setResourceManagerFactory( TestResourceManagerFactory.class.getName() );
  }

  private void setResourceManagerFactory( String value ) {
    String key = ResourceManagerProviderConfigurable.RESOURCE_MANAGER_FACTORY_PARAM;
    Fixture.setInitParameter( key, value );
  }
}