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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

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
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & SWT.SEPARATOR ) == 0 ) {
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
    if( ( style & SWT.SEPARATOR ) == 0 ) {
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
    if(    ( style & SWT.SEPARATOR ) == 0 
        && ( alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER ) ) != 0 ) 
    {
      style &= ~( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
      style |= alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    }
  }

  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      result = 0;
    } else if( ( style & SWT.LEFT ) != 0 ) {
      result = SWT.LEFT;
    } else if( ( style & SWT.CENTER ) != 0 ) {
      result = SWT.CENTER;
    } else if( ( style & SWT.RIGHT ) != 0 ) {
      result = SWT.RIGHT;
    } else {
      result = SWT.LEFT;
    }
    return result;
  }
  
  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    int width = 0, height = 0, border = getBorderWidth();
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      int lineWidth = 2;
      if( ( style & SWT.HORIZONTAL ) != 0 ) {
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
      if( ( style & SWT.WRAP ) != 0 && wHint != SWT.DEFAULT ) {
        wrapWidth = wHint;
      }
      Point extent = FontSizeEstimation.textExtent( text, wrapWidth, getFont() );
      width = extent.x + 8;
      height = extent.y + 2;
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
  
  public int getBorderWidth() {
    return ( ( style & SWT.BORDER ) != 0 ) ? 1 : 0;
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style;
    result |= SWT.NO_FOCUS;
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      result = checkBits( result, SWT.VERTICAL, SWT.HORIZONTAL, 0, 0, 0, 0 );
      result = checkBits ( result, 
                           SWT.SHADOW_OUT, 
                           SWT.SHADOW_IN, 
                           SWT.SHADOW_NONE, 
                           0, 
                           0, 
                           0 );
    }
    result = checkBits( result, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0 );
    return result;
  }
}
