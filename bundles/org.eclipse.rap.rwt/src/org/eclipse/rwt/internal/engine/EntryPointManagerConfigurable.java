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

import org.eclipse.rwt.internal.lifecycle.EntryPointManager;


class EntryPointManagerConfigurable implements Configurable {
  private final ServletContext servletContext;
  
  private static class EntryPointDeclaration {
    private final String className;
    private final String name;
    
    private EntryPointDeclaration( String className, String name ) {
      this.className = className;
      this.name = name;
    }

    String getClassName() {
      return className;
    }

    String getName() {
      return name;
    }
  }
  
  EntryPointManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    if( hasEntryPointsConfigured() ) {
      registerEntryPoints( context );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getEntryPointManager().deregisterAll();
  }

  private void registerEntryPoints( ApplicationContext context ) {
    String[] declarations = parseEntryPoints();
    for( int i = 0; i < declarations.length; i++ ) {
      EntryPointDeclaration declaration = parseEntryPointDeclaration( declarations[ i ] );
      registerEntryPoint( context, declaration );
    }
  }

  private void registerEntryPoint( ApplicationContext context, EntryPointDeclaration declaration ) {
    Class clazz = loadClass( declaration );
    context.getEntryPointManager().register( declaration.getName(), clazz );
  }

  private Class loadClass( EntryPointDeclaration declaration ) {
    Class result;
    try {
      result = Class.forName( declaration.getClassName() );
    } catch( ClassNotFoundException cnfe ) {
      String text = "Could not find class ''{0}'' for entry point ''{1}''.";
      Object[] args = new Object[] { declaration.getClassName(), declaration.getName() };
      String msg = MessageFormat.format( text, args );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }

  private EntryPointDeclaration parseEntryPointDeclaration( String declaration ) {
    String[] parts = declaration.trim().split( RWTServletContextListener.PARAMETER_SPLIT );
    String className = parts[ 0 ];
    String name = parts.length > 1 ? parts[ 1 ] : EntryPointManager.DEFAULT;
    return new EntryPointDeclaration( className, name );
  }

  private String[] parseEntryPoints() {
    String param = getEntryPointParameters();
    return param.split( RWTServletContextListener.PARAMETER_SEPARATOR );
  }

  private boolean hasEntryPointsConfigured() {
    return null != getEntryPointParameters();
  }
  
  private String getEntryPointParameters() {
    return servletContext.getInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM );
  }
}