/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.lang.reflect.Constructor;


public final class ClassUtil {
  
  public static Object newInstance( ClassLoader classLoader, String className ) {
    ParamCheck.notNull( className, "className" );
    ParamCheck.notNull( classLoader, "classLoader" );
    Class type;
    try {
      type = classLoader.loadClass( className );
    } catch( ClassNotFoundException cnfe ) {
      throw new ClassInstantiationException( "Failed to load type: " + className, cnfe );
    }
    return newInstance( type );
  }
  
  public static Object newInstance( Class type ) {
    return newInstance( type, null, null );
  }

  public static Object newInstance( Class type, Class[] paramTypes, Object[] paramValues ) {
    ParamCheck.notNull( type, "type" );
    try {
      return createInstance( type, paramTypes, paramValues );
    } catch( Exception e ) {
      String msg = "Failed to create instance of type: " + type.getName();
      throw new ClassInstantiationException( msg, e );
    }
  }

  private static Object createInstance( Class type, Class[] paramTypes, Object[] paramValues ) 
    throws Exception 
  {
    Constructor constructor = type.getDeclaredConstructor( paramTypes );
    if( !constructor.isAccessible() ) {
      constructor.setAccessible( true );
    } 
    return constructor.newInstance( paramValues );
  }

  private ClassUtil() {
    // prevent instantiation
  }
}
