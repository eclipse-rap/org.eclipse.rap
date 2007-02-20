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
  private Button defaultButton;
  private Button saveDefault;

  public Shell( final Display display ) {
    this( display, RWT.SHELL_TRIM );
  }
  
  public Shell( final Display display, final int style ) {
    this ( display, null, style, 0 );
  }
  
  public Shell( final Shell parent ) {
    this( parent, RWT.DIALOG_TRIM );
  }

  public Shell( final Shell parent, final int style ) {
    this( parent != null ? parent.display : null, parent, style, 0 );
  }
  
  Shell( Display display, Shell parent, int style, int handle ) {
    super();
    if( parent != null && parent.isDisposed () ) {
        error( RWT.ERROR_INVALID_ARGUMENT ); 
    }
    this.parent = parent;
    if( display != null ) {
      this.display = display;
    } else {
      this.display = Display.getCurrent();
    }
    this.style = checkStyle( style ); 
    state |= HIDDEN;
    this.display.addShell( this );    
  }

  public final Shell getShell() {
    return this;
  }

  /*
   * Returns an array containing all shells which are descendents of the
   * receiver.
   */
  public Shell [] getShells() {
    checkWidget();
    int count = 0;
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      Control shell = shells[ i ];
      do {
        shell = shell.getParent();
      } while( shell != null && shell != this );
      if( shell == this ) {
        count++;
      }
    }
    int index = 0;
    Shell[] result = new Shell[ count ];
    for( int i = 0; i < shells.length; i++ ) {
      Control shell = shells[ i ];
      do {
        shell = shell.getParent();
      } while( shell != null && shell != this );
      if( shell == this ) {
        result[ index++ ] = shells[ i ];
      }
    }
    return result;
  }

  public final Display getDisplay() {
    return display;
  }

  /* 
   * TODO [rst] Move to class Decorations, as soon as it exists
   */
  public Rectangle getClientArea() {
    checkWidget();
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
  public Rectangle computeTrim( final int x, final int y, final int width, final int height ) {
    checkWidget();
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
    checkWidget();
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
    checkWidget();
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
    checkWidget();
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
    checkWidget();
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
    checkWidget();
    ShellEvent shellEvent = new ShellEvent( this, ShellEvent.SHELL_CLOSED );
    shellEvent.processEvent();
    dispose();
  }

  ///////////////////////////
  // Title bar text and image
  
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }
  
  public String getText() {
    checkWidget();
    return text;
  }

  /* TODO [rst] move to Decorations as soon as it exists */
  public void setImage( final Image image ) {
    checkWidget();
    this.image = image;
  }
  
  /* TODO [rst] move to Decorations as soon as it exists */
  public Image getImage () {
    checkWidget();
    return image;
  }
  
  //////////////////////////////
  // Methods for default button
  
  // TODO [rst] move to class Decorations as soon as it exists
  public void setDefaultButton( final Button button ) {
    checkWidget();
    if( button != null ) {
      if( button.isDisposed() ) {
        error( RWT.ERROR_INVALID_ARGUMENT );
      }
      if( button.getShell() != this ) {
        error( RWT.ERROR_INVALID_PARENT );
      }
    }
    setDefaultButton( button, true );
  }

  // TODO [rst] move to class Decorations as soon as it exists
  void setDefaultButton( final Button button, final boolean save ) {
    if( button == null ) {
      if( defaultButton == saveDefault ) {
        if( save ) {
          saveDefault = null;
        }
        return;
      }
    } else {
      if( ( button.getStyle() & RWT.PUSH ) == 0 ) {
        return;
      }
      if( button == defaultButton ) {
        return;
      }
    }
    if( defaultButton != null ) {
      if( !defaultButton.isDisposed() ) {
        defaultButton.setDefault( false );
      }
    }
    if( ( defaultButton = button ) == null ) {
      defaultButton = saveDefault;
    }
    if( defaultButton != null ) {
      if( !defaultButton.isDisposed() ) {
        defaultButton.setDefault( true );
      }
    }
    if( save ) {
      saveDefault = defaultButton;
    }
    if( saveDefault != null && saveDefault.isDisposed() ) {
      saveDefault = null;
    }
  }

  // TODO [rst] move to class Decorations as soon as it exists
  public Button getDefaultButton() {
    checkWidget();
    return defaultButton;
  }

  /////////////////////////////////////////////////
  // Event listener registration and deregistration
  
  public void addShellListener( final ShellListener listener ) {
    ShellEvent.addListener( this, listener );
  }

  public void removeShellListener( final ShellListener listener ) {
    ShellEvent.removeListener( this, listener );
  }

  /////////////
  // Overrides
  
  protected final void releaseParent() {
    display.removeShell( this );
  }

  protected final void releaseWidget() {
    removeMenuBarDisposeListener();
  }

  //////////////////////////////////////////////////////////
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
  
  private static int checkStyle( final int style ) {
    return Decorations_checkStyle( style );
  }

  private static Shell checkParent( final Shell parent ) {
    if( parent != null && parent.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return parent;
  }
}
