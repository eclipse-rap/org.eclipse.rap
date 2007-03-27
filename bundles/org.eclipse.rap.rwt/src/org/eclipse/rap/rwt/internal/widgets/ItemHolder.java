/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.Item;
import org.eclipse.rap.rwt.widgets.Widget;

public final class ItemHolder implements IItemHolderAdapter {

  public static boolean isItemHolder( final Widget widget ) {
    return widget.getAdapter( IItemHolderAdapter.class ) != null;
  }

  public static Item[] getItems( final Widget widget ) {
    return getItemHolder( widget ).getItems();
  }

  public static void addItem( final Widget widget, final Item item ) {
    getItemHolder( widget ).add( item );
  }
  
  public static void insertItem( final Widget widget, 
                                 final Item item, 
                                 final int index ) 
  {
    getItemHolder( widget ).insert( item, index );
  }

  public static void removeItem( final Widget widget, final Item item ) {
    getItemHolder( widget ).remove( item );
  }
  
  private final List items;
  private final Class type;

  public ItemHolder( final Class type ) {
    this.type = type;
    items = new SlimList();
  }

  public int size() {
    return items.size();
  }

  public void add( final Item item ) {
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( items.contains( item ) ) {
      String msg = "The item was already added.";
      throw new IllegalArgumentException( msg );
    }
    items.add( item );
  }

  public void insert( final Item item, final int index ) {
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( index < 0 || index > size() ) {
      RWT.error( RWT.ERROR_INVALID_RANGE );
    }
    if( items.contains( item ) ) {
      String msg = "The item was already added.";
      throw new IllegalArgumentException( msg );
    }
    items.add( index, item );
  }
  
  public void remove( final Item item ) {
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( !items.contains( item ) ) {
      String msg = "The item was not added to this item holder.";
      throw new IllegalArgumentException( msg );
    }
    items.remove( item );
  }
  
  public Item[] getItems() {
    Object[] result = ( Object[] )Array.newInstance( type, items.size() );
    items.toArray( result );
    return ( Item[] )result;
  }

  public Item getItem( final int index ) {
    if( index < 0 || index >= items.size() ) {
      RWT.error( RWT.ERROR_INVALID_RANGE );
    }
    return ( Item )items.get( index );
  }
  
  public int indexOf ( final Item item ) {
    return items.indexOf( item );
  }

  ///////////////////
  // helping methods
  
  private static IItemHolderAdapter getItemHolder( final Widget widget ) {
    if( !isItemHolder( widget ) ) {
      Object[] params = new Object[]{
        widget.getClass().getName()
      };
      String txt = "Widgets of type ''{0}'' do not contain items";
      String msg = MessageFormat.format( txt, params );
      throw new IllegalArgumentException( msg );
    }
    return ( IItemHolderAdapter )widget.getAdapter( IItemHolderAdapter.class );
  }
}
