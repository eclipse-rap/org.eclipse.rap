/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.QxType;


public class StylePropertyMap implements IStylePropertyMap {

  private final Map properties;

  public StylePropertyMap() {
    properties = new HashMap();
  }

  public void setProperty( final String key, final QxType value ) {
    if( key == null || value == null ) {
      throw new NullPointerException( "null argument" );
    }
    properties.put( key, value );
  }

  public String[] getProperties() {
    Set keySet = properties.keySet();
    String[] result = new String[ keySet.size() ];
    keySet.toArray( result );
    return result;
  }

  public QxType getValue( final String propertyName ) {
    return ( QxType )properties.get( propertyName );
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append( "{\n" );
    String[] properties = getProperties();
    for( int i = 0; i < properties.length; i++ ) {
      String property = properties[ i ];
      QxType value = getValue( property );
      result.append( "  " );
      result.append( property );
      result.append( ": " );
      result.append( value );
      result.append( ";\n" );
    }
    result.append( "}" );
    return result.toString();
  }

  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj.getClass() == this.getClass() ) {
      StylePropertyMap other = ( StylePropertyMap )obj;
      result = properties.equals( other.properties );
    }
    return result;
  }

  public int hashCode() {
    return properties.hashCode();
  }
}
