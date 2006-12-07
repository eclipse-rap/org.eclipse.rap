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
import com.w4t.ParamCheck;


public class ToolItem extends Item {

  private final ToolBar parent;
  private boolean selected = false;
  private Control control;
  private int width = 0;
 

  public ToolItem( final ToolBar parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }

  static int checkStyle( final int style ) {
    return checkBits( style, 
                      RWT.PUSH, 
                      RWT.CHECK,
                      RWT.RADIO, 
                      RWT.SEPARATOR, 
                      RWT.DROP_DOWN,
                      0 );
  }
  
  public void setText( final String text ) {
    ParamCheck.notNull( text, "text" );
    if( ( style & RWT.SEPARATOR ) == 0  && !text.equals( this.text ) ) {
      super.setText( text );
    }
  }
  
  public ToolBar getParent () {
    return parent;
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
    return control;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    if( ( style & RWT.SEPARATOR ) != 0 &&  width >= 0  ) {
      this.width = width;
    }
  }
  
  public boolean getSelection() {
    boolean result = selected;
    if( ( style & ( RWT.CHECK | RWT.RADIO /* | RWT.TOGGLE */) ) == 0 ) {
      result = false;
    }
    return result;
  }
  
  public void setSelection( final boolean selected ) {
    if( ( style & ( RWT.CHECK | RWT.RADIO/* | SWT.TOGGLE */) ) != 0 ) {
      this.selected = selected;
    }
  }
  
  public void setImage( final Image image ) {
    if( image == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      super.setImage( image );
    }
  }
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }
  
  public Display getDisplay() {
    return parent.getDisplay();
  }

  
  ///////////////////////////////////
  // Methods to dispose the widget
  
  protected void releaseChildren() {
    // TODO Auto-generated method stub
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
    // TODO Auto-generated method stub
  }
}
