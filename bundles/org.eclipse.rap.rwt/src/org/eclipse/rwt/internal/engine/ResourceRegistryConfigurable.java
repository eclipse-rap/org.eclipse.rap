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

import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.resources.IResource;


class ResourceRegistryConfigurable implements Configurable {
  private final ServletContext servletContext;

  ResourceRegistryConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    if( hasResourcesConfigured() ) {
      registerResources( context );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getResourceRegistry().clear();
  }
  
  private void registerResources( ApplicationContext context ) {
    String[] resourceClassNames = parseClassNames();
    for( int i = 0; i < resourceClassNames.length; i++ ) {
      registerResource( context, resourceClassNames[ i ].trim() );
    }
  }

  private void registerResource( ApplicationContext context, String className ) {
    IResource resource = createResource( className );
    context.getResourceRegistry().add( resource );
  }

  private String[] parseClassNames() {
    String initParam = servletContext.getInitParameter( RWTServletContextListener.RESOURCES_PARAM );
    return initParam.split( RWTServletContextListener.PARAMETER_SEPARATOR );
  }

  private boolean hasResourcesConfigured() {
    return null != servletContext.getInitParameter( RWTServletContextListener.RESOURCES_PARAM );
  }

  private IResource createResource( String className ) {
    IResource result;
    try {
      result = ( IResource )ClassUtil.newInstance( getClass().getClassLoader(), className );
    } catch( ClassInstantiationException cie ) {
      String text = "Failed to create an instance of resource ''{0}''.";
      String msg = MessageFormat.format( text, new Object[] { className } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
}