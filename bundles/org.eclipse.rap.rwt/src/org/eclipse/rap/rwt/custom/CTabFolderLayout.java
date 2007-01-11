/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.custom;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.widgets.*;

/**
 * This class provides the layout for CTabFolder
 * 
 * @see CTabFolder
 */
class CTabFolderLayout extends Layout {

  private static final int DEFAULT_WIDTH = 64;
  private static final int DEFAULT_HEIGHT = 64;

  protected Point computeSize( final Composite composite,
                               final int wHint,
                               final int hHint,
                               final boolean flushCache )
  {
    CTabFolder folder = ( CTabFolder )composite;
    CTabItem[] items = folder.getItems();
    // preferred width of tab area to show all tabs
    int tabW = 0;
    for( int i = 0; i < items.length; i++ ) {
      if( ( folder.getStyle() & RWT.SINGLE ) != 0 ) {
        tabW = Math.max( tabW, items[ i ].preferredWidth( true, false ) );
      } else {
        tabW += items[ i ].preferredWidth( i == folder.getSelectionIndex(),
                                           false );
      }
    }
    tabW += 3;
    if( folder.getMaximizeVisible() ) {
      tabW += CTabFolder.BUTTON_SIZE;
    }
    if( folder.getMinimizeVisible() ) {
      tabW += CTabFolder.BUTTON_SIZE;
    }
    if( ( folder.getStyle() & RWT.SINGLE ) != 0 ) {
      tabW += 3 * CTabFolder.BUTTON_SIZE / 2; // chevron
    }
    if( folder.getTopRight() != null ) {
      Point pt = folder.getTopRight().computeSize( RWT.DEFAULT,
                                                   folder.getTabHeight(),
                                                   flushCache );
      tabW += 3 + pt.x;
    }
    // if (!folder.single && !folder.simple) tabW += folder.curveWidth -
    // 2*folder.curveIndent;
    // <<< end preliminary implementation
    int controlW = 0;
    int controlH = 0;
    // preferred size of controls in tab items
    for( int i = 0; i < items.length; i++ ) {
      Control control = items[ i ].getControl();
      if( control != null && !control.isDisposed() ) {
        Point size = control.computeSize( wHint, hHint, flushCache );
        controlW = Math.max( controlW, size.x );
        controlH = Math.max( controlH, size.y );
      }
    }
    int minWidth = Math.max( tabW, controlW );
    int minHeight = ( folder.getMinimized() ) ? 0 : controlH;
    if( minWidth == 0 ) {
      minWidth = DEFAULT_WIDTH;
    }
    if( minHeight == 0 ) {
      minHeight = DEFAULT_HEIGHT;
    }
    if( wHint != RWT.DEFAULT ) {
      minWidth = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      minHeight = hHint;
    }
    return new Point( minWidth, minHeight );
  }

  protected boolean flushCache( final Control control ) {
    return true;
  }

  protected void layout( final Composite composite, final boolean flushCache ) {
    CTabFolder folder = ( CTabFolder )composite;
    int selectionIndex = folder.getSelectionIndex();
    // resize content
    if( selectionIndex != -1 ) {
      Control control = folder.getItems()[ selectionIndex ].getControl();
      if( control != null && !control.isDisposed() ) {
        control.setBounds( folder.getClientArea() );
      }
    }
  }
}
