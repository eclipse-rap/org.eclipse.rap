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
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;


public class ToolItem extends Item {

  private static final int DEFAULT_WIDTH = 24;
  private static final int DEFAULT_HEIGHT = 22;
  private static final int DROP_DOWN_ARROW_WIDTH = 13;

  private final ToolBar parent;
  private boolean selected;
  private Control control;
  private int width;
 

  public ToolItem( final ToolBar parent, final int style ) {
    this( checkParent( parent ), checkStyle( style ), parent.getItemCount() );
  }

  public ToolItem( final ToolBar parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.insertItem( parent, this, index );
    computeInitialWidth();
  }

  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  public ToolBar getParent() {
    checkWidget();
    return parent;
  }

  ////////////////////////////////////////
  // Displayed content (text, image, etc.)
  
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      super.setText( text );
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
  
  public void setControl( final Control control ) {
    checkWidget();
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
    resizeControl();
  }

  public Control getControl() {
    checkWidget();
    return control;
  }
  
  /////////////
  // Dimensions
  
  // TODO [rh] decent implementation for VERTICAL adlignment missing
  public Rectangle getBounds() {
    checkWidget();
    Rectangle clientArea = parent.getClientArea();
    int left = clientArea.x;
    int top = clientArea.y;
    int index = parent.indexOf( this );
    for( int i = 0; i < index; i++ ) {
      left += parent.getItem( i ).getBounds().width;
    }
    return new Rectangle( left, top, getWidth(), DEFAULT_HEIGHT );
  }

  public int getWidth() {
    checkWidget();
    int result;
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = width;
    } else {
      // TODO [rh] must be kept in sync with DefaultAppearanceTheme.js
      result = 7; // approx left + right padding as defined in appearance theme 
      if( getImage() != null ) {
        result += 16; // TODO [rh] replace with actual image width
      }
      String text = getText();
      if( !"".equals( text ) ) {
        Font font = parent.getFont();
        result += 2 + FontSizeEstimation.stringExtent( getText(), font ).x;
      }
      if( ( style & RWT.DROP_DOWN ) != 0 ) {
        result += DROP_DOWN_ARROW_WIDTH;
      }
    }
    return result;
  }

  public void setWidth( final int width ) {
    checkWidget();
    if( ( style & RWT.SEPARATOR ) != 0 && width >= 0 ) {
      this.width = width;
      resizeControl();
    }
  }
  
  ////////////
  // Selection
  
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

  ///////////////////////////////////////////
  // Listener registration and deregistration
  
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

  private void resizeControl() {
    if( control != null && !control.isDisposed() ) {
      Rectangle itemRect = getBounds();
      control.setSize( itemRect.width, itemRect.height );
      // In contrast to SWT, placement is relative to the toolitem. 
      Rectangle rect = control.getBounds();
      int xoff = ( itemRect.width - rect.width ) / 2;
      int yoff = ( itemRect.height - rect.height ) / 2;
      control.setLocation( xoff, yoff );
    }
  }

  private void computeInitialWidth() {
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      width = 8;
    } else {
      width = DEFAULT_WIDTH;
      if( ( style & RWT.DROP_DOWN ) != 0 ) {
        width += DROP_DOWN_ARROW_WIDTH;
      }
    }
  }
  
  private static ToolBar checkParent( final ToolBar parent ) {
    if( parent == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return parent;
  }

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
