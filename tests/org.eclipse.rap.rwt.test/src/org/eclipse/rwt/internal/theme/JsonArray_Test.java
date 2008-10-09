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


public class JsonArray_Test extends TestCase {

  public void testAppend() throws Exception {
    JsonArray array = new JsonArray();
    assertEquals( "[]", array.toString() );
    array.append( "a" );
    assertEquals( "[ \"a\" ]", array.toString() );
    array.append( "b" );
    assertEquals( "[ \"a\", \"b\" ]", array.toString() );
  }

  public void testAppendArray() throws Exception {
    JsonArray array = new JsonArray();
    array.append( "a" );
    array.append( new JsonArray() );
    assertEquals( "[ \"a\", [] ]", array.toString() );
  }

  public void testAppendObject() throws Exception {
    JsonArray array = new JsonArray();
    array.append( "a" );
    array.append( new JsonObject() );
    assertEquals( "[ \"a\", {} ]", array.toString() );
  }
}
