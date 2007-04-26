/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.graphics.IColor;

/**
 * Instances of this class manage the operating system resources that implement
 * SWT's RGB color model. To create a color you can either specify the
 * individual color components as integers in the range 0 to 255 or provide an
 * instance of an <code>RGB</code>.
 * 
 * @see RGB
 */
public class Color {

  /**
   * Holds the color values within one integer.
   */
  private final int colorNr;

  private final static Map palette = new HashMap();
  
  /**
   * Extension of class <code>Color</code> with an additional method that
   * returns a color id to pass to qooxdoo.
   */
  private static class ColorExt extends Color implements IColor {
    private final String colorValue;

    private ColorExt( final int colorNr ) {
      super( colorNr );
      StringBuffer buffer = new StringBuffer();
      buffer.append( "#" );
      append( buffer, Integer.toHexString( getRed() ) );
      append( buffer, Integer.toHexString( getGreen() ) );
      append( buffer, Integer.toHexString( getBlue() ) );
      colorValue = buffer.toString();
    }

    private void append( final StringBuffer buffer, final String value ) {
      if( value.length() == 1  ) {
        buffer.append( "0" );    
      }
      buffer.append( value );
    }

    public String toColorValue() {
      return colorValue;
    }
  }

  /**
   * Prevents uninitialized instances from being created outside the package.
   */
  private Color( final int colorNr ) {
    this.colorNr = colorNr;
  }

  /**
   * Compares the argument to the receiver, and returns true if they represent
   * the <em>same</em> object using a class specific comparison.
   * 
   * @param object the object to compare with this object
   * @return <code>true</code> if the object is the same as this object and
   *         <code>false</code> otherwise
   * @see #hashCode
   */
  public boolean equals( final Object object ) {
    return object == this;
  }

  /**
   * Returns the amount of blue in the color, from 0 to 255.
   * 
   * @return the blue component of the color
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
   *              disposed</li>
   *              </ul>
   */
  public int getBlue() {
    return ( colorNr & 0xFF0000 ) >> 16;
  }

  /**
   * Returns the amount of green in the color, from 0 to 255.
   * 
   * @return the green component of the color
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
   *              disposed</li>
   *              </ul>
   */
  public int getGreen() {
    return ( colorNr & 0xFF00 ) >> 8;
  }

  /**
   * Returns the amount of red in the color, from 0 to 255.
   * 
   * @return the red component of the color
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
   *              disposed</li>
   *              </ul>
   */
  public int getRed() {
    return colorNr & 0xFF;
  }

  /**
   * Returns an <code>RGB</code> representing the receiver.
   * 
   * @return the RGB for the color
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
   *              disposed</li>
   *              </ul>
   */
  public RGB getRGB() {
    return new RGB( getRed(), getGreen(), getBlue() );
  }

  /**
   * Returns an integer hash code for the receiver. Any two objects that return
   * <code>true</code> when passed to <code>equals</code> must return the
   * same value for this method.
   * 
   * @return the receiver's hash
   * @see #equals
   */
  public int hashCode() {
    return colorNr;
  }

  /**
   * Returns a string containing a concise, human-readable description of the
   * receiver.
   * 
   * @return a string representation of the receiver
   */
  public String toString() {
    return "Color {" + getRed() + ", " + getGreen() + ", " + getBlue() + "}";
  }

  public static Color getColor( final int red, 
                                final int green,
                                final int blue )
  {
    if(    red > 255
        || red < 0
        || green > 255
        || green < 0
        || blue > 255
        || blue < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int colorNr = red | ( green << 8 ) | ( blue << 16 );
    return getColor( colorNr );
  }

  public static Color getColor( final RGB rgb ) {
    if( rgb == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return getColor( rgb.red, rgb.green, rgb.blue );
  }

  public static synchronized Color getColor( final int color ) {
    Color result;
    Integer key = new Integer( color );
    if( palette.containsKey( key ) ) {
      result = ( Color )palette.get( key );
    } else {
      result = new ColorExt( color );
      palette.put( key, result );
    }
    return result;
  }
}
