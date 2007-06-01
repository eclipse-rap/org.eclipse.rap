/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

import java.util.*;

public class Theme {

  private String name;

  private Map values;

  public Theme( final String name ) {
    this.name = checkName( name );
    values = new HashMap();
  }
  
  public Theme( final String name, final Theme defaultTheme ) {
    this.name = checkName( name );
    if( defaultTheme == null ) {
      throw new NullPointerException( "null argument" );
    }
    values = new HashMap( defaultTheme.values );
  }
  
  public String getName() {
    return name;
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
  
  public QxColor getColor( final String key ) {
    QxType value = getValue( key );
    if( !( value instanceof QxColor ) ) {
      throw new IllegalArgumentException( "Key has a different type: " + key );
    }
    return ( QxColor )value;
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
