/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

public final class NumberFormatUtil {

  public static int parseInt( final String value ) {
    double result = Double.parseDouble( value );
    if( result != Math.floor( result ) ) {
      String msg = "Not a valid integer number: " + value;
      throw new IllegalArgumentException( msg );
    }
    if( result > Integer.MAX_VALUE || result < Integer.MIN_VALUE ) {
      String msg = "Integer number out of range: " + value;
      throw new IllegalArgumentException( msg );
    }
    return ( int )result;
  }
  
  private NumberFormatUtil() {
    // prevent instantiation
  }  
}
