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

import org.eclipse.swt.graphics.Rectangle;

public class QxBoxDimensions implements QxType {

  public static final QxBoxDimensions ZERO = new QxBoxDimensions( 0, 0, 0, 0 );

  public final int top;

  public final int right;

  public final int bottom;

  public final int left;

  private QxBoxDimensions( final int top,
                           final int right,
                           final int bottom,
                           final int left )
  {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public static QxBoxDimensions valueOf( final String input ) {
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
    QxBoxDimensions result;
    if( top == 0 && right == 0 && bottom == 0 && left == 0 ) {
      result = ZERO;
    } else {
      result = new QxBoxDimensions( top, right, bottom, left );
    }
    return result;
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
    StringBuffer buffer = new StringBuffer();
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

  public boolean equals( final Object object ) {
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

  public int hashCode () {
    int result = 911;
    result += 23 * result + top;
    result += 23 * result + right;
    result += 23 * result + bottom;
    result += 23 * result + left;
    return result;
  }

  public String toString () {
    return "QxDimensions{ "
           + top
           + ", "
           + left
           + ", "
           + bottom
           + ", "
           + right
           + " }";
  }

  private static int parsePxValue( final String part ) {
    Integer result = QxDimension.parseLength( part );
    if( result == null ) {
      throw new IllegalArgumentException( "Illegal parameter: " + part );
    }
    return result.intValue();
  }

  public static Rectangle createRectangle( final QxBoxDimensions boxdim ) {
    return new Rectangle( boxdim.left,
                          boxdim.top,
                          boxdim.left + boxdim.right,
                          boxdim.top + boxdim.bottom );
  }
}
