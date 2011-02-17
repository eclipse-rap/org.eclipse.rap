/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.text.MessageFormat;

public class NumberFormatUtil {
  
  private static final String OUT_OF_RANGE_TEXT 
    = "Integer number ''{0}'' out of range.";
  
  public static int parseInt( final String value ) {
    double result = Double.parseDouble( value );
    if( result > Integer.MAX_VALUE || result < Integer.MIN_VALUE ) {
      Object[] args = new Object[] { value };
      String msg = MessageFormat.format( OUT_OF_RANGE_TEXT, args );
      throw new IllegalArgumentException( msg );
    }
    return ( int )Math.round( result );
  }
  
  private NumberFormatUtil() {
    // prevent instantiation
  }
  
}
