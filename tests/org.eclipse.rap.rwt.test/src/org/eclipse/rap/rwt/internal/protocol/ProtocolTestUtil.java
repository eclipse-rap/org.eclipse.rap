/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProtocolTestUtil {

  public static boolean jsonEquals( String json, JSONArray actualArray ) throws JSONException {
    boolean result = true;
    JSONArray expectedArray = new JSONArray( json );
    if( expectedArray.length() == actualArray.length() ) {
      for( int i = 0; i < expectedArray.length(); i++ ) {
        Object expected = expectedArray.get( i );
        Object actual = actualArray.get( i );
        boolean equal =    ( expected == null && actual == JSONObject.NULL )
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

}
