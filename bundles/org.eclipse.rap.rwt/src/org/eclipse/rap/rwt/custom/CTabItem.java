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
import org.eclipse.rap.rwt.widgets.*;


public class CTabItem extends Item {

  private final CTabFolder parent;
  private Control control;
  private String toolTipText;
  
  public CTabItem( final CTabFolder parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }
  
  public CTabFolder getParent() {
    return parent;
  }

  public Control getControl() {
    return control;
  }

  public void setControl( final Control control ) {
    if( control != null ) {
      if( control.getParent() != parent ) {
        String msg 
          = "The controls parent must be the same as this items parent.";
        throw new IllegalArgumentException( msg );
      }
    }
    this.control = control;
  }
  
  public void setToolTipText( final String toolTipText ) {
    this.toolTipText = toolTipText;
  }
  
  public String getToolTipText() {
    return toolTipText;
  }
  
  ///////////////////
  // Widget overrides

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    parent.destroyItem( this );
  }

  protected void releaseWidget() {
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
}
