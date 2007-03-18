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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IItemHolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;


public class ToolBar extends Composite {
  
  private final ItemHolder itemHolder = new ItemHolder( ToolItem.class );

  public ToolBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    /*
    * Ensure that either of HORIZONTAL or VERTICAL is set.
    * NOTE: HORIZONTAL and VERTICAL have the same values
    * as H_SCROLL and V_SCROLL so it is necessary to first
    * clear these bits to avoid scroll bars and then reset
    * the bits using the original style supplied by the
    * programmer.
    */
    if( ( style & RWT.VERTICAL ) != 0 ) {
      this.style |= RWT.VERTICAL;
    } else {
      this.style |= RWT.HORIZONTAL;
    }
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

  //////////////////
  // Item management
  
  public ToolItem getItem( final int index ) {
    checkWidget();
    return ( ToolItem )itemHolder.getItem( index );
  }
  
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public ToolItem[] getItems() {
    checkWidget();
    return ( ToolItem[] )itemHolder.getItems();
  }
  
  public int indexOf( final ToolItem item ) {
    checkWidget();
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }

  ////////////////////
  // Size computations

  // TODO [rh] decent size computation for VERTICAL alignment missing
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    int width = 0;
    int height = 0;
    for( int i = 0; i < itemHolder.size(); i++ ) {
      ToolItem item = ( ToolItem )itemHolder.getItem( i );
      Rectangle itemBounds = item.getBounds();
      height = Math.max( height, itemBounds.height );
      width += itemBounds.width;
    }
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
    Rectangle trim = computeTrim( 0, 0, width, height );
    width = trim.width;
    height = trim.height;
    return new Point( width, height );
  }
  
//  public Rectangle computeTrim( final int x,
//                                final int y,
//                                final int width,
//                                final int height )
//  {
//    checkWidget();
//    Rectangle trim = super.computeTrim( x, y, width, height );
//    return trim;
//  }
  
  public int getRowCount() {
    checkWidget();
    return itemHolder.size();
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return style & ~( RWT.H_SCROLL | RWT.V_SCROLL );
  }
}
