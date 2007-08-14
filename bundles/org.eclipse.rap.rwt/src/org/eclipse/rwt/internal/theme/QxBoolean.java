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

package org.eclipse.rwt.internal.theme;


public class QxBoolean implements QxType {

  public final boolean value;

  private static final String[] VALID_TRUE_STRINGS = new String[] {
    "true", "yes", "on"
  };

  private static final String[] VALID_FALSE_STRINGS = new String[] {
    "false", "no", "off"
  };

  public QxBoolean( final String bool ) {
    boolean result = false;
    boolean found = false;
    for( int i = 0; i < VALID_TRUE_STRINGS.length && !found; i++ ) {
      if( VALID_TRUE_STRINGS[ i ].equalsIgnoreCase( bool ) ) {
        result = true;
        found = true;
      }
    }
    for( int i = 0; i < VALID_FALSE_STRINGS.length && !found; i++ ) {
      if( VALID_FALSE_STRINGS[ i ].equalsIgnoreCase( bool ) ) {
        found = true;
      }
    }
    if( !found ) {
      throw new IllegalArgumentException( "Illegal boolean value: " + bool );
    }
    value = result;
  }

  public String toDefaultString() {
    return value ? "true" : "false";
  }
}
