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
import org.eclipse.rap.rwt.graphics.*;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public abstract class Control extends Widget {

  private static final Rectangle EMPTY_RECTANGLE = new Rectangle( 0, 0, 0, 0 );
  private final Composite parent;
  private Rectangle bounds = EMPTY_RECTANGLE;
  private Object layoutData;
  private String toolTipText;
  private Menu menu;
  private DisposeListener menuDisposeListener;
  private boolean visible = true;
  private Color foreground;
  private Color background;

  Control() {
    // prevent instantiation from outside this package
    this.parent = null;
  }

  public Control( final Composite parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ControlHolder.addControl( parent, this );
  }

  public final Composite getParent() {
    return parent;
  }

  public Shell getShell() {
    return parent.getShell();
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }

  
  /////////////
  // Visibility

  public void setVisible( final boolean visible ) {
    this.visible = visible;
  }

  // TODO [rh] rename to getVisible, add isVisible (wich also evaluates parent
  //      visibility)
  public boolean isVisible() {
    return visible;
  }

  
  /////////
  // Colors

  public void setBackground( final Color color ) {
    background = color;
  }
  
  public Color getBackground () {
    // Control control = findBackgroundControl ();
    // if (control == null) control = this;
    return background;
  }

  public void setForeground( final Color color ) {
    foreground = color;
  }

  public Color getForeground () {
    return foreground;
  }
  

  // ///////////////////////////////////////////////
  // Methods to manipulate the controls' dimensions
  
  public Rectangle getBounds() {
    return new Rectangle( bounds );
  }

  public void setBounds( final Rectangle bounds ) {
    if( bounds == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    Point oldLocation = getLocation();
    Point oldSize = getSize();
    this.bounds = new Rectangle( bounds );
    this.bounds.width = Math.max( 0, this.bounds.width );
    this.bounds.height = Math.max( 0, this.bounds.height );
    notifyMove( oldLocation );
    notifyResize( oldSize );
  }

  public void setBounds( final int x,
                         final int y,
                         final int width,
                         final int height )
  {
    setBounds( new Rectangle( x, y, width, height ) );
  }

  public void setLocation( final Point location ) {
    if( location == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    Rectangle newBounds = new Rectangle( location.x,
                                         location.y,
                                         this.bounds.width,
                                         this.bounds.height );
    setBounds( newBounds );
  }

  public void setLocation( final int x, final int y ) {
    setLocation( new Point( x, y ) );
  }

  public Point getLocation() {
    return new Point( this.bounds.x, this.bounds.y );
  }

  public void setSize( final Point size ) {
    if( size == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setBounds( new Rectangle( this.bounds.x, this.bounds.y, size.x, size.y ) );
  }

  public void setSize( final int width, final int height ) {
    setSize( new Point( width, height ) );
  }

  public Point getSize() {
    return new Point( this.bounds.width, this.bounds.height );
  }

  public Point computeSize( final int wHint, final int hHint ) {
    return computeSize( wHint, hHint, true );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    // TODO: [fappel] reasonable implementation
    // TODO: [gröver]: copied from swt:
    // checkWidget ();
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  public int getBorderWidth() {
    // TODO: [fappel] reasonable implementation
    return 0;
  }
  

  // ///////////////////////
  // Layout related methods
  
  public Object getLayoutData() {
    return layoutData;
  }

  public void setLayoutData( final Object layoutData ) {
    this.layoutData = layoutData;
  }
  

  // ///////////////////
  // ToolTip operations
  
  public void setToolTipText( final String toolTipText ) {
    this.toolTipText = toolTipText;
  }

  public String getToolTipText() {
    return toolTipText;
  }
  

  // ////////////////
  // Menu operations
  
  public void setMenu( final Menu menu ) {
    if( this.menu != menu ) {
      if( menu != null ) {
        if( menu.isDisposed() ) {
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        }
        if( ( menu.getStyle() & RWT.POP_UP ) == 0 ) {
          RWT.error( RWT.ERROR_MENU_NOT_POP_UP );
        }
        if( menu.getParent() != getShell() ) {
          RWT.error( RWT.ERROR_INVALID_PARENT );
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
  
  // ///////////////////////////////
  // Methods to add/remove listener
  
  public void addControlListener( final ControlListener listener ) {
    ControlEvent.addListener( this, listener );
  }

  public void removeControlListener( final ControlListener listener ) {
    ControlEvent.removeListener( this, listener );
  }
  

  // /////////
  // Disposal
  
  protected void releaseParent() {
    ControlHolder.removeControl( getParent(), this );
  }

  protected void releaseWidget() {
    if( menu != null ) {
      removeMenuDisposeListener();
      menu.dispose();
      menu = null;
    }
  }

  protected void releaseChildren() {
    // do nothing
  }

  
  /////////////////////////////////////////////////////
  // Helping methods that throw move- and resize-events
  
  void notifyResize( final Point oldSize ) {
    if( !oldSize.equals( getSize() ) ) {
      new ControlEvent( this, ControlEvent.CONTROL_RESIZED ).processEvent();
    }
  }

  void notifyMove( final Point oldLocation ) {
    if( !oldLocation.equals( getLocation() ) ) {
      new ControlEvent( this, ControlEvent.CONTROL_MOVED ).processEvent();
    }
  }

  
  // ////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menu
  
  private void addMenuDisposeListener() {
    if( menu != null ) {
      if( menuDisposeListener == null ) {
        menuDisposeListener = new DisposeListener() {

          public void widgetDisposed( final DisposeEvent event ) {
            Control.this.menu = null;
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
}
