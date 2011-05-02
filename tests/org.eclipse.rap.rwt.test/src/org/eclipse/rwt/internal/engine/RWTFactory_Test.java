/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class RWTFactory_Test extends TestCase {

  public void testApplicationScopedSingletons() {
    assertNotNull( RWTFactory.getThemeManager() );
    assertSame( RWTFactory.getThemeManager(), RWTFactory.getThemeManager() );

    assertNotNull( RWTFactory.getBrandingManager() );
    assertSame( RWTFactory.getBrandingManager(), RWTFactory.getBrandingManager() );
    
    assertNotNull( RWTFactory.getPhaseListenerRegistry() );
    assertSame( RWTFactory.getPhaseListenerRegistry(), RWTFactory.getPhaseListenerRegistry() );
    
    assertNotNull( RWTFactory.getLifeCycleFactory() );
    assertSame( RWTFactory.getLifeCycleFactory(), RWTFactory.getLifeCycleFactory() );
    
    assertNotNull( RWTFactory.getEntryPointManager() );
    assertSame( RWTFactory.getEntryPointManager(), RWTFactory.getEntryPointManager() );

    assertNotNull( RWTFactory.getResourceFactory() );
    assertSame( RWTFactory.getResourceFactory(), RWTFactory.getResourceFactory() );
    
    assertNotNull( RWTFactory.getImageFactory() );
    assertSame( RWTFactory.getImageFactory(), RWTFactory.getImageFactory() );
    
    assertNotNull( RWTFactory.getInternalImageFactory() );
    assertSame( RWTFactory.getInternalImageFactory(), RWTFactory.getInternalImageFactory() );
    
    assertNotNull( RWTFactory.getImageDataFactory() );
    assertSame( RWTFactory.getImageDataFactory(), RWTFactory.getImageDataFactory() );
    
    assertNotNull( RWTFactory.getFontDataFactory() );
    assertSame( RWTFactory.getFontDataFactory(), RWTFactory.getFontDataFactory() );
    
    assertNotNull( RWTFactory.getAdapterManager() );
    assertSame( RWTFactory.getAdapterManager(), RWTFactory.getAdapterManager() );

    assertNotNull( RWTFactory.getSettingStoreManager() );
    assertSame( RWTFactory.getSettingStoreManager(), RWTFactory.getSettingStoreManager() );
    
    assertNotNull( RWTFactory.getServiceManager() );
    assertSame( RWTFactory.getServiceManager(), RWTFactory.getServiceManager() );
    
    assertNotNull( RWTFactory.getResourceRegistry() );
    assertSame( RWTFactory.getResourceRegistry(), RWTFactory.getResourceRegistry() );
    
    assertNotNull( RWTFactory.getConfigurationReader() );
    assertSame( RWTFactory.getConfigurationReader(), RWTFactory.getConfigurationReader() );
    
    assertNotNull( RWTFactory.getResourceManagerProvider() );
    assertSame( RWTFactory.getResourceManagerProvider(), RWTFactory.getResourceManagerProvider() );
    
    assertNotNull( RWTFactory.getStartupPageConfigurer() );
    assertSame( RWTFactory.getStartupPageConfigurer(), RWTFactory.getStartupPageConfigurer() );
    
    assertNotNull( RWTFactory.getStartupPage() );
    assertSame( RWTFactory.getStartupPage(), RWTFactory.getStartupPage() );
    
    assertNotNull( RWTFactory.getDisplaysHolder() );
    assertSame( RWTFactory.getDisplaysHolder(), RWTFactory.getDisplaysHolder() );
    
    assertNotNull( RWTFactory.getThemeAdapterManager() );
    assertSame( RWTFactory.getThemeAdapterManager(), RWTFactory.getThemeAdapterManager() );
    
    assertNotNull( RWTFactory.getJSLibraryConcatenator() );
    assertSame( RWTFactory.getJSLibraryConcatenator(), RWTFactory.getJSLibraryConcatenator() );
    
    assertNotNull( RWTFactory.getTextSizeStorageRegistry() );
    assertSame( RWTFactory.getTextSizeStorageRegistry(), RWTFactory.getTextSizeStorageRegistry() );
    
    assertNotNull( RWTFactory.getTextSizeProbeStore() );
    assertSame( RWTFactory.getTextSizeProbeStore(), RWTFactory.getTextSizeProbeStore() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
