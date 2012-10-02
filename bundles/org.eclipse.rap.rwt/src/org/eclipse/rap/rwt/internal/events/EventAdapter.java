/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.events;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.SerializableCompatibility;


public class EventAdapter implements IEventAdapter, SerializableCompatibility {

  private static final EventListener[] EMPTY_LISTENERS = new EventListener[ 0 ];

  private Map<Integer,Set<EventListener>> listenerSets;

  public EventListener[] getListener( int eventType ) {
    Set<EventListener> listenerSet = getListenerSet( eventType );
    int size = listenerSet.size();
    EventListener[] result = ( EventListener[] )Array.newInstance( EventListener.class, size );
    listenerSet.toArray( result );
    return result;
  }

  public boolean hasListener( int eventType ) {
    boolean result = false;
    if( listenerSets != null && listenerSets.containsKey( Integer.valueOf( eventType ) ) ) {
      result = !getListenerSet( eventType ).isEmpty();
    } 
    return result;
  }

  public void addListener( int eventType, EventListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    getListenerSet( eventType ).add( listener );
  }

  public void removeListener( int eventType, EventListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( hasListener( eventType ) ) {
      getListenerSet( eventType ).remove( listener );
    }
  }

  public EventListener[] getListeners() {
    EventListener[] result = EMPTY_LISTENERS;
    if( listenerSets != null ) {
      Set<EventListener> buffer = new HashSet<EventListener>();
      Object[] sets = listenerSets.values().toArray();
      for( int i = 0; i < sets.length; i++ ) {
        Set set = ( Set )sets[ i ];
        Object[] listeners = set.toArray();
        for( int j = 0; j < listeners.length; j++ ) {
          buffer.add( ( EventListener )listeners[ j ] );
        }
      }
      result = buffer.toArray( new SWTEventListener[ buffer.size() ] );
    }
    return result;
  }

  private Set<EventListener> getListenerSet( int listenerType ) {
    if( listenerSets == null ) {
      // Create with low capacity, assuming that there are only few listener types used
      listenerSets = new HashMap<Integer,Set<EventListener>>( 4, 1.0f );
    }
    Integer listenerTypeValue = Integer.valueOf( listenerType );
    if( !listenerSets.containsKey( listenerTypeValue ) ) {
      listenerSets.put( listenerTypeValue, new ListenerSet() );
    }
    return listenerSets.get( listenerTypeValue );
  }

  private static final class ListenerSet implements Set<EventListener>, SerializableCompatibility {
  
    private final List<EventListener> list;
    
    public ListenerSet() {
      // Start with low capacity, assuming that only few listeners are added
      list = new ArrayList<EventListener>( 3 );
    }
  
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
  
    public boolean add( EventListener o ) {
      boolean result = !contains( o ) ;
      if( result ) {
        list.add( o );
      }
      return result;
    }
  
    public boolean contains( Object o ) {
      return list.contains( o );
    }
  
    public boolean remove( Object o ) {
      return list.remove( o );
    }
  
    public boolean addAll( Collection c ) {
      throw new UnsupportedOperationException();
    }
  
    public boolean containsAll( Collection c ) {
      throw new UnsupportedOperationException();
    }
  
    public boolean removeAll( Collection c ) {
      throw new UnsupportedOperationException();
    }
  
    public boolean retainAll( Collection c ) {
      throw new UnsupportedOperationException();
    }
  
    public Iterator<EventListener> iterator() {
      throw new UnsupportedOperationException();
    }
  
    public <T> T[] toArray( T[] a ) {
      return list.toArray( a );
    }
  }
}