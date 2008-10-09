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

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class JsonObject_Test extends TestCase {

  public void testAppend() throws Exception {
    JsonObject object = new JsonObject();
    assertEquals( "{}", object.toString() );
    object.append( "a", "b" );
    assertEquals( "{\n\"a\": \"b\"\n}", object.toString() );
    object.append( "b", "c" );
    assertEquals( "{\n\"a\": \"b\",\n\"b\": \"c\"\n}", object.toString() );
  }

  public void testAppendArray() throws Exception {
    JsonObject object = new JsonObject();
    object.append( "a", "b" );
    object.append( "b", new JsonArray() );
    assertEquals( "{\n\"a\": \"b\",\n\"b\": []\n}", object.toString() );
  }
  
  public void testAppendObject() throws Exception {
    JsonObject object = new JsonObject();
    object.append( "a", "b" );
    object.append( "b", new JsonObject() );
    assertEquals( "{\n\"a\": \"b\",\n\"b\": {}\n}", object.toString() );
  }
}
