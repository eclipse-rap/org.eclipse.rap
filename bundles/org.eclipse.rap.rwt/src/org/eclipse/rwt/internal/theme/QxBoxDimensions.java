/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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

  public final int top;

  public final int right;

  public final int bottom;

  public final int left;

  public QxBoxDimensions( final int top,
                          final int right,
                          final int bottom,
                          final int left )
  {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public QxBoxDimensions( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    String[] parts = input.split( "\\s+" );
    if( parts.length == 0 || parts.length > 4 ) {
      throw new IllegalArgumentException( "Illegal number of arguments for box dimensions" );
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
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
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
    return top + "px " + right + "px " + bottom + "px " + left + "px";
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
    return top ^ right ^ bottom ^ left;
  }

  public String toString () {
    return "QxDimensions {"
           + top
           + ", "
           + left
           + ", "
           + bottom
           + ", "
           + right
           + "}";
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
