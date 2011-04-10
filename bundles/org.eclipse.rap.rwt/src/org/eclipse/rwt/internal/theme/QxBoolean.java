/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;


public class QxBoolean implements QxType {

  public static final QxBoolean TRUE = new QxBoolean( true );
  public static final QxBoolean FALSE = new QxBoolean( false );

  private static final String[] VALID_TRUE_STRINGS = new String[] { "true", "yes", "on" };

  private static final String[] VALID_FALSE_STRINGS = new String[] { "false", "no", "off" };

  public static QxBoolean valueOf( String input ) {
    return evalInput( input ) ? TRUE : FALSE;
  }

  public final boolean value;

  private QxBoolean( boolean value ) {
    this.value = value;
  }

  public String toDefaultString() {
    return value ? VALID_TRUE_STRINGS[ 0 ] : VALID_FALSE_STRINGS[ 0 ];
  }

  public String toString () {
    return   "QxBoolean{ "
           + String.valueOf( value )
           + " }";
  }

  private static boolean evalInput( String input ) {
    boolean result = false;
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    boolean found = false;
    for( int i = 0; i < VALID_TRUE_STRINGS.length && !found; i++ ) {
      if( VALID_TRUE_STRINGS[ i ].equals( input ) ) {
        result = true;
        found = true;
      }
    }
    for( int i = 0; i < VALID_FALSE_STRINGS.length && !found; i++ ) {
      if( VALID_FALSE_STRINGS[ i ].equals( input ) ) {
        found = true;
      }
    }
    if( !found ) {
      String message = "Illegal boolean value: " + input;
      throw new IllegalArgumentException( message );
    }
    return result;
  }
}
