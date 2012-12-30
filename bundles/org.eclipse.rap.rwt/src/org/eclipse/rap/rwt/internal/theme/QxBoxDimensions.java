/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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


public class QxBoxDimensions implements QxType {

  public static final QxBoxDimensions ZERO = new QxBoxDimensions( 0, 0, 0, 0 );

  public final int top;

  public final int right;

  public final int bottom;

  public final int left;

  private QxBoxDimensions( int top, int right, int bottom, int left ) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public static QxBoxDimensions create( int top, int right, int bottom, int left ) {
    QxBoxDimensions result;
    if( top == 0 && right == 0 && bottom == 0 && left == 0 ) {
      result = ZERO;
    } else {
      result = new QxBoxDimensions( top, right, bottom, left );
    }
    return result;
  }

  public static QxBoxDimensions valueOf( String input ) {
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

  public String toJsArray() {
    return "[ " + top + ", " + right + ", " + bottom + ", " + left + " ]";
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

  public boolean equals( Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxBoxDimensions ) {
      QxBoxDimensions other = (QxBoxDimensions)object;
      result = ( other.top == this.top )
               && ( other.right == this.right )
               && ( other.bottom == this.bottom )
               && ( other.left == this.left );
    }
    return result;
}

  public int hashCode() {
    CRC32 result = new CRC32();
    result.update( top );
    result.update( right );
    result.update( bottom );
    result.update( left );
    return ( int )result.getValue();
  }

  public String toString () {
    return "QxBoxDimensions{ "
           + top
           + ", "
           + right
           + ", "
           + bottom
           + ", "
           + left
           + " }";
  }

  private static int parsePxValue( String part ) {
    Integer result = QxDimension.parseLength( part );
    if( result == null ) {
      throw new IllegalArgumentException( "Illegal parameter: " + part );
    }
    return result.intValue();
  }

  public static Rectangle createRectangle( QxBoxDimensions boxdim ) {
    return new Rectangle( boxdim.left,
                          boxdim.top,
                          boxdim.left + boxdim.right,
                          boxdim.top + boxdim.bottom );
  }
}
