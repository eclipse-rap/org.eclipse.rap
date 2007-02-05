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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;


public class ToolItem extends Item {

  private final ToolBar parent;
  private boolean selected;
  private Control control;
  private int width = 0;
 

  public ToolItem( final ToolBar parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }

  public ToolItem( final ToolBar parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.insertItem( parent, this, index );
  }
  
  public Display getDisplay() {
    return parent.getDisplay();
  }

  public ToolBar getParent () {
    return parent;
  }
  
  public void setText( final String text ) {
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      super.setText( text );
    }
  }
  
  public void setControl( final Control control ) {
    if( control != null ) {
      if( control.isDisposed() ) {
        RWT.error( RWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        RWT.error( RWT.ERROR_INVALID_PARENT );
      }
    }
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      this.control = control;
    }
  }

  public Control getControl() {
    checkWidget();
    return control;
  }

  public int getWidth() {
    checkWidget();
    return width;
  }

  public void setWidth( final int width ) {
    checkWidget();
    if( ( style & RWT.SEPARATOR ) != 0 && width >= 0  ) {
      this.width = width;
    }
  }
  
  public boolean getSelection() {
    checkWidget();
    boolean result = selected;
    if( ( style & ( RWT.CHECK | RWT.RADIO ) ) == 0 ) {
      result = false;
    }
    return result;
  }
  
  public void setSelection( final boolean selected ) {
    checkWidget();
    if( ( style & ( RWT.CHECK | RWT.RADIO ) ) != 0 ) {
      this.selected = selected;
    }
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    if( image == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      super.setImage( image );
    }
  }
  
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }
  
  ///////////////////////////////////
  // Methods to dispose of the widget
  
  protected void releaseChildren() {
    // do nothing
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
    // do nothing
  }
  
  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    return checkBits( style, 
                      RWT.PUSH, 
                      RWT.CHECK,
                      RWT.RADIO, 
                      RWT.SEPARATOR, 
                      RWT.DROP_DOWN,
                      0 );
  }
  
}
