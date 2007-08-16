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

package org.eclipse.swt.internal.graphics;

import org.eclipse.swt.graphics.Color;


/**
 * Provides a method that formats a Color id suitable for qooxdoo.
 */
public class ColorUtil {

  public static String formatColorForJs( final Color color ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "#" );
    append( buffer, Integer.toHexString( color.getRed() ) );
    append( buffer, Integer.toHexString( color.getGreen() ) );
    append( buffer, Integer.toHexString( color.getBlue() ) );
    return buffer.toString();
  }

  private static void append( final StringBuffer buffer, final String value ) {
    if( value.length() == 1 ) {
      buffer.append( "0" );
    }
    buffer.append( value );
  }
}
