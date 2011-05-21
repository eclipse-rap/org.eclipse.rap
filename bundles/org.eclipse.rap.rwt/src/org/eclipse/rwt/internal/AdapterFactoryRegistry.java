/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.util.ParamCheck;


class AdapterFactoryRegistry {

  // TODO [rh] if we decide to make the adapter mechanism internal, the concurrency lock can be 
  //      removed as AdapterFactories are then only registered during startup
  //      +1 [fappel]
  private final Object lock;
  /* key: Class<Adaptable>, value: List<AdapterFactory> */
  private final Map registry;
  
  AdapterFactoryRegistry() {
    lock = new Object();
    registry = new HashMap();
  }
  
  void register( Class adaptableClass, AdapterFactory adapterFactory ) {
    ParamCheck.notNull( adapterFactory, "adapterFactory" );
    ParamCheck.notNull( adaptableClass, "adaptableClass" );
    checkAdaptableClassImplementsAdaptable( adaptableClass );
    registerInternal( adaptableClass, adapterFactory ); 
  }

  void deregisterAdapters() {
    synchronized( lock ) {
      registry.clear();
    }
  }

  private void registerInternal( Class adaptableClass, AdapterFactory adapterFactory ) {
    synchronized( lock ) {
      List adapterFactories;
      if( registry.containsKey( adaptableClass ) ) {
        adapterFactories = ( List )registry.get( adaptableClass );
      } else {
        adapterFactories = new ArrayList();
        registry.put( adaptableClass, adapterFactories );
      }
      if( !adapterFactories.contains( adapterFactory ) ) {
        adapterFactories.add( adapterFactory );
      }
    }
  }
  
  Class[] getAdaptableClasses() {
    synchronized( lock ) {
      Set adaptableClasses = registry.keySet();
      Class[] result = new Class[ adaptableClasses.size() ];
      adaptableClasses.toArray( result );
      return result;
    }
  }
  
  AdapterFactory[] getAdapterFactories( Class adaptableClass ) {
    List adapterFactories = getAdapterFacoriesList( adaptableClass );
    AdapterFactory[] result = new AdapterFactory[ adapterFactories.size() ];
    adapterFactories.toArray( result );
    return result;
  }

  private List getAdapterFacoriesList( Class adaptableClass ) {
    List result;
    synchronized( lock ) {
      if( registry.containsKey( adaptableClass ) ) {
        result = ( List )registry.get( adaptableClass );
      } else {
        result = Collections.EMPTY_LIST;
      }
    }
    return result;
  }
  
  private static void checkAdaptableClassImplementsAdaptable( Class adaptableClass ) {
    if( !Adaptable.class.isAssignableFrom( adaptableClass ) ) {
      String text = "The adaptableClass must implement {0}.";
      String msg = MessageFormat.format( text, new Object[] { Adaptable.class.getName() } );
      throw new IllegalArgumentException( msg );
    }
  }
}