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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.widgets.ControlHolder.IControlHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Composite extends Scrollable {

  private Layout layout;
  private final ControlHolder controlHolder = new ControlHolder();
  private Control[] tabList;
  
  Composite( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
  }

  public Composite( final Composite parent, final int style ) {
    super( parent, style );
  }

  public Control[] getChildren() {
    checkWidget();
    return controlHolder.getControls();
  }

  public int getChildrenCount() {
    checkWidget();
    return controlHolder.size();
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IControlHolderAdapter.class ) {
      result = controlHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  //////////////////
  // Layout methods
  
  public void setLayout( final Layout layout ) {
    checkWidget();
    this.layout = layout;
  }

  public Layout getLayout() {
    checkWidget();
    return layout;
  }

  public void layout() {
    checkWidget();
    if( layout != null ) {
      layout.layout( this, true );
    }
  }
  
  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    Point size;
    boolean hasChanged = changed;
    if( layout != null ) {
      if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
        hasChanged |= ( state & LAYOUT_CHANGED ) != 0;
        state &= ~LAYOUT_CHANGED;
        size = layout.computeSize( this, wHint, hHint, hasChanged );
      } else {
        size = new Point( wHint, hHint );
      }
    } else {
      size = minimumSize( wHint, hHint, hasChanged );
    }
    if( size.x == 0 ) {
      size.x = DEFAULT_WIDTH;
    }
    if( size.y == 0 ) {
      size.y = DEFAULT_HEIGHT;
    }
    if( wHint != SWT.DEFAULT ) {
      size.x = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      size.y = hHint;
    }
    Rectangle trim = computeTrim( 0, 0, size.x, size.y );
    return new Point( trim.width, trim.height );
  }

  ////////////////////
  // setFocus override 
  
  public boolean setFocus() {
    checkWidget();
    Control[] children = getChildren();
//     for( int i = 0; i < children.length; i++ ) {
//      Control child = children[ i ];
//      if( child.setRadioFocus() )
//        return true;
//    }
    Control focusedChild = null;
    for( int i = 0; focusedChild == null && i < children.length; i++ ) {
      Control child = children[ i ];
      if( child.setFocus() ) {
        focusedChild = child;
      }
    }
    boolean result = true;
    if( focusedChild == null ) {
      result = super.setFocus();
    } 
    return result;
  }

  ////////////
  // Tab Order
  
  public void setTabList( final Control[] tabList ) {
    checkWidget();
    Control[] newList = tabList;
    if( tabList != null ) {
      for( int i = 0; i < tabList.length; i++ ) {
        Control control = tabList[ i ];
        if( control == null ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( control.isDisposed() ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( control.parent != this ) {
          error( SWT.ERROR_INVALID_PARENT );
        }
      }
      newList = new Control[ tabList.length ];
      System.arraycopy( tabList, 0, newList, 0, tabList.length );
    }
    this.tabList = newList;
  }
  
  // returns only tabGroups
  public Control[] getTabList() {
    checkWidget();
    Control[] result = _getTabList();
    if( result == null ) {
      int count = 0;
      Control[] list = controlHolder.getControls();
      for( int i = 0; i < list.length; i++ ) {
        if( list[ i ].isTabGroup() ) {
          count++;
        }
      }
      result = new Control[ count ];
      int index = 0;
      for( int i = 0; i < list.length; i++ ) {
        if( list[ i ].isTabGroup() ) {
          result[ index++ ] = list[ i ];
        }
      }
    }
    return result;
  }

  // filters disposed controls out
  Control[] _getTabList() {
    if( tabList != null ) {
      int count = 0;
      for( int i = 0; i < tabList.length; i++ ) {
        if( !tabList[ i ].isDisposed() ) {
          count++;
        }
      }
      if( count != tabList.length ) {
        Control[] newList = new Control[ count ];
        int index = 0;
        for( int i = 0; i < tabList.length; i++ ) {
          if( !tabList[ i ].isDisposed() ) {
            newList[ index++ ] = tabList[ i ];
          }
        }
        tabList = newList;
      }
    }
    return tabList;
  }
  
  boolean isTabGroup() {
    return true;
  }
  
  /////////////////////////////////////
  // Helping method used by computeSize
  
  Point minimumSize( final int wHint, final int hHint, final boolean changed ) {
    Control[] children = getChildren();
    int width = 0, height = 0;
    for( int i = 0; i < children.length; i++ ) {
      Rectangle rect = children[ i ].getBounds();
      width = Math.max( width, rect.x + rect.width );
      height = Math.max( height, rect.y + rect.height );
    }
    return new Point( width, height );
  }

  /////////////////////////////////////////////////
  // Internal methods to maintain the child controls
  
  protected void releaseChildren() {
    Control[] children = getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
  }
  
  void removeControl( final Control control ) {
    controlHolder.remove( control );
  }

  ////////////////
  // Resize helper
  
  void notifyResize( final Point oldSize ) {
    if( !oldSize.equals( getSize() ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          layout();
        }
      } );
    }
    super.notifyResize( oldSize );
  }
}