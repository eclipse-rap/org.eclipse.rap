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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;

public class MenuItem extends Item {

  private final Menu parent;
  private Menu menu;
  private DisposeListener menuDisposeListener;
  private boolean selection;

  public MenuItem( final Menu parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }
  
  public MenuItem( final Menu parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.insertItem( parent, this, index );
  }
  
  public Menu getParent() {
    checkWidget();
    return parent;
  }

  public void setMenu( final Menu menu ) {
    checkWidget();
    if( this.menu != menu ) {
      if( ( style & RWT.CASCADE ) == 0 ) {
        RWT.error( RWT.ERROR_MENUITEM_NOT_CASCADE );
      }
      if( menu != null ) {
        if( menu.isDisposed() ) {
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        }
        if( ( menu.getStyle() & RWT.DROP_DOWN ) == 0 ) {
          RWT.error( RWT.ERROR_MENU_NOT_DROP_DOWN );
        }
        if( menu.getParent() != getParent().getParent() ) {
          RWT.error( RWT.ERROR_INVALID_PARENT );
        }
      }
      removeMenuDisposeListener();
      /* Assign the new menu */
      if( this.menu != null ) {
        this.menu.cascade = null;
      }
      this.menu = menu;
      if( menu != null ) {
        menu.cascade = this;
      }
      addMenuDisposeListener();
    }
  }

  public Menu getMenu() {
    checkWidget();
    return menu;
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      super.setImage( image );
    }
  }
  
  //////////
  // Enabled

  public void setEnabled( final boolean enabled ) {
    checkWidget();
    state &= ~DISABLED;
    if( !enabled ) {
      state |= DISABLED;
    }
  }
  
  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }
  
  public boolean isEnabled() {
    return getEnabled() && parent.isEnabled();
  }
  
  ////////////
  // Selection
  
  public boolean getSelection() {
    checkWidget();
    return selection;
  }
  
  public void setSelection( final boolean selection ) {
    checkWidget();
    if( ( style & ( RWT.CHECK | RWT.RADIO ) ) != 0 ) {
      this.selection = selection;
    } 
  }

  ///////////////////////
  // Listener maintenance
  
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  
  // ///////////////
  // Item overrides
  
  public Display getDisplay() {
    checkWidget();
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

  ///////////////////////////////////////
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
