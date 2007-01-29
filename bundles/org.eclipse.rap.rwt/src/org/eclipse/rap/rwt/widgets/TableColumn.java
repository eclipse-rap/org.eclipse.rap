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
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;

public class TableColumn extends Item {

  private final Table parent;
  private int width = 0;

  public TableColumn( final Table parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }

  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }

  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    if( width >= 0 ) {
      this.width = width;
    }
  }
  
  
  ///////////////////////////////////
  // Methods to dispose of the widget

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    TableItem[] items = parent.getItems();
    int index = parent.indexOf( this );
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].removeText( index );
    }
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
  }
}
