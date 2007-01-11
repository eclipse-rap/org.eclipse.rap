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
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.widgets.ControlHolder.IControlHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Composite extends Scrollable {

  private Layout layout;
  private final ControlHolder controlHolder = new ControlHolder();

  Composite() {
    // prevent instantiation from outside this package
  }

  public Composite( final Composite parent, final int style ) {
    super( parent, style );
  }

  public Control[] getChildren() {
    return controlHolder.getControls();
  }

  public int getChildrenCount() {
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

  // ///////////////
  // Layout methods
  
  public void setLayout( final Layout layout ) {
    this.layout = layout;
  }

  public Layout getLayout() {
    return layout;
  }

  public void layout() {
    if( layout != null ) {
      layout.layout( this, true );
    }
  }
  
  public Point computeSize( final int wHint, final int hHint, boolean changed ) 
  {
//    checkWidget();
    Point size;
    if( layout != null ) {
      if( wHint == RWT.DEFAULT || hHint == RWT.DEFAULT ) {
        changed |= ( state & LAYOUT_CHANGED ) != 0;
        state &= ~LAYOUT_CHANGED;
        size = layout.computeSize( this, wHint, hHint, changed );
      } else {
        size = new Point( wHint, hHint );
      }
    } else {
      size = minimumSize( wHint, hHint, changed );
    }
    if( size.x == 0 ) {
      size.x = DEFAULT_WIDTH;
    }
    if( size.y == 0 ) {
      size.y = DEFAULT_HEIGHT;
    }
    if( wHint != RWT.DEFAULT ) {
      size.x = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      size.y = hHint;
    }
    Rectangle trim = computeTrim( 0, 0, size.x, size.y );
    return new Point( trim.width, trim.height );
  }

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