/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ApplicationContext_Test extends TestCase {
  private ApplicationContext context;


  @Override
  protected void setUp() throws Exception {
    context = new ApplicationContext( null, null );
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )context.getConfiguration();
    configuration.configure( Fixture.WEB_CONTEXT_DIR.getAbsolutePath() );
  }

  public void testApplicationContextSingletons() {
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

    assertNotNull( context.getSettingStoreManager() );
    assertSame( context.getSettingStoreManager(), context.getSettingStoreManager() );

    assertNotNull( context.getServiceManager() );
    assertSame( context.getServiceManager(), context.getServiceManager() );

    assertNotNull( context.getResourceRegistry() );
    assertSame( context.getResourceRegistry(), context.getResourceRegistry() );

    assertNotNull( context.getConfiguration() );
    assertSame( context.getConfiguration(), context.getConfiguration() );

    assertNotNull( context.getResourceManager() );
    assertSame( context.getResourceManager(), context.getResourceManager() );

    assertNotNull( context.getStartupPage() );
    assertSame( context.getStartupPage(), context.getStartupPage() );

    assertNotNull( context.getDisplaysHolder() );
    assertSame( context.getDisplaysHolder(), context.getDisplaysHolder() );

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

  private void checkUnallowedMethodAccessIfNotActivated() {
    try {
      context.deactivate();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

}
