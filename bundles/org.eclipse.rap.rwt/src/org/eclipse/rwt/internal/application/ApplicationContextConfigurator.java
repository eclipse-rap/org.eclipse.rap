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

import javax.servlet.ServletContext;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.internal.engine.RWTConfiguration;
import org.eclipse.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;


class ApplicationContextConfigurator {

  private final ApplicationConfiguration configuration;
  private final ServletContext servletContext;

  ApplicationContextConfigurator( ApplicationConfiguration configuration,
                                  ServletContext servletContext )
  {
    this.configuration = configuration;
    this.servletContext = servletContext;
  }

  void configure( ApplicationContext applicationContext ) {
    configureCustomSettings( applicationContext );
    configureInternalSettings( applicationContext );
  }

  void reset( ApplicationContext applicationContext ) {
    resetSubSystems( applicationContext );
    resetApplicationStore( applicationContext );
  }

  private void configureCustomSettings( ApplicationContext applicationContext ) {
    configuration.configure( createContext( applicationContext ) );
  }

  private void configureInternalSettings( ApplicationContext applicationContext ) {
    setContextDirectory( applicationContext );
    addInternalPhaseListeners( applicationContext );
    addInternalServiceHandlers( applicationContext );
    setInternalSettingStoreFactory( applicationContext );
  }

  private void setContextDirectory( ApplicationContext applicationContext ) {
    RWTConfiguration configurationInstance = applicationContext.getConfiguration();
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )configurationInstance;
    String location
      = ( String )servletContext.getAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION );
    if( location != null ) {
      configuration.configure( location );
    } else {
      configuration.configure( servletContext.getRealPath( "/" ) );
    }
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

  private void addInternalPhaseListeners( ApplicationContext applicationContext ) {
    applicationContext.getPhaseListenerRegistry().add( new MeasurementListener() );
  }

  private void addInternalServiceHandlers( ApplicationContext applicationContext ) {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    String uiCallBackId = UICallBackServiceHandler.HANDLER_ID;
    serviceManager.registerServiceHandler( uiCallBackId, new UICallBackServiceHandler() );
  }

  private void setInternalSettingStoreFactory( ApplicationContext applicationContext ) {
    if( !applicationContext.getSettingStoreManager().hasFactory() ) {
      applicationContext.getSettingStoreManager().register( new RWTFileSettingStoreFactory() );
    }
  }

  private ApplicationImpl createContext( ApplicationContext applicationContext ) {
    return new ApplicationImpl( applicationContext, configuration );
  }
}