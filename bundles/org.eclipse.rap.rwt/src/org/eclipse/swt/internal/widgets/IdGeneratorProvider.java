/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.util.ClassInstantiationException;


public class IdGeneratorProvider {

  private static final String ATTR_ID_GENERATOR_CLASS
    = IdGeneratorProvider.class.getName().concat( "#idGeneratorClass" );

  private IdGeneratorProvider() {
    // prevent instantiation
  }

  public static IdGenerator getIdGenerator() {
    return SingletonUtil.getSessionInstance( getIdGeneratorClass() );
  }

  @SuppressWarnings( "unchecked" )
  private static Class<? extends IdGenerator> getIdGeneratorClass() {
    ApplicationContextImpl applicationContext = getApplicationContext();
    Object result = applicationContext.getAttribute( ATTR_ID_GENERATOR_CLASS );
    if( result == null ) {
      result = getGeneratorClass();
      applicationContext.setAttribute( ATTR_ID_GENERATOR_CLASS, result );
    }
    return ( Class< ? extends IdGenerator> )result;
  }

  private static Class< ? extends IdGenerator> getGeneratorClass() {
    String className = RWTProperties.getIdGeneratorClassName();
    if( className != null ) {
      return loadCustomGeneratorClass( className );
    }
    return IdGeneratorImpl.class;
  }

  private static Class<? extends IdGenerator> loadCustomGeneratorClass( String className ) {
    try {
      return loadClass( className ).asSubclass( IdGenerator.class );
    } catch( ClassCastException exception ) {
      String message = "Class is not an instance of IdGenerator: " + className;
      throw new ClassInstantiationException( message, exception );
    }
  }

  private static Class<?> loadClass( String className ) {
    try {
      return IdGeneratorProvider.class.getClassLoader().loadClass( className );
    } catch( ClassNotFoundException exception ) {
      throw new ClassInstantiationException( "Failed to load class: " + className, exception );
    }
  }

}
