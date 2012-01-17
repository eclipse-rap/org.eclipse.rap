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
package org.eclipse.rwt.internal.protocol;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.json.*;


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

  /**
   * Temporary helper method to migrate old JSWriter-based tests to protocol.
   * Replace Fixture.getAllMarkup with this method.
   */
  public static String getMessageScript() {
    String result = "";
    Message message = Fixture.getProtocolMessage();
    if( message.getOperationCount() > 0 ) {
      CallOperation operation = ( CallOperation )message.getOperation( 0 );
      result = ( String )operation.getProperty( "content" );
    }
    return result;
  }

}
