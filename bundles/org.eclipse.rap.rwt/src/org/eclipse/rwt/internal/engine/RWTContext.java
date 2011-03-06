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

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.util.ParamCheck;


public class RWTContext {
  private final Map instances;
  private boolean closedForRegistration;

  public interface InstanceTypeFactory {
    Object createInstance();
    Class getInstanceType();
  }

  public RWTContext() {
    instances = new HashMap();
  }

  public static Object getSingleton( final Class instanceType ) {
    return RWTContextUtil.getInstance().getInstance( instanceType );
  }

  // TODO [RWTContext] consider passing instanceTypes in constructor,
  //      with this change RWTContext would become an immutable class
  public void registerInstanceTypes( final Class[] instanceTypes ) {
    ParamCheck.notNull( instanceTypes, "instanceTypes" );
    checkIfRegistrationAllowed();
    createInstances( instanceTypes );
    closeContextForFurtherRegistrations();
  }

  public Object getInstance( final Class instanceType ) {
    ParamCheck.notNull( instanceType, "instanceType" );
    Object result = findInstance( instanceType );
    // do param check here to avoid duplicate map access
    checkRegistered( instanceType, result );
    return result;
  }

  private void createInstances( final Class[] instanceTypes ) {
    for( int i = 0; i < instanceTypes.length; i++ ) {
      Object instance = createInstance( instanceTypes[ i ] );
      bufferInstance( instanceTypes[ i ], instance );
    }
  }

  private Object createInstance( final Class instanceType ) {
    Object result = null;
    try {
      Constructor constructor = instanceType.getDeclaredConstructor( null );
      constructor.setAccessible( true );
      result = constructor.newInstance( null );
    } catch( Exception shouldNotHappen ) {
      handleCreationProblem( instanceType, shouldNotHappen );
    }
    return result;
  }

  private void bufferInstance( final Class instanceType,
                               final Object instance )
  {
    Object toRegister = createInstanceFromFactory( instance );
    Class registrationType = getTypeFromFactory( instanceType, instance );
    checkInstanceOf( toRegister, registrationType );
    checkAlreadyRegistered( registrationType );
    instances.put( registrationType, toRegister );
  }

  private Object createInstanceFromFactory( final Object instance ) {
    Object result = instance;
    if( instance instanceof InstanceTypeFactory ) {
      InstanceTypeFactory factory = ( InstanceTypeFactory )instance;
      result = factory.createInstance();
    }
    return result;
  }

  private Class getTypeFromFactory( final Class instanceType,
                                    final Object instance )
  {
    Class result = instanceType;
    if( instance instanceof InstanceTypeFactory ) {
      InstanceTypeFactory factory = ( InstanceTypeFactory )instance;
      result = factory.getInstanceType();
    }
    return result;
  }

  private Object findInstance( final Class instanceType ) {
    return instances.get( instanceType );
  }

  private void closeContextForFurtherRegistrations() {
    closedForRegistration = true;
  }

  private void handleCreationProblem( final Class instanceType,
                                      final Exception cause )
  {
    String pattern = "Could not create instance of type ''{0}''.";
    Object[] arguments = new Object[] { instanceType.getName() };
    String msg = MessageFormat.format( pattern, arguments );
    throw new IllegalArgumentException( msg ) {
      private static final long serialVersionUID = 1L;
      public Throwable getCause() {
        return cause;
      }
    };
  }

  private void checkRegistered( final Class instanceType,
                                final Object instance )
  {
    if( instance == null ) {
      String pattern = "Unregistered instance type ''{0}''";
      Object[] arguments = new Object[] { instanceType };
      throwIllegalArgumentException( pattern, arguments );
    }
  }

  private void checkIfRegistrationAllowed() {
    if( closedForRegistration ) {
      String msg =   "This context has already been "
                   + "closed for instance type registration.";
      throw new IllegalStateException( msg );
    }
  }

  private void checkInstanceOf( final Object instance, final Class type ) {
    if( !type.isInstance( instance ) ) {
      String pattern
        = "Instance to register does not match declared type ''{0}''.";
      Object[] arguments = new Object[] { type.getName() };
      throwIllegalArgumentException( pattern, arguments );
    }
  }

  private void checkAlreadyRegistered( final Class registrationType ) {
    if( instances.containsKey( registrationType ) ) {
      String pattern
        = "The instance type ''{0}'' has already been registered.";
      Object[] arguments = new Object[] { registrationType.getName() };
      throwIllegalArgumentException( pattern, arguments );
    }
  }

  private void throwIllegalArgumentException( final String pattern,
                                              final Object[] arx )
  {
    String msg = MessageFormat.format( pattern, arx );
    throw new IllegalArgumentException( msg );
  }
}