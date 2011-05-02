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

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.util.ParamCheck;


public class AdapterManagerImpl implements AdapterManager {

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
  
  /* key: Class<Adaptable> (of adaaptable), value: List<AdapterFactory> */
  private final Map registry;
  /* key: hash of adaptableClass * adapterClass, value: AdapterFactory */
  private final Map bufferedAdapterFactories;
  
  public AdapterManagerImpl() {
    registry = new HashMap();
    bufferedAdapterFactories = new HashMap();
  }
  
  ////////////////////////////
  // interface implementations
  
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
    Class[] adaptableClasses = new Class[ registry.size() ];
    registry.keySet().toArray( adaptableClasses );
    for( int i = 0; result == null && i < adaptableClasses.length; i++ ) {
      if( adaptableClasses[ i ].isAssignableFrom( adaptable.getClass() ) ) {
        List factoryList = ( List )registry.get( adaptableClasses[ i ] );
        AdapterFactory[] factories = new AdapterFactory[ factoryList.size() ];
        factoryList.toArray( factories );
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
  
  public void registerAdapterFactory( Class factoryClass, Class adaptableClass ) {
    AdapterFactory adapterFactory = AdapterFactoryCreator.create( factoryClass );
    registerAdapters( adapterFactory, adaptableClass );
  }

  public synchronized void registerAdapters( AdapterFactory adapterFactory, Class adaptableClass ) {
    ParamCheck.notNull( adapterFactory, "adapterFactory" );
    ParamCheck.notNull( adaptableClass, "adaptableClass" );
    checkAdaptableClassImplementsAdaptable( adaptableClass );
    if( registry.containsKey( adaptableClass ) ) {
      List adapterFactories = ( List )registry.get( adaptableClass );
      if( !adapterFactories.contains( adapterFactory ) ) {
        adapterFactories.add( adapterFactory );
      } 
    } else {
      List adapterFactories = new ArrayList();
      adapterFactories.add( adapterFactory );
      registry.put( adaptableClass, adapterFactories );
    }
    bufferedAdapterFactories.clear();
  }

  private static void checkAdaptableClassImplementsAdaptable( Class adaptableClass ) {
    if( !Adaptable.class.isAssignableFrom( adaptableClass ) ) {
      String text = "The adaptableClass must implement {0}.";
      String msg = MessageFormat.format( text, new Object[] { Adaptable.class.getName() } );
      throw new IllegalArgumentException( msg );
    }
  }
}