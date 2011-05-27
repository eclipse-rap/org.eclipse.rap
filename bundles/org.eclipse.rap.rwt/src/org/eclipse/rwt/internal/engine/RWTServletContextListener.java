/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.*;


public class RWTServletContextListener implements ServletContextListener {
  public static final String PARAMETER_SEPARATOR = ",";
  public static final String PARAMETER_SPLIT = "#";
  
  private static final String CONFIGURABLES
    = RWTServletContextListener.class.getName() + "#CONFIGURABLES";

  public void contextInitialized( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext applicationContext = new ApplicationContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    addConfigurables( servletContext, applicationContext );
    applicationContext.activate();
  }

  public void contextDestroyed( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    applicationContext.deactivate();
    removeConfigurables( servletContext, applicationContext );
    ApplicationContextUtil.remove( servletContext );
  }
  
  protected Configurable[] createConfigurables( ServletContext servletContext ) {
    Configurable[] result = new Configurable[] {
      new ConfigurationReaderConfigurable( servletContext ),
      new ResourceManagerProviderConfigurable( servletContext ),
      new EntryPointManagerConfigurable( servletContext ),
      new BrandingManagerConfigurable( servletContext ),
      new SettingStoreManagerConfigurable( servletContext ),
      new PhaseListenerRegistryConfigurable( servletContext ),
      new AdapterManagerConfigurable( servletContext ),
      new ResourceRegistryConfigurable( servletContext ),
      new ServiceManagerConfigurable(),
      new ThemeManagerConfigurable( servletContext )
    };
    return result;
  }

  private void addConfigurables( ServletContext servletContext, ApplicationContext appContext ) {
    Configurable[] configurables = createConfigurables( servletContext );
    for( int i = 0; i < configurables.length; i++ ) {
      appContext.addConfigurable( configurables[ i ] );
    }
    bufferConfigurables( configurables, servletContext );
  }

  private void removeConfigurables( ServletContext servletContext, ApplicationContext appContext ) {
    Configurable[] configurables = getConfigurables( servletContext );
    for( int i = 0; i < configurables.length; i++ ) {
      appContext.removeConfigurable( configurables[ i ] );
    }
    removeConfigurables( servletContext );
  }
    
  private void bufferConfigurables( Configurable[] configurables, ServletContext servletContext ) {
    servletContext.setAttribute( CONFIGURABLES, configurables );
  }

  private Configurable[] getConfigurables( ServletContext servletContext ) {
    return ( Configurable[] )servletContext.getAttribute( CONFIGURABLES );
  }

  private void removeConfigurables( ServletContext servletContext ) {
    servletContext.removeAttribute( CONFIGURABLES );
  }
}