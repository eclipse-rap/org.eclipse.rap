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

  public void testQuoteString() throws Exception {
    try {
      JsonUtil.quoteString( null );
      fail( "NPE expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    // empty string
    assertEquals( "\"\"", JsonUtil.quoteString( "" ) );
    // one char
    assertEquals( "\"a\"", JsonUtil.quoteString( "a" ) );
    // leading and trailing white spaces
    assertEquals( "\" a b \"", JsonUtil.quoteString( " a b " ) );
    // new line
    assertEquals( "\"a\n\"", JsonUtil.quoteString( "a\n" ) );
    // escape a\b -> "a\\b"
    assertEquals( "\"a\\\\b\"", JsonUtil.quoteString( "a\\b" ) );
    // escape a"b -> "a\"b"
    assertEquals( "\"a\\\"b\"", JsonUtil.quoteString( "a\"b" ) );
    // escape a\"b\" -> "a\\\"b\\\""
    assertEquals( "\"a\\\\\\\"b\\\\\\\"\"", JsonUtil.quoteString( "a\\\"b\\\"" ) );
  }
}
