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
    this.name = checkName( name );
    this.defaultTheme = defaultTheme;
    values = new HashMap();
  }

  public String getName() {
    return name;
  }

  public Theme getDefaultTheme() {
    return defaultTheme;
  }

  /**
   * Indicates whether this theme has a value for the specified key, no matter
   * if the value is defined in the theme itself or derived from the default
   * theme.
   *
   * @return <code>true</code> if either the theme itself or its default theme
   *         has a value for key, <code>false</code> otherwise.
   */
  public boolean hasKey( final String key ) {
    boolean result = values.containsKey( key );
    if( defaultTheme != null ) {
      result |= defaultTheme.values.containsKey( key );
    }
    return result;
  }

  /**
   * Indicates whether this theme defines the specified key. If the key is only
   * defined in the default theme, <code>false</code> is returned.
   */
  public boolean definesKey( final String key ) {
    return values.containsKey( key );
  }

  /**
   * Returns all keys defined in this theme, including the keys of the default
   * theme.
   */
  public String[] getKeys() {
    Set keySet = new HashSet( values.keySet() );
    if( defaultTheme != null ) {
      keySet.addAll( defaultTheme.values.keySet() );
    }
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
    if( value == null && defaultTheme != null ) {
      value = ( QxType )defaultTheme.values.get( key );
    }
    if( value == null ) {
      throw new IllegalArgumentException( "Undefined key: " + key );
    }
    return value;
  }

  public void setValue( final String key, final QxType value ) {
    if( values.containsKey( key ) ) {
      String msg = "Tried to redefine key: " + key;
      throw new IllegalArgumentException( msg );
    }
    if( defaultTheme != null ) {
      if( defaultTheme.values.containsKey( key ) ) {
        if( !defaultTheme.values.get( key ).getClass().isInstance( value ) ) {
          String msg = "Tried to define key with wrong type: " + key;
          throw new IllegalArgumentException( msg );
        }
      } else {
        String msg = "Key does not exist in default theme :" + key;
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
