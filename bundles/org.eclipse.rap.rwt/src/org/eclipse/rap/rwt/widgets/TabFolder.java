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
import org.eclipse.rap.rwt.graphics.Rectangle;
import com.w4t.ParamCheck;

public class TabFolder extends Composite {

  private static final TabItem[] EMPTY_TAB_ITEMS = new TabItem[ 0 ];
  private final ItemHolder itemHolder = new ItemHolder( TabItem.class );
  private int selectionIndex = -1;

  public TabFolder( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }

  public TabItem[] getItems() {
    return ( TabItem[] )itemHolder.getItems();
  }

  public TabItem getItem( final int index ) {
    return ( TabItem )itemHolder.getItem( index );
  }

  public int getItemCount() {
    return itemHolder.size();
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

  void releaseChildren() {
    TabItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }

  public Rectangle getClientArea() {
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int border = 1;
    int hTabBar = 23;
    return new Rectangle( -10,
                          -10,
                          width - border * 2,
                          height - ( hTabBar + border * 2 ) );
  }

  public TabItem[] getSelection() {
    TabItem[] result = EMPTY_TAB_ITEMS;
    if( getSelectionIndex() != -1 ) {
      TabItem selected = ( TabItem )itemHolder.getItem( getSelectionIndex() );
      result = new TabItem[]{
        selected
      };
    }
    return result;
  }

  public void setSelection( final TabItem[] items ) {
    ParamCheck.notNull( items, "items" );
    Item[] list = itemHolder.getItems();
    int newIndex = -1;
    for( int i = 0; i < list.length; i++ ) {
      if( items.length > 0 && items[ 0 ] == list[ i ] ) {
        newIndex = i;
      }
    }
    setSelection( newIndex );
  }

  public void setSelection( final int selectionIndex ) {
    if( selectionIndex >= -1 && selectionIndex < itemHolder.size() ) {
      this.selectionIndex = selectionIndex;
    }
  }

  public int getSelectionIndex() {
    if( selectionIndex >= itemHolder.size() ) {
      selectionIndex = itemHolder.size() - 1;
    }
    return selectionIndex;
  }
  
  public int indexOf( final TabItem item ) {
    return itemHolder.indexOf( item );
  }

  public void layout() {
    Control[] children = getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].setBounds( getClientArea() );
    }
  }

  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }
}
