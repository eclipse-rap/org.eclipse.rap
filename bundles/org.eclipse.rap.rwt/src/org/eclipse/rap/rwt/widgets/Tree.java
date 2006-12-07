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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;

/**
 * TODO: [fappel] comment
 */
public class Tree extends Composite {

  private final ItemHolder itemHolder;

  public Tree( final Composite parent, final int style ) {
    super( parent, style );
    itemHolder = new ItemHolder( TreeItem.class );
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
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  public int getItemCount() {
    return itemHolder.size();
  }

  public TreeItem[] getItems() {
    return ( TreeItem[] )itemHolder.getItems();
  }
  
  public TreeItem getItem( final int index ) {
    return ( TreeItem )itemHolder.getItem( index );
  }
  
  public int indexOf( final TreeItem item ) {
    return itemHolder.indexOf( item );
  }

  protected void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  

  //////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }
}
