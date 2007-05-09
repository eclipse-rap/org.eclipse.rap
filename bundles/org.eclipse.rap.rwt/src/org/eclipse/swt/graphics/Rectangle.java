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
 * Instances of this class represent rectangular areas in an
 * (x, y) coordinate system. The top left corner of the rectangle
 * is specified by its x and y values, and the extent of the
 * rectangle is specified by its width and height.
 * <p>
 * The coordinate space for rectangles and points is considered
 * to have increasing values downward and to the right from its
 * origin making this the normal, computer graphics oriented notion
 * of (x, y) coordinates rather than the strict mathematical one.
 * </p>
 * <p>
 * The hashCode() method in this class uses the values of the public
 * fields to compute the hash value. When storing instances of the
 * class in hashed collections, do not modify these fields after the
 * object has been inserted.  
 * </p>
 * <p>
 * Application code does <em>not</em> need to explicitly release the
 * resources managed by each instance when those instances are no longer
 * required, and thus no <code>dispose()</code> method is provided.
 * </p>
 *
 * @see Point
 */
public class Rectangle {

	/**
	 * the x coordinate of the rectangle
	 */
	public int x;
	
	/**
	 * the y coordinate of the rectangle
	 */
	public int y;
	
	/**
	 * the width of the rectangle
	 */
	public int width;
	
	/**
	 * the height of the rectangle
	 */
	public int height;

	/**
	 * Construct a new instance of this class given the 
	 * x, y, width and height values.
	 *
	 * @param x the x coordinate of the origin of the rectangle
	 * @param y the y coordinate of the origin of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
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

  /**
   * Returns <code>true</code> if the point specified by the
   * arguments is inside the area specified by the receiver,
   * and <code>false</code> otherwise.
   *
   * @param x the x coordinate of the point to test for containment
   * @param y the y coordinate of the point to test for containment
   * @return <code>true</code> if the rectangle contains the point and <code>false</code> otherwise
   */
  public boolean contains( final Point point ) {
    if( point == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return contains( point.x, point.y );
  }
  
  /**
   * Compares the argument to the receiver, and returns true
   * if they represent the <em>same</em> object using a class
   * specific comparison.
   *
   * @param object the object to compare with this object
   * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
   *
   * @see #hashCode()
   */
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

  /**
   * Returns an integer hash code for the receiver. Any two 
   * objects that return <code>true</code> when passed to 
   * <code>equals</code> must return the same value for this
   * method.
   *
   * @return the receiver's hash
   *
   * @see #equals(Object)
   */
  public int hashCode() {
    return x ^ y ^ width ^ height;
  }
  
  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the rectangle
   */
  public String toString () {
    return "Rectangle {" + x + ", " + y + ", " + width + ", " + height + "}";
  }
}
