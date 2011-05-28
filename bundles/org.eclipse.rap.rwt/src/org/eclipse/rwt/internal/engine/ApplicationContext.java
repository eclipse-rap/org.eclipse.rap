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

import java.util.*;

import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.textsize.ProbeStore;
import org.eclipse.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rwt.internal.theme.ThemeAdapterManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.widgets.DisplaysHolder;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCAFacade;


public class ApplicationContext {
  // TODO [fappel]: the testMode flag is used to ignore resource registration. Think about
  //                a less intrusive solution
  public static boolean ignoreResoureRegistration;
  // TODO [fappel]: themeManagerHolder isn't final for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose. Think about
  //                a less intrusive solution.
  private ThemeManagerHolder themeManagerHolder;
  private final ResourceManagerProvider resourceManagerProvider;
  private final BrandingManager brandingManager;
  private final PhaseListenerRegistry phaseListenerRegistry;
  private final LifeCycleFactory lifeCycleFactory;
  private final EntryPointManager entryPointManager;
  private final AdapterManager adapterManager;
  private final SettingStoreManager settingStoreManager;
  private final ServiceManager serviceManager;
  private final ConfigurationReader configurationReader;
  private final ResourceRegistry resourceRegistry;
  private final JSLibraryConcatenator jsLibraryConcatenator;
  private final ApplicationStoreImpl applicationStoreImpl;
  private final ResourceFactory resourceFactory;
  private final ImageFactory imageFactory;
  private final InternalImageFactory internalImageFactory;
  private final ImageDataFactory imageDataFactory;
  private final FontDataFactory fontDataFactory;
  private final StartupPage startupPage;
  private final DisplaysHolder displaysHolder;
  private final ThemeAdapterManager themeAdapterManager;
  private final TextSizeStorage textSizeStorage;
  private final ProbeStore probeStore;
  private final Set configurables;
  private boolean activated;
  
  public ApplicationContext() {
    applicationStoreImpl = new ApplicationStoreImpl();
    configurationReader = new ConfigurationReader();
    resourceManagerProvider = new ResourceManagerProvider();
    lifeCycleFactory = new LifeCycleFactory( configurationReader );
    themeManagerHolder = new ThemeManagerHolder();
    brandingManager = new BrandingManager();
    phaseListenerRegistry = new PhaseListenerRegistry();
    entryPointManager = new EntryPointManager();
    resourceFactory = new ResourceFactory();
    imageFactory = new ImageFactory();
    internalImageFactory = new InternalImageFactory();
    imageDataFactory = new ImageDataFactory();
    fontDataFactory = new FontDataFactory();
    adapterManager = new AdapterManager();
    settingStoreManager = new SettingStoreManager();
    resourceRegistry = new ResourceRegistry();
    startupPage = new StartupPage( resourceRegistry );
    serviceManager = createServiceManager();
    displaysHolder = new DisplaysHolder();
    themeAdapterManager = new ThemeAdapterManager();
    jsLibraryConcatenator = new JSLibraryConcatenator();
    textSizeStorage = new TextSizeStorage();
    probeStore = new ProbeStore();
    configurables = new HashSet();
  }

  public boolean isActivated() {
    return activated;
  }
  
  public void activate() {
    checkIsActivated();
    activated = true;
    notifyConfigurablesAboutActivation();
    activateInstances();
  }

  public void deactivate() {
    checkIsNotActivated();
    deactivateInstances();
    notifyConfigurablesAboutDeactivation();
    activated = false;
  }

  public void addConfigurable( Configurable configurable ) {
    checkIsActivated();
    ParamCheck.notNull( configurable, "configurable" );
    configurables.add( configurable );
  }
  
  public void removeConfigurable( Configurable configurable ) {
    checkIsActivated();
    ParamCheck.notNull( configurable, "configurable" );
    configurables.remove( configurable );
  }
  
  public ConfigurationReader getConfigurationReader() {
    return configurationReader;
  }
  
  public ResourceManagerProvider getResourceManagerProvider() {
    return resourceManagerProvider;
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
  
  public ResourceRegistry getResourceRegistry() {
    return resourceRegistry;
  }
  
  public ServiceManager getServiceManager() {
    return serviceManager;
  }

  public JSLibraryConcatenator getJSLibraryConcatenator() {
    return jsLibraryConcatenator;
  }

  public ThemeManagerHolder getThemeManager() {
    return themeManagerHolder;
  }

  // TODO [fappel]: setThemeManager exists only for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose using this
  //                method. Think about a less intrusive solution.
  void setThemeManager( ThemeManagerHolder themeManagerHolder ) {
    this.themeManagerHolder = themeManagerHolder;
  }

  public LifeCycleFactory getLifeCycleFactory() {
    return lifeCycleFactory;
  }
  
  public IApplicationStore getApplicationStore() {
    return applicationStoreImpl;
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
  
  public ThemeAdapterManager getThemeAdapterManager() {
    return themeAdapterManager;
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
  
  private void notifyConfigurablesAboutActivation() {
    Iterator iterator = configurables.iterator();
    while( iterator.hasNext() ) {
      Configurable configurable = ( Configurable )iterator.next();
      configurable.configure( this );
    }
  }
  
  private void notifyConfigurablesAboutDeactivation() {
    Iterator iterator = configurables.iterator();
    while( iterator.hasNext() ) {
      Configurable configurable = ( Configurable )iterator.next();
      configurable.reset( this );
    }
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
    serviceManager.activate();
    // Note: order is crucial here
    jsLibraryConcatenator.startJSConcatenation();
    if( !ignoreResoureRegistration ) {
      DisplayLCAFacade.registerResources();
    }
    themeManagerHolder.activate();
    jsLibraryConcatenator.finishJSConcatenation();
  }
  
  private void deactivateInstances() {
    ApplicationContextUtil.runWith( this, new Runnable() {
      public void run() {
        doDeactivateInstances();
      }
    } );
  }

  private void doDeactivateInstances() {
    lifeCycleFactory.deactivate();
    serviceManager.deactivate();
    themeManagerHolder.deactivate();
  }
  

  private ServiceManager createServiceManager() {
    return new ServiceManager( new LifeCycleServiceHandler( lifeCycleFactory, startupPage ));
  }
}