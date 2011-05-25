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

import java.util.*;

import javax.servlet.*;


public final class RWTServletContextListener implements ServletContextListener {
  private static final String PREFIX = "org.eclipse.rwt.";
  public static final String ENTRY_POINTS_PARAM = PREFIX + "entryPoints";
  public static final String THEMES_PARAM = PREFIX + "themes";
  public static final String RESOURCE_MANAGER_FACTORY_PARAM = PREFIX + "resourceManagerFactory";
  public static final String SETTING_STORE_FACTORY_PARAM = PREFIX + "settingStoreFactory";
  public static final String ADAPTER_FACTORIES_PARAM = PREFIX + "adapterFactories";
  public static final String PHASE_LISTENERS_PARAM = PREFIX + "phaseListeners";
  public static final String RESOURCES_PARAM = PREFIX + "resources";
  public static final String BRANDINGS_PARAM = PREFIX + "brandings";
  
  public static final String PARAMETER_SEPARATOR = ",";
  public static final String PARAMETER_SPLIT = "#";
  
  private static final String CONFIGURABLES
    = RWTServletContextListener.class.getName() + "#CONFIGURABLES";
  
  static class Configurables {
    private final Set configurables;

    Configurables( ServletContext servletContext ) {
      configurables = new HashSet();
      configurables.add( new ConfigurationReaderConfigurable( servletContext ) );
      configurables.add( new ResourceManagerProviderConfigurable( servletContext ) );
      configurables.add( new EntryPointManagerConfigurable( servletContext ) );
      configurables.add( new BrandingManagerConfigurable( servletContext ) );
      configurables.add( new SettingStoreManagerConfigurable( servletContext ) );
      configurables.add( new PhaseListenerRegistryConfigurable( servletContext ) );
      configurables.add( new AdapterManagerConfigurable( servletContext ) );
      configurables.add( new ResourceRegistryConfigurable( servletContext ) );
      configurables.add( new ServiceManagerConfigurable() );
      configurables.add( new ThemeManagerConfigurable( servletContext ) );
      configurables.add( new LifeCycleFactoryConfigurable() );
    }

    void add( ApplicationContext applicationContext ) {
      Iterator iterator = configurables.iterator();
      while( iterator.hasNext() ) {
        Configurable configurable = ( Configurable )iterator.next();
        applicationContext.addConfigurable( configurable );
      }
    }

    void remove( ApplicationContext applicationContext ) {
      Iterator iterator = configurables.iterator();
      while( iterator.hasNext() ) {
        Configurable configurable = ( Configurable )iterator.next();
        applicationContext.removeConfigurable( configurable );
      }
    }
  }
  

  ///////////////////////////////////////////
  // implementation of ServletContextListener

  public void contextInitialized( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.createContext( servletContext );
    registerConfigurables( servletContext, applicationContext );
    applicationContext.activate();
  }

  public void contextDestroyed( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext appContext = ApplicationContextUtil.getApplicationContext( servletContext );
    appContext.deactivate();
    deregisterConfigurables( servletContext, appContext );
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
  }

  private static void registerConfigurables( ServletContext servletContext, 
                                             ApplicationContext applicationContext )
  {
    Configurables configurables = new Configurables( servletContext );
    configurables.add( applicationContext );
    bufferConfigurables( configurables, servletContext );
  }
  
  private static void deregisterConfigurables( ServletContext servletContext,
                                               ApplicationContext applicationContext )
  {
    Configurables configurables = getConfigurables( servletContext );
    configurables.remove( applicationContext );
    removeConfigurables( servletContext );
  }
  
  static void bufferConfigurables( Configurables configurables, ServletContext servletContext ) {
    servletContext.setAttribute( CONFIGURABLES, configurables );
  }

  static Configurables getConfigurables( ServletContext servletContext ) {
    return ( Configurables )servletContext.getAttribute( CONFIGURABLES );
  }

  static void removeConfigurables( ServletContext servletContext ) {
    servletContext.removeAttribute( CONFIGURABLES );
  }
}