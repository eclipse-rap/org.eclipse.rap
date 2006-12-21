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

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.rap.rwt.RWT;


final class ListModel {

  private static final int[] EMPTY_SELECTION = new int[ 0 ];

  private final boolean single;
  private final java.util.List items;
  private int[] selection = EMPTY_SELECTION;

  ListModel( boolean single ) {
    this.single = single;
    items = new ArrayList();
  }

  ///////////////////////////////
  // Methods to get/set selection

  int getSelectionIndex() {
    int result = -1;
    if( selection.length > 0 ) {
      result = selection[ 0 ];
    }
    return result;
  }

  int[] getSelectionIndices() {
    int[] result = new int[ selection.length ];
    System.arraycopy( selection, 0, result, 0, selection.length );
    return result;
  }
  
  int getSelectionCount() {
    return selection.length;
  }

  void setSelection( final int selection ) {
    deselectAll();
    if( selection >= 0 || selection <= getItemCount() - 1 ) {
      this.selection = new int[]{ selection };
    }
  }

  void setSelection( final int[] selection ) {
    if( selection == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    deselectAll();
    int length = selection.length;
    if( single ) {
      int end = getItemCount() - 1;
      if( length == 1 && selection[ 0 ] >= 0 && selection[ 0 ] <= end ) {
        this.selection = new int[]{ selection[ 0 ] };
      }
    } else {
      int end = getItemCount() - 1;
      int newLength = 0;
      for( int i = 0; i < length; i++ ) {
        if( selection[ i ] >= 0 && selection[ i ] <= end ) {
          newLength++;
        }
      }
      this.selection = new int[ newLength ];
      int pos = 0;
      for( int i = 0; i < length; i++ ) {
        if( selection[ i ] >= 0 && selection[ i ] <= end ) {
          this.selection[ pos ] = selection[ i ];
          pos++;
        }
      }
    }
  }
  
  void setSelection( final int start, final int end ) {
    deselectAll();
    if( end >= 0 && start <= end && start <= getItemCount() - 1 ) {
      if( single ) {
        if( start == end ) {
          this.selection = new int[]{ start };
        }
      } else {
        int first = Math.max( 0, start );
        int last = Math.min( end, getItemCount() - 1 );
        this.selection = new int[ last - first + 1 ];
        int current = first;
        for( int i = 0; i < this.selection.length; i++ ) {
          this.selection[ i ] = current;
          current++;
        }
      }
    }
  }
  
  void setSelection( final String[] selection ) {
    if( selection == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    deselectAll();
    int length = selection.length;
    if( ( single && length == 1 ) || ( !single && length > 0 ) ) {
      int newLength = 0;
      for( int i = 0; i < length; i++ ) {
        if( selection[ i ] != null && indexOf( selection[ i ] ) != -1 ) {
          newLength++;
        }
      }
      this.selection = new int[ newLength ];
      int pos = 0;
      for( int i = 0; i < length; i++ ) {
        if( selection[ i ] != null && indexOf( selection[ i ] ) != -1 ) {
          this.selection[ pos ] = indexOf( selection[ i ] );
          pos++;
        }
      }
    }
  }

  void deselectAll() {
    this.selection = EMPTY_SELECTION;
  }
  
  ////////////////////////////////
  // Methods to maintain the items
  
  void add( final String string ) {
    if( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    items.add( string );
  }

  void add( final String string, final int index ) {
    if( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( index != getItemCount() ) {
      checkIndex( index );
    }
    items.add( index, string );
  }

  void remove( final int index ) {
    checkIndex( index );
    items.remove( index );
    removeFromSelection( index );
  }

  void remove( final int start, final int end ) {
    checkIndex( start );
    checkIndex( end );
    for( int i = end; i >= start; i-- ) {
      remove( i );
    }
  }
  
  void remove( final int[] indices ) {
    if( indices == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( indices.length > 0 ) {
      int[] newIndices = new int[ indices.length ];
      System.arraycopy( indices, 0, newIndices, 0, indices.length );
      Arrays.sort( newIndices );
      checkIndex( newIndices[ 0 ] );
      checkIndex( newIndices[ newIndices.length - 1 ] );
      for( int i = newIndices.length - 1; i >= 0; i-- ) {
        remove( newIndices[ i ] );
      }
    }
  }

  void remove( final String string ) {
    if( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int index = indexOf( string );
    checkIndex( index );
    remove( index );
  }

  void removeAll() {
    items.clear();
    deselectAll();
  }

  void setItem( final int index, final String string ) {
    if( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    checkIndex( index );
    items.set( index, string );
  }

  void setItems( final String[] items ) {
    if( items == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    } 
    for( int i = 0; i < items.length; i++ ) {
      if( items[ i ] == null ) {
        RWT.error( RWT.ERROR_INVALID_ARGUMENT );
      }
    }
    this.items.clear();
    this.items.addAll( Arrays.asList( items ) );
    deselectAll();
  }

  String getItem( final int index ) {
    checkIndex( index );
    return ( String )items.get( index );
  }

  int getItemCount() {
    return items.size();
  }

  String[] getItems() {
    String[] result = new String[ items.size() ];
    items.toArray( result );
    return result;
  }

  //////////////////
  // Helping methods 
  
  /* If the given index is contained in the selection, it will be removed. */
  private void removeFromSelection( final int index ) {
    boolean found = false;
    for( int i = 0; !found && i < selection.length; i++ ) {
      if( index == selection[ i ] ) {
        int[] newSelection = new int[ selection.length - 1 ];
        System.arraycopy( selection, 0, newSelection, 0, index );
        if( index < selection.length - 1 ) {
          int length = selection.length - index - 1;
          System.arraycopy( selection, index + 1, newSelection, index, length );
        }
        selection = newSelection;
        found = true;
      }
    }
  }
  
  private int indexOf( final String string ) {
    return items.indexOf( string );
  }

  private void checkIndex( final int index ) {
    if( index < 0 || index >= getItemCount() ) {
      RWT.error( RWT.ERROR_INVALID_RANGE );
    }
  }
}
