/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ExceptionHandler;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushServiceHandler;
import org.eclipse.rap.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rap.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.SettingStoreManager;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.internal.util.SerializableLock;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.ApplicationContextEvent;
import org.eclipse.rap.rwt.service.ApplicationContextListener;
import org.eclipse.rap.rwt.service.FileSettingStoreFactory;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.internal.graphics.FontDataFactory;
import org.eclipse.swt.internal.graphics.ImageDataFactory;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.graphics.InternalImageFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.DisplaysHolder;
import org.eclipse.swt.internal.widgets.displaykit.ClientResources;


public class ApplicationContextImpl implements ApplicationContext {

  private static enum State { INACTIVE, ACTIVATING, ACTIVE, DEACTIVATING }

  private final static String ATTR_APPLICATION_CONTEXT
    = ApplicationContextImpl.class.getName() + "#instance";

  // TODO [fappel]: this allows to set a fake double of the resource manager for testing purpose.
  //                Think about a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static ResourceManager testResourceManager;

  // TODO [fappel]: this flag is used to skip resource registration. Think about
  //                a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static boolean skipResoureRegistration;

  // TODO [fappel]: this flag is used to skip resource deletion. Think about
  //                a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static boolean skipResoureDeletion;

  // TODO [fappel]: themeManager isn't final for performance reasons of the testsuite.
  //                TestServletContext#setAttribute(String,Object) will replace the runtime
  //                implementation with an optimized version for testing purpose. Think about
  //                a less intrusive solution.
  private ThemeManager themeManager;

  private final ApplicationConfiguration applicationConfiguration;
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
  private final ClientSelector clientSelector;
  private final Set<ApplicationContextListener> listeners;
  private final SerializableLock listenersLock;
  private final AtomicReference<State> state;
  private ExceptionHandler exceptionHandler;

  public ApplicationContextImpl( ApplicationConfiguration applicationConfiguration,
                                 ServletContext servletContext )
  {
    this.applicationConfiguration = applicationConfiguration;
    this.servletContext = servletContext;
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
    clientSelector = new ClientSelector();
    listeners = new HashSet<ApplicationContextListener>();
    listenersLock = new SerializableLock();
    state = new AtomicReference<State>( State.INACTIVE );
  }

  public static ApplicationContextImpl getFrom( ServletContext servletContext ) {
    return ( ApplicationContextImpl )servletContext.getAttribute( ATTR_APPLICATION_CONTEXT );
  }

  public void attachToServletContext() {
    servletContext.setAttribute( ATTR_APPLICATION_CONTEXT, this );
  }

  public void removeFromServletContext() {
    servletContext.removeAttribute( ATTR_APPLICATION_CONTEXT );
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

  public boolean addApplicationContextListener( ApplicationContextListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( listenersLock ) {
      if( state.get().equals( State.ACTIVE ) ) {
        result = true;
        listeners.add( listener );
      }
    }
    return result;
  }

  public boolean removeApplicationContextListener( ApplicationContextListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( listenersLock ) {
      if( state.get().equals( State.ACTIVE ) ) {
        result = true;
        listeners.remove( listener );
      }
    }
    return result;
  }

  public boolean isActive() {
    return state.get().equals( State.ACTIVE );
  }

  public void activate() {
    if( !state.compareAndSet( State.INACTIVE, State.ACTIVATING ) ) {
      throw new IllegalStateException( "ApplicationContext is already active" );
    }
    try {
      doActivate();
      state.set( State.ACTIVE );
    } catch( RuntimeException rte ) {
      state.set( State.INACTIVE );
      throw rte;
    }
  }

  public void deactivate() {
    if( !state.compareAndSet( State.ACTIVE, State.DEACTIVATING ) ) {
      throw new IllegalStateException( "ApplicationContext is not active" );
    }
    try {
      fireBeforeDestroy();
      doDeactivate();
    } finally {
      state.set( State.INACTIVE );
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

  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  public void setExceptionHandler( ExceptionHandler exceptionHandler ) {
    this.exceptionHandler = exceptionHandler;
  }

  void doActivate() {
    themeManager.initialize();
    applicationConfiguration.configure( new ApplicationImpl( this, applicationConfiguration ) );
    resourceDirectory.configure( getContextDirectory() );
    addInternalPhaseListeners();
    addInternalServiceHandlers();
    setInternalSettingStoreFactory();
    startupPage.activate();
    lifeCycleFactory.activate();
    // Note: order is crucial here
    themeManager.activate();
    if( !skipResoureRegistration ) {
      ClientResources clientResources = new ClientResources( this );
      clientResources.registerResources();
    }
    resourceRegistry.registerResources();
    clientSelector.activate();
  }

  void doDeactivate() {
    startupPage.deactivate();
    lifeCycleFactory.deactivate();
    serviceManager.clear();
    themeManager.deactivate();
    if( !skipResoureDeletion ) {
      getResourceDirectory().deleteDirectory();
    }
    entryPointManager.deregisterAll();
    phaseListenerRegistry.removeAll();
    resourceRegistry.clear();
    settingStoreManager.deregisterFactory();
    resourceDirectory.reset();
    applicationStore.reset();
  }

  private ServiceManagerImpl createServiceManager() {
    return new ServiceManagerImpl( new LifeCycleServiceHandler( lifeCycleFactory, startupPage ) );
  }

  private String getContextDirectory() {
    String location
      = ( String )servletContext.getAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION );
    if( location == null ) {
      location = servletContext.getRealPath( "/" );
    }
    return location;
  }

  private void addInternalPhaseListeners() {
    phaseListenerRegistry.add( new MeasurementListener() );
  }

  private void addInternalServiceHandlers() {
    serviceManager.registerServiceHandler( ServerPushServiceHandler.HANDLER_ID,
                                           new ServerPushServiceHandler() );
  }

  private void setInternalSettingStoreFactory() {
    if( !settingStoreManager.hasFactory() ) {
      settingStoreManager.register( new FileSettingStoreFactory() );
    }
  }

  private void fireBeforeDestroy() {
    ApplicationContextEvent event = new ApplicationContextEvent( this );
    for( ApplicationContextListener listener : copyListeners() ) {
      try {
        listener.beforeDestroy( event );
      } catch( RuntimeException exception ) {
        handleBeforeDestroyException( listener, exception );
      }
    }
  }

  private List<ApplicationContextListener> copyListeners() {
    synchronized( listenersLock ) {
      return new ArrayList<ApplicationContextListener>( listeners );
    }
  }

  private void handleBeforeDestroyException( ApplicationContextListener listener,
                                             RuntimeException exception )
  {
    String txt = "Could not execute {0}.beforeDestroy(ApplicationContextEvent).";
    Object[] param = new Object[] { listener.getClass().getName() };
    String msg = MessageFormat.format( txt, param );
    servletContext.log( msg, exception );
  }

}
