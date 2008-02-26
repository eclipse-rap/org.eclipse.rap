/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Color;

public class QxColor implements QxType {

  private static final Map NAMED_COLORS = new HashMap();

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

  public final String name;

  public final int red;

  public final int green;

  public final int blue;

  public QxColor( final String color ) {
    if( color == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( color.startsWith( "#" ) ) {
      try {
        if( color.length() == 7 ) {
          red = Integer.parseInt( color.substring( 1, 3 ), 16 );
          green = Integer.parseInt( color.substring( 3, 5 ), 16 );
          blue = Integer.parseInt( color.substring( 5, 7 ), 16 );
          name = color;
        } else if( color.length() == 4 ) {
          red = Integer.parseInt( color.substring( 1, 2 ), 16 ) * 17;
          green = Integer.parseInt( color.substring( 2, 3 ), 16 ) * 17;
          blue = Integer.parseInt( color.substring( 3, 4 ), 16 ) * 17;
          name = color;
        } else {
          String mesg = "Illegal number of characters in color definition: "
                        + color;
          throw new IllegalArgumentException( mesg );
        }
      } catch( final NumberFormatException e ) {
        String mesg =  "Illegal number format in color definition: " + color;
        throw new IllegalArgumentException( mesg );
      }
    } else if( NAMED_COLORS.containsKey( color.toLowerCase() ) ) {
      int[] values = ( int[] )NAMED_COLORS.get( color.toLowerCase() );
      red = values[ 0 ];
      green = values[ 1 ];
      blue = values[ 2 ];
      name = color.toLowerCase();
    } else {
      String[] parts = color.split( "\\s*,\\s*" );
      if( parts.length == 3 ) {
        try {
          red = Integer.parseInt( parts[ 0 ] );
          green = Integer.parseInt( parts[ 1 ] );
          blue = Integer.parseInt( parts[ 2 ] );
          name = toHtmlString( red, green, blue );
        } catch( final NumberFormatException e ) {
          String mesg =  "Illegal number format in color definition: " + color;
          throw new IllegalArgumentException( mesg );
        }
      } else {
        throw new IllegalArgumentException( "Invalid color name: " + color );
      }
    }
  }

  public String toDefaultString() {
    return toHtmlString( red, green, blue );
  }

  public static String toHtmlString( final int red,
                                     final int green,
                                     final int blue )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( "#" );
    sb.append( getHexStr( red ) );
    sb.append( getHexStr( green ) );
    sb.append( getHexStr( blue ) );
    return sb.toString();
  }

  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof QxColor ) {
      QxColor other = ( QxColor )obj;
      result =  other.red == red
             && other.green == green
             && other.blue == blue;
    }
    return result;
  }

  public int hashCode() {
    return red ^ green ^ blue ;
  }

  public String toString() {
    return "QxColor {"
           + red
           + ", "
           + green
           + ", "
           + blue
           + "}";
  }

  public static Color createColor( final QxColor color ) {
    return Graphics.getColor( color.red, color.green, color.blue );
  }

  private static String getHexStr( final int value ) {
    String hex = Integer.toHexString( value );
    return hex.length() == 1 ? "0" + hex : hex;
  }
}
