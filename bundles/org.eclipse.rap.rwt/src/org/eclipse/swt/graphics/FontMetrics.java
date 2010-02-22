/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.graphics.Graphics;

/**
 * Instances of this class provide measurement information about fonts <!-- including
 * ascent, descent, height, leading space between rows, and average character
 * width -->. <code>FontMetrics</code> are obtained from <code>GC</code>s using the
 * <code>getFontMetrics()</code> method.
 * 
 * @see GC#getFontMetrics
 * @since 1.3
 */
public final class FontMetrics {

  private Font font;
  
  /**
   * Prevents instances from being created outside the package.
   */
  FontMetrics( final Font font ) {
    this.font = font;
  }

  /**
   * Returns the average character width, measured in pixels, of the font
   * described by the receiver.
   * 
   * @return the average character width of the font
   */
  public int getAverageCharWidth() {
    return ( int )Graphics.getAvgCharWidth( font );
  }

  /**
   * Returns the height of the font described by the receiver, measured in
   * pixels. A font's <em>height</em> is the sum of its ascent, descent and
   * leading area.
   * 
   * @return the height of the font
   * @see #getAscent
   * @see #getDescent
   * @see #getLeading
   */
  public int getHeight() {
    return Graphics.getCharHeight( font );
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
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof FontMetrics ) {
      FontMetrics other = ( FontMetrics )object;
      result = font.equals( other.font );
    } 
    return result;
  }

  /**
   * Returns an integer hash code for the receiver. Any two objects that return
   * <code>true</code> when passed to <code>equals</code> must return the same
   * value for this method.
   * 
   * @return the receiver's hash
   * @see #equals
   */
  public int hashCode() {
    return font.hashCode();
  }
}
