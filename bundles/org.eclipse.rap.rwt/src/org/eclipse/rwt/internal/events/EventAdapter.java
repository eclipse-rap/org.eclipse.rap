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
package org.eclipse.rwt.internal.events;

import java.util.*;

import org.eclipse.rwt.internal.util.ParamCheck;

public class EventAdapter implements IEventAdapter {

  private static final Object[] EMPTY_RESULT = new Object[ 0 ];

  private Map listenerSets;

  /**
   * <p>Custom Set implementation (intended to hold Listeners) to reduce 
   * memory consumption.</p>
   */
  private final class ListenerSet implements Set {

    // Start with low capacity, assuming that only few listeners are added
    private final List list = new ArrayList( 3 );

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

    public boolean add( final Object o ) {
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

    public Iterator iterator() {
      throw new UnsupportedOperationException();
    }

    public Object[] toArray( final Object[] a ) {
      throw new UnsupportedOperationException();
    }
  }

  public Object[] getListener( final Class listenerType ) {
    ParamCheck.notNull( listenerType, "listenerType" );
    Object[] result = EMPTY_RESULT;
    if( hasListener( listenerType ) ) {
      result = getListenerSet( listenerType ).toArray();
    }
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

  public void addListener( final Class listenerType, 
                           final Object listener ) 
  {
    ParamCheck.notNull( listenerType, "listenerType" );
    ParamCheck.notNull( listener, "listener" );
    checkTypeCompatibility( listenerType, listener );
    getListenerSet( listenerType ).add( listener );
  }

  public void removeListener( final Class listenerType, 
                              final Object listener )
  {
    ParamCheck.notNull( listenerType, "listenerType" );
    ParamCheck.notNull( listener, "listener" );
    checkTypeCompatibility( listenerType, listener );
    if( hasListener( listenerType ) ) {
      getListenerSet( listenerType ).remove( listener );
    }
  }

  private Set getListenerSet( final Class listenerType ) {
    checkListenerType( listenerType );
    if( listenerSets == null ) {
      // Create with low capacity, assuming that there are only few 
      // listener types used
      listenerSets = new HashMap( 4, 1.0f );
    }
    if( !listenerSets.containsKey( listenerType ) ) {
      listenerSets.put( listenerType, new ListenerSet() );
    }
    return ( Set )listenerSets.get( listenerType );
  }

  public Object[] getListener() {
    Object[] result = EMPTY_RESULT;
    if( listenerSets != null ) {
      Set buffer = new HashSet();
      Object[] sets = listenerSets.values().toArray();
      for( int i = 0; i < sets.length; i++ ) {
        Set set = ( Set )sets[ i ];
        Object[] listeners = set.toArray();
        for( int j = 0; j < listeners.length; j++ ) {
          buffer.add( listeners[ j ] );
        }
      }
      result = buffer.toArray();
    }
    return result;
  }
  
  private void checkTypeCompatibility( final Class listenerType, 
                                       final Object listener ) 
  {
    if( !listenerType.isAssignableFrom( listener.getClass() ) ) {
      String msg =   "Parameter 'listener' must be of type '" 
                   + listenerType.getName() 
                   + "'.";
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