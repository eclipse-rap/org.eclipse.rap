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

import java.io.IOException;
import java.io.InputStream;


public class QxCursor implements QxType {

  private static final String[] PREDEFINED_CURSORS = new String[] {
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
    "sw-resize",
    "col-resize",
    "row-resize"
  };

  public final String value;
  public final ResourceLoader loader;

  private QxCursor( final String value, final ResourceLoader loader ) {
    this.value = value;
    this.loader = loader;
    if( isCustomCursor() ) {
      try {
        InputStream inputStream = loader.getResourceAsStream( value );
        if( inputStream == null ) {
          throw new IllegalArgumentException( "Failed to read cursor '"
                                              + value
                                              + "'" );
        }
      } catch( IOException e ) {
        throw new IllegalArgumentException( "Failed to read cursor "
                                            + value
                                            + ": "
                                            + e.getMessage() );
      }
    }
  }

  public static QxCursor valueOf( final String input,
                                  final ResourceLoader loader )
  {
    if( input == null || loader == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( input.length() == 0 ) {
      throw new IllegalArgumentException( "Empty cursor path" );
    }
    return new QxCursor( input, loader );
  }

  public static QxCursor valueOf( final String input ) {
    if( !isPredefinedCursor( input ) ) {
      throw new IllegalArgumentException( "Invalid value for cursor: " + input );
    }
    return new QxCursor( input, null );
  }

  public static boolean isPredefinedCursor( final String value ) {
    boolean result = false;
    for( int i = 0; i < PREDEFINED_CURSORS.length && !result; i++ ) {
      if( PREDEFINED_CURSORS[ i ].equalsIgnoreCase( value ) ) {
        result = true;
      }
    }
    return result;
  }

  public boolean isCustomCursor() {
    return !isPredefinedCursor( value );
  }

  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxCursor ) {
      QxCursor other = ( QxCursor )object;
      result =    ( value == null
                    ? other.value == null
                    : value.equals( other.value ) )
               && ( loader == null
                    ? other.loader == null
                    : loader.equals( other.loader ) );
    }
    return result;
  }

  public int hashCode() {
    return value.hashCode();
  }

  public String toDefaultString() {
    // returns an empty string for custom cursor , because the default resource
    // path is only valid for the bundle that specified it
    return isCustomCursor() ? "" : value;
  }

  public String toString() {
    return "QxCursor{ " + value + " }";
  }
}
