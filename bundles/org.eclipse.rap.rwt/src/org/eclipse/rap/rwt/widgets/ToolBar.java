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
import org.eclipse.rap.rwt.internal.widgets.IItemHolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;


public class ToolBar extends Composite {
  
  private final ItemHolder itemHolder = new ItemHolder( ToolItem.class );


  public ToolBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
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

  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    // TODO: [fappel] reasonable implementation
    Point result = super.computeSize( wHint, hHint, changed );
    return new Point( result.x, 24 );
  }
  
  public ToolItem getItem( final int index ) {
    checkWidget();
    return ( ToolItem ) itemHolder.getItem( index );
  }
  
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public ToolItem[] getItems() {
    checkWidget();
    return ( ToolItem[] ) itemHolder.getItems();
  }
  
  public int getRowCount() {
    checkWidget();
    return itemHolder.size();
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

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }
}
