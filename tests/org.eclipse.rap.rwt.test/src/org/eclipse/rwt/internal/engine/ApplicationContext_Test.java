/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class ApplicationContext_Test extends TestCase {
  private ApplicationContext context;
  
  private static class TestConfigurable implements Configurable {
    private ApplicationContext configureContext;
    private ApplicationContext resetContext;
    private boolean activatedOnConfigure;
    private boolean activatedOnReset;

    public void configure( ApplicationContext context ) {
      this.configureContext = context;
      activatedOnConfigure = context.isActivated();
    }

    public void reset( ApplicationContext context ) {
      this.resetContext = context;
      activatedOnReset = context.isActivated();
    }

    ApplicationContext getConfigureContext() {
      return configureContext;
    }
    
    ApplicationContext getResetContext() {
      return resetContext;
    }

    boolean isActivatedDuringConfigure() {
      return activatedOnConfigure;
    }

    boolean isActivatedDuringReset() {
      return activatedOnReset;
    }
  }  
  
  public void testApplicationContextSingletons() {
    ApplicationContext context = new ApplicationContext();
    assertNotNull( context.getThemeManager() );
    assertSame( context.getThemeManager(), context.getThemeManager() );

    assertNotNull( context.getBrandingManager() );
    assertSame( context.getBrandingManager(), context.getBrandingManager() );
    
    assertNotNull( context.getPhaseListenerRegistry() );
    assertSame( context.getPhaseListenerRegistry(), context.getPhaseListenerRegistry() );
    
    assertNotNull( context.getLifeCycleFactory() );
    assertSame( context.getLifeCycleFactory(), context.getLifeCycleFactory() );
    
    assertNotNull( context.getEntryPointManager() );
    assertSame( context.getEntryPointManager(), context.getEntryPointManager() );

    assertNotNull( context.getResourceFactory() );
    assertSame( context.getResourceFactory(), context.getResourceFactory() );
    
    assertNotNull( context.getImageFactory() );
    assertSame( context.getImageFactory(), context.getImageFactory() );
    
    assertNotNull( context.getInternalImageFactory() );
    assertSame( context.getInternalImageFactory(), context.getInternalImageFactory() );
    
    assertNotNull( context.getImageDataFactory() );
    assertSame( context.getImageDataFactory(), context.getImageDataFactory() );
    
    assertNotNull( context.getFontDataFactory() );
    assertSame( context.getFontDataFactory(), context.getFontDataFactory() );
    
    assertNotNull( context.getAdapterManager() );
    assertSame( context.getAdapterManager(), context.getAdapterManager() );

    assertNotNull( context.getSettingStoreManager() );
    assertSame( context.getSettingStoreManager(), context.getSettingStoreManager() );
    
    assertNotNull( context.getServiceManager() );
    assertSame( context.getServiceManager(), context.getServiceManager() );
    
    assertNotNull( context.getResourceRegistry() );
    assertSame( context.getResourceRegistry(), context.getResourceRegistry() );
    
    assertNotNull( context.getConfigurationReader() );
    assertSame( context.getConfigurationReader(), context.getConfigurationReader() );
    
    assertNotNull( context.getResourceManagerProvider() );
    assertSame( context.getResourceManagerProvider(), context.getResourceManagerProvider() );
    
    assertNotNull( context.getStartupPage() );
    assertSame( context.getStartupPage(), context.getStartupPage() );
    
    assertNotNull( context.getDisplaysHolder() );
    assertSame( context.getDisplaysHolder(), context.getDisplaysHolder() );
    
    assertNotNull( context.getThemeAdapterManager() );
    assertSame( context.getThemeAdapterManager(), context.getThemeAdapterManager() );
    
    assertNotNull( context.getJSLibraryConcatenator() );
    assertSame( context.getJSLibraryConcatenator(), context.getJSLibraryConcatenator() );
    
    assertNotNull( context.getTextSizeStorage() );
    assertSame( context.getTextSizeStorage(), context.getTextSizeStorage() );
    
    assertNotNull( context.getProbeStore() );
    assertSame( context.getProbeStore(), context.getProbeStore() );
  }
  
  public void testStateAfterCreation() {
    assertFalse( context.isActivated() );
    checkUnallowedMethodAccessIfNotActivated();
  }
  
  public void testActivate() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );

    context.activate();
    
    assertTrue( context.isActivated() );
    assertSame( context, configurable.getConfigureContext() );
    assertTrue( configurable.isActivatedDuringConfigure() );
    checkUnallowedMethodAccessIfActivated();
  }
  
  public void testApplicationContextDeactivate() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );
    context.activate();

    context.deactivate();
    
    assertFalse( context.isActivated() );
    assertSame( context, configurable.getResetContext() );
    assertTrue( configurable.isActivatedDuringReset() );
    checkUnallowedMethodAccessIfNotActivated();
  }
    
  public void testAddConfigurableWithNullParam() {
    try {
      context.addConfigurable( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
    
  public void testRemoveConfigurable() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );
    
    context.removeConfigurable( configurable );
    context.activate();
    
    assertNull( configurable.getConfigureContext() );
  }
  
  public void testRemoveConfigurableWithNullParam() {
    try {
      context.removeConfigurable( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    ServletContext servletContext = Fixture.createServletContext();
    context = new ApplicationContext();
    context.addConfigurable( new ResourceManagerProviderConfigurable( servletContext ) );
    context.addConfigurable( new ConfigurationReaderConfigurable( servletContext ) );
  }

  private void checkUnallowedMethodAccessIfNotActivated() {
    try {
      context.deactivate();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  private void checkUnallowedMethodAccessIfActivated() {
    try {
      context.activate();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      context.addConfigurable( null );
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      context.removeConfigurable( null );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
}