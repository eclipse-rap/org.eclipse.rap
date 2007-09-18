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

public final class Theme {

  private final String name;

  private final Map values;

  private final Map defaultValues;

  /**
   * Creates a new theme which has no default theme. In RWT theming, only the
   * default theme itself has no default theme.
   * @param name the name of the theme
   */
  public Theme( final String name ) {
    this( name, null );
  }

  /**
   * Creates a new theme with the given default theme. The default theme
   * specifies the possible keys and their default values. <strong>Important:</strong>
   * Modifying the default theme afterwards has no effect on this theme.
   *
   * @param name the name of the theme
   * @param defaultTheme the default theme
   */
  public Theme( final String name, final Theme defaultTheme ) {
    checkName( name );
    this.name = name;
    this.defaultValues = defaultTheme != null ? defaultTheme.values : null;
    values = new HashMap();
  }

  public String getName() {
    return name;
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
    if( defaultValues != null ) {
      result |= defaultValues.containsKey( key );
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
    Set keySet;
    if( defaultValues != null ) {
      keySet = defaultValues.keySet();
    } else {
      keySet = values.keySet();
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

  /**
   * Returns the value for the given key. If the theme does not define a value
   * for this key, the default value is returned.
   */
  public QxType getValue( final String key ) {
    QxType value = ( QxType )values.get( key );
    if( value == null && defaultValues != null ) {
      value = ( QxType )defaultValues.get( key );
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
    if( defaultValues != null ) {
      if( defaultValues.containsKey( key ) ) {
        if( !defaultValues.get( key ).getClass().isInstance( value ) ) {
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

  public boolean equals( final Object obj ) {
    boolean result;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof Theme ) {
      Theme other = ( Theme )obj;
      result = name.equals( other.name );
      if( defaultValues == null ) {
        result &= other.defaultValues == null;
      } else {
        result &= defaultValues.equals( other.defaultValues );
      }
      result &= values.equals( other.values );
    } else {
      result = false;
    }
    return result;
  }

  public int hashCode() {
    int result = name.hashCode();
    if( defaultValues != null ) {
      result = result * 23 + defaultValues.hashCode() * 23;
    }
    result = result + values.hashCode() * 23;
    return result;
  }

  private void checkName( final String name ) {
    if( name == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( name.length() == 0 ) {
      throw new IllegalArgumentException( "empty argument" );
    }
  }
}
