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

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.branding.BrandingManager;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.resources.JSLibraryConcatenator;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rap.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rap.rwt.internal.service.ServiceManager;
import org.eclipse.rap.rwt.internal.service.SettingStoreManager;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.resources.IResourceManager;
import org.eclipse.rap.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.FontDataFactory;
import org.eclipse.swt.internal.graphics.ImageDataFactory;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.graphics.InternalImageFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.DisplaysHolder;


public class ApplicationContext {
  // TODO [fappel]: this allows to set a fake double of the resource manager for testing purpose.
  //                Think about a less intrusive solution.
  static IResourceManager testResourceManager;
  // TODO [fappel]: themeManager isn't final for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose. Think about
  //                a less intrusive solution.
  private ThemeManager themeManager;
  private final ResourceDirectory resourceDirectory;
  private final ResourceManagerImpl resourceManager;
  private final BrandingManager brandingManager;
  private final PhaseListenerRegistry phaseListenerRegistry;
  private final LifeCycleFactory lifeCycleFactory;
  private final EntryPointManager entryPointManager;
  private final LifeCycleAdapterFactory lifeCycleAdapterFactory;
  private final SettingStoreManager settingStoreManager;
  private final ServiceManager serviceManager;
  private final ResourceRegistry resourceRegistry;
  private final JSLibraryConcatenator jsLibraryConcatenator;
  private final ApplicationStoreImpl applicationStore;
  private final ResourceFactory resourceFactory;
  private final ImageFactory imageFactory;
  private final InternalImageFactory internalImageFactory;
  private final ImageDataFactory imageDataFactory;
  private final FontDataFactory fontDataFactory;
  private final StartupPage startupPage;
  private final DisplaysHolder displaysHolder;
  private final TextSizeStorage textSizeStorage;
  private final ProbeStore probeStore;
  private final ServletContext servletContext;
  private final ApplicationContextConfigurator contextConfigurator;
  private final ApplicationContextActivator contextActivator;
  private final ClientSelector clientSelector;
  private boolean activated;

  public ApplicationContext( ApplicationConfiguration applicationConfiguration,
                             ServletContext servletContext )
  {
    applicationStore = new ApplicationStoreImpl();
    resourceDirectory = new ResourceDirectory();
    resourceManager = new ResourceManagerImpl( resourceDirectory );
    phaseListenerRegistry = new PhaseListenerRegistry();
    entryPointManager = new EntryPointManager();
    lifeCycleFactory = new LifeCycleFactory( phaseListenerRegistry );
    themeManager = new ThemeManager();
    brandingManager = new BrandingManager();
    resourceFactory = new ResourceFactory();
    imageFactory = new ImageFactory();
    internalImageFactory = new InternalImageFactory();
    imageDataFactory = new ImageDataFactory( resourceManager );
    fontDataFactory = new FontDataFactory();
    lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    settingStoreManager = new SettingStoreManager();
    resourceRegistry = new ResourceRegistry();
    startupPage = new StartupPage( resourceRegistry );
    serviceManager = createServiceManager();
    displaysHolder = new DisplaysHolder();
    jsLibraryConcatenator = new JSLibraryConcatenator( resourceManager );
    textSizeStorage = new TextSizeStorage();
    probeStore = new ProbeStore( textSizeStorage );
    this.servletContext = servletContext;
    contextConfigurator = new ApplicationContextConfigurator( applicationConfiguration,
                                                              servletContext );
    contextActivator = new ApplicationContextActivator( this );
    clientSelector = new ClientSelector();
  }

  public boolean isActivated() {
    return activated;
  }

  public void activate() {
    checkIsActivated();
    activated = true;
    try {
      doActivate();
    } catch( RuntimeException rte ) {
      activated = false;
      throw rte;
    }
  }

  public void deactivate() {
    checkIsNotActivated();
    try {
      doDeactivate();
    } finally {
      activated = false;
    }
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public ResourceDirectory getResourceDirectory() {
    return resourceDirectory;
  }

  public IResourceManager getResourceManager() {
    return testResourceManager != null ? testResourceManager : resourceManager;
  }

  public EntryPointManager getEntryPointManager() {
    return entryPointManager;
  }

  public BrandingManager getBrandingManager() {
    return brandingManager;
  }

  public SettingStoreManager getSettingStoreManager() {
    return settingStoreManager;
  }

  public PhaseListenerRegistry getPhaseListenerRegistry() {
    return phaseListenerRegistry;
  }

  public LifeCycleAdapterFactory getLifeCycleAdapterFactory() {
    return lifeCycleAdapterFactory;
  }

  public ResourceRegistry getResourceRegistry() {
    return resourceRegistry;
  }

  public ServiceManager getServiceManager() {
    return serviceManager;
  }

  public JSLibraryConcatenator getJSLibraryConcatenator() {
    return jsLibraryConcatenator;
  }

  public ThemeManager getThemeManager() {
    return themeManager;
  }

  // TODO [fappel]: setThemeManager exists only for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose using this
  //                method. Think about a less intrusive solution.
  public void setThemeManager( ThemeManager themeManager ) {
    this.themeManager = themeManager;
  }

  public LifeCycleFactory getLifeCycleFactory() {
    return lifeCycleFactory;
  }

  public IApplicationStore getApplicationStore() {
    return applicationStore;
  }

  public ResourceFactory getResourceFactory() {
    return resourceFactory;
  }

  public ImageFactory getImageFactory() {
    return imageFactory;
  }

  public InternalImageFactory getInternalImageFactory() {
    return internalImageFactory;
  }

  public ImageDataFactory getImageDataFactory() {
    return imageDataFactory;
  }

  public FontDataFactory getFontDataFactory() {
    return fontDataFactory;
  }

  public StartupPage getStartupPage() {
    return startupPage;
  }

  public DisplaysHolder getDisplaysHolder() {
    return displaysHolder;
  }

  public TextSizeStorage getTextSizeStorage() {
    return textSizeStorage;
  }

  public ProbeStore getProbeStore() {
    return probeStore;
  }

  public ClientSelector getClientSelector() {
    return clientSelector;
  }

  private void checkIsNotActivated() {
    if( !activated ) {
      throw new IllegalStateException( "The ApplicationContext has not been activated." );
    }
  }

  private void checkIsActivated() {
    if( activated ) {
      throw new IllegalStateException( "The ApplicationContext has already been activated." );
    }
  }

  private void doActivate() {
    contextConfigurator.configure( this );
    contextActivator.activate();
  }

  private void doDeactivate() {
    contextActivator.deactivate();
    contextConfigurator.reset( this );
  }

  private ServiceManager createServiceManager() {
    return new ServiceManager( new LifeCycleServiceHandler( lifeCycleFactory, startupPage ) );
  }

}
