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
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ParamCheck;


/**
 * @since 2.0
 */
public class RemoteUtil {

  public static interface EventNotificationHandler<T> {
    
    void notify( T object, Map<String, Object> properties );
  }
  
  public static interface CallHandler<T> {
    
    void call( T object, Map<String, Object> properties );
  }
  
  public static interface PropertyHandler<T> {
    
    void set( T object, Object propertyValue );
  }
  
  public static <T> EventNotification<T> createEventNotification( 
    final String eventName, 
    final EventNotificationHandler<T> eventNotificationHandler ) 
  {
    return new EventNotification<T>() {

      public String getName() {
        return eventName;
      }

      public void notify( T object, Map<String, Object> properties ) {
        eventNotificationHandler.notify( object, properties );
      }
    };
  }
  
  public static <T> Call<T> createCall( final String methodName, final CallHandler<T> callHandler ) {
    return new Call<T>() {
      
      public String getName() {
        return methodName;
      }
      
      public void call( T object, Map<String, Object> properties ) {
        callHandler.call( object, properties );
      }
    };
  }
  
  public static <T> Property<T> createProperty( final String propertyName, final PropertyHandler<T> propertyHandler ) {
    return new Property<T>() {

      public String getName() {
        return propertyName;
      }

      public void set( T object, Object value ) {
        propertyHandler.set( object, value );
      }
      
    };
  }
  
  public static <T> Property<T> createBooleanProperty( Class<T> remoteType, String propertyName ) {
    return createReflectiveProperty( Boolean.class, remoteType, propertyName );
  }
  
  public static <T> Property<T> createStringProperty( Class<T> remoteType, String propertyName ) {
    return createReflectiveProperty( String.class, remoteType, propertyName );
  }
  
  public static <T> Property<T> createIntProperty( Class<T> remoteType, String propertyName ) {
    return createReflectiveProperty( Integer.class, remoteType, propertyName );
  }
  
  public static <T> Property<T> createDoubleProperty( Class<T> remoteType, String propertyName ) {
    return createReflectiveProperty( Double.class, remoteType, propertyName );
  }
  
  public static <T> Property<T> createObjectProperty( Class<T> remoteType, String propertyName ) {
    return createReflectiveProperty( Object.class, remoteType, propertyName );
  }

  private static <T> Property<T> createReflectiveProperty( Class<?> type, Class<T> remoteType, String propertyName ) {
    ParamCheck.notNullOrEmpty( propertyName, "propertyName" );
    ParamCheck.notNull( remoteType, "remoteType" );
    return new ReflectiveProperty<T>( propertyName, type );
  }
  
  private static class ReflectiveProperty<T> implements Property<T> {
    
    private final String name;
    private final Class<?> type;
  
    public ReflectiveProperty( String name, Class<?> type ) {
      this.name = name;
      this.type = type;
    }
  
    public String getName() {
      return name;
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
      Method method = object.getClass().getDeclaredMethod( methodName, getTypeToCast( type ) );
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
