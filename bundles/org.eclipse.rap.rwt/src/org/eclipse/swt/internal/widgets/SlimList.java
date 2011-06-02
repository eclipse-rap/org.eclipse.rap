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
package org.eclipse.swt.internal.widgets;

import java.util.*;

/**
 * This class is a <code>List</code> implementation that is designed for 
 * storing </code>Widget</code>s. The implementation is optimized for 
 * minimal memory footprint, frequent reads and infrequent writes.
 * <p>Only those methods known to be used by the well-known consumers are
 * implemented. Other methods throw <code>UnsupportedOperationException</code>s.
 * </p> 
 */
public final class SlimList<E> implements List<E> {

  private static final Object[] EMPTY = new Object[ 0 ];
  
  private Object[] data;

  public SlimList() {
    data = EMPTY;
  }

  public boolean add( E element ) {
    Object[] newData = new Object[ data.length + 1 ];
    System.arraycopy( data, 0, newData, 0, data.length );
    newData[ data.length ] = element;
    data = newData;
    return true;
  }

  public void add( int index, E element ) {
    if( index < 0 || index > size() ) {
      String msg = "Index: " + index + ", Size: " + size();
      throw new IndexOutOfBoundsException( msg );
    }
    Object[] newData = new Object[ data.length + 1 ];
    System.arraycopy( data, 0, newData, 0, data.length );
    int length = newData.length - 1 - index;
    System.arraycopy( newData, index, newData, index + 1, length );
    newData[ index ] = element;
    data = newData;
  }

  public boolean addAll( Collection<? extends E> collection ) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll( int index, Collection<? extends E> collection ) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    data = EMPTY;
  }

  public boolean contains( Object object ) {
    boolean result = false;
    for( int i = 0; !result && i < data.length; i++ ) {
      result = data[ i ] == object;
    }
    return result;
  }

  public boolean containsAll( Collection<?> collection ) {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  public E get( int index ) {
    return ( E )data[ index ];
  }

  public int indexOf( Object object ) {
    int result = -1;
    for( int i = 0; result == -1 && i < data.length; i++ ) {
      if( object == data[ i ] ) {
        result = i;
      }
    }
    return result;
  }

  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  public Iterator<E> iterator() {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf( Object object ) {
    throw new UnsupportedOperationException();
  }

  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  public ListIterator<E> listIterator( int index ) {
    throw new UnsupportedOperationException();
  }

  public E remove( int index ) {
    throw new UnsupportedOperationException();
  }

  public boolean remove( Object object ) {
    int index = indexOf( object );
    if( index != -1 ) {
      Object[] newData = new Object[ data.length - 1 ];
      System.arraycopy( data, 0, newData, 0, index );
      if( index < data.length - 1 ) {
        int length = data.length - index - 1;
        System.arraycopy( data, index + 1, newData, index, length );
      }
      data = newData;
    }
    return index != -1;
  }

  public boolean removeAll( Collection<?> c ) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll( Collection<?> c ) {
    throw new UnsupportedOperationException();
  }

  public E set( int index, E element ) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return data.length;
  }

  public List<E> subList( int fromIndex, int toIndex ) {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  public <T> T[] toArray( T[] array ) {
    System.arraycopy( data, 0, array, 0, data.length );
    return array;
  }
}
