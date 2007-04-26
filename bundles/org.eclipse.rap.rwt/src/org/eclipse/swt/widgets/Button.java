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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

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
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & SWT.ARROW ) == 0 ) {
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
    if( ( style & ( SWT.CHECK | SWT.RADIO | SWT.TOGGLE ) ) != 0 ) {
      result = selected;
    }
    return result;
  }
  
  public void setSelection( final boolean selected ) {
    checkWidget();
    if( ( style & ( SWT.CHECK | SWT.RADIO | SWT.TOGGLE ) ) != 0 ) {
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
    if( ( style & SWT.ARROW ) == 0 ) {
      this.image = image;
    }
  }
  
  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & SWT.ARROW ) != 0 ) {
      if( ( style & SWT.UP ) != 0 ) {
        result = SWT.UP;
      } else if( ( style & SWT.DOWN ) != 0 ) {
        result = SWT.DOWN;
      } else if( ( style & SWT.LEFT ) != 0 ) {
        result = SWT.LEFT;
      } else if( ( style & SWT.RIGHT ) != 0 ) {
        result = SWT.RIGHT;
      } else {
        result = SWT.UP;
      }
    } else {
      if( ( style & SWT.LEFT ) != 0 ) {
        result = SWT.LEFT;
      } else if( ( style & SWT.CENTER ) != 0 ) {
        result = SWT.CENTER;
      } else if( ( style & SWT.RIGHT ) != 0 ) {
        result = SWT.RIGHT;
      } else {
        result = SWT.LEFT;
      }
    }
    return result;
  }
  
  public void setAlignment( final int alignment ) {
    checkWidget();
    if( ( style & SWT.ARROW ) != 0 ) {
      if( ( style & ( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT ) ) != 0 ) {
        style &= ~( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT );
        style |= alignment & ( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT );
      }
    } else if( ( alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER ) ) != 0 ) {
      style &= ~( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
      style |= alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    }
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0, height = 0, border = getBorderWidth();
    // TODO [rst] Cleanup this part
//    if ((style & SWT.ARROW) != 0) {
//      if ((style & (SWT.UP | SWT.DOWN)) != 0) {
//        width += OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//        height += OS.GetSystemMetrics (OS.SM_CYVSCROLL);
//      } else {
//        width += OS.GetSystemMetrics (OS.SM_CXHSCROLL);
//        height += OS.GetSystemMetrics (OS.SM_CYHSCROLL);
//      }
//      if (wHint != SWT.DEFAULT) width = wHint;
//      if (hHint != SWT.DEFAULT) height = hHint;
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
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      width += CHECK_WIDTH + 12 + extra;
      height = Math.max( height, CHECK_HEIGHT + 3 );
      height += 4;
    }
    if( ( style & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      width += 12;
      height += 10;
    }
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
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
                            SWT.PUSH,
                            SWT.ARROW,
                            SWT.CHECK,
                            SWT.RADIO,
                            SWT.TOGGLE,
                            0 );
    if( ( result & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      result = checkBits( result, SWT.CENTER, SWT.LEFT, SWT.RIGHT, 0, 0, 0 );
    } else if( ( result & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      result = checkBits( result, SWT.LEFT, SWT.RIGHT, SWT.CENTER, 0, 0, 0 );
    } else if( ( result & SWT.ARROW ) != 0 ) {
      result |= SWT.NO_FOCUS;
      result = checkBits( result, SWT.UP, SWT.DOWN, SWT.LEFT, SWT.RIGHT, 0, 0 );
    }
    return result;
  }
}
