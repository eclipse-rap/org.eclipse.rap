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
  
  public static class ContextDestroyer implements Runnable {
    private final ServletContext servletContext;

    public ContextDestroyer( ServletContext servletContext ) {
      this.servletContext = servletContext;
    }

    public void run() {
/////////////////////////////////////////////////////////////////////////
// TODO [fappel]: check which deregistration methods are really necessary
//                since all singletons get destroyed at the end of 
//                the context lifecycle. Commented deregisteredThemes
//                since this causes performance problems of tests...  
//      deregisterThemes( servletContext );
      
      RWTFactory.getLifeCycleFactory().destroy();
      ApplicationContextUtil.getApplicationContext( servletContext ).deactivate();
    }
  }

  public static class ContextInitializer implements Runnable {
    protected final ServletContext servletContext;

    public ContextInitializer( ServletContext servletContext ) {
      this.servletContext = servletContext;
    }

    public void run() {
      ApplicationContextUtil.getApplicationContext( servletContext ).activate();
    }
  }

  ///////////////////////////////////////////
  // implementation of ServletContextListener

  public void contextInitialized( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext applicationContext = registerDefaultApplicationContext( servletContext );
    registerConfigurables( servletContext, applicationContext );
    applicationContext.activate();
//    ContextInitializer initializer = new ContextInitializer( servletContext );
//    ApplicationContextUtil.runWithInstance( applicationContext, initializer );
  }

  public void contextDestroyed( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationContext applicationContext
      = ApplicationContextUtil.getApplicationContext( servletContext );
//    ContextDestroyer destroyer = new ContextDestroyer( servletContext );
//    ApplicationContextUtil.runWithInstance( applicationContext, destroyer );
    applicationContext.deactivate();
    deregisterConfigurables( servletContext, applicationContext );
    deregisterDefaultApplicationContext( servletContext );
  }

  public static void registerConfigurables( ServletContext servletContext,
                                            ApplicationContext applicationContext )
  {
    Configurables configurables = new Configurables( servletContext );
    configurables.add( applicationContext );
    bufferConfigurables( configurables, servletContext );
  }
  
  public static void deregisterConfigurables( ServletContext servletContext,
                                              ApplicationContext applicationContext )
  {
    Configurables configurables = getConfigurables( servletContext );
    configurables.remove( applicationContext );
    removeConfigurables( servletContext );
  }

  //////////////////
  // helping methods
  
  private ApplicationContext registerDefaultApplicationContext( ServletContext servletContext ) {
    return ApplicationContextUtil.registerDefaultApplicationContext( servletContext );
  }
  
  void deregisterDefaultApplicationContext( ServletContext servletContext ) {
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
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
  
  static  void removeBuffer( String key, ServletContext servletContext ) {
    servletContext.removeAttribute( key );
  }

  static Set getBuffer( String key, ServletContext servletContext ) {
    Set result = ( Set )servletContext.getAttribute( key );
    if( result == null ) {
      result = new HashSet();
      servletContext.setAttribute( key, result );
    }
    return result;
  }
}