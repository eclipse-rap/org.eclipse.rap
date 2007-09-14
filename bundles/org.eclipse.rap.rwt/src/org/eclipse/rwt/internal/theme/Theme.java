/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.util.*;

public class Theme {

  private String name;

  private Map values;

  private final Theme defaultTheme;

  /**
   * Creates a default theme.
   */
  public Theme( final String name ) {
    this( name, null );
  }

  /**
   * Creates a new theme with the given default theme.
   */
  public Theme( final String name, final Theme defaultTheme ) {
    this.defaultTheme = defaultTheme;
    this.name = checkName( name );
    if( defaultTheme != null ) {
      values = new HashMap( defaultTheme.values );
    } else {
      values = new HashMap();
    }
  }

  public String getName() {
    return name;
  }

  public Theme getDefaultTheme() {
    return defaultTheme;
  }

  public boolean hasKey( final String key ) {
    return values.containsKey( key );
  }

  public String[] getKeys() {
    Set keySet = values.keySet();
    return ( String[] )keySet.toArray( new String[ keySet.size() ] );
  }

  public QxBorder getBorder( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxBorder ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxBorder )value;
  }

  public QxBoxDimensions getBoxDimensions( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxBoxDimensions ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxBoxDimensions )value;
  }

  public QxFont getFont( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxFont ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxFont )value;
  }

  public QxColor getColor( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxColor ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxColor )value;
  }

  public QxImage getImage( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxImage ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxImage )value;
  }

  public QxDimension getDimension( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxDimension ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxDimension )value;
  }

  public QxType getValue( final String key ) {
    QxType value = ( QxType )values.get( key );
    if( value == null ) {
      throw new IllegalArgumentException( "Undefined key: " + key );
    }
    return value;
  }

  public void setValue( final String key, final QxType value ) {
    if( values.containsKey( key ) ) {
      if( !values.get( key ).getClass().isInstance( value ) ) {
        String msg = "Key '" + key + "' already defined with different type";
        throw new IllegalArgumentException( msg );
      }
    }
    values.put( key, value );
  }

  // TODO [rst] implement equals and hashcode completely

  public boolean equals( final Object obj ) {
    boolean result;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof Theme ) {
      Theme other = ( Theme )obj;
      result = name.equals( other.name );
    } else {
      result = false;
    }
    return result;
  }

  public int hashCode() {
    return name.hashCode();
  }

  private String checkName( final String name ) {
    if( name == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( name.length() == 0 ) {
      throw new IllegalArgumentException( "empty argument" );
    }
    StringBuffer sb = new StringBuffer();
    sb.append( name.substring( 0, 1 ).toUpperCase() );
    sb.append( name.substring( 1, name.length() ) );
    return sb.toString();
  }
}
