/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.internal.widgets.MenuHolder;
import org.eclipse.swt.internal.widgets.MenuHolder.IMenuHolderAdapter;


/**
 * <p>This class was introduced to be API compatible with SWT and does only 
 * provide those methods that are absolutely necessary to serve this purpose.
 * </p>
 */
public class Decorations extends Canvas {

  private Menu menuBar;
  private MenuHolder menuHolder;
  private DisposeListener menuBarDisposeListener;

  Decorations( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IMenuHolderAdapter.class ) {
      if( menuHolder == null ) {
        menuHolder = new MenuHolder();
      }
      result = menuHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  //////////
  // MenuBar
  
  /**
   * Sets the receiver's menu bar to the argument, which
   * may be null.
   *
   * @param menuBar the new menu bar
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMenuBar( final Menu menuBar ) {
    checkWidget();
    if( this.menuBar != menuBar ) {
      if( menuBar != null ) {
        if( menuBar.isDisposed() ) {
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( menuBar.getParent() != this ) {
          SWT.error( SWT.ERROR_INVALID_PARENT );
        }
        if( ( menuBar.getStyle() & SWT.BAR ) == 0 ) {
          SWT.error( SWT.ERROR_MENU_NOT_BAR );
        }
      }
      removeMenuBarDisposeListener();
      this.menuBar = menuBar;
      addMenuBarDisposeListener();
    }
  }

  /**
   * Returns the receiver's menu bar if one had previously
   * been set, otherwise returns null.
   *
   * @return the menu bar or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Menu getMenuBar() {
    checkWidget();
    return menuBar;
  }

  ///////////
  // Disposal
  
  final void releaseWidget() {
    removeMenuBarDisposeListener();
    super.releaseWidget();
  }

  //////////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menuBar
  
  private void addMenuBarDisposeListener() {
    if( menuBar != null ) {
      if( menuBarDisposeListener == null ) {
        menuBarDisposeListener = new DisposeListener() {
          public void widgetDisposed( final DisposeEvent event ) {
            Decorations.this.menuBar = null;
          }
        };
      }
      menuBar.addDisposeListener( menuBarDisposeListener );
    }
  }

  private void removeMenuBarDisposeListener() {
    if( menuBar != null ) {
      menuBar.removeDisposeListener( menuBarDisposeListener );
    }
  }
}
