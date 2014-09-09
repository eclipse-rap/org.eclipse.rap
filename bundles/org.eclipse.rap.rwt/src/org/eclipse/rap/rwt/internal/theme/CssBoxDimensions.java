/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import java.util.zip.CRC32;

import org.eclipse.swt.graphics.Rectangle;


public class CssBoxDimensions implements CssType {

  public static final CssBoxDimensions ZERO = new CssBoxDimensions( 0, 0, 0, 0 );

  public final int top;
  public final int right;
  public final int bottom;
  public final int left;

  private CssBoxDimensions( int top, int right, int bottom, int left ) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public static CssBoxDimensions create( int top, int right, int bottom, int left ) {
    if( top == 0 && right == 0 && bottom == 0 && left == 0 ) {
      return ZERO;
    }
    return new CssBoxDimensions( top, right, bottom, left );
  }

  public static CssBoxDimensions valueOf( String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    String[] parts = input.split( "\\s+" );
    if( parts.length == 0 || parts.length > 4 ) {
      String msg = "Illegal number of arguments for box dimensions";
      throw new IllegalArgumentException( msg );
    }
    int top, right, left, bottom;
    top = right = bottom = left = parsePxValue( parts[ 0 ] );
    if( parts.length >= 2 ) {
      right = left = parsePxValue( parts[ 1 ] );
    }
    if( parts.length >= 3 ) {
      bottom = parsePxValue( parts[ 2 ] );
    }
    if( parts.length == 4 ) {
      left = parsePxValue( parts[ 3 ] );
    }
    return create( top, right, bottom, left );
  }

  /**
   * Returns <code>left + right</code> for convenience.
   */
  public int getWidth() {
    return left + right;
  }

  /**
   * Returns <code>top + bottom</code> for convenience.
   */
  public int getHeight() {
    return top + bottom;
  }

  public String toDefaultString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append( top + "px" );
    if( right != top || bottom != top || left != top ) {
      buffer.append( " " + right + "px" );
    }
    if( bottom != top || left != right ) {
      buffer.append( " " + bottom + "px" );
    }
    if( left != right ) {
      buffer.append( " " + left + "px" );
    }
    return buffer.toString();
  }

  @Override
  public boolean equals( Object object ) {
    if( object == this ) {
      return true;
    }
    if( object instanceof CssBoxDimensions ) {
      CssBoxDimensions other = ( CssBoxDimensions )object;
      return    ( other.top == this.top )
             && ( other.right == this.right )
             && ( other.bottom == this.bottom )
             && ( other.left == this.left );
    }
    return false;
  }

  @Override
  public int hashCode() {
    CRC32 result = new CRC32();
    result.update( top );
    result.update( right );
    result.update( bottom );
    result.update( left );
    return ( int )result.getValue();
  }

  @Override
  public String toString() {
    return "CssBoxDimensions{ " + top + ", " + right + ", " + bottom + ", " + left + " }";
  }

  private static int parsePxValue( String part ) {
    Integer result = CssDimension.parseLength( part );
    if( result == null ) {
      throw new IllegalArgumentException( "Illegal parameter: " + part );
    }
    return result.intValue();
  }

  public static Rectangle createRectangle( CssBoxDimensions boxdim ) {
    return new Rectangle( boxdim.left,
                          boxdim.top,
                          boxdim.left + boxdim.right,
                          boxdim.top + boxdim.bottom );
  }

}
