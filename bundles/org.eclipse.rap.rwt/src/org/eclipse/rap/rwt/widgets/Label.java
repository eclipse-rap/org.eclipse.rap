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
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;

/**
 * A Label can display a text or an image, but not both. The label always
 * displays those of both values that has been set last.
 * <p>
 * <strong>Note:</strong> Unlike in SWT, setting an image clears the text of the
 * label and vice versa. Thus, after calling <code>setText()</code>, the method
 * <code>getImage()</code> will return <code>null</code>, and after calling
 * <code>setImage()</code>, <code>getText</code> will return the empty string.
 * </p>
 */
// TODO [rh] check what should happen with style == SEPARATOR and setForeground
public class Label extends Control {

  private String text = "";
  private Image image;

  public Label( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      this.text = text;
      image = null;
    }
  }

  public String getText() {
    checkWidget();
    return text;
  }
  
  /**
   * <p>The LCA does not yet handle images. So, setting an image currently 
   * does not have any effect.</p>
   */
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      this.image = image;
      text = "";
    }
  }
  
  public Image getImage() {
    checkWidget();
    return image;
  }

  public void setAlignment( final int alignment ) {
    checkWidget();
    if(    ( style & RWT.SEPARATOR ) == 0 
        && ( alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER ) ) != 0 ) 
    {
      style &= ~( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
      style |= alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
    }
  }

  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = 0;
    } else if( ( style & RWT.LEFT ) != 0 ) {
      result = RWT.LEFT;
    } else if( ( style & RWT.CENTER ) != 0 ) {
      result = RWT.CENTER;
    } else if( ( style & RWT.RIGHT ) != 0 ) {
      result = RWT.RIGHT;
    } else {
      result = RWT.LEFT;
    }
    return result;
  }
  
  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    int width = 0, height = 0, border = getBorderWidth();
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      int lineWidth = 2;
      if( ( style & RWT.HORIZONTAL ) != 0 ) {
        width = DEFAULT_WIDTH;
        height = lineWidth;
      } else {
        width = lineWidth;
        height = DEFAULT_HEIGHT;
      }
    } else if( ( image != null ) ) {
      Rectangle rect = image.getBounds();
      width = rect.width;
      height = rect.height;
    } else if( ( text.length() > 0 ) ) {
      int wrapWidth = 0;
      if( ( style & RWT.WRAP ) != 0 && wHint != RWT.DEFAULT ) {
        wrapWidth = wHint;
      }
      Point extent = FontSizeEstimation.textExtent( text, wrapWidth, getFont() );
      width = extent.x + 8;
      height = extent.y + 2;
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
  
  public int getBorderWidth() {
    return ( ( style & RWT.BORDER ) != 0 ) ? 1 : 0;
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style;
    result |= RWT.NO_FOCUS;
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = checkBits( result, RWT.VERTICAL, RWT.HORIZONTAL, 0, 0, 0, 0 );
      result = checkBits ( result, 
                           RWT.SHADOW_OUT, 
                           RWT.SHADOW_IN, 
                           RWT.SHADOW_NONE, 
                           0, 
                           0, 
                           0 );
    }
    result = checkBits( result, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    return result;
  }
}
