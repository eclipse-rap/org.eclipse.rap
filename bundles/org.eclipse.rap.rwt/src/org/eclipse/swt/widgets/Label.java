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
// TODO [doc] Extend javadoc of label
// TODO [rh] check what should happen with style == SEPARATOR and setForeground
public class Label extends Control {

  private String text = "";
  private Image image;

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#SEPARATOR
   * @see SWT#HORIZONTAL
   * @see SWT#VERTICAL
   * @see SWT#SHADOW_IN
   * @see SWT#SHADOW_OUT
   * @see SWT#SHADOW_NONE
   * @see SWT#CENTER
   * @see SWT#LEFT
   * @see SWT#RIGHT
   * @see SWT#WRAP
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Label( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  /**
   * Sets the receiver's text.
   * <p>
   * This method sets the widget label.  The label may include
   * the mnemonic character and line delimiters.
   * </p>
   * <p>
   * Mnemonics are indicated by an '&amp;' that causes the next
   * character to be the mnemonic.  When the user presses a
   * key sequence that matches the mnemonic, focus is assigned
   * to the control that follows the label. On most platforms,
   * the mnemonic appears underlined but may be emphasised in a
   * platform specific manner.  The mnemonic indicator character
   * '&amp;' can be escaped by doubling it in the string, causing
   * a single '&amp;' to be displayed.
   * </p>
   * 
   * @param string the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
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

  /**
   * Returns the receiver's text, which will be an empty
   * string if it has never been set or if the receiver is
   * a <code>SEPARATOR</code> label.
   *
   * @return the receiver's text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText() {
    checkWidget();
    return text;
  }
  
  /**
   * Sets the receiver's image to the argument, which may be
   * null indicating that no image should be displayed.
   *
   * @param image the image to display on the receiver (may be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO: The LCA does not yet handle images. So, setting an image currently 
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & SWT.SEPARATOR ) == 0 ) {
      this.image = image;
      text = "";
    }
  }
  
  /**
   * Returns the receiver's image if it has one, or null
   * if it does not.
   *
   * @return the receiver's image
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Image getImage() {
    checkWidget();
    return image;
  }

  /**
   * Controls how text and images will be displayed in the receiver.
   * The argument should be one of <code>LEFT</code>, <code>RIGHT</code>
   * or <code>CENTER</code>.  If the receiver is a <code>SEPARATOR</code>
   * label, the argument is ignored and the alignment is not changed.
   *
   * @param alignment the new alignment 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setAlignment( final int alignment ) {
    checkWidget();
    if(    ( style & SWT.SEPARATOR ) == 0 
        && ( alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER ) ) != 0 ) 
    {
      style &= ~( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
      style |= alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    }
  }

  /**
   * Returns a value which describes the position of the
   * text or image in the receiver. The value will be one of
   * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
   * unless the receiver is a <code>SEPARATOR</code> label, in 
   * which case, <code>NONE</code> is returned.
   *
   * @return the alignment 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
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
