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
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rap.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.SettingStoreManager;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.internal.graphics.FontDataFactory;
import org.eclipse.swt.internal.graphics.ImageDataFactory;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.graphics.InternalImageFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.DisplaysHolder;


public class ApplicationContextImpl implements ApplicationContext {
  // TODO [fappel]: this allows to set a fake double of the resource manager for testing purpose.
  //                Think about a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static ResourceManager testResourceManager;

  // TODO [fappel]: themeManager isn't final for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose. Think about
  //                a less intrusive solution.
  private ThemeManager themeManager;
  private final ResourceDirectory resourceDirectory;
  private final ResourceManagerImpl resourceManager;
  private final PhaseListenerRegistry phaseListenerRegistry;
  private final LifeCycleFactory lifeCycleFactory;
  private final EntryPointManager entryPointManager;
  private final LifeCycleAdapterFactory lifeCycleAdapterFactory;
  private final SettingStoreManager settingStoreManager;
  private final ServiceManagerImpl serviceManager;
  private final ResourceRegistry resourceRegistry;
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
  private boolean active;

  public ApplicationContextImpl( ApplicationConfiguration applicationConfiguration,
                                 ServletContext servletContext )
  {
    applicationStore = new ApplicationStoreImpl();
    resourceDirectory = new ResourceDirectory();
    resourceManager = new ResourceManagerImpl( resourceDirectory );
    phaseListenerRegistry = new PhaseListenerRegistry();
    entryPointManager = new EntryPointManager();
    lifeCycleFactory = new LifeCycleFactory( this );
    themeManager = new ThemeManager();
    resourceFactory = new ResourceFactory();
    imageFactory = new ImageFactory();
    internalImageFactory = new InternalImageFactory();
    imageDataFactory = new ImageDataFactory( resourceManager );
    fontDataFactory = new FontDataFactory();
    lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    settingStoreManager = new SettingStoreManager();
    resourceRegistry = new ResourceRegistry( getResourceManager() );
    startupPage = new StartupPage( this );
    serviceManager = createServiceManager();
    displaysHolder = new DisplaysHolder();
    textSizeStorage = new TextSizeStorage();
    probeStore = new ProbeStore( textSizeStorage );
    contextConfigurator = new ApplicationContextConfigurator( applicationConfiguration,
                                                              servletContext );
    contextActivator = new ApplicationContextActivator( this );
    clientSelector = new ClientSelector();
    this.servletContext = servletContext;
  }

  public void setAttribute( String name, Object value ) {
    applicationStore.setAttribute( name, value );
  }

  public Object getAttribute( String name ) {
    return applicationStore.getAttribute( name );
  }

  public void removeAttribute( String name ) {
    applicationStore.removeAttribute( name );
  }

  public boolean isActive() {
    return active;
  }

  public void activate() {
    checkIsActivated();
    active = true;
    try {
      doActivate();
    } catch( RuntimeException rte ) {
      active = false;
      throw rte;
    }
  }

  public void deactivate() {
    checkIsNotActivated();
    try {
      doDeactivate();
    } finally {
      active = false;
    }
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public ResourceDirectory getResourceDirectory() {
    return resourceDirectory;
  }

  public ResourceManager getResourceManager() {
    return testResourceManager != null ? testResourceManager : resourceManager;
  }

  public EntryPointManager getEntryPointManager() {
    return entryPointManager;
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

  public ServiceManagerImpl getServiceManager() {
    return serviceManager;
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
    if( !active ) {
      throw new IllegalStateException( "The ApplicationContext has not been activated." );
    }
  }

  private void checkIsActivated() {
    if( active ) {
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
    applicationStore.reset();
  }

  private ServiceManagerImpl createServiceManager() {
    return new ServiceManagerImpl( new LifeCycleServiceHandler( lifeCycleFactory, startupPage ) );
  }

}
