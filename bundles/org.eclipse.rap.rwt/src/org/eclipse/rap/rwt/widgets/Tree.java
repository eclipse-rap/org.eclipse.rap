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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;

/**
 * TODO: [fappel] comment
 */
public class Tree extends Composite {

  private static final TreeItem[] EMPTY_SELECTION = new TreeItem[ 0 ];
  
  private final ItemHolder itemHolder;
  private TreeItem[] selection;


  public Tree( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TreeItem.class );
    selection = EMPTY_SELECTION;
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ///////////////////////////
  // Methods to manage items 

  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public TreeItem[] getItems() {
    checkWidget();
    return ( TreeItem[] )itemHolder.getItems();
  }
  
  public TreeItem getItem( final int index ) {
    checkWidget();
    return ( TreeItem )itemHolder.getItem( index );
  }
  
  public int indexOf( final TreeItem item ) {
    checkWidget();
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }
  
  public void removeAll() {
    checkWidget();
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    selection = EMPTY_SELECTION;
  }
  
  /////////////////////////////////////
  // Methods to get/set/clear selection
  
  public TreeItem[] getSelection() {
    checkWidget();
    TreeItem[] result = new TreeItem[ selection.length ];
    System.arraycopy( selection, 0, result, 0, selection.length );
    return result;
  }
  
  public int getSelectionCount() {
    checkWidget();
    return selection.length;
  }
  
  public void setSelection( final TreeItem selection ) {
    checkWidget();
    if( selection == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( new TreeItem[] { selection } );
  }

  public void setSelection( final TreeItem[] selection ) {
    checkWidget();
    if( selection == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int length = selection.length;
    if( ( style & RWT.SINGLE ) != 0 ) {
      if( length == 0 || length > 1 ) {
        deselectAll();
      } else {
        TreeItem item = selection[ 0 ];
        if( item != null ) {
          if( item.isDisposed() ) {
            RWT.error( RWT.ERROR_INVALID_ARGUMENT );
          }
          this.selection = new TreeItem[] { item };
        }
      }
    } else {
      if( length == 0 ) {
        deselectAll();
      } else {
        // Construct an array that contains all non-null items to be selected
        TreeItem[] validSelection = new TreeItem[ length ];
        int validLength = 0;
        for( int i = 0; i < length; i++ ) {
          if( selection[ i ] != null ) {
            if( selection[ i ].isDisposed() ) {
              RWT.error( RWT.ERROR_INVALID_ARGUMENT );
            }
            validSelection[ validLength ] = selection[ i ];
            validLength++;
          }
        }
        if( validLength > 0 ) {
          // Copy the above created array to its 'final destination'
          this.selection = new TreeItem[ validLength ];
          System.arraycopy( validSelection, 0, this.selection, 0, validLength );
        }
      }
    }
  }
  
  public void selectAll() {
    checkWidget();
    if( ( style & RWT.MULTI ) != 0 ) {
      final java.util.List allItems = new ArrayList();
      WidgetTreeVisitor.accept( this, new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
          if( widget instanceof TreeItem ) {
            allItems.add( widget );
          }
          return true;
        }
      } );
      selection = new TreeItem[ allItems.size() ];
      allItems.toArray( selection );
    }
  }
  
  public void deselectAll() {
    checkWidget();
    this.selection = EMPTY_SELECTION;
  }

  //////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  public void addTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.addListener( this, listener );
  }
  
  public void removeTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.removeListener( this, listener );
  }

  ////////////////////////////////
  // Methods to cleanup on dispose
  
  protected void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  
  void removeFromSelection( final TreeItem item ) {
    int index = -1;
    for( int i = 0; index == -1 && i < selection.length; i++ ) {
      if( selection[ i ] == item ) {
        index = i;
      }
    }
    if( index != -1 ) {
      TreeItem[] newSelection = new TreeItem[ selection.length - 1 ];
      System.arraycopy( selection, 0, newSelection, 0, index );
      if( index < selection.length - 1 ) {
        int length = selection.length - index - 1;
        System.arraycopy( selection, index + 1, newSelection, index, length );
      }
      selection = newSelection;
    }
  }
  
  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style | RWT.H_SCROLL | RWT.V_SCROLL;
    return checkBits( result, RWT.SINGLE, RWT.MULTI, 0, 0, 0, 0 );
  }
}
