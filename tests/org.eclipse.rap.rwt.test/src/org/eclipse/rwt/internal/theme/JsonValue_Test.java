/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class JsonValue_Test extends TestCase {

  public void testValueOfInts() {
    assertEquals( "0", JsonValue.valueOf( 0 ).toString() );
    assertEquals( "23", JsonValue.valueOf( 23 ).toString() );
    assertEquals( "-1", JsonValue.valueOf( -1 ).toString() );
    assertEquals( "2147483647", JsonValue.valueOf( Integer.MAX_VALUE ).toString() );
    assertEquals( "-2147483648", JsonValue.valueOf( Integer.MIN_VALUE ).toString() );
  }

  public void testValueOfFloats() {
    assertEquals( "10.0", JsonValue.valueOf( 10f ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123f ).toString() );
    assertEquals( "-1.23E7", JsonValue.valueOf( -12300000f ).toString() );
  }

  public void testValueOfDoubles() {
    assertEquals( "10.0", JsonValue.valueOf( 10d ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123d ).toString() );
    assertEquals( "1.7976931348623157E308", JsonValue.valueOf( 1.7976931348623157E308d ).toString() );
  }

  public void testValueOfBooleans() {
    assertEquals( "true", JsonValue.valueOf( true ).toString() );
    assertEquals( "false", JsonValue.valueOf( false ).toString() );
  }

  public void testValueOfStrings() {
    assertEquals( "null", JsonValue.valueOf( null ).toString() );
    assertEquals( "\"\"", JsonValue.valueOf( "" ).toString() );
    assertEquals( "\"Hallo\"", JsonValue.valueOf( "Hallo" ).toString() );
    assertEquals( "\"\\\"Hallo\\\"\"", JsonValue.valueOf( "\"Hallo\"" ).toString() );
  }

  public void testValueOfConstants() {
    assertSame( JsonValue.valueOf( true ), JsonValue.valueOf( true ) );
    assertSame( JsonValue.valueOf( false ), JsonValue.valueOf( false ) );
    assertSame( JsonValue.valueOf( null ), JsonValue.valueOf( null ) );
  }

  public void testQuoteString() {
    // empty string
    assertEquals( "\"\"", JsonValue.quoteAndEscapeString( "" ) );
    // one char
    assertEquals( "\"a\"", JsonValue.quoteAndEscapeString( "a" ) );
    // leading and trailing white spaces
    assertEquals( "\" a b \"", JsonValue.quoteAndEscapeString( " a b " ) );
  }
  
  public void testEscapeStringWithQuotes() {
    // escape a\b -> "a\\b"
    assertEquals( "a\\\\b", JsonValue.escapeString( "a\\b" ) );
    // escape a"b -> "a\"b"
    assertEquals( "a\\\"b", JsonValue.escapeString( "a\"b" ) );
    // escape a\"b\" -> "a\\\"b\\\""
    assertEquals( "a\\\\\\\"b\\\\\\\"", JsonValue.escapeString( "a\\\"b\\\"" ) );
  }

  public void testEscapeStringWithNewLines() {
    assertEquals( "a\\n", JsonValue.escapeString( "a\n" ) );
    assertEquals( "a\\r\\nb", JsonValue.escapeString( "a\r\nb" ) );
  }

  public void testEscapeStringWithTabs() {
    assertEquals( "a\\tb", JsonValue.escapeString( "a\tb" ) );
  }
}
