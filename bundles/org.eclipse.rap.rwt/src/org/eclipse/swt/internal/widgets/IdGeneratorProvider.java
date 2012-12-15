/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rap.rwt.service.ApplicationContext;


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
    ApplicationContext applicationContext = RWTFactory.getApplicationContext();
    Object result = applicationContext.getAttribute( ATTR_ID_GENERATOR_CLASS );
    if( result == null ) {
      String className = RWTProperties.getIdGeneratorClassName();
      if( className == null ) {
        result = IdGeneratorImpl.class;
      } else {
        try {
          result = loadClass( className ).asSubclass( IdGenerator.class );
        } catch( ClassCastException exception ) {
          String message = "Class is not an instance of IdGenerator: " + className;
          throw new ClassInstantiationException( message, exception );
        }
      }
      applicationContext.setAttribute( ATTR_ID_GENERATOR_CLASS, result );
    }
    return ( Class<? extends IdGenerator> )result;
  }

  private static Class<?> loadClass( String className ) {
    Class<?> result;
    try {
      result = IdGeneratorProvider.class.getClassLoader().loadClass( className );
    } catch( ClassNotFoundException exception ) {
      throw new ClassInstantiationException( "Failed to load class: " + className, exception );
    }
    return result;
  }

}
