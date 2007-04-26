/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;


/**
 * TODO: [fappel] comment
 */
public class Rectangle {

  public int x;
  public int y;
  public int width;
  public int height;

  public Rectangle( final int x, 
                    final int y, 
                    final int width, 
                    final int height )
  {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
  }

  public Rectangle( final Rectangle rectangle ) {
    this( rectangle.x, rectangle.y, rectangle.width, rectangle.height );
  }

  public boolean contains( final int x, final int y ) {
    return    x >= this.x 
           && y >= this.y
           && x - this.x < width 
           && y - this.y < height;
  }

  public boolean contains( final Point point ) {
    if( point == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return contains( point.x, point.y );
  }
  
  public boolean equals( final Object object ) {
    boolean result = object == this;
    if( !result && object instanceof Rectangle ) {
      Rectangle toCompare = ( Rectangle )object;
      result =    toCompare.x == this.x
               && toCompare.y == this.y
               && toCompare.width == this.width
               && toCompare.height == this.height;
    }
    return result;
  }

  public int hashCode() {
    return x ^ y ^ width ^ height;
  }
  
  public String toString () {
    return "Rectangle {" + x + ", " + y + ", " + width + ", " + height + "}";
  }
}
