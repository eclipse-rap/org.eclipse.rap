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


public class JsonUtil_Test extends TestCase {

  public void testStringToJson() throws Exception {
    assertEquals( "null", JsonUtil.toJson( ( String )null ) );
    // empty string
    assertEquals( "\"\"", JsonUtil.toJson( "" ) );
    // one char
    assertEquals( "\"a\"", JsonUtil.toJson( "a" ) );
    // leading and trailing white spaces
    assertEquals( "\" a b \"", JsonUtil.toJson( " a b " ) );
    // new line
    assertEquals( "\"a\n\"", JsonUtil.toJson( "a\n" ) );
    // escape a\b -> "a\\b"
    assertEquals( "\"a\\\\b\"", JsonUtil.toJson( "a\\b" ) );
    // escape a"b -> "a\"b"
    assertEquals( "\"a\\\"b\"", JsonUtil.toJson( "a\"b" ) );
    // escape a\"b\" -> "a\\\"b\\\""
    assertEquals( "\"a\\\\\\\"b\\\\\\\"\"", JsonUtil.toJson( "a\\\"b\\\"" ) );
  }

  public void testArrayToJson() throws Exception {
    assertEquals( "null", JsonUtil.toJson( ( JsonArray )null ) );
  }

  public void testObjectToJson() throws Exception {
    assertEquals( "null", JsonUtil.toJson( ( JsonObject )null ) );
  }
}
