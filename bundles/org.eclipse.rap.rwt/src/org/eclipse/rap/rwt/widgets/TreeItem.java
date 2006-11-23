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


/**
 * TODO: [fappel] comment
 */
public class TreeItem extends Item {

  private final TreeItem parentItem;
  private final Tree parent;
  private final ItemHolder itemHolder;

  public TreeItem( final Tree parent, final int style ) {
    this( parent, null, style );
  }

  public TreeItem( final TreeItem parentItem, final int style ) {
    this( parentItem == null
                            ? null
                            : parentItem.parent, parentItem, style );
  }

  private TreeItem( final Tree parent,
                    final TreeItem parentItem,
                    final int style )
  {
    super( parent, style );
    this.parent = parent;
    this.parentItem = parentItem;
    if( parentItem != null ) {
      ItemHolder.addItem( parentItem, this );
    } else {
      ItemHolder.addItem( parent, this );
    }
    itemHolder = new ItemHolder( TreeItem.class );
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

  public final Display getDisplay() {
    return parent.getDisplay();
  }

  public final Tree getParent() {
    return parent;
  }

  public TreeItem getParentItem() {
    return parentItem;
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
  
  
  ///////////////////////////////////
  // Methods to dispose of the widget
  
  final void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  final void releaseParent() {
    if( parentItem != null ) {
      ItemHolder.removeItem( parentItem, this );
    } else {
      ItemHolder.removeItem( parent, this );
    }
  }

  final void releaseWidget() {
  }
}
