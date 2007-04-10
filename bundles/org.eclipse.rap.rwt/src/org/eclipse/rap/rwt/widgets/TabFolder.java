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
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;
import org.eclipse.rap.rwt.internal.widgets.IItemHolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;

public class TabFolder extends Composite {

  private static final TabItem[] EMPTY_TAB_ITEMS = new TabItem[ 0 ];
  
  private final ItemHolder itemHolder = new ItemHolder( TabItem.class );
  private int selectionIndex = -1;

  public TabFolder( final Composite parent, final int style ) {
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

  //////////////////
  // Item management
  
  public TabItem[] getItems() {
    checkWidget();
    return ( TabItem[] )itemHolder.getItems();
  }

  public TabItem getItem( final int index ) {
    checkWidget();
    return ( TabItem )itemHolder.getItem( index );
  }

  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public int indexOf( final TabItem item ) {
    checkWidget();
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }

  /////////////////////
  // Seletion handling
  
  public TabItem[] getSelection() {
    checkWidget();
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
    checkWidget();
    if( items == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
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
    checkWidget();
    if( selectionIndex >= -1 && selectionIndex < itemHolder.size() ) {
      this.selectionIndex = selectionIndex;
    }
  }

  public int getSelectionIndex() {
    checkWidget();
    if( selectionIndex >= itemHolder.size() ) {
      selectionIndex = itemHolder.size() - 1;
    }
    return selectionIndex;
  }
  
  ///////////////////////////////
  // Layout and size computations

  public void layout() {
    checkWidget();
    Control[] children = getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].setBounds( getClientArea() );
    }
  }

  public Rectangle getClientArea() {
    checkWidget();
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int border = 1;
    int hTabBar = 23;
    return new Rectangle( border,
                          hTabBar + border,
                          width - border * 2,
                          height - ( hTabBar + border * 2 ) );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    Point itemsSize = new Point( 0, 0 );
    Point contentsSize = new Point( 0, 0 );
    TabItem[] items = getItems();
    // TODO: one item should be enough since layout already includes all items
    for( int i = 0; i < items.length; i++ ) {
      Point thisItemSize = computeItemSize( items[ i ] );
      itemsSize.x += thisItemSize.x;
      itemsSize.y = Math.max( itemsSize.y, thisItemSize.y );
      Control control = items[ i ].getControl();
      if( control != null ) {
        Point thisSize = control.computeSize( RWT.DEFAULT, RWT.DEFAULT );
        contentsSize.x = Math.max( contentsSize.x, thisSize.x );
        contentsSize.y = Math.max( contentsSize.y, thisSize.y );
      }
    }
    int width = Math.max( itemsSize.x, contentsSize.x );
    int height = itemsSize.y + contentsSize.y;
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
    int border = getBorderWidth();
    width += 2 * border;
    height += 2 * border;
    return new Point( width, height );
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }
  
  ///////////
  // Disposal
  
  protected void releaseChildren() {
    TabItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  
  ////////////////
  // Item creation
  
  void createItem( final TabItem item, final int index ) {
    itemHolder.insert( item, index );
    if( getItemCount() == 1 ) {
      // TODO [rh] mismatch to SWT: SWT fires a SelectionEvent here
      setSelection( 0 );
    }
  }

  ///////////////////
  // Helping methods
  
  private Point computeItemSize( final TabItem item ) {
    Point result = new Point( 0, 0 );
    String text = item.getText();
    if( text != null ) {
      Point extent = FontSizeEstimation.stringExtent( text, getFont() );
      // TODO [rst] these are only rough estimations
      result.x += extent.x + 10 + 6;
      result.y = extent.y + 4 + 6;
    }
    Image image = item.getImage();
    if( image != null ) {
      // TODO [rst] use image.getBounds()
      Point size = new Point( 16, 16 );
      result.x += size.x + 4;
      result.y = Math.max( size.x, result.x );
    }
    return result;
  }
  
  private static int checkStyle( final int style ) {
    int result = checkBits( style, RWT.TOP, RWT.BOTTOM, 0, 0, 0, 0 );
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return result & ~( RWT.H_SCROLL | RWT.V_SCROLL );
  }
}
