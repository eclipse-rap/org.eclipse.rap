/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.events;

import java.lang.reflect.Array;
import java.util.*;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.SerializableCompatibility;


public class EventAdapter implements IEventAdapter, SerializableCompatibility {
  private static final long serialVersionUID = 1L;

  private static final SWTEventListener[] EMPTY_LISTENERS = new SWTEventListener[ 0 ];

  private Map<Class,Set<SWTEventListener>> listenerSets;

  /**
   * <p>Custom Set implementation (intended to hold Listeners) to reduce 
   * memory consumption.</p>
   */
  private static final class ListenerSet implements Set<SWTEventListener> {

    // Start with low capacity, assuming that only few listeners are added
    private final List<SWTEventListener> list = new ArrayList<SWTEventListener>( 3 );

    public int size() {
      return list.size();
    }

    public void clear() {
      list.clear();
    }

    public boolean isEmpty() {
      return list.isEmpty();
    }

    public Object[] toArray() {
      return list.toArray();
    }

    public boolean add( SWTEventListener o ) {
      boolean result = !contains( o ) ;
      if( result ) {
        list.add( o );
      }
      return result;
    }

    public boolean contains( final Object o ) {
      return list.contains( o );
    }

    public boolean remove( final Object o ) {
      return list.remove( o );
    }

    public boolean addAll( final Collection c ) {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll( final Collection c ) {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll( final Collection c ) {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll( final Collection c ) {
      throw new UnsupportedOperationException();
    }

    public Iterator<SWTEventListener> iterator() {
      throw new UnsupportedOperationException();
    }

    public <T> T[] toArray( T[] a ) {
      return list.toArray( a );
    }
  }

  public SWTEventListener[] getListener( Class listenerType ) {
    ParamCheck.notNull( listenerType, "listenerType" );
    Set<SWTEventListener> listenerSet = getListenerSet( listenerType );
    int size = listenerSet.size();
    SWTEventListener[] result = ( SWTEventListener[] )Array.newInstance( listenerType, size );
    listenerSet.toArray( result );
    return result;
  }

  public boolean hasListener( final Class listenerType ) {
    ParamCheck.notNull( listenerType, "listenerType" );
    checkListenerType( listenerType );
    boolean result = false;
    if( listenerSets != null && listenerSets.containsKey( listenerType ) ) {
      result = !getListenerSet( listenerType ).isEmpty();
    } 
    return result;
  }

  public void addListener( Class listenerType, SWTEventListener listener ) {
    if( listenerType == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    checkTypeCompatibility( listenerType, listener );
    getListenerSet( listenerType ).add( listener );
  }

  public void removeListener( Class listenerType, SWTEventListener listener ) {
    if( listenerType == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    checkTypeCompatibility( listenerType, listener );
    if( hasListener( listenerType ) ) {
      getListenerSet( listenerType ).remove( listener );
    }
  }

  private Set<SWTEventListener> getListenerSet( Class listenerType ) {
    checkListenerType( listenerType );
    if( listenerSets == null ) {
      // Create with low capacity, assuming that there are only few listener types used
      listenerSets = new HashMap<Class,Set<SWTEventListener>>( 4, 1.0f );
    }
    if( !listenerSets.containsKey( listenerType ) ) {
      listenerSets.put( listenerType, new ListenerSet() );
    }
    return listenerSets.get( listenerType );
  }

  public SWTEventListener[] getListeners() {
    SWTEventListener[] result = EMPTY_LISTENERS;
    if( listenerSets != null ) {
      Set<SWTEventListener> buffer = new HashSet<SWTEventListener>();
      Object[] sets = listenerSets.values().toArray();
      for( int i = 0; i < sets.length; i++ ) {
        Set set = ( Set )sets[ i ];
        Object[] listeners = set.toArray();
        for( int j = 0; j < listeners.length; j++ ) {
          buffer.add( ( SWTEventListener )listeners[ j ] );
        }
      }
      result = buffer.toArray( new SWTEventListener[ buffer.size() ] );
    }
    return result;
  }
  
  private static void checkTypeCompatibility( Class<?> listenerType, Object listener ) {
    if( !listenerType.isAssignableFrom( listener.getClass() ) ) {
      String msg = "Parameter 'listener' must be of type '" + listenerType.getName() + "'.";
      throw new IllegalArgumentException( msg );
    }
  }

  private static void checkListenerType( final Class listenerType ) {
    if( !EventListener.class.isAssignableFrom( listenerType ) ) {
      String msg =   "Parameter 'listenerType' must implement '" 
                   + EventListener.class.getName() 
                   + "'";
      throw new IllegalArgumentException( msg );
    }
  }
}