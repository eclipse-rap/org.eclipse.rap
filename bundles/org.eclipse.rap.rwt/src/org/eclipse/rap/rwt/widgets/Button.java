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
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;

/**
 * TODO: [fappel] comment
 */
public class Button extends Control {

  private static final int MARGIN = 4;
  // Width of checkboxes and radiobuttons
  private static final int CHECK_WIDTH = 13;
  // Height of checkboxes and radiobuttons
  private static final int CHECK_HEIGHT = 13;

  private String text = "";
  private boolean selected;
  private Image image;
  private boolean isDefault;

  public Button( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  ////////////////
  // Getter/setter
  
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.ARROW ) == 0 ) {
      this.text = text;
    }
  }

  public String getText() {
    checkWidget();
    return text;
  }

  public boolean getSelection() {
    checkWidget();
    boolean result = false;
    if( ( style & ( RWT.CHECK | RWT.RADIO | RWT.TOGGLE ) ) != 0 ) {
      result = selected;
    }
    return result;
  }
  
  public void setSelection( final boolean selected ) {
    checkWidget();
    if( ( style & ( RWT.CHECK | RWT.RADIO | RWT.TOGGLE ) ) != 0 ) {
      this.selected = selected;
    }
  }
  
  public Image getImage() {
    checkWidget();
    return image;
  }

  // TODO [rh] implement handling of images for CHECK and RADIO as SWT does
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & RWT.ARROW ) == 0 ) {
      this.image = image;
    }
  }
  
  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & RWT.ARROW ) != 0 ) {
      if( ( style & RWT.UP ) != 0 ) {
        result = RWT.UP;
      } else if( ( style & RWT.DOWN ) != 0 ) {
        result = RWT.DOWN;
      } else if( ( style & RWT.LEFT ) != 0 ) {
        result = RWT.LEFT;
      } else if( ( style & RWT.RIGHT ) != 0 ) {
        result = RWT.RIGHT;
      } else {
        result = RWT.UP;
      }
    } else {
      if( ( style & RWT.LEFT ) != 0 ) {
        result = RWT.LEFT;
      } else if( ( style & RWT.CENTER ) != 0 ) {
        result = RWT.CENTER;
      } else if( ( style & RWT.RIGHT ) != 0 ) {
        result = RWT.RIGHT;
      } else {
        result = RWT.LEFT;
      }
    }
    return result;
  }
  
  public void setAlignment( final int alignment ) {
    checkWidget();
    if( ( style & RWT.ARROW ) != 0 ) {
      if( ( style & ( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT ) ) != 0 ) {
        style &= ~( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT );
        style |= alignment & ( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT );
      }
    } else if( ( alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER ) ) != 0 ) {
      style &= ~( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
      style |= alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
    }
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0, height = 0, border = getBorderWidth();
    // TODO [rst] Cleanup this part
//    if ((style & RWT.ARROW) != 0) {
//      if ((style & (RWT.UP | RWT.DOWN)) != 0) {
//        width += OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//        height += OS.GetSystemMetrics (OS.SM_CYVSCROLL);
//      } else {
//        width += OS.GetSystemMetrics (OS.SM_CXHSCROLL);
//        height += OS.GetSystemMetrics (OS.SM_CYHSCROLL);
//      }
//      if (wHint != RWT.DEFAULT) width = wHint;
//      if (hHint != RWT.DEFAULT) height = hHint;
//      width += border * 2; height += border * 2;
//      return new Point (width, height);
//    }
    int extra = 0;
    boolean hasImage = image != null;
    boolean hasText = text.length() > 0;
    if( hasImage ) {
      // TODO [rst] Change this as soon as Image.getBounds is implemented.
      // Rectangle rect = image.getBounds ();
      width = 16;
      height = 16;
      extra = MARGIN * 2;
      if( hasText ) {
        width += MARGIN * 2;
      }
    }
    if( hasText ) {
      Point extent = FontSizeEstimation.stringExtent( text, getFont() );
      height = Math.max( height, extent.y );
      width += extent.x;
    }
    if( ( style & ( RWT.CHECK | RWT.RADIO ) ) != 0 ) {
      width += CHECK_WIDTH + 12 + extra;
      height = Math.max( height, CHECK_HEIGHT + 3 );
      height += 4;
    }
    if( ( style & ( RWT.PUSH | RWT.TOGGLE ) ) != 0 ) {
      width += 12;
      height += 10;
    }
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////////////
  // Default Button handling
  
  void setDefault( final boolean isDefault ) {
    this.isDefault = isDefault;
  }
  
  boolean getDefault() {
    return isDefault;
  }
  
  boolean isTabGroup() {
    return true;
  }
  
  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = checkBits( style,
                            RWT.PUSH,
                            RWT.ARROW,
                            RWT.CHECK,
                            RWT.RADIO,
                            RWT.TOGGLE,
                            0 );
    if( ( result & ( RWT.PUSH | RWT.TOGGLE ) ) != 0 ) {
      result = checkBits( result, RWT.CENTER, RWT.LEFT, RWT.RIGHT, 0, 0, 0 );
    } else if( ( result & ( RWT.CHECK | RWT.RADIO ) ) != 0 ) {
      result = checkBits( result, RWT.LEFT, RWT.RIGHT, RWT.CENTER, 0, 0, 0 );
    } else if( ( result & RWT.ARROW ) != 0 ) {
      result |= RWT.NO_FOCUS;
      result = checkBits( result, RWT.UP, RWT.DOWN, RWT.LEFT, RWT.RIGHT, 0, 0 );
    }
    return result;
  }
}
