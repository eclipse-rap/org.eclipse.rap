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
import org.eclipse.rap.rwt.graphics.Image;

public class TabItem extends Item {

  private final TabFolder parent;
  private Control control;

  public TabItem( final TabFolder parent, final int style ) {
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

  public TabFolder getParent() {
    return parent;
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }

  public Control getControl() {
    return control;
  }

  public void setControl( final Control control ) {
    if( control != null ) {
      if( control.getParent() != parent ) {
        String msg = "The control has not the same tabfolder-parent as "
                     + "this tabitem.";
        throw new IllegalArgumentException( msg );
      }
    }
    this.control = control;
  }
  
  public void setImage ( final Image image ) {
    int index = parent.indexOf (this);
    if (index > -1) {
      super.setImage (image);
    }
  }
  
  ///////////////////////////////////
  // Methods to dispose of the widget

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
  }
}
