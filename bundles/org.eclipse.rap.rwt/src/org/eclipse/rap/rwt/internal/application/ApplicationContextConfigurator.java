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

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rap.rwt.service.RWTFileSettingStoreFactory;


class ApplicationContextConfigurator {

  private final ApplicationConfiguration configuration;
  private final ServletContext servletContext;

  ApplicationContextConfigurator( ApplicationConfiguration configuration,
                                  ServletContext servletContext )
  {
    this.configuration = configuration;
    this.servletContext = servletContext;
  }

  void configure( ApplicationContextImpl applicationContext ) {
    createDefaultTheme( applicationContext );
    configureCustomSettings( applicationContext );
    configureInternalSettings( applicationContext );
  }

  void reset( ApplicationContextImpl applicationContext ) {
    resetSubSystems( applicationContext );
  }

  private void configureCustomSettings( ApplicationContextImpl applicationContext ) {
    Application application = new ApplicationImpl( applicationContext, configuration );
    configuration.configure( application );
  }

  private void configureInternalSettings( ApplicationContextImpl applicationContext ) {
    setContextDirectory( applicationContext );
    addInternalPhaseListeners( applicationContext );
    addInternalServiceHandlers( applicationContext );
    setInternalSettingStoreFactory( applicationContext );
  }

  private void setContextDirectory( ApplicationContextImpl applicationContext ) {
    String location
      = ( String )servletContext.getAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION );
    if( location == null ) {
      location = servletContext.getRealPath( "/" );
    }
    applicationContext.getResourceDirectory().configure( location );
  }

  private void resetSubSystems( ApplicationContextImpl applicationContext ) {
    applicationContext.getEntryPointManager().deregisterAll();
    applicationContext.getPhaseListenerRegistry().removeAll();
    applicationContext.getResourceRegistry().clear();
    applicationContext.getSettingStoreManager().deregisterFactory();
    resetContextDirectory( applicationContext );
  }

  private void resetContextDirectory( ApplicationContextImpl applicationContext ) {
    applicationContext.getResourceDirectory().reset();
  }

  private void addInternalPhaseListeners( ApplicationContextImpl applicationContext ) {
    applicationContext.getPhaseListenerRegistry().add( new MeasurementListener() );
  }

  private void addInternalServiceHandlers( ApplicationContextImpl applicationContext ) {
    ServiceManagerImpl serviceManager = applicationContext.getServiceManager();
    String uiCallBackId = UICallBackServiceHandler.HANDLER_ID;
    serviceManager.registerServiceHandler( uiCallBackId, new UICallBackServiceHandler() );
  }

  private void setInternalSettingStoreFactory( ApplicationContextImpl applicationContext ) {
    if( !applicationContext.getSettingStoreManager().hasFactory() ) {
      applicationContext.getSettingStoreManager().register( new RWTFileSettingStoreFactory() );
    }
  }

  private void createDefaultTheme( ApplicationContextImpl applicationContext ) {
    applicationContext.getThemeManager().initialize();
  }

}
