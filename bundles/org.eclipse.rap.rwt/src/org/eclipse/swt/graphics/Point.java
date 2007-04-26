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


public class Point {
  
  public int x;
  public int y;

  public Point( final int x, final int y ) {
    this.x = x;
    this.y = y;
  }
  
  public boolean equals( final Object object ) {
    boolean result = object == this;
    if( !result && object instanceof Point ) {
      Point toCompare = ( Point )object;
      result =    toCompare.x == this.x
               && toCompare.y == this.y;
    }
    return result;
  }

  public int hashCode() {
    return x ^ y;
  }

  public String toString () {
    return "Point {" + x + ", " + y + "}";
  }
}
