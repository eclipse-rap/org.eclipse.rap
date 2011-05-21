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
import org.eclipse.rwt.internal.service.MemorySettingStoreFactory;
import org.eclipse.rwt.internal.service.SettingStoreManager;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;


public class SettingStoreManagerConfigurable_Test extends TestCase {
  private SettingStoreManagerConfigurable configurable;
  private ApplicationContext applicationContext;

  public void testGetFactoryName() {
    String defaultFactoryName = configurable.getFactoryName();
    setTestFactoryNameAsInitParam();
    String testFactoryName = configurable.getFactoryName();
    
    assertEquals( RWTFileSettingStoreFactory.class.getName(), defaultFactoryName );
    assertEquals( MemorySettingStoreFactory.class.getName(), testFactoryName );
  }
  
  public void testConfigure() {
    setTestFactoryNameAsInitParam();

    applicationContext.activate();
    
    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }
  
  public void testConfigureWithUnknownFactoryName() {
    Fixture.setInitParameter( RWTServletContextListener.SETTING_STORE_FACTORY_PARAM, "unkown" );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setTestFactoryNameAsInitParam();
    applicationContext.activate();
    SettingStoreManager settingStoreManager = applicationContext.getSettingStoreManager();
    
    applicationContext.deactivate();
    
    assertFalse( settingStoreManager.hasFactory() );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new SettingStoreManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext( new Class[] { SettingStoreManager.class } );
    applicationContext.addConfigurable( configurable );
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( RWTServletContextListener.SETTING_STORE_FACTORY_PARAM, null );
  }
    
  private void setTestFactoryNameAsInitParam() {
    String factoryName = MemorySettingStoreFactory.class.getName();
    Fixture.setInitParameter( RWTServletContextListener.SETTING_STORE_FACTORY_PARAM, factoryName );
  }
}
