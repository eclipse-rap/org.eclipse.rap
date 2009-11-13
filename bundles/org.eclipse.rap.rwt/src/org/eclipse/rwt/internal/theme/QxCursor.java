/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;


public class QxCursor implements QxType {

  private static final String[] VALID_CURSORS = new String[] {
    "default",
    "wait",
    "crosshair",
    "help",
    "move",
    "text",
    "pointer",
    "e-resize",
    "n-resize",
    "w-resize",
    "s-resize",
    "ne-resize",
    "se-resize",
    "nw-resize",
    "sw-resize"
  };

  public final String value;

  public QxCursor( final String value ) {
    this.value = value;
  }

  public static boolean isValidCursor( final String value ) {
    boolean result = false;
    for( int i = 0; i < VALID_CURSORS.length && !result; i++ ) {
      if( VALID_CURSORS[ i ].equalsIgnoreCase( value ) ) {
        result = true;
      }
    }
    return result;
  }

  public String toDefaultString() {
    return value;
  }

  public String toString() {
    return "QxCursor{ " + value + " }";
  }
}
