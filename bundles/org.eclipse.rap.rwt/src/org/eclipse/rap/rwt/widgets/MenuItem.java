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
import org.eclipse.rap.rwt.events.*;

public class MenuItem extends Item {

  private final Menu parent;
  private Menu menu;
  private DisposeListener menuDisposeListener;

  // TODO [rh] constructor MenuItem(Menu,int,int) missing
  public MenuItem( final Menu parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    this.style = checkStyle( style );
    ItemHolder.addItem( parent, this );
  }

  public Menu getParent() {
    return parent;
  }

  public void setMenu( final Menu menu ) {
    if( this.menu != menu ) {
      if( ( style & RWT.CASCADE ) == 0 ) {
        // will become RWT.ERROR_MENUITEM_NOT_CASCADE
        throw new IllegalArgumentException( "The menu item is not a CASCADE" );
      }
      if( menu != null ) {
        if( menu.isDisposed() ) {
          // will become RWT.ERROR_INVALID_ARGUMENT
          String msg = "The argument 'menu' was already disposed.";
          throw new IllegalArgumentException( msg );
        }
        if( ( menu.getStyle() & RWT.DROP_DOWN ) == 0 ) {
          // will become RWT.ERROR_MENU_NOT_DROP_DOWN
          String msg = "The argument 'Menu' must be a DROP_DOWN.";
          throw new IllegalArgumentException( msg );
        }
        if( menu.getParent() != getParent().getParent() ) {
          // will become RWT.ERROR_INVALID_PARENT
          String msg = "The argument 'menu' has an invalid parent.";
          throw new IllegalArgumentException( msg );
        }
      }
      removeMenuDisposeListener();
      this.menu = menu;
      addMenuDisposeListener();
    }
  }

  public Menu getMenu() {
    return menu;
  }
  

  ///////////////////////
  // Listener maintenance
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  
  // ///////////////
  // Item overrides
  
  public Display getDisplay() {
    return parent.getDisplay();
  }

  protected final void releaseChildren() {
    if( menu != null ) {
      removeMenuDisposeListener();
      menu.dispose();
      menu = null;
    }
  }

  protected final void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected final void releaseWidget() {
    // do nothing
  }

  
  // ////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menu
  
  private void addMenuDisposeListener() {
    if( menu != null ) {
      if( menuDisposeListener == null ) {
        menuDisposeListener = new DisposeListener() {

          public void widgetDisposed( final DisposeEvent event ) {
            MenuItem.this.menu = null;
          }
        };
      }
      menu.addDisposeListener( menuDisposeListener );
    }
  }

  private void removeMenuDisposeListener() {
    if( menu != null ) {
      menu.removeDisposeListener( menuDisposeListener );
    }
  }

  
  // ////////////////////////////////////
  // Helping methods to verify arguments
  
  private static int checkStyle( final int style ) {
    return checkBits( style,
                      RWT.PUSH,
                      RWT.CHECK,
                      RWT.RADIO,
                      RWT.SEPARATOR,
                      RWT.CASCADE,
                      0 );
  }
}
