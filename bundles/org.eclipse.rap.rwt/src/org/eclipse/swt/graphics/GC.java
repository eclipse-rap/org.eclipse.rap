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
import org.eclipse.swt.*;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.graphics.GCOperation.*;
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

  private final Control control;
  private Font font;
  private Color background;
  private Color foreground;
  private int alpha;
  private int lineWidth;
  private int lineCap;
  private int lineJoin;

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
    // Assume that Drawable is either a Control or a Device
    if( drawable instanceof Control ) {
      control = ( Control )drawable;
    } else {
      control = null;
    }
    font = determineFont( drawable );
    background = determineBackground( drawable );
    foreground = determineForeground( drawable );
    alpha = 255;
    lineCap = SWT.CAP_FLAT;
    lineJoin = SWT.JOIN_MITER;
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
  public void setFont( final Font font ) {
    checkDisposed();
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    Font newFont = font != null ? font : getDevice().getSystemFont();
    if( !newFont.equals( this.font ) ) {
      this.font = newFont;
      SetFont operation = new SetFont( this.font );
      addGCOperation( operation );
    }
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
    checkDisposed();
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
    checkDisposed();
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
    checkDisposed();
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
    checkDisposed();
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
    checkDisposed();
    return new FontMetrics( font );
  }

  /**
   * Sets the background color. The background color is used
   * for fill operations and as the background color when text
   * is drawn.
   *
   * @param color the new background color for the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the color is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the color has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setBackground( final Color color ) {
    checkDisposed();
    if( color == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( color.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( !color.equals( background ) ) {
      background = color;
      SetProperty operation
        = new SetProperty( SetProperty.BACKGROUND, background );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the background color.
   *
   * @return the receiver's background color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Color getBackground() {
    checkDisposed();
    return background;
  }

  /**
   * Sets the foreground color. The foreground color is used
   * for drawing operations including when text is drawn.
   *
   * @param color the new foreground color for the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the color is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the color has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setForeground( final Color color ) {
    checkDisposed();
    if( color == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( color.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( !color.equals( foreground ) ) {
      foreground = color;
      SetProperty operation
        = new SetProperty( SetProperty.FOREGROUND, foreground );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the receiver's foreground color.
   *
   * @return the color used for drawing foreground things
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Color getForeground() {
    checkDisposed();
    return foreground;
  }
  
  /** 
   * Returns the bounding rectangle of the receiver's clipping
   * region. If no clipping region is set, the return value
   * will be a rectangle which covers the entire bounds of the
   * object the receiver is drawing on.
   *
   * @return the bounding rectangle of the clipping region
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Rectangle getClipping() {
    checkDisposed();
    Rectangle result;
    if( control != null ) {
      result = control.getBounds();
    } else {
      result = device.getBounds();
    }
    return result;
  }

  /**
   * Sets the receiver's alpha value which must be
   * between 0 (transparent) and 255 (opaque).
   * <p>
   * This operation requires the operating system's advanced
   * graphics subsystem which may not be available on some
   * platforms.
   * </p>
   * @param alpha the alpha value
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are not available</li>
   * </ul>
   */
  public void setAlpha( final int alpha ) {
    checkDisposed();
    if( alpha >= 0 && alpha <= 255 && this.alpha != alpha ) {
      this.alpha = alpha;
      SetProperty operation
        = new SetProperty( SetProperty.ALPHA, new Integer( alpha ) );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the receiver's alpha value. The alpha value
   * is between 0 (transparent) and 255 (opaque).
   *
   * @return the alpha value
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public int getAlpha() {
    checkDisposed();
    return alpha;
  }

  /**
   * Sets the width that will be used when drawing lines
   * for all of the figure drawing operations (that is,
   * <code>drawLine</code>, <code>drawRectangle</code>,
   * <code>drawPolyline</code>, and so forth.
   *
   * @param lineWidth the width of a line
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setLineWidth( final int lineWidth ) {
    checkDisposed();
    if( this.lineWidth != lineWidth ) {
      this.lineWidth = lineWidth;
      SetProperty operation
        = new SetProperty( SetProperty.LINE_WIDTH, new Integer( lineWidth ) );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the width that will be used when drawing lines
   * for all of the figure drawing operations (that is,
   * <code>drawLine</code>, <code>drawRectangle</code>,
   * <code>drawPolyline</code>, and so forth.
   *
   * @return the receiver's line width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public int getLineWidth() {
    checkDisposed();
    return lineWidth;
  }

  /**
   * Sets the receiver's line cap style to the argument, which must be one
   * of the constants <code>SWT.CAP_FLAT</code>, <code>SWT.CAP_ROUND</code>,
   * or <code>SWT.CAP_SQUARE</code>.
   *
   * @param lineCap the cap style to be used for drawing lines
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the style is not valid</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setLineCap( final int lineCap ) {
    checkDisposed();
    if( this.lineCap != lineCap ) {
      switch( lineCap ) {
        case SWT.CAP_ROUND:
        case SWT.CAP_FLAT:
        case SWT.CAP_SQUARE:
          break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      this.lineCap = lineCap;
      SetProperty operation
        = new SetProperty( SetProperty.LINE_CAP, new Integer( lineCap ) );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the receiver's line cap style, which will be one
   * of the constants <code>SWT.CAP_FLAT</code>, <code>SWT.CAP_ROUND</code>,
   * or <code>SWT.CAP_SQUARE</code>.
   *
   * @return the cap style used for drawing lines
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public int getLineCap() {
    checkDisposed();
    return lineCap;
  }

  /**
   * Sets the receiver's line join style to the argument, which must be one
   * of the constants <code>SWT.JOIN_MITER</code>, <code>SWT.JOIN_ROUND</code>,
   * or <code>SWT.JOIN_BEVEL</code>.
   *
   * @param lineJoin the join style to be used for drawing lines
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the style is not valid</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void setLineJoin( final int lineJoin ) {
    checkDisposed();
    if( this.lineJoin != lineJoin ) {
      switch( lineJoin ) {
        case SWT.JOIN_MITER:
        case SWT.JOIN_ROUND:
        case SWT.JOIN_BEVEL:
          break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      this.lineJoin = lineJoin;
      SetProperty operation
        = new SetProperty( SetProperty.LINE_JOIN, new Integer( lineJoin ) );
      addGCOperation( operation );
    }
  }

  /**
   * Returns the receiver's line join style, which will be one
   * of the constants <code>SWT.JOIN_MITER</code>, <code>SWT.JOIN_ROUND</code>,
   * or <code>SWT.JOIN_BEVEL</code>.
   *
   * @return the join style used for drawing lines
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public int getLineJoin() {
    checkDisposed();
    return lineJoin;
  }

  /**
   * Sets the receiver's line attributes.
   * <p>
   * This operation requires the operating system's advanced
   * graphics subsystem which may not be available on some
   * platforms.
   * </p>
   * @param attributes the line attributes
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the attributes is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if any of the line attributes is not valid</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are not available</li>
   * </ul>
   *
   * @see LineAttributes
   */
  public void setLineAttributes( final LineAttributes attributes ) {
    checkDisposed();
    if( attributes == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setLineWidth( ( int )attributes.width );
    setLineCap( attributes.cap );
    setLineJoin( attributes.join );
  }

  /**
   * Returns the receiver's line attributes.
   *
   * @return the line attributes used for drawing lines
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public LineAttributes getLineAttributes() {
    checkDisposed();
    return new LineAttributes( lineWidth, lineCap, lineJoin );
  }

  /**
   * Draws a line, using the foreground color, between the points
   * (<code>x1</code>, <code>y1</code>) and (<code>x2</code>, <code>y2</code>).
   *
   * @param x1 the first point's x coordinate
   * @param y1 the first point's y coordinate
   * @param x2 the second point's x coordinate
   * @param y2 the second point's y coordinate
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawLine( final int x1, final int y1, final int x2, final int y2 )
  {
    checkDisposed();
    DrawLine operation = new DrawLine( x1, y1, x2, y2 );
    addGCOperation( operation );
  }

  /**
   * Draws the outline of the specified rectangle, using the receiver's
   * foreground color. The left and right edges of the rectangle are at
   * <code>rect.x</code> and <code>rect.x + rect.width</code>. The top
   * and bottom edges are at <code>rect.y</code> and
   * <code>rect.y + rect.height</code>.
   *
   * @param rect the rectangle to draw
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawRectangle( final Rectangle rect ) {
    if( rect == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    drawRectangle( rect.x, rect.y, rect.width, rect.height );
  }

  /**
   * Draws the outline of the rectangle specified by the arguments,
   * using the receiver's foreground color. The left and right edges
   * of the rectangle are at <code>x</code> and <code>x + width</code>.
   * The top and bottom edges are at <code>y</code> and <code>y + height</code>.
   *
   * @param x the x coordinate of the rectangle to be drawn
   * @param y the y coordinate of the rectangle to be drawn
   * @param width the width of the rectangle to be drawn
   * @param height the height of the rectangle to be drawn
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawRectangle( final int x,
                             final int y,
                             final int width,
                             final int height )
  {
    checkDisposed();
    DrawRectangle operation = new DrawRectangle( x, y, width, height, false );
    addGCOperation( operation );
  }

  /**
   * Draws a rectangle, based on the specified arguments, which has
   * the appearance of the platform's <em>focus rectangle</em> if the
   * platform supports such a notion, and otherwise draws a simple
   * rectangle in the receiver's foreground color.
   *
   * @param x the x coordinate of the rectangle
   * @param y the y coordinate of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawRectangle(int, int, int, int)
   */
  public void drawFocus( final int x,
                         final int y,
                         final int width,
                         final int height )
  {
    drawRectangle( x, y, width, height );
  }

  /**
   * Fills the interior of the specified rectangle, using the receiver's
   * background color.
   *
   * @param rect the rectangle to be filled
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawRectangle(int, int, int, int)
   */
  public void fillRectangle( final Rectangle rect ) {
    if( rect == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    fillRectangle( rect.x, rect.y, rect.width, rect.height );
  }

  /**
   * Fills the interior of the rectangle specified by the arguments,
   * using the receiver's background color.
   *
   * @param x the x coordinate of the rectangle to be filled
   * @param y the y coordinate of the rectangle to be filled
   * @param width the width of the rectangle to be filled
   * @param height the height of the rectangle to be filled
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawRectangle(int, int, int, int)
   */
  public void fillRectangle( final int x,
                             final int y,
                             final int width,
                             final int height )
  {
    checkDisposed();
    DrawRectangle operation = new DrawRectangle( x, y, width, height, true );
    addGCOperation( operation );
  }

  /**
   * Fills the interior of the specified rectangle with a gradient
   * sweeping from left to right or top to bottom progressing
   * from the receiver's foreground color to its background color.
   *
   * @param x the x coordinate of the rectangle to be filled
   * @param y the y coordinate of the rectangle to be filled
   * @param width the width of the rectangle to be filled, may be negative
   *        (inverts direction of gradient if horizontal)
   * @param height the height of the rectangle to be filled, may be negative
   *        (inverts direction of gradient if vertical)
   * @param vertical if true sweeps from top to bottom, else
   *        sweeps from left to right
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawRectangle(int, int, int, int)
   */
  public void fillGradientRectangle( final int x,
                                     final int y,
                                     final int width,
                                     final int height,
                                     final boolean vertical )
  {
    checkDisposed();
    if( background.equals( foreground  ) ) {
      fillRectangle( x, y, width, height );
    } else {
      FillGradientRectangle operation
        = new FillGradientRectangle( x, y, width, height, vertical );
      addGCOperation( operation );
    }
  }

  /**
   * Draws the outline of the round-cornered rectangle specified by
   * the arguments, using the receiver's foreground color. The left and
   * right edges of the rectangle are at <code>x</code> and <code>x + width</code>.
   * The top and bottom edges are at <code>y</code> and <code>y + height</code>.
   * The <em>roundness</em> of the corners is specified by the
   * <code>arcWidth</code> and <code>arcHeight</code> arguments, which
   * are respectively the width and height of the ellipse used to draw
   * the corners.
   *
   * @param x the x coordinate of the rectangle to be drawn
   * @param y the y coordinate of the rectangle to be drawn
   * @param width the width of the rectangle to be drawn
   * @param height the height of the rectangle to be drawn
   * @param arcWidth the width of the arc
   * @param arcHeight the height of the arc
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawRoundRectangle( final int x,
                                  final int y,
                                  final int width,
                                  final int height,
                                  final int arcWidth,
                                  final int arcHeight )
  {
    checkDisposed();
    DrawRoundRectangle operation = new DrawRoundRectangle( x,
                                                           y,
                                                           width,
                                                           height,
                                                           arcWidth,
                                                           arcHeight,
                                                           false );
    addGCOperation( operation );
  }

  /**
   * Fills the interior of the round-cornered rectangle specified by
   * the arguments, using the receiver's background color.
   *
   * @param x the x coordinate of the rectangle to be filled
   * @param y the y coordinate of the rectangle to be filled
   * @param width the width of the rectangle to be filled
   * @param height the height of the rectangle to be filled
   * @param arcWidth the width of the arc
   * @param arcHeight the height of the arc
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawRoundRectangle
   */
  public void fillRoundRectangle( final int x,
                                  final int y,
                                  final int width,
                                  final int height,
                                  final int arcWidth,
                                  final int arcHeight )
  {
    checkDisposed();
    DrawRoundRectangle operation = new DrawRoundRectangle( x,
                                                           y,
                                                           width,
                                                           height,
                                                           arcWidth,
                                                           arcHeight,
                                                           true );
    addGCOperation( operation );
  }

  /**
   * Draws the outline of an oval, using the foreground color,
   * within the specified rectangular area.
   * <p>
   * The result is a circle or ellipse that fits within the
   * rectangle specified by the <code>x</code>, <code>y</code>,
   * <code>width</code>, and <code>height</code> arguments.
   * </p><p>
   * The oval covers an area that is <code>width + 1</code>
   * pixels wide and <code>height + 1</code> pixels tall.
   * </p>
   *
   * @param x the x coordinate of the upper left corner of the oval to be drawn
   * @param y the y coordinate of the upper left corner of the oval to be drawn
   * @param width the width of the oval to be drawn
   * @param height the height of the oval to be drawn
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawOval( final int x,
                        final int y,
                        final int width,
                        final int height )
  {
    checkDisposed();
    DrawArc operation = new DrawArc( x, y, width, height, 0, 360, false );
    addGCOperation( operation );
  }

  /**
   * Fills the interior of an oval, within the specified
   * rectangular area, with the receiver's background
   * color.
   *
   * @param x the x coordinate of the upper left corner of the oval to be filled
   * @param y the y coordinate of the upper left corner of the oval to be filled
   * @param width the width of the oval to be filled
   * @param height the height of the oval to be filled
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawOval
   */
  public void fillOval( final int x,
                        final int y,
                        final int width,
                        final int height )
  {
    checkDisposed();
    DrawArc operation = new DrawArc( x, y, width, height, 0, 360, true );
    addGCOperation( operation );
  }

  /**
   * Draws the outline of a circular or elliptical arc
   * within the specified rectangular area.
   * <p>
   * The resulting arc begins at <code>startAngle</code> and extends
   * for <code>arcAngle</code> degrees, using the current color.
   * Angles are interpreted such that 0 degrees is at the 3 o'clock
   * position. A positive value indicates a counter-clockwise rotation
   * while a negative value indicates a clockwise rotation.
   * </p><p>
   * The center of the arc is the center of the rectangle whose origin
   * is (<code>x</code>, <code>y</code>) and whose size is specified by the
   * <code>width</code> and <code>height</code> arguments.
   * </p><p>
   * The resulting arc covers an area <code>width + 1</code> pixels wide
   * by <code>height + 1</code> pixels tall.
   * </p>
   *
   * @param x the x coordinate of the upper-left corner of the arc to be drawn
   * @param y the y coordinate of the upper-left corner of the arc to be drawn
   * @param width the width of the arc to be drawn
   * @param height the height of the arc to be drawn
   * @param startAngle the beginning angle
   * @param arcAngle the angular extent of the arc, relative to the start angle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawArc( final int x,
                       final int y,
                       final int width,
                       final int height,
                       final int startAngle,
                       final int arcAngle )
  {
    checkDisposed();
    DrawArc operation
      = new DrawArc( x, y, width, height, startAngle, arcAngle, false );
    addGCOperation( operation );
  }

  /**
   * Fills the interior of a circular or elliptical arc within
   * the specified rectangular area, with the receiver's background
   * color.
   * <p>
   * The resulting arc begins at <code>startAngle</code> and extends
   * for <code>arcAngle</code> degrees, using the current color.
   * Angles are interpreted such that 0 degrees is at the 3 o'clock
   * position. A positive value indicates a counter-clockwise rotation
   * while a negative value indicates a clockwise rotation.
   * </p><p>
   * The center of the arc is the center of the rectangle whose origin
   * is (<code>x</code>, <code>y</code>) and whose size is specified by the
   * <code>width</code> and <code>height</code> arguments.
   * </p><p>
   * The resulting arc covers an area <code>width + 1</code> pixels wide
   * by <code>height + 1</code> pixels tall.
   * </p>
   *
   * @param x the x coordinate of the upper-left corner of the arc to be filled
   * @param y the y coordinate of the upper-left corner of the arc to be filled
   * @param width the width of the arc to be filled
   * @param height the height of the arc to be filled
   * @param startAngle the beginning angle
   * @param arcAngle the angular extent of the arc, relative to the start angle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawArc
   */
  public void fillArc( final int x,
                       final int y,
                       final int width,
                       final int height,
                       final int startAngle,
                       final int arcAngle )
  {
    checkDisposed();
    DrawArc operation
      = new DrawArc( x, y, width, height, startAngle, arcAngle, true );
    addGCOperation( operation );
  }

  /**
   * Draws the closed polygon which is defined by the specified array
   * of integer coordinates, using the receiver's foreground color. The array
   * contains alternating x and y values which are considered to represent
   * points which are the vertices of the polygon. Lines are drawn between
   * each consecutive pair, and between the first pair and last pair in the
   * array.
   *
   * @param pointArray an array of alternating x and y values which are the vertices of the polygon
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawPolygon( final int[] pointArray ) {
    checkDisposed();
    DrawPolyline operation = new DrawPolyline( pointArray, true, false );
    addGCOperation( operation );
  }

  /**
   * Fills the interior of the closed polygon which is defined by the
   * specified array of integer coordinates, using the receiver's
   * background color. The array contains alternating x and y values
   * which are considered to represent points which are the vertices of
   * the polygon. Lines are drawn between each consecutive pair, and
   * between the first pair and last pair in the array.
   *
   * @param pointArray an array of alternating x and y values which are the vertices of the polygon
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #drawPolygon
   */
  public void fillPolygon( final int[] pointArray ) {
    checkDisposed();
    DrawPolyline operation = new DrawPolyline( pointArray, true, true );
    addGCOperation( operation );
  }

  /**
   * Draws the polyline which is defined by the specified array
   * of integer coordinates, using the receiver's foreground color. The array
   * contains alternating x and y values which are considered to represent
   * points which are the corners of the polyline. Lines are drawn between
   * each consecutive pair, but not between the first pair and last pair in
   * the array.
   *
   * @param pointArray an array of alternating x and y values which are the corners of the polyline
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point array is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawPolyline( final int[] pointArray ) {
    checkDisposed();
    DrawPolyline operation = new DrawPolyline( pointArray, false, false );
    addGCOperation( operation );
  }

  /**
   * Draws a pixel, using the foreground color, at the specified
   * point (<code>x</code>, <code>y</code>).
   * <p>
   * Note that the receiver's line attributes do not affect this
   * operation.
   * </p>
   *
   * @param x the point's x coordinate
   * @param y the point's y coordinate
   *
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawPoint( final int x, final int y ) {
    checkDisposed();
    DrawPoint operation = new DrawPoint( x, y );
    addGCOperation( operation );
  }

  /**
   * Draws the given image in the receiver at the specified
   * coordinates.
   *
   * @param image the image to draw
   * @param x the x coordinate of where to draw
   * @param y the y coordinate of where to draw
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the image is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the given coordinates are outside the bounds of the image</li>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES - if no handles are available to perform the operation</li>
   * </ul>
   */
  public void drawImage( final Image image, final int x, final int y) {
    checkDisposed();
    if( image == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( image.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    DrawImage operation
      = new DrawImage( image, 0, 0, -1, -1, x, y, -1, -1, true );
    addGCOperation( operation );
  }

  /**
   * Copies a rectangular area from the source image into a (potentially
   * different sized) rectangular area in the receiver. If the source
   * and destination areas are of differing sizes, then the source
   * area will be stretched or shrunk to fit the destination area
   * as it is copied. The copy fails if any part of the source rectangle
   * lies outside the bounds of the source image, or if any of the width
   * or height arguments are negative.
   *
   * @param image the source image
   * @param srcX the x coordinate in the source image to copy from
   * @param srcY the y coordinate in the source image to copy from
   * @param srcWidth the width in pixels to copy from the source
   * @param srcHeight the height in pixels to copy from the source
   * @param destX the x coordinate in the destination to copy to
   * @param destY the y coordinate in the destination to copy to
   * @param destWidth the width in pixels of the destination rectangle
   * @param destHeight the height in pixels of the destination rectangle
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the image is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   *    <li>ERROR_INVALID_ARGUMENT - if any of the width or height arguments are negative.
   *    <li>ERROR_INVALID_ARGUMENT - if the source rectangle is not contained within the bounds of the source image</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES - if no handles are available to perform the operation</li>
   * </ul>
   */
  public void drawImage( final Image image,
                         final int srcX,
                         final int srcY,
                         final int srcWidth,
                         final int srcHeight,
                         final int destX,
                         final int destY,
                         final int destWidth,
                         final int destHeight )
  {
    checkDisposed();
    if( srcWidth != 0 && srcHeight != 0 && destWidth != 0 && destHeight != 0 ) {
      if(    srcX < 0
          || srcY < 0
          || srcWidth < 0
          || srcHeight < 0
          || destWidth < 0
          || destHeight < 0 )
      {
        SWT.error (SWT.ERROR_INVALID_ARGUMENT);
      }
      if( image == null ) {
        SWT.error( SWT.ERROR_NULL_ARGUMENT );
      }
      if( image.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      DrawImage operation = new DrawImage( image,
                                           srcX,
                                           srcY,
                                           srcWidth,
                                           srcHeight,
                                           destX,
                                           destY,
                                           destWidth,
                                           destHeight,
                                           false );
      addGCOperation( operation );
    }
  }

  /**
   * Draws the given string, using the receiver's current font and
   * foreground color. No tab expansion or carriage return processing
   * will be performed. The background of the rectangular area where
   * the string is being drawn will be filled with the receiver's
   * background color.
   *
   * @param string the string to be drawn
   * @param x the x coordinate of the top left corner of the rectangular area where the string is to be drawn
   * @param y the y coordinate of the top left corner of the rectangular area where the string is to be drawn
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawString( final String string, final int x, final int y ) {
    drawString( string, x, y, false );
  }

  /**
   * Draws the given string, using the receiver's current font and
   * foreground color. No tab expansion or carriage return processing
   * will be performed. If <code>isTransparent</code> is <code>true</code>,
   * then the background of the rectangular area where the string is being
   * drawn will not be modified, otherwise it will be filled with the
   * receiver's background color.
   *
   * @param string the string to be drawn
   * @param x the x coordinate of the top left corner of the rectangular area where the string is to be drawn
   * @param y the y coordinate of the top left corner of the rectangular area where the string is to be drawn
   * @param isTransparent if <code>true</code> the background will be transparent, otherwise it will be opaque
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawString( final String string,
                          final int x,
                          final int y,
                          final boolean isTransparent )
  {
    int flags = isTransparent ? SWT.DRAW_TRANSPARENT : SWT.NONE;
    drawText( string, x, y, flags );
  }

  /**
   * Draws the given string, using the receiver's current font and
   * foreground color. Tab expansion and carriage return processing
   * are performed. The background of the rectangular area where
   * the text is being drawn will be filled with the receiver's
   * background color.
   *
   * @param string the string to be drawn
   * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
   * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawText( final String string, final int x, final int y ) {
    drawText( string, x, y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB );
  }

  /**
   * Draws the given string, using the receiver's current font and
   * foreground color. Tab expansion and carriage return processing
   * are performed. If <code>isTransparent</code> is <code>true</code>,
   * then the background of the rectangular area where the text is being
   * drawn will not be modified, otherwise it will be filled with the
   * receiver's background color.
   *
   * @param string the string to be drawn
   * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
   * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
   * @param isTransparent if <code>true</code> the background will be transparent, otherwise it will be opaque
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawText( final String string,
                        final int x,
                        final int y,
                        final boolean isTransparent )
  {
    int flags = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;
    if( isTransparent ) {
      flags |= SWT.DRAW_TRANSPARENT;
    }
    drawText( string, x, y, flags );
  }

  /**
   * Draws the given string, using the receiver's current font and
   * foreground color. Tab expansion, line delimiter and mnemonic
   * processing are performed according to the specified flags. If
   * <code>flags</code> includes <code>DRAW_TRANSPARENT</code>,
   * then the background of the rectangular area where the text is being
   * drawn will not be modified, otherwise it will be filled with the
   * receiver's background color.
   * <p>
   * The parameter <code>flags</code> may be a combination of:
   * <dl>
   * <dt><b>DRAW_DELIMITER</b></dt>
   * <dd>draw multiple lines</dd>
   * <dt><b>DRAW_TAB</b></dt>
   * <dd>expand tabs</dd>
   * <dt><b>DRAW_MNEMONIC</b></dt>
   * <dd>underline the mnemonic character</dd>
   * <dt><b>DRAW_TRANSPARENT</b></dt>
   * <dd>transparent background</dd>
   * </dl>
   * </p>
   *
   * @param string the string to be drawn
   * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
   * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
   * @param flags the flags specifying how to process the text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public void drawText( final String string,
                        final int x,
                        final int y,
                        final int flags )
  {
    checkDisposed();
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( string.length() != 0 ) {
      DrawText operation = new DrawText( string, x, y, flags );
      addGCOperation( operation );
    }
  }

  private void checkDisposed() {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
  }

  GCAdapter getGCAdapter() {
    GCAdapter result = null;
    if( control != null ) {
      result = ( GCAdapter )control.getAdapter( IGCAdapter.class );
    }
    return result;
  }

  private void addGCOperation( final GCOperation operation ) {
    GCAdapter adapter = getGCAdapter();
    if( adapter != null ) {
      adapter.addGCOperation( operation );
    }
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

  private static Font determineFont( final Drawable drawable ) {
    Font result = null;
    if( drawable instanceof Control ) {
      result = ( ( Control )drawable ).getFont();
    } else if( drawable instanceof Display ) {
      result = ( ( Display )drawable ).getSystemFont();
    }
    return result;
  }

  private static Color determineBackground( final Drawable drawable ) {
    Color result = null;
    if( drawable instanceof Control ) {
      result = ( ( Control )drawable ).getBackground();
    } else if( drawable instanceof Display ) {
      result = ( ( Display )drawable ).getSystemColor( SWT.COLOR_WHITE );
    }
    return result;
  }

  private static Color determineForeground( final Drawable drawable ) {
    Color result = null;
    if( drawable instanceof Control ) {
      result = ( ( Control )drawable ).getForeground();
    } else if( drawable instanceof Display ) {
      result = ( ( Display )drawable ).getSystemColor( SWT.COLOR_BLACK );
    }
    return result;
  }
}
