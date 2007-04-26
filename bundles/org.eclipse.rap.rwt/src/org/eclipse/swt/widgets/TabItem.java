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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.ItemHolder;

public class TabItem extends Item {

  private final TabFolder parent;
  private Control control;

  public TabItem( final TabFolder parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, parent.getItemCount() );
  }

  public TabItem( final TabFolder parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, index );
  }
  
  public TabFolder getParent() {
    checkWidget();
    return parent;
  }

  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  public Control getControl() {
    checkWidget();
    return control;
  }

  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        SWT.error( SWT.ERROR_INVALID_PARENT );
      }
    }
    this.control = control;
  }
  
  public void setImage( final Image image ) {
    checkWidget();
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

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    int result = SWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }
}
