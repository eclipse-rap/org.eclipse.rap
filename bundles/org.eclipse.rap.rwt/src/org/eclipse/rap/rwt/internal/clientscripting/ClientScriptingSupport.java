/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.clientscripting;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;


/*
 * Adds provisional support for ClientScripting, see http://wiki.eclipse.org/RAP/ClientScripting
 * Unit tests are located in org.eclipse.rap.clientscripting.test
 */
public class ClientScriptingSupport {

  private static final String CLIENT_LISTENER_CLASS_NAME
    = "org.eclipse.rap.clientscripting.ClientListener";

  public static boolean isClientListener( Listener listener ) {
    return CLIENT_LISTENER_CLASS_NAME.equals( listener.getClass().getName() );
  }

  public static void addClientListenerTo( Widget widget, int eventType, Listener listener ) {
    invokeMethod( "addTo", widget, eventType, listener );
  }

  public  static void removeClientListenerFrom( Widget widget, int eventType, Listener listener ) {
    invokeMethod( "removeFrom", widget, eventType, listener );
  }

  private static void invokeMethod( String methodName,
                                    Widget widget,
                                    int eventType,
                                    Listener listener )
  {
    try {
      Method method = findMethod( methodName, listener );
      if( method != null ) {
        method.setAccessible( true );
        method.invoke( listener, widget, Integer.valueOf( eventType ) );
      }
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private static Method findMethod( String methodName, Listener listener ) {
    Method[] declaredMethods = listener.getClass().getDeclaredMethods();
    for( Method method : declaredMethods ) {
      if( hasClientListenerSignature( methodName, method ) ) {
        return method;
      }
    }
    return null;
  }

  private static boolean hasClientListenerSignature( String methodName, Method method ) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    return method.getName().equals( methodName )
           && parameterTypes.length == 2
           && parameterTypes[ 0 ] == Widget.class
           && parameterTypes[ 1 ] == int.class;
  }

  private ClientScriptingSupport() {
    // prevent instantiation
  }

}
