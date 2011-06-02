/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.util.ParamCheck;


public class StylePropertyMap implements IStylePropertyMap {

  private final Map<String,QxType> properties;

  public StylePropertyMap() {
    properties = new HashMap<String,QxType>();
  }

  public void setProperty( String key, QxType value ) {
    ParamCheck.notNull( key, "key" );
    ParamCheck.notNull( value, "value" );
    properties.put( key, value );
  }

  public String[] getProperties() {
    Set<String> keySet = properties.keySet();
    return keySet.toArray( new String[ keySet.size() ] );
  }

  public QxType getValue( String propertyName ) {
    return properties.get( propertyName );
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

  public boolean equals( Object obj ) {
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
