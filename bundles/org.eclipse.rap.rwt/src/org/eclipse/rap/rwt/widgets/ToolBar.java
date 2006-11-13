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

  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }
  
  public ToolItem getItem( final int index) {
    return ( ToolItem ) itemHolder.getItem( index );
  }
  
  public int getItemCount() {
    return itemHolder.size();
  }

  public ToolItem[] getItems() {
    return ( ToolItem[] ) itemHolder.getItems();
  }
  
  public int getRowCount() {
    return itemHolder.size();
  }

  public int indexOf( final ToolItem item) {
    return itemHolder.indexOf( item );
  }

}
