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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.MenuHolder.IMenuHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Shell extends Composite {

  private static final int TITLE_BAR_HEIGHT = 18;
  private static final int MENU_BAR_HEIGHT = 20;
  private static final int MODE_NONE = 0;
  private static final int MODE_MAXIMIZED = 1;
  private static final int MODE_MINIMIZED = 2;
  
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
  private Control savedFocus;  // TODO [rh] move to Decorations when exist
  private int mode = MODE_NONE;
  
  private Shell( final Display display, 
                 final Shell parent, 
                 final int style, 
                 final int handle ) 
  {
    super( checkParent( parent ) );
    if( display != null ) {
      this.display = display;
    } else {
      this.display = Display.getCurrent();
    }
    this.style = checkStyle( style ); 
    state |= HIDDEN;
    this.display.addShell( this );    
  }

  public Shell( final Display display ) {
    this( display, SWT.SHELL_TRIM );
  }
  
  public Shell( final Display display, final int style ) {
    this ( display, null, style, 0 );
  }
  
  public Shell( final Shell parent ) {
    this( parent, SWT.DIALOG_TRIM );
  }

  public Shell( final Shell parent, final int style ) {
    this( parent != null ? parent.display : null, parent, style, 0 );
  }
  
  public final Display getDisplay() {
    return display;
  }

  public final Shell getShell() {
    return this;
  }

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

  public void setActive () {
    checkWidget();
    if( isVisible() ) {
      display.setActiveShell( this );
    }
  }

  /////////////////////
  // Shell measurements
  
  // TODO [rst] Move to class Decorations, as soon as it exists
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle bounds = getBounds();
    int hTopTrim = ( style & SWT.TITLE ) != 0 ? TITLE_BAR_HEIGHT + 1 : 0;
    if( getMenuBar() != null ) {
      hTopTrim += MENU_BAR_HEIGHT;
    }
    int margin = getMargin();
    int border = getBorderWidth();
    return new Rectangle( 0 + margin,
                          hTopTrim + margin,
                          bounds.width - margin * 2 - border * 2,
                          bounds.height - hTopTrim - margin * 2 - border * 2 );
  }
  
  // TODO [rst] Move to class Decorations, as soon as it exists
  public Rectangle computeTrim( final int x, 
                                final int y, 
                                final int width, 
                                final int height ) 
  {
    checkWidget();
    int hTopTrim = ( style & SWT.TITLE ) != 0 ? TITLE_BAR_HEIGHT + 1 : 0;
    if( getMenuBar() != null ) {
      hTopTrim += MENU_BAR_HEIGHT;
    }
    int margin = getMargin();
    int border = getBorderWidth();
    Rectangle rect = new Rectangle( x - margin - border,
                                    y - hTopTrim - margin - border,
                                    width + margin * 2 + border * 2,
                                    height + hTopTrim + margin * 2 + border * 2 );
    return rect;
  }
  
  public int getBorderWidth() {
    checkWidget();
    return ( style & ( SWT.BORDER | SWT.TITLE ) ) != 0 ? 2 : 1;
  }

  private Rectangle getMenuBounds() {
    Rectangle result = null;
    if( getMenuBar() != null ) {
      Rectangle bounds = getBounds();
      int hTop = ( style & SWT.TITLE ) != 0 ? TITLE_BAR_HEIGHT : 0;
      int margin = getMargin();
      int border = getBorderWidth();
      result = new Rectangle( margin,
                              hTop + margin,
                              bounds.width - margin * 2 - border * 2,
                              MENU_BAR_HEIGHT );
    }
    return result;
  }
  
  // margin of the client area
  private int getMargin() {
    return ( style & SWT.TITLE ) != 0 ? 2 : 0;
  }
  
  //////////
  // MenuBar
  
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
          public Rectangle getMenuBounds() {
            return Shell.this.getMenuBounds();
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

  public void setEnabled( final boolean enabled ) {
    checkWidget();
    if( getEnabled() != enabled ) {
      super.setEnabled( enabled );
      if( enabled ) {
        if( !restoreFocus() ) {
          traverseGroup( true );
        }
      }
    }
  }
  
  public boolean isEnabled() {
    checkWidget();
    return getEnabled ();
  }

  /////////////
  // Visibility

  public boolean isVisible() {
    checkWidget();
    return getVisible();
  }
  
  public void open() {
    checkWidget();
    bringToTop();
    display.setActiveShell( this );
    setVisible( true );
    if( !restoreFocus() && !traverseGroup( true ) ) {
      setFocus();
    }
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
      error( SWT.ERROR_NULL_ARGUMENT );
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
  public Image getImage() {
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
        error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( button.getShell() != this ) {
        error( SWT.ERROR_INVALID_PARENT );
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
      if( ( button.getStyle() & SWT.PUSH ) == 0 ) {
        return;
      }
      if( button == defaultButton ) {
        return;
      }
    }
    if( defaultButton != null && !defaultButton.isDisposed() ) {
      defaultButton.setDefault( false );
    }
    defaultButton = button;
    if( defaultButton == null ) {
      defaultButton = saveDefault;
    }
    if( defaultButton != null && !defaultButton.isDisposed() ) {
      defaultButton.setDefault( true );
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

  ///////////
  // Disposal
  
  protected final void releaseParent() {
    // Do not call super.releaseParent() 
    // This method would try to remove a child-shell from its ControlHolder
    // but shells are currently not added to the ControlHolder of its parent
    display.removeShell( this );
  }

  protected final void releaseWidget() {
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
  
  /////////////////////////
  // Focus handling methods

  // TODO [rh] move to Decorations as soon as exists
  final void setSavedFocus( final Control control ) {
    savedFocus = control;
  }
  
  // TODO [rh] move to Decorations as soon as exists
  final void saveFocus() {
    Control control = display.getFocusControl();
    if( control != null && control != this && this == control.getShell() ) {
      setSavedFocus( control );
    }
  }
  
  // TODO [rh] move to Decorations as soon as exists
  final boolean restoreFocus() {
    if( savedFocus != null && savedFocus.isDisposed() ) {
      savedFocus = null;
    }
    boolean result = false;
    if( savedFocus != null && savedFocus.setSavedFocus() ) {
      result = true;
    }
    return result;
  }
  
  private void bringToTop() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setFocusControl( this );
  }

  ////////////////
  // Tab traversal
  
  private boolean traverseGroup( final boolean next ) {
    // TODO [rh] fake implementation
    boolean result = false;
    if( getChildren().length > 0 ) {
      result = getChildren()[ 0 ].forceFocus();
    }
    return result;
  }
  
  //////////////////////
  // minimize / maximize
  //
  // TODO [rst] Move these methods to class Decorations when implemented

  public void setMinimized( final boolean minimized ) {
    if( minimized ) {
      mode |= MODE_MINIMIZED;
    } else {
      if( ( mode & MODE_MINIMIZED ) != 0 ) {
        setActive();
      }
      mode &= ~MODE_MINIMIZED;
    }
  }
  
  public void setMaximized( final boolean maximized ) {
    if( maximized ) {
      if( mode != MODE_MAXIMIZED ) {
        setActive();
        setBounds( display.getBounds() );
      }
      mode |= MODE_MAXIMIZED;
      mode &= ~MODE_MINIMIZED;
    } else {
      mode &= ~MODE_MAXIMIZED;
    }
  }
  
  public boolean getMinimized() {
    return ( this.mode & MODE_MINIMIZED ) != 0;
  }
  
  public boolean getMaximized() {
    return this.mode == MODE_MAXIMIZED;
  }
  
  ///////////////////
  // check... methods
  
  // TODO [rh] move to class Decorations as soon as it exists
  static int Decorations_checkStyle( final int style ) {
    int result = style;
    if( ( result & SWT.NO_TRIM ) != 0 ) {
      int trim = ( SWT.CLOSE 
                 | SWT.TITLE 
                 | SWT.MIN 
                 | SWT.MAX 
                 | SWT.RESIZE 
                 | SWT.BORDER );
      result &= ~trim;
    }
    if( ( result & ( /* SWT.MENU | */ SWT.MIN | SWT.MAX | SWT.CLOSE ) ) != 0 ) {
      result |= SWT.TITLE;
    }
    if( ( result & ( SWT.MIN | SWT.MAX ) ) != 0 ) {
      result |= SWT.CLOSE;
    }
    return result;
  }
  
  private static int checkStyle( final int style ) {
    return Decorations_checkStyle( style );
  }

  private static Shell checkParent( final Shell parent ) {
    if( parent != null && parent.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return parent;
  }
}
