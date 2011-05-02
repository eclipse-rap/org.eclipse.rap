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
  
  private final AdapterFactoryRegistry registry;
  /* key: hash of adaptableClass * adapterClass, value: AdapterFactory */
  private final Map bufferedAdapterFactories;
  
  public AdapterManager() {
    registry = new AdapterFactoryRegistry();
    bufferedAdapterFactories = new HashMap();
  }
  
  // TODO [rh] synchronize more fine grained
  public synchronized Object getAdapter( Object adaptable, Class adapter ) {
    // [fappel] This code is performance critical, don't change without checking against a profiler
    Integer hash = calculateHash( adaptable, adapter );
    AdapterFactory factory = ( AdapterFactory )bufferedAdapterFactories.get( hash );
    if( factory == null ) {
      factory = findAndBufferAdapterFactory( adaptable, adapter, hash );
    } 
    return factory.getAdapter( adaptable, adapter );
  }
  
  public synchronized void registerAdapters( Class adaptableClass, AdapterFactory adapterFactory ) {
    registry.register( adaptableClass, adapterFactory );
    bufferedAdapterFactories.clear();
  }

  private static Integer calculateHash( Object adaptable, Class adapterClass ) {
    Class adaptableClass = adaptable.getClass();
    int hash = 23273 + adaptableClass.hashCode() * 37 + adapterClass.hashCode();
    return new Integer( hash );
  }

  private AdapterFactory findAndBufferAdapterFactory( Object adaptable, 
                                                      Class adapter, 
                                                      Integer hash ) 
  {
    AdapterFactory result = null;
    Class[] adaptableClasses = registry.getAdaptableClasses();
    for( int i = 0; result == null && i < adaptableClasses.length; i++ ) {
      if( adaptableClasses[ i ].isAssignableFrom( adaptable.getClass() ) ) {
        AdapterFactory[] factories = registry.getAdapterFactories( adaptableClasses[ i ] );
        for( int j = 0; result == null && j < factories.length; j++ ) {
          Class[] adapters = factories[ j ].getAdapterList();
          for( int k = 0; result == null && k < adapters.length; k++ ) {
            if( adapter.isAssignableFrom( adapters[ k ] ) ) {
              result = factories[ j ];
            }
          }          
        }
      }
    }
    if( result == null ) {
      result = NULL_ADAPTER_FACTORY;
    }
    bufferedAdapterFactories.put( hash, result );
    return result;
  }
}