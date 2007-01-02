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

package org.eclipse.rap.rwt.custom;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.*;


public class CTabFolder extends Composite {

  public static final int DEFAULT_TAB_HEIGHT = 20;

  // internal constants
  static final int DEFAULT_WIDTH = 64;
  static final int DEFAULT_HEIGHT = 64;
  static final int BUTTON_SIZE = 18;

  private final ItemHolder itemHolder = new ItemHolder( CTabItem.class );
  private int selectionIndex = -1;
  private boolean maximizeVisible = true;
  private boolean minimizeVisible = true;
  private boolean inDispose = false;
  private boolean minimized = false;
  private boolean maximized = false;
  private int tabHeight = DEFAULT_TAB_HEIGHT;
  private Control topRight;
  private int topRightAlignment = RWT.RIGHT;
  
  public CTabFolder( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    super.setLayout( new CTabFolderLayout() );
    addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        inDispose = true;
      }
    } );
  }
  

  //////////////////
  // Item management
  
  public CTabItem[] getItems() {
    return (org.eclipse.rap.rwt.custom.CTabItem[] )itemHolder.getItems();
  }

  public CTabItem getItem( final int index ) {
    return ( CTabItem )itemHolder.getItem( index );
  }

  public int getItemCount() {
    return itemHolder.size();
  }

  public int indexOf( final CTabItem item ) {
    return itemHolder.indexOf( item );
  }

  ///////////////////////
  // Selection management
  
  public void setSelection( final int index ) {
    if( index >= 0 && index <= itemHolder.size() - 1 ) {
      selectionIndex = index;
      Control control = getItem( selectionIndex ).getControl();
      if( control != null ) {
        control.setBounds( getClientArea() );
      }
    }
  }

  public int getSelectionIndex() {
    return selectionIndex;
  }
  
  public void setSelection( final CTabItem item ) {
    if( item == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int index = itemHolder.indexOf( item );
    setSelection( index );
  }
  
  public CTabItem getSelection() {
    CTabItem result = null;
    if( selectionIndex != -1 ) {
      result = ( CTabItem )itemHolder.getItem( selectionIndex );
    }
    return result; 
  }

  ////////////////////////////////
  // Minimize / Maximize / Restore
  
  public void setMaximizeVisible( final boolean maximizeVisible ) {
    this.maximizeVisible = maximizeVisible;
  }
  
  public boolean getMaximizeVisible() {
    return maximizeVisible;
  }
  
  public void setMinimizeVisible( final boolean minimizeVisible ) {
    this.minimizeVisible = minimizeVisible;
  }

  public boolean getMinimizeVisible() {
    return minimizeVisible;
  }
  
  public void setMinimized( final boolean minimized ) {
    if( this.minimized != minimized ) {
      if( minimized && this.maximized ) {
        setMaximized( false );
      }
      this.minimized = minimized;
    }
  }

  public boolean getMinimized() {
    return minimized;
  }
  
  public void setMaximized( final boolean maximized ) {
    if( this.maximized != maximized ) {
      if( maximized && this.minimized ) {
        setMinimized( false );
      }
      this.maximized = maximized;
    }
  }

  public boolean getMaximized() {
    return maximized;
  }

  //////////////////////////////////////
  // Appearance and dimension properties 
  
  public void setLayout( final Layout layout ) {
    // ignore - CTabFolder manages its own layout
  }

  public void setTabHeight( final int tabHeight ) {
    if( tabHeight == RWT.DEFAULT ) {
      this.tabHeight = DEFAULT_TAB_HEIGHT;
    } else if( tabHeight >= 0 ) {
      this.tabHeight = tabHeight;
    } else {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
  }

  public int getTabHeight() {
    return tabHeight;
  }

  ///////////////////////////////////
  // Manipulation of topRight control 

  public void setTopRight( final Control control ) {
    setTopRight( control, RWT.RIGHT );
  }

  public void setTopRight( final Control control, final int alignment ) {
    if( alignment != RWT.RIGHT && alignment != RWT.FILL ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    if( control != null && control.getParent() != this ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    topRight = control;
    topRightAlignment = alignment;
  }

  public Control getTopRight() {
    return topRight;
  }
  
  // TODO [rh] not part of SWT API but necessary for LCA; take another solution?
  public int getTopRightAlignment() {
    return topRightAlignment;
  }

  ///////////////////////////
  // Adaptable implementation
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  
  //////////////////////
  // Composite overrides
  
  public Rectangle getClientArea() {
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int border = 1;
    int hTabBar = 23;
    return new Rectangle( border,
                          hTabBar + border,
                          width - border * 2,
                          height - ( hTabBar + border * 2 ) );
  }

  protected void releaseChildren() {
    CTabItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  
  ///////////////////////////////////////
  // Listener registration/deregistration 

  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  public void addCTabFolder2Listener( final CTabFolder2Listener listener ) {
    CTabFolderEvent.addListener( this, listener );
  }

  public void removeCTabFolder2Listener( final CTabFolder2Listener listener ) {
    CTabFolderEvent.removeListener( this, listener );
  }
  
  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }

  void setSelection( final int index, final boolean notify ) {
    int oldSelectedIndex = selectionIndex;
    setSelection( index );
    if( notify && selectionIndex != oldSelectedIndex && selectionIndex != -1 ) {
      SelectionEvent event = new SelectionEvent( this, 
                                                 getSelection(), 
                                                 SelectionEvent.WIDGET_SELECTED, 
                                                 new Rectangle( 0, 0, 0, 0 ), 
                                                 true, 
                                                 0 );
      event.processEvent();
    }
  }
  
  void destroyItem( final CTabItem item ) {
    int index = indexOf( item );
    ItemHolder.removeItem( this, item );
    if( !inDispose ) {
      if( getItemCount() == 0 ) {
        selectionIndex = -1;
        Control control = item.getControl();
        if( control != null && !control.isDisposed() ) {
          control.setVisible( false );
        }
      } else { 
        // move the selection if this item is selected
        if( getSelectionIndex() == index ) {
          Control control = item.getControl();
          int nextSelection = Math.max( 0, index - 1 );
          setSelection( nextSelection, true );
          if( control != null && !control.isDisposed() ) {
            control.setVisible( false );
          }
        } else if( getSelectionIndex() > index ) {
          selectionIndex--;
        }
      }
    }
  }
}
