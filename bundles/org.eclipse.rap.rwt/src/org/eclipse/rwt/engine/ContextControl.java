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
package org.eclipse.rwt.engine;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;

// TODO [fappel]: clarify API and write Tests...
public class ContextControl {
  public final static String RESOURCES = ResourceManagerImpl.RESOURCES;
  
  private final ServletContext servletContext;
  private final Configurator configurator;
  private final ApplicationContext applicationContext;
  
  public ContextControl( ServletContext servletContext, Configurator configurator ) {
    this.applicationContext = new ApplicationContext();
    this.servletContext = servletContext;
    this.configurator = configurator;
  }
  
  public void startContext() {
    setContextDirectory();
    applicationContext.addConfigurable( new ApplicationConfigurable( configurator ) );
    ApplicationContextUtil.set( servletContext, applicationContext );
    applicationContext.activate();
  }
  
  public void stopContext() {
    applicationContext.deactivate();
    ApplicationContextUtil.remove( servletContext );
  }
  
  public HttpServlet createServlet() {
    return new RWTDelegate();
  }
  
  public String[] getServletNames() {
    AbstractBranding[] all = applicationContext.getBrandingManager().getAll();
    Set<String> names = new HashSet<String>();
    for( AbstractBranding branding : all ) {
      names.add( branding.getServletName() );
    }
    return names.toArray( new String[ names.size() ] );
  }
  
  private void setContextDirectory() {
    RWTConfiguration configurationInstance = applicationContext.getConfiguration();
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )configurationInstance;
    configuration.configure( servletContext.getRealPath( "/" ) );
  }
}