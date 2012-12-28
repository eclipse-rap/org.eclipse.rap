/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.junit.Before;
import org.junit.Test;


public class ApplicationContextActivator_Test {

  private ApplicationContextImpl applicationContext;
  private LifeCycleFactory lifeCycleFactory;
  private ThemeManager themeManager;
  private ServiceManagerImpl serviceManager;
  private ClientSelector clientSelector;
  private StartupPage startupPage;

  @Before
  public void setUp() {
    mockApplicationContext();
  }

  @Test
  public void testActivate() {
    ApplicationContextActivator activator = new ApplicationContextActivator( applicationContext );

    activator.activate();

    verify( themeManager ).activate();
    verify( lifeCycleFactory ).activate();
    verify( clientSelector ).activate();
    verify( startupPage ).activate();
  }

  @Test
  public void testDeactivate() {
    ApplicationContextActivator activator = new ApplicationContextActivator( applicationContext );

    activator.deactivate();

    verify( lifeCycleFactory ).deactivate();
    verify( serviceManager ).clear();
    verify( themeManager ).deactivate();
    verify( startupPage ).deactivate();
  }

  private void mockApplicationContext() {
    applicationContext = mock( ApplicationContextImpl.class );

    themeManager = mock( ThemeManager.class );
    when( themeManager.getRegisteredThemeIds() ).thenReturn( new String[ 0 ] );
    when( applicationContext.getThemeManager() ).thenReturn( themeManager );

    lifeCycleFactory = mock( LifeCycleFactory.class );
    when( applicationContext.getLifeCycleFactory() ).thenReturn( lifeCycleFactory );

    ResourceDirectory resourceDirectory = mock( ResourceDirectory.class );
    when( applicationContext.getResourceDirectory() ).thenReturn( resourceDirectory );

    serviceManager = mock( ServiceManagerImpl.class );
    when( applicationContext.getServiceManager() ).thenReturn( serviceManager );

    ResourceManager resourceManager = mock( ResourceManager.class );
    when( applicationContext.getResourceManager() ).thenReturn( resourceManager );

    startupPage = mock( StartupPage.class );
    when( applicationContext.getStartupPage() ).thenReturn( startupPage );

    clientSelector = mock( ClientSelector.class );
    when( applicationContext.getClientSelector() ).thenReturn( clientSelector );

    ResourceRegistry resourceRegistry = mock( ResourceRegistry.class );
    when( applicationContext.getResourceRegistry() ).thenReturn( resourceRegistry );
  }

}
