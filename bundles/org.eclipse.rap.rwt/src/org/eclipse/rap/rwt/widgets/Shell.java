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
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.MenuHolder.IMenuHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Shell extends Composite {

  // TODO [rh] preliminary: constants extracted to be used in MenuLCA
  public static final int TITLE_BAR_HEIGHT = 15;
  public static final int MENU_BAR_HEIGHT = 20;
  private final Display display;
  private Menu menuBar;
  private MenuHolder menuHolder;
  private DisposeListener menuBarDisposeListener;

  // TODO [rh] preliminary: yet no null-check, default/current substitute, etc
  public Shell( final Display display, final int style ) {
    super();
    this.style = style;
    this.display = display;
    display.addShell( this );
  }

  public final Shell getShell() {
    return this;
  }

  public final Display getDisplay() {
    return display;
  }

  public Rectangle getClientArea() {
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int hTitleBar = TITLE_BAR_HEIGHT;
    if( getMenuBar() != null ) {
      hTitleBar += MENU_BAR_HEIGHT;
    }
    int border = 5;
    return new Rectangle( border - 3,
                          hTitleBar + border,
                          width - border * 2,
                          height - ( hTitleBar + border * 2 ) - 3 );
  }

  public void setMenuBar( final Menu menuBar ) {
    if( this.menuBar != menuBar ) {
      if( menuBar != null ) {
        if( menuBar.isDisposed() ) {
          // will become SWT.ERROR_INVALID_ARGUMENT
          String msg = "The argument 'menuBar' is already disposed of.";
          throw new IllegalArgumentException( msg );
        }
        if( menuBar.getParent() != this ) {
          // will become SWT.ERROR_INVALID_PARENT
          String msg = "The argument 'menuBar' has an invalid parent.";
          throw new IllegalArgumentException( msg );
        }
        if( ( menuBar.getStyle() & RWT.BAR ) == 0 ) {
          String msg = "The argument 'menuBar' is not a BAR.";
          throw new IllegalArgumentException( msg );
        }
      }
      removeMenuBarDisposeListener();
      this.menuBar = menuBar;
      addMenuBarDisposeListener();
    }
  }

  public Menu getMenuBar() {
    return menuBar;
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

  public void close() {
    ShellEvent shellEvent = new ShellEvent( this, ShellEvent.SHELL_CLOSED );
    shellEvent.processEvent();
    dispose();
  }

  // ///////////////////////////////////////////////
  // Event listener registration and deregistration
  public void addShellListener( final ShellListener listener ) {
    ShellEvent.addListener( this, listener );
  }

  public void removeShellListener( final ShellListener listener ) {
    ShellEvent.removeListener( this, listener );
  }

  // //////////
  // Overrides
  protected final void releaseParent() {
    display.removeShell( this );
  }

  protected final void releaseWidget() {
    removeMenuBarDisposeListener();
  }

  // ///////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menuBar
  private void addMenuBarDisposeListener() {
    if( menuBar != null ) {
      if( menuBarDisposeListener == null ) {
        menuBarDisposeListener = new DisposeListener() {

          public void widgetDisposed( final DisposeEvent event ) {
            Shell.this.menuBar = null;
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
