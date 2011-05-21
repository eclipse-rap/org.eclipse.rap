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
    String defaultFactoryName = configurable.getFactoryName();
    setTestFactoryNameAsInitParam();
    String testFactoryName = configurable.getFactoryName();
    
    assertEquals( DefaultResourceManagerFactory.class.getName(), defaultFactoryName );
    assertEquals( TestResourceManagerFactory.class.getName(), testFactoryName );
  }
  
  public void testConfigure() {
    setTestFactoryNameAsInitParam();

    applicationContext.activate();
    
    assertTrue( getResourceManager() instanceof TestResourceManager );
  }
  
  public void testConfigureWithUnknownFactoryName() {
    Fixture.setInitParameter( RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM, "unkown" );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setTestFactoryNameAsInitParam();
    applicationContext.activate();
    ResourceManagerProvider provider = applicationContext.getResourceManagerProvider();
    
    applicationContext.deactivate();
    
    checkResourceManagerFactoryHasBeenDeregistered( provider );
  }

  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new ResourceManagerProviderConfigurable( servletContext );
    applicationContext = new ApplicationContext( new Class[] { ResourceManagerProvider.class } );
    applicationContext.addConfigurable( configurable );
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM, null );
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
    String name = TestResourceManagerFactory.class.getName();
    Fixture.setInitParameter( RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM, name );
  }
}