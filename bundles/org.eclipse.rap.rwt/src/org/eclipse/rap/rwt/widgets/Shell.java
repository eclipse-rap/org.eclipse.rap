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
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IShellAdapter;
import org.eclipse.rap.rwt.widgets.MenuHolder.IMenuHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Shell extends Composite {

  // TODO [rh] preliminary: constants extracted to be used in MenuLCA
  public static final int TITLE_BAR_HEIGHT = 18 + 1;
  public static final int MENU_BAR_HEIGHT = 20;
  
  private final Display display;
  private Menu menuBar;
  private MenuHolder menuHolder;
  private DisposeListener menuBarDisposeListener;
  private Control lastActive;
  private IShellAdapter shellAdapter;
  private String text = "";
  private Image image;

  public Shell( final Display display ) {
    this( display, RWT.SHELL_TRIM );
  }
  
  // TODO [rh] preliminary: yet no null-check, default/current substitute, etc
  public Shell( final Display display, final int style ) {
    super();
    this.style = checkStyle( style );
    if( display == null ) {
      this.display = Display.getCurrent();
    } else {
      this.display = display;
    }
    state |= HIDDEN;
    this.display.addShell( this );
  }
  
  public Shell( final Shell parent ) {
    this( parent, RWT.DIALOG_TRIM );
  }

  // TODO: [fappel] this is just a fake constructor for dialog shells,
  //                but no special dialog support implemented yet.
  public Shell( final Shell parent, final int style ) {
    this( checkParent( parent ) == null 
                                ? Display.getCurrent()
                                : parent.getDisplay(), 
          style );
  }

  public final Shell getShell() {
    return this;
  }

  public final Display getDisplay() {
    return display;
  }

  /* 
   * TODO [rst] Move to class Decorations, as soon as it exists
   */
  public Rectangle getClientArea() {
    checkWidget ();
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int hTitleBar = ( style & RWT.TITLE ) != 0 ? TITLE_BAR_HEIGHT : 0;
    if( getMenuBar() != null ) {
      hTitleBar += MENU_BAR_HEIGHT;
    }
    int border = getBorderWidth();
    int margin = ( style & RWT.TITLE ) != 0 ? 2 : 0;
    return new Rectangle( 0 + margin,
                          hTitleBar + margin,
                          width - margin * 2 - border * 2,
                          height - hTitleBar - margin * 2 - border * 2 );
  }
  
  /* 
   * TODO [rst] Move to class Decorations, as soon as it exists
   */
  public Rectangle computeTrim( int x, int y, int width, int height ) {
    checkWidget ();
    int hTitleBar = ( style & RWT.TITLE ) != 0 ? TITLE_BAR_HEIGHT : 0;
    if( getMenuBar() != null ) {
      hTitleBar += MENU_BAR_HEIGHT;
    }
    int border = getBorderWidth();
    int margin = ( style & RWT.TITLE ) != 0 ? 2 : 0;
    Rectangle rect = new Rectangle( x - margin - border,
                                    y - hTitleBar - margin - border,
                                    width + margin * 2 + border * 2,
                                    height + hTitleBar + margin * 2 + border * 2 );
    return rect;
  }
  
  public int getBorderWidth() {
    return ( style & ( RWT.BORDER | RWT.TITLE ) ) != 0 ? 2 : 1;
  }

  public void setActive () {
    checkWidget();
    if( isVisible() ) {
      display.setActiveShell( this );
    }
  }

  //////////
  // MenuBar
  
  public void setMenuBar( final Menu menuBar ) {
    if( this.menuBar != menuBar ) {
      if( menuBar != null ) {
        if( menuBar.isDisposed() ) {
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        }
        if( menuBar.getParent() != this ) {
          RWT.error( RWT.ERROR_INVALID_PARENT );
        }
        if( ( menuBar.getStyle() & RWT.BAR ) == 0 ) {
          RWT.error( RWT.ERROR_MENU_NOT_BAR );
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

  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IMenuHolderAdapter.class ) {
      if( menuHolder == null ) {
        menuHolder = new MenuHolder();
      }
      result = menuHolder;
    } else if( adapter == IShellAdapter.class ) {
      if( shellAdapter == null ) {
        shellAdapter = new IShellAdapter() {
          public Control getActiveControl() {
            return Shell.this.lastActive;
          }
          public void setActiveControl( final Control control ) {
            Shell.this.setActiveControl( control );
          }
        };
      }
      result = shellAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /////////////
  // Enablement

  public boolean isEnabled () {
    checkWidget ();
    return getEnabled ();
  }

  /////////////
  // Visibility

  public boolean isVisible() {
    checkWidget();
    return getVisible();
  }
  
  public void open () {
    checkWidget();
    state &= ~HIDDEN;
    display.setActiveShell( this );
  }

  public void close() {
    ShellEvent shellEvent = new ShellEvent( this, ShellEvent.SHELL_CLOSED );
    shellEvent.processEvent();
    dispose();
  }

  public void setText( final String string ) {
    checkWidget ();
    if( string == null ) {
      error (RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = string;
  }
  
  public String getText() {
    checkWidget ();
    return text;
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
  
  ////////////////////////////////////////////////////////////
  // Methods to maintain activeControl and send ActivateEvents

  private void setActiveControl( final Control activateControl ) {
    Control control = activateControl;
    if( control != null && control.isDisposed() ) {
      control = null;
    }
    if( lastActive != null && lastActive.isDisposed() ) {
      lastActive = null;
    }
    if( lastActive != control ) {
      // Compute the list of controls to be activated and deactivated by finding
      // the first common parent control.
      Control[] deactivate 
        = ( lastActive == null ) ? new Control[ 0 ] : getPath( lastActive );
      Control[] activate 
        = ( control == null ) ? new Control[ 0 ] : getPath( control );
      lastActive = control;
      
      int index = 0;
      int length = Math.min( activate.length, deactivate.length );
      while( index < length && activate[ index ] == deactivate[ index ] ) {
        index++;
      }
      // It is possible (but unlikely), that application code could have
      // destroyed some of the widgets. If this happens, keep processing those
      // widgets that are not disposed.
      ActivateEvent evt;
      for( int i = deactivate.length - 1; i >= index; --i ) {
        if( !deactivate[ i ].isDisposed() ) {
          evt = new ActivateEvent( deactivate[ i ], ActivateEvent.DEACTIVATED );
          evt.processEvent();
        }
      }
      for( int i = activate.length - 1; i >= index; --i ) {
        if( !activate[ i ].isDisposed() ) {
          evt = new ActivateEvent( activate[ i ], ActivateEvent.ACTIVATED );
          evt.processEvent();
        }
      }
    }
  }

  private Control[] getPath( final Control ctrl ) {
    int count = 0;
    Control control = ctrl;
    while( control != this ) {
      count++;
      control = control.getParent();
    }
    control = ctrl;
    Control[] result = new Control[ count ];
    while( control != this ) {
      count--;
      result[ count ] = control;
      control = control.getParent();
    }
    return result;
  }

  /* TODO [rst] move to Decorations as soon as it exists */
  public void setImage( Image image ) {
    checkWidget();
    this.image = image;
  }
  
  /* TODO [rst] move to Decorations as soon as it exists */
  public Image getImage () {
    checkWidget ();
    return image;
  }

  ///////////////////
  // check... methods
  
  // TODO [rh] move to class Decorations as soon as it exists
  static int Decorations_checkStyle( final int style ) {
    int result = style;
    if( ( result & RWT.NO_TRIM ) != 0 ) {
      int trim = ( RWT.CLOSE 
                 | RWT.TITLE 
                 | RWT.MIN 
                 | RWT.MAX 
                 | RWT.RESIZE 
                 | RWT.BORDER );
      result &= ~trim;
    }
    if( ( result & ( /* RWT.MENU | */ RWT.MIN | RWT.MAX | RWT.CLOSE ) ) != 0 ) {
      result |= RWT.TITLE;
    }
    if( ( result & ( RWT.MIN | RWT.MAX ) ) != 0 ) {
      result |= RWT.CLOSE;
    }
    return result;
  }
  
  static int checkStyle( final int style ) {
    return Decorations_checkStyle( style );
  }

  private static Shell checkParent( final Shell parent ) {
    if( parent != null && parent.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return parent;
  }
}
