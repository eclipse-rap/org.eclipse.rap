/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Color;


public class QxColor implements QxType {

  private static final String TRANSPARENT_STR = "transparent";

  private static final Map NAMED_COLORS = new HashMap();

  public static final QxColor BLACK = new QxColor( 0, 0, 0, 1f );

  public static final QxColor WHITE = new QxColor( 255, 255, 255, 1f );

  public static final QxColor TRANSPARENT = new QxColor();

  public final int red;

  public final int green;

  public final int blue;
  
  public final float alpha;

  static {
    // register 16 standard HTML colors
    NAMED_COLORS.put( "black", new int[] { 0, 0, 0 } );
    NAMED_COLORS.put( "gray", new int[] { 128, 128, 128 } );
    NAMED_COLORS.put( "silver", new int[] { 192, 192, 192 } );
    NAMED_COLORS.put( "white", new int[] { 255, 255, 255 } );
    NAMED_COLORS.put( "maroon", new int[] { 128, 0, 0 } );
    NAMED_COLORS.put( "red", new int[] { 255, 0, 0 } );
    NAMED_COLORS.put( "purple", new int[] { 128, 0, 128 } );
    NAMED_COLORS.put( "fuchsia", new int[] { 255, 0, 255 } );
    NAMED_COLORS.put( "green", new int[] { 0, 128, 0 } );
    NAMED_COLORS.put( "lime", new int[] { 0, 255, 0 } );
    NAMED_COLORS.put( "navy", new int[] { 0, 0, 128 } );
    NAMED_COLORS.put( "blue", new int[] { 0, 0, 255 } );
    NAMED_COLORS.put( "olive", new int[] { 128, 128, 0 } );
    NAMED_COLORS.put( "yellow", new int[] { 255, 255, 0 } );
    NAMED_COLORS.put( "teal", new int[] { 0, 128, 128 } );
    NAMED_COLORS.put( "aqua", new int[] { 0, 255, 255 } );
  }

  private QxColor() {
    this.red = 0;
    this.green = 0;
    this.blue = 0;
    this.alpha = 0f;
  }

  private QxColor( int red, int green, int blue, float alpha ) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }

  public static QxColor create( int red, int green, int blue ) {
    QxColor result;
    if( red == 0 && green == 0 && blue == 0 ) {
      result = BLACK;
    } else if( red == 255 && green == 255 && blue == 255 ) {
      result = WHITE;
    } else {
      result = new QxColor( red, green, blue, 1f );
    }
    return result;
  }
  
  public static QxColor create( int red, int green, int blue, float alpha ) {
    checkAlpha( alpha );
    QxColor result;
    if( alpha == 1f ) {
      result = create( red, green, blue );
    } else {
      result = new QxColor( red, green, blue, alpha );
    }
    return result;
  }

  private static void checkAlpha( float alpha ) {
    if( alpha < 0 || alpha > 1 ) {
      String msg = "Alpha out of range [ 0, 1 ]: " + alpha;
      throw new IllegalArgumentException( msg );
    }
  }

  public static QxColor valueOf( String input ) {
    QxColor result;
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( TRANSPARENT_STR.equals( input ) ) {
      result = TRANSPARENT;
    } else {
      int red, green, blue;
      float alpha = 1f;
      String lowerCaseInput = input.toLowerCase( Locale.ENGLISH );
      if( input.startsWith( "#" ) ) {
        try {
          if( input.length() == 7 ) {
            red = Integer.parseInt( input.substring( 1, 3 ), 16 );
            green = Integer.parseInt( input.substring( 3, 5 ), 16 );
            blue = Integer.parseInt( input.substring( 5, 7 ), 16 );
          } else if( input.length() == 4 ) {
            red = Integer.parseInt( input.substring( 1, 2 ), 16 ) * 17;
            green = Integer.parseInt( input.substring( 2, 3 ), 16 ) * 17;
            blue = Integer.parseInt( input.substring( 3, 4 ), 16 ) * 17;
          } else {
            String pattern = "Illegal number of characters in color definition ''{0}''";
            Object[] arguments = new Object[] { input };
            String message = MessageFormat.format( pattern, arguments );
            throw new IllegalArgumentException( message );
          }
        } catch( NumberFormatException e ) {
          String pattern = "Illegal number format in color definition ''{0}''";
          Object[] arguments = new Object[] { input };
          String message = MessageFormat.format( pattern, arguments );
          throw new IllegalArgumentException( message );
        }
      } else if( NAMED_COLORS.containsKey( lowerCaseInput ) ) {
        int[] values = ( int[] )NAMED_COLORS.get( lowerCaseInput );
        red = values[ 0 ];
        green = values[ 1 ];
        blue = values[ 2 ];
      } else {
        String[] parts = input.split( "\\s*,\\s*" );
        if( parts.length >= 3 && parts.length <= 4 ) {
          try {
            red = Integer.parseInt( parts[ 0 ] );
            green = Integer.parseInt( parts[ 1 ] );
            blue = Integer.parseInt( parts[ 2 ] );
            if( parts.length == 4 ) {
              alpha = Float.parseFloat( parts[ 3 ] );
            }
          } catch( NumberFormatException e ) {
            String pattern = "Illegal number format in color definition ''{0}''";
            Object[] arguments = new Object[] { input };
            String message = MessageFormat.format( pattern, arguments );
            throw new IllegalArgumentException( message );
          }
        } else {
          String pattern = "Invalid color name ''{0}''";
          Object[] arguments = new Object[] { input };
          String message = MessageFormat.format( pattern, arguments );
          throw new IllegalArgumentException( message );
        }
      }
      result = create( red, green, blue, alpha );
    }
    return result;
  }

  public boolean isTransparent() {
    return alpha == 0f;
  }

  public String toDefaultString() {
    String result;
    if( isTransparent() ) {
      result = TRANSPARENT_STR;
    } else if( alpha == 1f ) {
      result = toHtmlString( red, green, blue );
    } else {
      result = toRgbaString( red, green, blue, alpha );
    }
    return result;
  }

  public boolean equals( Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof QxColor ) {
      QxColor other = ( QxColor )obj;
      result =    other.red == red
               && other.green == green
               && other.blue == blue
               && other.alpha == alpha;
    }
    return result;
  }

  public int hashCode() {
    int result = -1;
    if( !isTransparent() ) {
      result = 41;
      result += 19 * result + red;
      result += 19 * result + green;
      result += 19 * result + blue;
      result += 19 * result + Float.floatToIntBits( alpha );
    }
    return result;
  }

  public String toString() {
    String colors = red + ", " + green + ", " + blue + ", " + alpha;
    return "QxColor{ " + ( isTransparent() ? TRANSPARENT_STR : colors ) + " }";
  }

  public static String toHtmlString( int red, int green, int blue ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "#" );
    sb.append( getHexStr( red ) );
    sb.append( getHexStr( green ) );
    sb.append( getHexStr( blue ) );
    return sb.toString();
  }

  public static Color createColor( QxColor color ) {
    Color result = null;
    if( color.alpha != 0f ) {
      result = Graphics.getColor( color.red, color.green, color.blue );
    }
    return result;
  }

  private static String getHexStr( int value ) {
    String hex = Integer.toHexString( value );
    return hex.length() == 1 ? "0" + hex : hex;
  }

  private static String toRgbaString( int red, int green, int blue, float alpha ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "rgba(" );
    sb.append( red );
    sb.append( "," );
    sb.append( green );
    sb.append( "," );
    sb.append( blue );
    sb.append( "," );
    sb.append( alpha );
    sb.append( ")" );
    return sb.toString();
  }
}
