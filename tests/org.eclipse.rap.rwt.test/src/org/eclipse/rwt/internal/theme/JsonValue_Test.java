/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
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


public class JsonValue_Test extends TestCase {

  public void testQuoteString() {
    // empty string
    assertEquals( "\"\"", JsonValue.quoteString( "" ) );
    // one char
    assertEquals( "\"a\"", JsonValue.quoteString( "a" ) );
    // leading and trailing white spaces
    assertEquals( "\" a b \"", JsonValue.quoteString( " a b " ) );
    // new line
    assertEquals( "\"a\n\"", JsonValue.quoteString( "a\n" ) );
    // escape a\b -> "a\\b"
    assertEquals( "\"a\\\\b\"", JsonValue.quoteString( "a\\b" ) );
    // escape a"b -> "a\"b"
    assertEquals( "\"a\\\"b\"", JsonValue.quoteString( "a\"b" ) );
    // escape a\"b\" -> "a\\\"b\\\""
    assertEquals( "\"a\\\\\\\"b\\\\\\\"\"",
                  JsonValue.quoteString( "a\\\"b\\\"" ) );
  }

  public void testValueOf() {
    assertEquals( "\"\"", JsonValue.valueOf( "" ).toString() );
    assertEquals( "\"Hallo\"", JsonValue.valueOf( "Hallo" ).toString() );
    assertEquals( "\"\\\"Hallo\\\"\"",
                  JsonValue.valueOf( "\"Hallo\"" ).toString() );
    assertEquals( "23", JsonValue.valueOf( 23 ).toString() );
    assertEquals( "0", JsonValue.valueOf( 0 ).toString() );
    assertEquals( "-1", JsonValue.valueOf( -1 ).toString() );
    assertEquals( "true", JsonValue.valueOf( true ).toString() );
    assertEquals( "false", JsonValue.valueOf( false ).toString() );
    assertEquals( "null", JsonValue.valueOf( null ).toString() );
    assertSame( JsonValue.valueOf( true ), JsonValue.valueOf( true ) );
    assertSame( JsonValue.valueOf( false ), JsonValue.valueOf( false ) );
    assertSame( JsonValue.valueOf( null ), JsonValue.valueOf( null ) );
  }
}
