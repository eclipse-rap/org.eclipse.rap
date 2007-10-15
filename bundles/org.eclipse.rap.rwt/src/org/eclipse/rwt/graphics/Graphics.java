/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.graphics;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;


/**
 * This is a helper class for operations with text, fonts, colors and images.
 * As RAP needs to handle with multiple clients simultaneously there are no
 * constructors for these objects to share them between different sessions
 * in order to have a much smaller memory footprint.
 * 
 * @since 1.0
 */
public final class Graphics {

  /**
   * Returns an instance of {@link Color} given an
   * <code>RGB</code> describing the desired red, green and blue values.
   * 
   * @param rgb the RGB values of the desired color
   * @return the color
   * 
   * @see RGB
   * @see Device#getSystemColor
   */
  public static Color getColor( final RGB rgb ) {
    return ResourceFactory.getColor( rgb.red, rgb.green, rgb.blue );
  }

  /**
   * Returns a {@link Color} given the
   * desired red, green and blue values expressed as ints in the range
   * 0 to 255 (where 0 is black and 255 is full brightness).
   * 
   * @param red the amount of red in the color
   * @param green the amount of green in the color
   * @param blue the amount of blue in the color
   * @return the color
   */
  public static Color getColor( final int red, final int green, final int blue )
  {
    return ResourceFactory.getColor( red, green, blue );
  }

  /**
   * Returns a new font given a font data
   * which describes the desired font's appearance.
   * 
   * @param data the {@link FontData} to use
   * @return the font
   */
  public static Font getFont( final FontData data ) {
    return getFont( data.getName(), data.getHeight(), data.getStyle() );
  }

  /**
   * Returns a {@link Font} object given a font name,
   * the height of the desired font in points, and a font
   * style.
   * 
   * @param name the name of the font (must not be null)
   * @param height the font height in points
   * @param style a bit or combination of NORMAL, BOLD, ITALIC
   * @return the font
   */
  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    return ResourceFactory.getFont( name, height, style );
  }

  /**
   * Returns an instance of {@link Image} based on the specified
   * image path. The image has to be on the applications class-path.
   * 
   * @param path the path to the image
   * 
   * @return the image
   */
  public static Image getImage( final String path ) {
    return ResourceFactory.findImage( path );
  }

  /**
   * Returns an instance of {@link Image} based on the specified
   * image path. The image has to be on the applications class-path.
   * Uses the specified classloader to load the image.
   * 
   * @param path the path to the image
   * @param imageLoader the classloader to use
   * 
   * @return the image
   */
  public static Image getImage( final String path,
                                final ClassLoader imageLoader )
  {
    return ResourceFactory.findImage( path, imageLoader );
  }

  
  /**
   * Returns an instance of {@link Image} based on the specified
   * image path. The image will be read from the provided InputStream.
   * 
   * @param path the path to the image
   * @param inputStream the input stream for the image
   * 
   * @return the image
   */
  public static Image getImage( final String path,
                                final InputStream inputStream )
  {
    return ResourceFactory.findImage( path, inputStream );
  }
  
  //////////////////////////
  // Text-Size-Determination
  
  /**
   * Returns the extent of the given string. Tab expansion and carriage return
   * processing are performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of the
   * rectangular area it would cover if drawn in a particular font.
   * </p>
   * 
   * @param font the font for which the result is valid
   * @param string the string to measure
   * @param wrapWidth the maximum width of the text. The text will be wrapped to
   *            match this width. If set to 0, no wrapping will be performed.
   * @return a point containing the extent of the string
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *                </ul>
   */
  public static Point textExtent( final Font font,
                                  final String string,
                                  final int wrapWidth )
  {
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeDetermination.textExtent( font, string, wrapWidth );
  }
  
  /**
   * Returns the extent of the given string. No tab expansion or carriage return
   * processing will be performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of the
   * rectangular area it would cover if drawn in a particular font.
   * </p>
   * 
   * @param font the font for which the result is valid
   * @param string the string to measure
   * @return a point containing the extent of the string
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *                </ul>
   */
  public static Point stringExtent( final Font font, final String string ) {
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeDetermination.stringExtent( font, string );
  }
  
  /**
   * Returns the height of the specified font, measured in pixels.
   * 
   * @param font the font for which the result is valid
   * @return the height of the font
   */
  public static int getCharHeight( final Font font ) {
    return TextSizeDetermination.getCharHeight( font );
  }
  
  /**
   * Returns the average character width of the specified font, measured in
   * pixels.
   * 
   * @param font the font for which the result is valid
   * @return the average character width of the font
   */
  public static float getAvgCharWidth( final Font font ) {
    return TextSizeDetermination.getAvgCharWidth( font );
  }

  private Graphics() {
    // prevent instantiation
  }
}
