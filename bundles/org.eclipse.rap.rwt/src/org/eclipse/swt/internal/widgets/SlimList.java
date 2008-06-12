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
public final class SlimList implements List {

  private static final Object[] EMPTY = new Object[ 0 ];
  private Object[] data;

  public SlimList() {
    data = EMPTY;
  }

  public boolean add( final Object element ) {
    Object[] newData = new Object[ data.length + 1 ];
    System.arraycopy( data, 0, newData, 0, data.length );
    newData[ data.length ] = element;
    data = newData;
    return true;
  }

  public void add( final int index, final Object element ) {
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

  public boolean addAll( final Collection c ) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll( final int index, final Collection c ) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    data = EMPTY;
  }

  public boolean contains( final Object object ) {
    boolean result = false;
    for( int i = 0; !result && i < data.length; i++ ) {
      result = data[ i ] == object;
    }
    return result;
  }

  public boolean containsAll( final Collection c ) {
    throw new UnsupportedOperationException();
  }

  public Object get( final int index ) {
    return data[ index ];
  }

  public int indexOf( final Object object ) {
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

  public Iterator iterator() {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf( final Object o ) {
    throw new UnsupportedOperationException();
  }

  public ListIterator listIterator() {
    throw new UnsupportedOperationException();
  }

  public ListIterator listIterator( final int index ) {
    throw new UnsupportedOperationException();
  }

  public Object remove( final int index ) {
    throw new UnsupportedOperationException();
  }

  public boolean remove( final Object object ) {
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

  public boolean removeAll( final Collection c ) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll( final Collection c ) {
    throw new UnsupportedOperationException();
  }

  public Object set( final int index, final Object element ) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return data.length;
  }

  public List subList( final int fromIndex, final int toIndex ) {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray( final Object[] a ) {
    System.arraycopy( data, 0, a, 0, data.length );
    return a;
  }
}
