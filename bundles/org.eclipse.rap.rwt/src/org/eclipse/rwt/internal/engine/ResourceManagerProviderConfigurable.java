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

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public class ResourceManagerProviderConfigurable implements Configurable {
  public static final String RESOURCE_MANAGER_FACTORY_PARAM
    = "org.eclipse.rwt.resourceManagerFactory";
  
  private final ServletContext servletContext;

  ResourceManagerProviderConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }
  
  public void configure( ApplicationContext context ) {
    IResourceManagerFactory factory = createFactory();
    context.getResourceManagerProvider().registerFactory( factory );
  }

  public void reset( ApplicationContext context ) {
    context.getResourceManagerProvider().deregisterFactory();
  }
  
  String getFactoryName() {
    String result = servletContext.getInitParameter( RESOURCE_MANAGER_FACTORY_PARAM );
    if( result == null ) {
      result = DefaultResourceManagerFactory.class.getName();
    }
    return result;
  }
  
  private IResourceManagerFactory createFactory() {
    String factoryName = getFactoryName();
    ClassLoader classLoader = getClass().getClassLoader();
    IResourceManagerFactory result;
    try {
      result = ( IResourceManagerFactory )ClassUtil.newInstance( classLoader, factoryName );
    } catch( ClassInstantiationException cie ) {
      String pattern = "Unable to create a resource manager factory instance of ''{0}''.";
      String msg = MessageFormat.format( pattern, new Object[] { factoryName } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
}