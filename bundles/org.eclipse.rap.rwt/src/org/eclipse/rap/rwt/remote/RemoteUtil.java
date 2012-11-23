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
package org.eclipse.rap.rwt.remote;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.rap.rwt.internal.protocol.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


/**
 * @since 2.0
 */
public class RemoteUtil {
  
  // TODO: don't return Impl. Move API method into interface.
  public static <T> RemoteObjectImpl<T> createRemoteObject( 
    T object,
    Class< ? extends RemoteObjectSpecification<T>> specificationType )
  {
    return new RemoteObjectImpl<T>( object, specificationType );
  }
  
  public static <T> PropertyHandler<T> createBooleanPropertyHandler( Class<T> remoteType, String propertyName ) {
    return createReflectivePropertyHandler( Boolean.class, remoteType, propertyName );
  }
  
  public static <T> PropertyHandler<T> createStringPropertyHandler( Class<T> remoteType, String propertyName ) {
    return createReflectivePropertyHandler( String.class, remoteType, propertyName );
  }
  
  public static <T> PropertyHandler<T> createIntPropertyHandler( Class<T> remoteType, String propertyName ) {
    return createReflectivePropertyHandler( Integer.class, remoteType, propertyName );
  }
  
  public static <T> PropertyHandler<T> createDoublePropertyHandler( Class<T> remoteType, String propertyName ) {
    return createReflectivePropertyHandler( Double.class, remoteType, propertyName );
  }
  
  public static <T> PropertyHandler<T> createObjectPropertyHandler( Class<T> remoteType, String propertyName ) {
    return createReflectivePropertyHandler( Object.class, remoteType, propertyName );
  }

  private static <T> PropertyHandler<T> createReflectivePropertyHandler( 
    Class<?> type, 
    Class<T> remoteType, 
    String propertyName ) 
  {
    ParamCheck.notNull( remoteType, "remoteType" );
    ParamCheck.notNullOrEmpty( propertyName, "Property Name" );
    return new ReflectivePropertyHandler<T>( propertyName, type );
  }
  
  private static class ReflectivePropertyHandler<T> implements PropertyHandler<T> {
    
    private final String name;
    private final Class<?> type;
  
    public ReflectivePropertyHandler( String name, Class<?> type ) {
      this.name = name;
      this.type = type;
    }
  
    public void set( Object object, Object value ) throws IllegalStateException {
      String methodName = "set" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1, name.length() );
      try {
        invokeSetMethod( methodName, object, value );
      } catch( Exception invokeException ) {
        throw new IllegalStateException( "Could not invoke method " 
                                         + methodName 
                                         + " on object of type " 
                                         + object.getClass().getName(), 
                                         invokeException );
      } 
    }

    private void invokeSetMethod( String methodName, Object object, Object propertyValue )
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
      java.lang.reflect.Method method = object.getClass().getDeclaredMethod( methodName, getTypeToCast( type ) );
      if( !method.isAccessible() ) {
        method.setAccessible( true );
      }
      method.invoke( object, type.cast( propertyValue ) );
    }

    private static Class<?> getTypeToCast( Class<?> typeToCast ) {
      Class<?> result = typeToCast;
      if( typeToCast == Boolean.class ) {
        result = boolean.class;
      } else if( typeToCast == Integer.class ) {
        result = int.class;
      } else if( typeToCast == Double.class ) {
        result = double.class;
      }
      return result;
    }
    
  }
  
  private RemoteUtil() {
    // prevent instantiation
  }
}
