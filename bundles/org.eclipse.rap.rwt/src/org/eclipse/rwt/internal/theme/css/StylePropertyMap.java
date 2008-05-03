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

import org.w3c.css.sac.LexicalUnit;

public class StylePropertyMap {

  private final Map properties = new HashMap();

  public LexicalUnit getProperty( final String key ) {
    return ( LexicalUnit )properties.get( key );
  }

  public void setProperty( final String key, final LexicalUnit value ) {
    if( key == null || value == null ) {
      throw new NullPointerException( "null argument" );
    }
    properties.put( key, value );
  }

  public String[] getKeys() {
    Set keySet = properties.keySet();
    String[] result = new String[ keySet.size() ];
    keySet.toArray( result );
    return result;
  }

  public void merge( final StylePropertyMap styles ) {
    String[] keys = styles.getKeys();
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      properties.put( key, styles.getProperty( key ) );
    }
  }
}
