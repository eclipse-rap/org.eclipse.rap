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
import com.w4t.ParamCheck;


public class Menu extends Widget {

  private final Shell parent;
  private final ItemHolder itemHolder;
  private int x;
  private int y;
  private boolean visible = false;

  public Menu( final Menu menu ) {
    this( menu.getParent(), RWT.DROP_DOWN );
  }

  public Menu( final MenuItem parent ) {
    this( parent.getParent().getParent(), RWT.DROP_DOWN );
  }

  public Menu( final Control parent ) {
    this( parent.getShell(), RWT.POP_UP );
  }

  public Menu( final Shell parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    this.style
      = checkBits( style, RWT.POP_UP, RWT.BAR, RWT.DROP_DOWN, 0, 0, 0 );
    itemHolder = new ItemHolder( MenuItem.class );
    MenuHolder.addMenu( parent, this );
  }

  public final Display getDisplay() {
    return parent.getDisplay();
  }

  public final Shell getParent() {
    return parent;
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
  
  public void setLocation( final int x, final int y ) {
    if( ( style & ( RWT.BAR | RWT.DROP_DOWN ) ) == 0 ) {
      this.x = x;
      this.y = y;
    }
  }
  
  public void setLocation( final Point location ) {
    ParamCheck.notNull( location, "location" );
    setLocation( location.x, location.y );
  }
  
  public void setVisible( final boolean visible ) {
    if( ( style & ( RWT.BAR | RWT.DROP_DOWN ) ) == 0 ) {
      this.visible = visible;
    }
  }
  
  public boolean isVisible (){
    return visible;
  }

  public Rectangle getBounds() {
    // TODO: [fappel] how to calculate width and height?
    return new Rectangle( x, y, 0, 0 );
  }
  
  
  // /////////////////////////
  // Management of menu items
  
  public int getItemCount() {
    return itemHolder.size();
  }

  public MenuItem[] getItems() {
    return ( MenuItem[] )itemHolder.getItems();
  }

  public MenuItem getItem( final int index ) {
    return ( MenuItem )itemHolder.getItem( index );
  }
  
  // /////////////////
  // Widget overrides
  // TODO [rh] disposal of Menu and its items not yet completely implemented
  
  final void releaseChildren() {
    MenuItem[] menuItems = ( MenuItem[] )ItemHolder.getItems( this );
    for( int i = 0; i < menuItems.length; i++ ) {
      menuItems[ i ].dispose();
    }
  }

  final void releaseParent() {
  }

  final void releaseWidget() {
    MenuHolder.removeMenu( parent, this );
  }
}
