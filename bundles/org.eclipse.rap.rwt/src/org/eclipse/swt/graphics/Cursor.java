/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;

/**
 * Instances of this class manage operating system resources that
 * specify the appearance of the on-screen pointer.
 *
 * <p>Cursors may be constructed using one of the <code>getCursor</code> methods
 * in class <code>Graphics</code> by providing a style information.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>
 *   CURSOR_ARROW, CURSOR_WAIT, CURSOR_CROSS, CURSOR_HELP,
 *   CURSOR_SIZEALL, CURSOR_SIZENS, CURSOR_SIZEWE,
 *   CURSOR_SIZEN, CURSOR_SIZES, CURSOR_SIZEE, CURSOR_SIZEW, CURSOR_SIZENE, CURSOR_SIZESE,
 *   CURSOR_SIZESW, CURSOR_SIZENW, CURSOR_IBEAM, CURSOR_HAND
 * </dd>
 * </dl>
 * <p>
 * Note: Only one of the above styles may be specified.
 * </p>
 *
 * @see Graphics
 *
 * @since 1.2
 */
public final class Cursor extends Resource {

  private int value;

  //prevent instance creation
  private Cursor( final int style ) {
    value = style;
  	switch( style ) {
  	  case SWT.CURSOR_ARROW:
  		case SWT.CURSOR_WAIT:
  		case SWT.CURSOR_CROSS:
  		case SWT.CURSOR_HELP:
  		case SWT.CURSOR_SIZEALL:
  		case SWT.CURSOR_SIZENS:
  		case SWT.CURSOR_SIZEWE:
  		case SWT.CURSOR_SIZEN:
  		case SWT.CURSOR_SIZES:
  		case SWT.CURSOR_SIZEE:
  		case SWT.CURSOR_SIZEW:
  		case SWT.CURSOR_SIZENE:
  		case SWT.CURSOR_SIZESE:
  		case SWT.CURSOR_SIZESW:
  		case SWT.CURSOR_SIZENW:
  		case SWT.CURSOR_IBEAM:
  		case SWT.CURSOR_HAND:
  		  break;
  		default:
  			SWT.error( SWT.ERROR_INVALID_ARGUMENT );
  	}
  }

  /**
   * Compares the argument to the receiver, and returns true
   * if they represent the <em>same</em> object using a class
   * specific comparison.
   *
   * @param object the object to compare with this object
   * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
   *
   * @see #hashCode
   */
  public boolean equals( final Object object ) {
    return object == this;
  }

  /**
   * Returns an integer hash code for the receiver. Any two 
   * objects that return <code>true</code> when passed to 
   * <code>equals</code> must return the same value for this
   * method.
   *
   * @return the receiver's hash
   *
   * @see #equals
   */
  public int hashCode () {
    return 101 * value;
  }

  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the receiver
   */
  public String toString () {
    return "Cursor {" + value + "}";
  }
}
