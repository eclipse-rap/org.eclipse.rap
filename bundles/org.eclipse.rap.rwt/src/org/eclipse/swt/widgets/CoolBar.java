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

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;


/**
 * TODO [rh] JavaDoc
 */
public class CoolBar extends Composite {

  private final ItemHolder itemHolder = new ItemHolder( CoolItem.class );
  private boolean locked;
  
  public CoolBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    if( ( style & SWT.VERTICAL ) != 0 ) {
      this.style |= SWT.VERTICAL;
    } else {
      this.style |= SWT.HORIZONTAL;
    }
  }
  
  ///////////////////////////
  // Adaptable implementation
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ////////////////
  // Getter/setter
  
  
  public void setLocked( final boolean locked ) {
    checkWidget();
    this.locked = locked;
  }

  
  public boolean getLocked() {
    checkWidget();
    return locked;
  }
  
  ///////////////////////
  // Size-related methods
  
  public Point computeSize( final int wHint,
                            final int hHint, 
                            final boolean changed )
  {
    checkWidget();
    // TODO [rh] replace with decent implementation
    Point result = super.computeSize( wHint, hHint, changed );
    return new Point( result.x, 30 );
  }
  
  //////////////////////////
  // Management of CoolItems
  
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }
  
  public CoolItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.CoolItem[] )itemHolder.getItems();
  }
  
  public CoolItem getItem( final int index ) {
    checkWidget();
    return ( CoolItem )itemHolder.getItem( index );
  }
  
  public int indexOf( final CoolItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }
  
  public int[] getItemOrder() {
    checkWidget();
    int[] result = new int[ getItemCount() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = getItem( i ).getOrder();
    }
    return result;
  }
  
  public void setItemOrder( final int[] itemOrder ) {
    checkWidget();
    if( itemOrder == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int itemCount = getItemCount();
    if( itemOrder.length != itemCount ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    // Ensure that itemOrder does not contain any duplicates.
    boolean[] set = new boolean[ itemCount ];
    for( int i = 0; i < itemOrder.length; i++ ) {
      int index = itemOrder[ i ];
      if( index < 0 || index >= itemCount ) {
        SWT.error( SWT.ERROR_INVALID_RANGE );
      }
      if( set[ index ] ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      set[ index ] = true;
    }
    for( int i = 0; i < itemOrder.length; i++ ) {
      CoolItem item = getItem( i );
      item.setOrder( itemOrder[ i ] );
    }
  }
  
  ///////////////////////////////////////////////////
  // Internal methods to maintain the child controls
  
  protected void releaseChildren() {
    CoolItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style | SWT.NO_FOCUS;
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    return result & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }

  int getMargin( final int index ) {
    return CoolItem.HANDLE_SIZE;
  }
}
