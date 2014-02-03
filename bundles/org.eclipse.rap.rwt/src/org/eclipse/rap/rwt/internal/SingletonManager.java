/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.internal.util.ClassUtil;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.internal.SerializableCompatibility;


public class SingletonManager implements SerializableCompatibility {

  private static final String ATTR_SINGLETON_MANAGER
    = SingletonManager.class.getName() + "#instance";

  private final SharedInstanceBuffer<Class<?>, AtomicReference<Object>> singletonHolders;

  SingletonManager() {
    singletonHolders = new SharedInstanceBuffer<Class<?>, AtomicReference<Object>>();
  }

  public <T> T getSingleton( Class<T> type ) {
    AtomicReference<T> singletonHolder = getSingletonHolder( type );
    synchronized( singletonHolder ) {
      T singleton = singletonHolder.get();
      if( singleton == null ) {
        singleton = ClassUtil.newInstance( type );
        singletonHolder.set( singleton );
      }
      return singleton;
    }
  }

  @SuppressWarnings( "unchecked" )
  private <T> AtomicReference<T> getSingletonHolder( Class<T> type ) {
    Object result = singletonHolders.get( type, new IInstanceCreator<AtomicReference<Object>>() {
      public AtomicReference<Object> createInstance() {
        return new AtomicReference<Object>();
      }
    } );
    return ( AtomicReference<T> )result;
  }

  public static void install( UISession uiSession ) {
    checkNotInstalled( uiSession );
    uiSession.setAttribute( ATTR_SINGLETON_MANAGER, new SingletonManager() );
  }

  public static void install( ApplicationContext applicationContext ) {
    checkNotInstalled( applicationContext );
    applicationContext.setAttribute( ATTR_SINGLETON_MANAGER, new SingletonManager() );
  }

  public static SingletonManager getInstance( UISession uiSession ) {
    return ( SingletonManager )uiSession.getAttribute( ATTR_SINGLETON_MANAGER );
  }

  public static SingletonManager getInstance( ApplicationContext applicationContext ) {
    return ( SingletonManager )applicationContext.getAttribute( ATTR_SINGLETON_MANAGER );
  }

  private static void checkNotInstalled( UISession uiSession ) {
    if( getInstance( uiSession ) != null ) {
      String msg = "SingletonManager already installed for UI session: " + uiSession.getId();
      throw new IllegalStateException( msg );
    }
  }

  private static void checkNotInstalled( ApplicationContext applicationContext ) {
    if( getInstance( applicationContext ) != null ) {
      String msg = "SingletonManager already installed for application context";
      throw new IllegalStateException( msg );
    }
  }

}
