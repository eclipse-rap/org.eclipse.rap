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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.Before;
import org.junit.Test;


public class ApplicationContextImpl_Test {
  private ApplicationContextImpl context;


  @Before
  public void setUp() {
    context = new ApplicationContextImpl( null, null );
    ResourceDirectory resourceDirectory = context.getResourceDirectory();
    resourceDirectory.configure( Fixture.WEB_CONTEXT_DIR.getAbsolutePath() );
  }

  @Test
  public void testApplicationContextSingletons() {
    assertNotNull( context.getThemeManager() );
    assertSame( context.getThemeManager(), context.getThemeManager() );

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

    assertNotNull( context.getResourceDirectory() );
    assertSame( context.getResourceDirectory(), context.getResourceDirectory() );

    assertNotNull( context.getResourceManager() );
    assertSame( context.getResourceManager(), context.getResourceManager() );

    assertNotNull( context.getStartupPage() );
    assertSame( context.getStartupPage(), context.getStartupPage() );

    assertNotNull( context.getDisplaysHolder() );
    assertSame( context.getDisplaysHolder(), context.getDisplaysHolder() );

    assertNotNull( context.getTextSizeStorage() );
    assertSame( context.getTextSizeStorage(), context.getTextSizeStorage() );

    assertNotNull( context.getProbeStore() );
    assertSame( context.getProbeStore(), context.getProbeStore() );

    assertNotNull( context.getClientSelector() );
    assertSame( context.getClientSelector(), context.getClientSelector() );
  }

  @Test
  public void testStateAfterCreation() {
    assertFalse( context.isActive() );
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
