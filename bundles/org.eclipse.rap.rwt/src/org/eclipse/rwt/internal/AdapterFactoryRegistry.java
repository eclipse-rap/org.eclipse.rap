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
  private final Map<Class,List<AdapterFactory>> registry;
  
  AdapterFactoryRegistry() {
    lock = new Object();
    registry = new HashMap<Class,List<AdapterFactory>>();
  }
  
  // TODO [rh] consied to change signature to register(Class<? extends Adaptable>, AdapterFactory)
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
      List<AdapterFactory> adapterFactories;
      if( registry.containsKey( adaptableClass ) ) {
        adapterFactories = registry.get( adaptableClass );
      } else {
        adapterFactories = new ArrayList<AdapterFactory>();
        registry.put( adaptableClass, adapterFactories );
      }
      if( !adapterFactories.contains( adapterFactory ) ) {
        adapterFactories.add( adapterFactory );
      }
    }
  }
  
  Class[] getAdaptableClasses() {
    synchronized( lock ) {
      Set<Class> adaptableClasses = registry.keySet();
      return adaptableClasses.toArray( new Class[ adaptableClasses.size() ] );
    }
  }
  
  AdapterFactory[] getAdapterFactories( Class adaptableClass ) {
    List<AdapterFactory> adapterFactories = getAdapterFacoriesList( adaptableClass );
    AdapterFactory[] result = new AdapterFactory[ adapterFactories.size() ];
    adapterFactories.toArray( result );
    return result;
  }

  private List<AdapterFactory> getAdapterFacoriesList( Class adaptableClass ) {
    List<AdapterFactory> result;
    synchronized( lock ) {
      if( registry.containsKey( adaptableClass ) ) {
        result = registry.get( adaptableClass );
      } else {
        result = Collections.emptyList();
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