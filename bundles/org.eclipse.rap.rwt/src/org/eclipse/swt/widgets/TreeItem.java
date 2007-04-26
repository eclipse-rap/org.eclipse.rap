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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;


/**
 * TODO: [fappel] comment
 */
public class TreeItem extends Item {

  private final TreeItem parentItem;
  private final Tree parent;
  private final ItemHolder itemHolder;
  private Font font;
  private boolean expanded;
  private boolean checked;

  public TreeItem( final Tree parent, final int style ) {
    this( parent, null, style, -1 );
  }

  public TreeItem( final TreeItem parentItem, final int style ) {
    this( parentItem == null ? null : parentItem.parent, 
          parentItem,
          style,
          -1 );
  }

  public TreeItem( final TreeItem parentItem, final int style, final int index ) 
  {
    this( parentItem == null ? null : parentItem.parent, 
          parentItem, 
          style,
          -1 );
  }
  
  private TreeItem( final Tree parent,
                    final TreeItem parentItem,
                    final int style,
                    final int index )
  {
    super( parent, style );
    this.parent = parent;
    this.parentItem = parentItem;
    if( parentItem != null ) {
      int newIndex = index == -1 ? parentItem.getItemCount() : index;
      ItemHolder.insertItem( parentItem, this, newIndex );
    } else {
      int newIndex = index == -1 ? parent.getItemCount() : index;
      ItemHolder.insertItem( parent, this, newIndex );
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

  /////////////////
  // Item overrides
  
  public final Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  /////////////////////////
  // Parent/child relations
  
  public final Tree getParent() {
    checkWidget();
    return parent;
  }

  public TreeItem getParentItem() {
    checkWidget();
    return parentItem;
  }
  
  ////////////////
  // Getter/Setter

  public void setExpanded( final boolean expanded ) {
    checkWidget();
    if( !expanded || getItemCount() > 0 ) {
      this.expanded = expanded; 
    }
  }
  
  public boolean getExpanded() {
    checkWidget();
    return expanded;
  }

  public void setFont( final Font font ) {
    checkWidget();
    this.font = font;
  }
  
  public Font getFont() {
    checkWidget();
    Font result;
    if( font == null ) {
      result = getParent().getFont();
    } else {
      result = font;
    }
    return result;
  }
  
  public void setChecked( final boolean checked ) {
    checkWidget();
    if( ( parent.getStyle() & SWT.CHECK ) != 0 ) {
      this.checked = checked;
    }
  }
  
  public boolean getChecked() {
    checkWidget();
    return checked;
  }
  
  ///////////////////////////////////////
  // Methods to maintain (sub-) TreeItems
  
  public TreeItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.TreeItem[] )itemHolder.getItems();
  }
  
  public TreeItem getItem( final int index ) {
    checkWidget();
    return ( TreeItem )itemHolder.getItem( index );
  }
  
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }
  
  public int indexOf( final TreeItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }

  public void removeAll() {
    checkWidget();
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  /////////////////////////////////
  // Methods to dispose of the item
  
  protected final void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  protected final void releaseParent() {
    if( parentItem != null ) {
      ItemHolder.removeItem( parentItem, this );
    } else {
      ItemHolder.removeItem( parent, this );
    }
    parent.removeFromSelection( this );
  }

  protected final void releaseWidget() {
    // do nothing
  }
}
