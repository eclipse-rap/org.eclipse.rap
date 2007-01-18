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

package org.eclipse.rap.rwt.widgets;

import java.util.*;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;


/**
 * <p>Though Table inherits the font property from Control, it is currently
 * not evaluated client-side.</p> 
 */
public class Table extends Composite {
  
  private static final TableItem[] EMPTY_SELECTION = new TableItem[ 0 ];
  
  private final ItemHolder itemHolder;
  private final ItemHolder columnHolder;
  private TableItem[] selection;
  
  // handle the fact that we have two item types to deal with
  private final class CompositeItemHolder implements IItemHolderAdapter {
    public void add( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.add( item );
      } else {
        columnHolder.add( item );
      }
    }
    public void insert( final Item item, final int index ) {
      if( item instanceof TableItem ) {
        itemHolder.insert( item, index );
      } else {
        columnHolder.insert( item, index );
      }
    }
    public void remove( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.remove( item );
      } else {
        columnHolder.remove( item );
      }
    }
    public Item[] getItems() {
      Item[] iTable = itemHolder.getItems();
      Item[] iColumn = columnHolder.getItems();
      Item[] result = new Item[ iTable.length + iColumn.length ];
      System.arraycopy( iTable, 0, result, 0, iTable.length );
      System.arraycopy( iColumn, 0, result, iTable.length, iColumn.length );
      return result;
    }
  }

  public Table( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TableItem.class );
    columnHolder = new ItemHolder( TableColumn.class );
    selection = EMPTY_SELECTION;
  }
  
  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = new CompositeItemHolder();
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  public int getItemCount() {
   return itemHolder.size();
  }

  public TableItem[] getItems() {
    return ( TableItem[] )itemHolder.getItems();
  }

  public TableItem getItem( final int index ) {
    return ( TableItem )itemHolder.getItem( index );
  }

  public int indexOf( final TableItem tableItem ) {
    if( tableItem == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return itemHolder.indexOf( tableItem );
  }
  
  public int getColumnCount() {
    return columnHolder.size();
  }

  public TableColumn[] getColumns() {
    return ( TableColumn[] )columnHolder.getItems();
  }

  public TableColumn getColumn( final int index ) {
    return ( TableColumn )columnHolder.getItem( index );
  }

  public int indexOf( final TableColumn tableColumn ) {
    if( tableColumn == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return columnHolder.indexOf( tableColumn );
  }

  public int getSelectionIndex() {
    // TODO: [fappel] currently we do not have an focus indicator, so
    //                we return simply return the first index in range
    int selectionIndex = -1;
    TableItem[] currentSelection = getSelection();
    if( currentSelection.length > 0 ) {
      for( int i = 0; selectionIndex == -1 && i < itemHolder.size(); i++ ) {
        if( itemHolder.getItem( i ) == currentSelection[ 0 ] ) {
          selectionIndex = i;
        }
      }
    }
    return selectionIndex;
  }

  public void setSelection( final int index ) {
    if( index >= 0 && index < itemHolder.size() ) {
      TableItem item = ( TableItem )itemHolder.getItem( index );
      this.selection = new TableItem[] { item };
    } else {
      selection = EMPTY_SELECTION;
    }
  }

  public int getSelectionCount() {
    return getSelection().length;
  }

  public void setSelection( int start, int end ) {
    // TODO: [fappel] style bits for single/multi selection
    if( end >= 0 && start <= end ) {
      int actualStart = Math.max( 0, start );
      int actualEnd = Math.min( end, itemHolder.size() - 1 );
      selection = new TableItem[ actualEnd - actualStart + 1 ];
      int count = 0;
      for( int i = actualStart; i < actualEnd + 1; i++ ) {
        selection[ count ] = ( TableItem )itemHolder.getItem( i );
        count++;
      }
    }
  }

  public TableItem[] getSelection() {
    List buffer = new ArrayList();
    for( int i = 0; i < selection.length; i++ ) {
      if( !selection[ i ].isDisposed() ) {
        buffer.add( selection[ i ] );
      }
    }
    selection = new TableItem[ buffer.size() ];
    buffer.toArray( selection );
    return ( TableItem[] )selection.clone();
  }

  public void setSelection( final int[] indices ) {
    if( indices == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    // TODO: [fappel] style bits for single/multi selection
    
    Integer[] filteredIndices = filterIndices( indices );    
    TableItem[] newSelection = new TableItem[ filteredIndices.length ];
    for( int i = 0; i < filteredIndices.length; i++ ) {
      int index = filteredIndices[ i ].intValue();
      newSelection[ i ] = ( TableItem )itemHolder.getItem( index );
    }
    selection = newSelection;
  }
  
  public void setSelection( final TableItem[] items ) {
    if( items == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int[] indices = new int[ items.length ];
    for( int i = 0; i < items.length; i++ ) {
      indices[ i ] = indexOf( items[ i ] );
    }
    setSelection( indices );
  }
  
  public int[] getSelectionIndices() {
    TableItem[] currentSelection = getSelection();
    int[] result = new int[ currentSelection.length ];
    for( int i = 0; i < currentSelection.length; i++ ) {
      result[ i ] = indexOf( currentSelection[ i ] );
    }
    return result;
  }
  
  public boolean isSelected( final int index ) {
    boolean result = false;
    if( index >= 0 && index < itemHolder.size() ) {
      Item item = itemHolder.getItem( index );
      TableItem[] currentSelection = getSelection();
      for( int i = 0; !result && i < currentSelection.length; i++ ) {
        result = currentSelection[ i ] == item;
      }
    }
    return result;
  }
  
  
  //////////////////
  // selection event
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }
  
  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  
  //////////////////
  // helping methods
  
  private Integer[] filterIndices( final int[] indices ) {
    Set buffer = new HashSet();
    for( int i = 0; i < indices.length; i++ ) {
      if( indices[ i ] >= 0 && indices[ i ] < itemHolder.size() ) {
        buffer.add( new Integer( indices[ i ] ) );
      }
    }
    Integer[] result = new Integer[ buffer.size() ];
    buffer.toArray( result );
    Arrays.sort( result );
    return result;
  }
}