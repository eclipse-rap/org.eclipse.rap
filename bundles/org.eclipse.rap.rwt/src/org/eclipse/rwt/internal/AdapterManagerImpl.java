/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.util.*;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.SessionSingletonBase;


/** 
 * <p>Implementation of the <code>AdapterManager</code> protocol.</p>
 * <p>Implementation as Singleton.</p>
 */
public class AdapterManagerImpl
  extends SessionSingletonBase
  implements AdapterManager
{

  /** <p>the internal datastructure of <code>AdapterManagerImpl</code></p>*/
  private final Map registry;
  private final Map factoryCache;
  private final NullFactory nullFactory = new NullFactory();
  
  private static class NullFactory implements AdapterFactory {
    private static final Class[] EMPTY = new Class[ 0 ];
    public Object getAdapter( final Object adaptable, final Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return EMPTY;
    }
  }
  
  /** <p>creates the singleton instance of <code>AdapterManagerImpl</code></p>*/
  private AdapterManagerImpl() {
    registry = new HashMap();
    factoryCache = new HashMap();
  }
  
  /** <p>returns the singleton instance of this
   *  <code>AdapterManager</code> implementation.</p> */
  public static AdapterManager getInstance() {
    return ( AdapterManager )getInstance( AdapterManagerImpl.class );
  }
  
  
  ////////////////////////////
  // interface implementations
  
  public Object getAdapter( final Object adaptable, 
                            final Class adapter )
  {
    // Note [fappel]: Since this code is performance critical, don't change
    //                anything without checking it against a profiler.
    Object result = null;
    Object hash = calculateHash( adaptable, adapter );
    AdapterFactory cachedFactory = ( AdapterFactory )factoryCache.get( hash );
    if( cachedFactory != null ) {
      result = cachedFactory.getAdapter( adaptable, adapter );
    } else {
      result = doGetAdapter( adaptable, adapter, hash );
    }
    return result;
  }
  
  private Object calculateHash( final Object adaptable, final Class adapter ) {
    Class adaptableClass = adaptable.getClass();
    int hash = 23273 + adaptableClass.hashCode() * 37 + adapter.hashCode();
    return new Integer( hash );
  }

  private Object doGetAdapter( final Object adaptable,
                               final Class adapter, 
                               final Object hash ) {
    Object result = null;
    Class[] keys = new Class[ registry.size() ];
    registry.keySet().toArray( keys );
    for( int i = 0; result == null && i < keys.length; i++ ) {
      if( keys[ i ].isAssignableFrom( adaptable.getClass() ) ) {
        List factoryList = ( List )registry.get( keys[ i ] );
        AdapterFactory[] factories = new AdapterFactory[ factoryList.size() ];
        factoryList.toArray( factories );
        for( int j = 0; result == null && j < factories.length; j++ ) {
          Class[] adapters = factories[ j ].getAdapterList();
          for( int k = 0; result == null && k < adapters.length; k++ ) {
            if( adapter.isAssignableFrom( adapters[ k ] ) ) {
              result = factories[ j ].getAdapter( adaptable, adapter );
              factoryCache.put( hash, factories[ j ] );
            }
          }          
        }
      }
    }
    if( result == null ) {
      factoryCache.put( hash, nullFactory );
    }
    return result;
  }

  public void registerAdapters( final AdapterFactory factory, 
                                final Class adaptable ) 
  {
    if( registry.containsKey( adaptable ) ) {
      List factories = ( List )registry.get( adaptable );
      if( !factories.contains( factory ) ) {
        factories.add( factory );
      }
    } else {
      List factories = new ArrayList();
      factories.add( factory );
      registry.put( adaptable, factories );
    }
    factoryCache.clear();
  }

  public void deregisterAdapters( final AdapterFactory factory, 
                                  final Class adaptable ) {
    if( registry.containsKey( adaptable ) ) {
      List factories = ( List )registry.get( adaptable );
      if( factories.contains( factory ) ) {
        factories.remove( factory );
      }
    }
    factoryCache.clear();
  }
}