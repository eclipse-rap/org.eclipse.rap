/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class ContextConfigurable implements Configurable {
  public static final String CONFIGURATOR_PARAM = "org.eclipse.rwt.Configurator";
  
  private final Configurator configurator;
  private final ServletContext servletContext;

  public ContextConfigurable( Configurator configurator, ServletContext servletContext ) {
    this.configurator = configurator;
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext applicationContext ) {
    configureCustomSettings( applicationContext );
    configureInternalSettings( applicationContext );
  }

  public void reset( ApplicationContext applicationContext ) {
    resetSubSystems( applicationContext );
    resetApplicationStore( applicationContext );
  }

  private void configureCustomSettings( ApplicationContext applicationContext ) {
    configurator.configure( createContext( applicationContext ) );
  }

  private void configureInternalSettings( ApplicationContext applicationContext ) {
    setContextDirectory( applicationContext );
    addInternalAdapterFactories( applicationContext );
    addInternalPhaseListeners( applicationContext );
    addInternalServiceHandlers( applicationContext );
    setInternalSettingStoreFactory( applicationContext );
  }
  
  private void setContextDirectory( ApplicationContext applicationContext ) {
    RWTConfiguration configurationInstance = applicationContext.getConfiguration();
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )configurationInstance;
    configuration.configure( servletContext.getRealPath( "/" ) );
  }

  private void resetApplicationStore( ApplicationContext applicationContext ) {
    IApplicationStore storeInstance = applicationContext.getApplicationStore();
    ApplicationStoreImpl applicationStore = ( ApplicationStoreImpl )storeInstance;
    applicationStore.reset();
  }

  private void resetSubSystems( ApplicationContext applicationContext ) {
    applicationContext.getAdapterManager().deregisterAdapters();
    applicationContext.getBrandingManager().deregisterAll();
    applicationContext.getEntryPointManager().deregisterAll();
    applicationContext.getPhaseListenerRegistry().removeAll();
    applicationContext.getResourceRegistry().clear();
    applicationContext.getSettingStoreManager().deregisterFactory();
    resetConfiguration( applicationContext );
  }

  private void resetConfiguration( ApplicationContext applicationContext ) {
    RWTConfiguration configurationInstance = applicationContext.getConfiguration();
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )configurationInstance;
    configuration.reset();
  }
  
  private void addInternalAdapterFactories( ApplicationContext applicationContext ) {
    LifeCycleAdapterFactory adapterFactory = new LifeCycleAdapterFactory();
    applicationContext.getAdapterManager().registerAdapters( Widget.class, adapterFactory );
    applicationContext.getAdapterManager().registerAdapters( Display.class, adapterFactory );
  }

  private void addInternalPhaseListeners( ApplicationContext applicationContext ) {
    applicationContext.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    applicationContext.getPhaseListenerRegistry().add( new MeasurementListener() );
  }
  
  private void addInternalServiceHandlers( ApplicationContext applicationContext ) {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    String uiCallBackId = UICallBackServiceHandler.HANDLER_ID;
    serviceManager.registerServiceHandler( uiCallBackId, new UICallBackServiceHandler() );
    String jsLibraryId = JSLibraryServiceHandler.HANDLER_ID;
    serviceManager.registerServiceHandler( jsLibraryId, new JSLibraryServiceHandler() );
  }

  private void setInternalSettingStoreFactory( ApplicationContext applicationContext ) {
    if( !applicationContext.getSettingStoreManager().hasFactory() ) {
      applicationContext.getSettingStoreManager().register( new RWTFileSettingStoreFactory() );
    }
  }

  private ContextImpl createContext( ApplicationContext applicationContext ) {
    return new ContextImpl( applicationContext, configurator );
  }
}