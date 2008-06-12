/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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
 * Instances of this class are graphics which have been prepared
 * for display on a specific device. That is, they are to display 
 * on widgets with, for example, <code>Button.setImage()</code>.
 * 
 * <p>If loaded from a file format that supports it, an
 * <code>Image</code> may have transparency, meaning that certain
 * pixels are specified as being transparent when drawn. Examples
 * of file formats that support transparency are GIF and PNG.</p>
 * 
 * <p>In RWT, images are shared among all sessions. Therefore they
 * lack a public constructor. Images can be created using the 
 * <code>getImage()</code> methods of class <code>Graphics</code>
 *
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String)
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String, ClassLoader)
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String, java.io.InputStream)
 */
public final class Image extends Resource {

  private int width;
  private int height;

  /* prevent instantiation from outside */
  private Image() {
    width = -1;
    height = -1;
  }
  private Image( final int width, final int height ) {
    this.width = width;
    this.height = height;
  }

  ///////////////////////
  // Public Image methods

  /**
   * Returns the bounds of the receiver. The rectangle will always
   * have x and y values of 0, and the width and height of the
   * image.
   *
   * @return a rectangle specifying the image's bounds
   *
   * @exception SWTException <ul>
   * <!--   <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li> -->
   *    <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or an icon</li>
   * </ul>
   */
  public Rectangle getBounds() {
    Rectangle result = null;
//    TODO [rst] Uncomment if constructor provided
//    if( isDisposed() ) {
//      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
//    }
    if( width != -1 && height != -1 ) {
      result = new Rectangle( 0, 0, width, height );
    } else {
      // TODO [rst] check types
      SWT.error( SWT.ERROR_INVALID_IMAGE );
    }
    return result;
  }
}