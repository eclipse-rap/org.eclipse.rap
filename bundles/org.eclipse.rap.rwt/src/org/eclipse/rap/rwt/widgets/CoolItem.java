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

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;



/**
 * TODO [rh] JavaDoc
 */
public class CoolItem extends Item {

  private static final class CoolItemOrderComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      int result;
      CoolItem item1 = ( CoolItem )object1;
      CoolItem item2 = ( CoolItem )object2;
      if( item1.getOrder() > item2.getOrder() ) {
        result = +1;
      } else if( item1.getOrder() < item2.getOrder() ) {
        result = -1;
      } else {
        result = 0;
      }
      return result;
    }
  }

  // Keep in sync with defaultValue for handleSize in CoolItem.js
  public static final int HANDLE_SIZE = 3; 
  
  private final CoolBar parent;
  private int order;
  // TODO [rh] reasonable default value
  private Point size = new Point( 0, 0 );
  private Control control;

  // TODO [rh] constructor missing: CoolItem(CoolBar,int,int)
  public CoolItem( final CoolBar parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
    order = parent.getItemCount() - 1;
  }
  
  ///////////////////
  // Widget overrides 
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }
  
  ////////////////
  // Getter/setter
  
  public CoolBar getParent () {
    checkWidget();
    return parent;
  }

  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        RWT.error( RWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != getParent() ) {
        RWT.error( RWT.ERROR_INVALID_PARENT );
      }
    }
    this.control = control;
  }

  public Control getControl() {
    checkWidget();
    return control;
  }
  
  ///////////////////////
  // Size-related methods
  
  public Point computeSize( final int wHint, final int hHint ) {
    checkWidget();
    Point result;
    int index = parent.indexOf( this );
    if( index == -1 ) {
      result = new Point( 0, 0 );
    } else {
      int width = wHint;
      int height = hHint;
      if( wHint == RWT.DEFAULT ) {
        width = 32;
      }
      if( hHint == RWT.DEFAULT ) {
        height = 32;
      }
      if( ( parent.style & RWT.VERTICAL ) != 0 ) {
        height += parent.getMargin( index );
      } else {
        width += parent.getMargin( index );
      }
      result = new Point( width, height );
    }
    return result;
  }

  public void setPreferredSize( final Point preferredSize ) {
    checkWidget();
    if( preferredSize == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setPreferredSize( preferredSize.x, preferredSize.y );
  }

  public void setPreferredSize( final int wHint, final int hHint ) {
    checkWidget();
    if( parent.indexOf( this ) != -1 ) {
      int width = Math.max( 0, wHint );
      int height = Math.max( 0, hHint );
      int x, y;
      if( ( parent.style & RWT.VERTICAL ) != 0 ) {
        x = Math.max( 0, height - parent.getMargin( parent.indexOf( this ) ) );
        y = width;
      } else {
        x = Math.max( 0, width - parent.getMargin( parent.indexOf( this ) ) );
        y = height;
      }
      setSize( x, y );
    }
  }
  
  public void setSize( final int wHint, final int hHint ) {
    checkWidget();
    if( parent.indexOf( this ) != -1 ) {
      int width = Math.max( 0, wHint );
      int height = Math.max( 0, hHint );
      int x;
      int y;
      if( ( parent.style & RWT.VERTICAL ) != 0 ) {
        x = height + HANDLE_SIZE;
        y = width;
      } else {
        x = width + HANDLE_SIZE;
        y = height;
      }
      size = new Point( x, y );
    }
  }
  
  public void setSize( final Point size ) {
    checkWidget();
    if( size == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setSize( size.x, size.y );
  }
  
  public Point getSize() {
    checkWidget();
    return new Point( size.x, size.y );
  }
  
  public Rectangle getBounds() {
    checkWidget();
    int left = 0;
    int top = 0;
    CoolItem[] items = getOrderedItems();
    for( int i = 0; items[ i ] != this && i < items.length; i++ ) {
      Point itemSize = parent.getItem( i ).getSize();
      if( ( parent.style & RWT.VERTICAL ) != 0 ) {
        top += itemSize.x;
      } else {
        left += itemSize.x;
      }
    }
    return new Rectangle( left, top, size.x, size.y );
  }

  ////////////////////////////
  // Item overrides - disposal 
  
  protected void releaseChildren() {
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
    control = null;
  }
  
  /////////////////////////////////////////
  // Helping methods to maintain item order
  
  int getOrder() {
    return order;
  }

  void setOrder( final int order ) {
    this.order = order;
  }

  private CoolItem[] getOrderedItems() {
    CoolItem[] result = parent.getItems();
    Arrays.sort( result, new CoolItemOrderComparator() );
    return result;
  }
}
