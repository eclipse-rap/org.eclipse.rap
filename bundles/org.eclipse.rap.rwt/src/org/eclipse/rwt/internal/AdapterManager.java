/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.AdapterFactory;


public class AdapterManager {

  private static final NullAdapterFactory NULL_ADAPTER_FACTORY = new NullAdapterFactory();

  private static class NullAdapterFactory implements AdapterFactory {
    private static final Class[] EMPTY = new Class[ 0 ];
    public Object getAdapter( Object adaptable, Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return EMPTY;
    }
  }
  
  private final Object lock;
  private final AdapterFactoryRegistry registry;
  /* key: hash of adaptableClass * adapterClass, value: AdapterFactory */
  private final Map bufferedAdapterFactories;
  
  public AdapterManager() {
    lock = new Object();
    registry = new AdapterFactoryRegistry();
    bufferedAdapterFactories = new HashMap();
  }
  
  public Object getAdapter( Object adaptable, Class adapter ) {
    // [fappel] This code is performance critical, don't change without checking against a profiler
    AdapterFactory adapterFactory;
    synchronized( lock ) {
      adapterFactory = findBufferedAdapterFactory( adaptable, adapter );
      if( adapterFactory == null ) {
        adapterFactory = determineAdapterFactory( adaptable, adapter );
        bufferAdapterFactory( adaptable, adapter, adapterFactory );
      }
    }
    return adapterFactory.getAdapter( adaptable, adapter );
  }

  public void registerAdapters( Class adaptableClass, AdapterFactory adapterFactory ) {
    registry.register( adaptableClass, adapterFactory );
    synchronized( lock ) {
      bufferedAdapterFactories.clear();
    }
  }

  public void deregisterAdapters() {
    registry.deregisterAdapters();
    synchronized( lock ) {
      bufferedAdapterFactories.clear();
    }
  }

  private AdapterFactory findBufferedAdapterFactory( Object adaptable, Class adapter ) {
    Integer hash = calculateHash( adaptable, adapter );
    return ( AdapterFactory )bufferedAdapterFactories.get( hash );
  }

  private AdapterFactory determineAdapterFactory( Object adaptable, Class adapter ) {
    AdapterFactory result = NULL_ADAPTER_FACTORY;
    boolean found = false;
    Class[] adaptableClasses = registry.getAdaptableClasses();
    for( int i = 0; !found && i < adaptableClasses.length; i++ ) {
      if( adaptableClasses[ i ].isAssignableFrom( adaptable.getClass() ) ) {
        AdapterFactory[] factories = registry.getAdapterFactories( adaptableClasses[ i ] );
        for( int j = 0; !found && j < factories.length; j++ ) {
          Class[] adapters = factories[ j ].getAdapterList();
          for( int k = 0; !found && k < adapters.length; k++ ) {
            if( adapter.isAssignableFrom( adapters[ k ] ) ) {
              result = factories[ j ];
              found = true;
            }
          }          
        }
      }
    }
    return result;
  }

  private void bufferAdapterFactory( Object adaptable, 
                                     Class adapter, 
                                     AdapterFactory adapterFactory )
  {
    Integer hash = calculateHash( adaptable, adapter );
    bufferedAdapterFactories.put( hash, adapterFactory );
  }

  private static Integer calculateHash( Object adaptable, Class adapterClass ) {
    Class adaptableClass = adaptable.getClass();
    int hash = 23273 + adaptableClass.hashCode() * 37 + adapterClass.hashCode();
    return new Integer( hash );
  }
}