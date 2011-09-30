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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.engine.configurables.EntryPointManagerConfigurable;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class RWTServletContextListener implements ServletContextListener {
  
  public static final String PARAMETER_SEPARATOR = ",";
  public static final String PARAMETER_SPLIT = "#";
  
  private Application application;
  
  private static class EntryPointRunnerConfigurator implements ApplicationConfigurator {

    private final Class<? extends IEntryPoint> entryPointClass;

    private EntryPointRunnerConfigurator( Class<? extends IEntryPoint> entryPointClass ) {
      this.entryPointClass = entryPointClass;
    }

    public void configure( ApplicationConfiguration configuration ) {
      configuration.addEntryPoint( EntryPointManager.DEFAULT, entryPointClass );
    }
  }
    
  public void contextInitialized( ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    ApplicationConfigurator configurator = readConfigurator( servletContext );
    application = new Application( servletContext, configurator );
    application.start();
  }

  public void contextDestroyed( ServletContextEvent evt ) {
    application.stop();
    application = null;
  }

  private ApplicationConfigurator readConfigurator( ServletContext servletContext ) {
    ApplicationConfigurator result;
    if( hasConfiguratorParam( servletContext ) ) {
      result = readApplicationConfigurator( servletContext );
    } else {
      result = readEntryPointRunnerConfigurator( servletContext );
    }
    return result;
  }

  private boolean hasConfiguratorParam( ServletContext servletContext ) {
    return null != servletContext.getInitParameter( ApplicationConfigurable.CONFIGURATOR_PARAM );
  }
  
  private ApplicationConfigurator readApplicationConfigurator( ServletContext servletContext ) {
    String name = servletContext.getInitParameter( ApplicationConfigurable.CONFIGURATOR_PARAM );
    return newConfigurator( name );
  }
  
  private ApplicationConfigurator newConfigurator( String className ) {
    ClassLoader loader = getClass().getClassLoader();
    return ( ApplicationConfigurator )ClassUtil.newInstance( loader, className );
  }

  private ApplicationConfigurator readEntryPointRunnerConfigurator( ServletContext context ) {
    try {
      return doReadEntryPointRunnerConfigurator( context );
    } catch( ClassNotFoundException cnfe ) {
      throw new IllegalArgumentException( cnfe );
    }
  }

  @SuppressWarnings("unchecked")
  private ApplicationConfigurator doReadEntryPointRunnerConfigurator( ServletContext context )
    throws ClassNotFoundException
  {
    String entryPointsParam = EntryPointManagerConfigurable.ENTRY_POINTS_PARAM;
    String className = context.getInitParameter( entryPointsParam );
    ClassLoader loader = getClass().getClassLoader();
    Class<?> entryPointClass = loader.loadClass( className );
    return new EntryPointRunnerConfigurator( ( Class<? extends IEntryPoint> )entryPointClass );
  }
}