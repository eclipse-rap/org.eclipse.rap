/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import org.eclipse.rap.rwt.internal.json.JsonArray;
import org.eclipse.rap.rwt.internal.json.JsonObject;


public class ProtocolTestUtil {

  public static boolean jsonEquals( String json, JsonArray actualArray ) {
    boolean result = true;
    JsonArray expectedArray = JsonArray.readFrom( json );
    if( expectedArray.size() == actualArray.size() ) {
      for( int i = 0; i < expectedArray.size(); i++ ) {
        Object expected = expectedArray.get( i );
        Object actual = actualArray.get( i );
        boolean equal =    ( expected == null && actual == JsonObject.NULL )
                        || ( expected != null && expected.equals( actual ) );
        if( !equal ) {
          result = false;
        }
      }
    } else {
      result = false;
    }
    return result;
  }

  public static String join( JsonArray array, String separator ) {
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < array.size(); i++ ) {
      if( i > 0 ) {
        result.append( separator );
      }
      result.append( array.get( i ).toString() );
    }
    return result.toString();
  }

}
