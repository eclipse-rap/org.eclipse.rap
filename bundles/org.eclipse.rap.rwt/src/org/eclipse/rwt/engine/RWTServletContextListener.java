/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Innoopract Informationssysteme GmbH - initial API and implementation
 *   Frank Appel - replaced singletons and static fields (Bug 337787)
 *   EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.engine;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.rwt.application.ApplicationRunner;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.IEntryPoint;


/**
 * A ServletContextListener that creates and starts an RWT application on
 * initialization and stops it on shutdown. The application to start is read
 * from the init parameter <code>org.eclipse.rwt.Configurator</code>.
 *
 * @since 1.5
 * @noextend This class is not intended to be subclassed by clients.
 */
public class RWTServletContextListener implements ServletContextListener {

  /*
   * This parameter has been used prior to RAP 1.5 to register entrypoints.
   * It is considered obsolete but still supported in 1.5.
   */
  static final String ENTRY_POINTS_PARAM = "org.eclipse.rwt.entryPoints";

  private ApplicationRunner applicationRunner;

  public void contextInitialized( ServletContextEvent event ) {
    ServletContext servletContext = event.getServletContext();
    ApplicationConfigurator configurator = readConfigurator( servletContext );
    applicationRunner = new ApplicationRunner( configurator, servletContext );
    applicationRunner.start();
  }

  public void contextDestroyed( ServletContextEvent event ) {
    applicationRunner.stop();
    applicationRunner = null;
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
    return null != servletContext.getInitParameter( ApplicationConfigurator.CONFIGURATOR_PARAM );
  }

  private ApplicationConfigurator readApplicationConfigurator( ServletContext servletContext ) {
    String name = servletContext.getInitParameter( ApplicationConfigurator.CONFIGURATOR_PARAM );
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
    String className = context.getInitParameter( ENTRY_POINTS_PARAM );
    ClassLoader loader = getClass().getClassLoader();
    Class<?> entryPointClass = loader.loadClass( className );
    return new EntryPointRunnerConfigurator( ( Class<? extends IEntryPoint> )entryPointClass );
  }

  private static class EntryPointRunnerConfigurator implements ApplicationConfigurator {

    private final Class<? extends IEntryPoint> entryPointClass;

    private EntryPointRunnerConfigurator( Class<? extends IEntryPoint> entryPointClass ) {
      this.entryPointClass = entryPointClass;
    }

    public void configure( ApplicationConfiguration configuration ) {
      configuration.addEntryPoint( "/rap", entryPointClass, null );
    }
  }

}
