/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


/**
 * Class <code>GC</code> is provided to ease single-sourcing SWT and RWT code.
 * Its text measurement methods directly delegate to the respective 
 * <code>Graphics</code> methods.
 * <!-- 
 * Class <code>GC</code> is where all of the drawing capabilities that are 
 * supported by SWT are located. Instances are used to draw on either an 
 * <code>Image</code>, a <code>Control</code>, or directly on a <code>Display</code>.
 * -->
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT_TO_RIGHT <!--, RIGHT_TO_LEFT --></dd>
 * </dl>
 * 
 * <!--
 * <p>
 * The SWT drawing coordinate system is the two-dimensional space with the origin
 * (0,0) at the top left corner of the drawing area and with (x,y) values increasing
 * to the right and downward respectively.
 * </p>
 * 
 * <p>
 * The result of drawing on an image that was created with an indexed
 * palette using a color that is not in the palette is platform specific.
 * Some platforms will match to the nearest color while other will draw
 * the color itself. This happens because the allocated image might use
 * a direct palette on platforms that do not support indexed palette.
 * </p>
 * -->
 * 
 * <p>
 * Application code must explicitly invoke the <code>GC.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required. <!-- This is <em>particularly</em>
 * important on Windows95 and Windows98 where the operating system has a limited
 * number of device contexts available. -->
 * </p>
 * 
 * <!--
 * <p>
 * Note: Only one of LEFT_TO_RIGHT and RIGHT_TO_LEFT may be specified.
 * </p>
 *
 * @see org.eclipse.swt.events.PaintEvent
 * @see <a href="http://www.eclipse.org/swt/snippets/#gc">GC snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Examples: GraphicsExample, PaintExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * -->
 * @since 1.3
 */
public class GC extends Resource {

  private Font font;

  /**  
   * Constructs a new instance of this class which has been
   * configured to draw on the specified drawable. Sets the
   * foreground color, background color and font in the GC
   * to match those in the drawable.
   * <p>
   * You must dispose the graphics context when it is no longer required. 
   * </p>
   * @param drawable the drawable to draw on
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the drawable is null</li>
   *    <li>ERROR_NULL_ARGUMENT - if there is no current device</li>
   *    <li>ERROR_INVALID_ARGUMENT
   *          - if the drawable is an image that is not a bitmap or an icon
   *          - if the drawable is an image or printer that is already selected
   *            into another graphics context</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES if a handle could not be obtained for GC creation</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS if not called from the thread that created the drawable</li>
   * </ul>
   */
  public GC( final Drawable drawable ) {
    super( determineDevice( drawable ) );
    if( drawable == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    font = determineFont( drawable );
  }

  /** 
   * Sets the font which will be used by the receiver
   * to draw and measure text to the argument. If the
   * argument is null, then a default font appropriate
   * for the platform will be used instead.
   *
   * @param font the new font for the receiver, or null to indicate a default font
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the font has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setFont( Font font ) {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    this.font = font;
  }

  /** 
   * Returns the font currently being used by the receiver
   * to draw and measure text.
   *
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Font getFont() {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    return font;
  }

  /**
   * Returns the width of the specified character in the font
   * selected into the receiver. 
   * <p>
   * The width is defined as the space taken up by the actual
   * character, not including the leading and tailing whitespace
   * or overhang.
   * </p>
   *
   * @param ch the character to measure
   * @return the width of the character
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public int getCharWidth( final char ch ) {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    return Graphics.stringExtent( font, Character.toString( ch ) ).x;
  }

  /**
   * Returns the extent of the given string. No tab
   * expansion or carriage return processing will be performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of
   * the rectangular area it would cover if drawn in a particular
   * font (in this case, the current font in the receiver).
   * </p>
   *
   * @param string the string to measure
   * @return a point containing the extent of the string
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Point stringExtent( final String string ) {
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    return Graphics.stringExtent( font, string );
  }
  
  /**
   * Returns the extent of the given string. Tab expansion and
   * carriage return processing are performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of
   * the rectangular area it would cover if drawn in a particular
   * font (in this case, the current font in the receiver).
   * </p>
   *
   * @param string the string to measure
   * @return a point containing the extent of the string
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Point textExtent( final String string ) {
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    return Graphics.textExtent( font, string, 0 );
  }

  /**
   * Returns a FontMetrics which contains information
   * about the font currently being used by the receiver
   * to draw and measure text.
   *
   * @return font metrics for the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public FontMetrics getFontMetrics() {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    return new FontMetrics( font );
  }

  private static Device determineDevice( final Drawable drawable ) {
    Device result = null;
    if( drawable instanceof Control ) {
      result = ( ( Control )drawable ).getDisplay();
    } else if( drawable instanceof Device ) {
      result = ( Device )drawable;
    }
    return result;
  }

  private Font determineFont( final Drawable drawable ) {
    Font result = null;
    if( drawable instanceof Control ) {
      result = ( ( Control )drawable ).getFont();
    } else if( drawable instanceof Display ) {
      result = ( ( Display )drawable ).getSystemFont();
    }
    return result;
  }

}
