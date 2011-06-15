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
  
  private final ConfigurablesProvider configurablesProvider;

  public RWTServletContextListener() {
    this( new ConfigurablesProvider() );
  }
  
  RWTServletContextListener( ConfigurablesProvider configurablesProvider ) {
    this.configurablesProvider = configurablesProvider;
  }

  public void contextInitialized( ServletContextEvent evt ) {
    createApplicationContext( evt.getServletContext() );
    configureApplicationContext( evt.getServletContext() );
    activateApplicationContext( evt.getServletContext() );
  }

  public void contextDestroyed( ServletContextEvent evt ) {
    deactivateApplicationContext( evt.getServletContext() );
    deconfigureApplicationContext( evt.getServletContext() );
    disposeOfApplicationContext( evt.getServletContext() );
  }

  private void createApplicationContext( ServletContext servletContext ) {
    ApplicationContext applicationContext = new ApplicationContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
  }
  
  private void configureApplicationContext( ServletContext servletContext ) {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    Configurable[] configurables = configurablesProvider.createConfigurables( servletContext );
    ConfigurablesProvider.addConfigurables( configurables, applicationContext );
    ConfigurablesProvider.bufferConfigurables( configurables, servletContext );
  }
  
  private void activateApplicationContext( ServletContext servletContext ) {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    applicationContext.activate();
  }

  private void deactivateApplicationContext( ServletContext servletContext ) {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    applicationContext.deactivate();
  }
  
  private void deconfigureApplicationContext( ServletContext servletContext ) {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    ConfigurablesProvider.removeConfigurables( servletContext, applicationContext );
  }
  
  private void disposeOfApplicationContext( ServletContext servletContext ) {
    ApplicationContextUtil.remove( servletContext );
  }
}