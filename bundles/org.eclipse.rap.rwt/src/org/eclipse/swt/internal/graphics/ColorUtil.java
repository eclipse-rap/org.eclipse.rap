/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.swt.SWT;


public class ColorUtil {


  public static int computeColorNr( int red, int green, int blue, int alpha ) {
    if(    red > 255 || red < 0
        || green > 255 || green < 0
        || blue > 255 || blue < 0
        || alpha > 255 || alpha < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return red | green << 8 | blue << 16 | alpha << 24;
  }

  private ColorUtil() {
    // prevent instantiation
  }

}
