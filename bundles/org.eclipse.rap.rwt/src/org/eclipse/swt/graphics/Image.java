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