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
package org.eclipse.rwt.internal.application;

import java.io.File;

import javax.servlet.ServletContext;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.engine.RWTConfiguration;
import org.eclipse.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rwt.internal.resources.JSLibraryConcatenator;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.service.SettingStoreManager;
import org.eclipse.rwt.internal.service.StartupPage;
import org.eclipse.rwt.internal.textsize.ProbeStore;
import org.eclipse.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.FontDataFactory;
import org.eclipse.swt.internal.graphics.ImageDataFactory;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.graphics.InternalImageFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.DisplaysHolder;
import org.eclipse.swt.internal.widgets.displaykit.ClientResources;


public class ApplicationContext {
  // TODO [fappel]: this allows to set a fake double of the resource manager for testing purpose.
  //                Think about a less intrusive solution.
  static IResourceManager testResourceManager;
  // TODO [fappel]: this flag is used to skip resource registration. Think about
  //                a less intrusive solution.
  static boolean skipResoureRegistration;
  // TODO [fappel]: this flag is used to skip resource deletion. Think about
  //                a less intrusive solution.
  static boolean skipResoureDeletion;
  // TODO [fappel]: themeManager isn't final for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose. Think about
  //                a less intrusive solution.
  private ThemeManager themeManager;
  private final RWTConfiguration configuration;
  private final ResourceManagerImpl resourceManager;
  private final BrandingManager brandingManager;
  private final PhaseListenerRegistry phaseListenerRegistry;
  private final LifeCycleFactory lifeCycleFactory;
  private final EntryPointManager entryPointManager;
  private final AdapterManager adapterManager;
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
  private boolean activated;

  public ApplicationContext( ApplicationConfiguration configurator, ServletContext servletContext ) {
    applicationStore = new ApplicationStoreImpl();
    configuration = new RWTConfigurationImpl();
    resourceManager = new ResourceManagerImpl( configuration );
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
    adapterManager = new AdapterManager();
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
    contextConfigurator = new ApplicationContextConfigurator( configurator, servletContext );
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

  public RWTConfiguration getConfiguration() {
    return configuration;
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

  public AdapterManager getAdapterManager() {
    return adapterManager;
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
    themeManager.initialize();
    contextConfigurator.configure( this );
    activateInstances();
  }

  private void activateInstances() {
    ApplicationContextUtil.runWith( this, new Runnable() {
      public void run() {
        doActivateInstances();
      }
    } );
  }

  private void doActivateInstances() {
    // TODO [SystemStart]: Unit testing
    lifeCycleFactory.activate();
    // Note: order is crucial here
    jsLibraryConcatenator.startJSConcatenation();
    themeManager.activate();
    if( !skipResoureRegistration ) {
      new ClientResources( getResourceManager(), themeManager ).registerResources();
    }
    jsLibraryConcatenator.activate();
  }

  private void doDeactivate() {
    deactivateInstances();
    contextConfigurator.reset( this );
  }

  private void deactivateInstances() {
    ApplicationContextUtil.runWith( this, new Runnable() {
      public void run() {
        doDeactivateInstances();
      }
    } );
  }

  private void doDeactivateInstances() {
    // TODO [SystemStart]: Unit testing
    jsLibraryConcatenator.deactivate();
    lifeCycleFactory.deactivate();
    serviceManager.clear();
    themeManager.deactivate();

    // TODO [fappel]: think of better solution. This maps directly to the
    //                default resource manager implementation while
    //                the resource manager factory is configurable. Is
    //                the latter really necessary since the only other factory
    //                in use is for testing purpose (unfortunately API).
    if( !skipResoureDeletion ) {
      File resourcesDir = new File( configuration.getContextDirectory(),
                                    ResourceManagerImpl.RESOURCES );
      ApplicationContextUtil.delete( resourcesDir );
    }
  }


  private ServiceManager createServiceManager() {
    return new ServiceManager( new LifeCycleServiceHandler( lifeCycleFactory, startupPage ) );
  }
}