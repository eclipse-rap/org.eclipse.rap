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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class ProtocolTestUtil_Test {

  @Test
  public void testJsonEqualsEmptyArray() throws JSONException {
    assertTrue( ProtocolTestUtil.jsonEquals( "[]", new JSONArray()) );
  }

  @Test
  public void testJsonEqualsNullArray() throws JSONException {
    JSONArray nullJsonArr = new JSONArray();
    nullJsonArr.put( JSONObject.NULL );
    JSONArray jsonArr = new JSONArray();
    jsonArr.put( new Integer( 2 ) );

    assertFalse( ProtocolTestUtil.jsonEquals( "[]", nullJsonArr ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ null ]", nullJsonArr ) );
    assertFalse( ProtocolTestUtil.jsonEquals( "[ null ]", jsonArr ) );
  }

  @Test
  public void testJsonEqualsArrayWithValue() throws JSONException {
    JSONArray jsonArr = new JSONArray();
    jsonArr.put( new Integer( 2 ) );

    assertTrue( ProtocolTestUtil.jsonEquals( "[ 2 ]", jsonArr ) );
    assertFalse( ProtocolTestUtil.jsonEquals( "[ 3 ]", jsonArr ) );
  }

}
