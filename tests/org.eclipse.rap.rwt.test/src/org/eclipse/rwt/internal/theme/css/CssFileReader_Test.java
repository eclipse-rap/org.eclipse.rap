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
package org.eclipse.rwt.internal.theme.css;

import java.io.*;

import junit.framework.TestCase;

import org.w3c.css.sac.CSSException;


public class CssFileReader_Test extends TestCase {

  private static final String PACKAGE = "resources/theme/";

  private static final String TEST_SYNTAX_CSS = "TestSyntax.css";

  private static final String TEST_INVALID_CSS = "TestInvalidProps.css";

  public void testSyntax() throws Exception {
    InputStream inStream = getInputStream( TEST_SYNTAX_CSS );
    assertNotNull( inStream );
    
    // capture stderr
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( stderr ) );

    CssFileReader reader = new CssFileReader();
    try {
      StyleSheet styleSheet = reader.parse( inStream, TEST_SYNTAX_CSS, null );
      StyleRule[] rules = styleSheet.getStyleRules();
      assertNotNull( rules );
      assertTrue( rules.length > 0 );
    } finally {
      inStream.close();
    }

    // check for problems
    assertTrue( stderr.size() > 0 );

    CSSException[] problems = reader.getProblems();
    assertNotNull( problems );
    assertTrue( problems.length > 0 );
    assertTrue( containsProblem( problems, "import rules not supported" ) );
    assertTrue( containsProblem( problems, "page rules not supported" ) );
  }

  public void testInvalidProperties() throws Exception {
    InputStream inStream = getInputStream( TEST_INVALID_CSS );
    assertNotNull( inStream );

    // capture stderr
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( stderr ) );

    CssFileReader reader = new CssFileReader();
    try {
      StyleSheet styleSheet = reader.parse( inStream, TEST_INVALID_CSS, null );
      StyleRule[] rules = styleSheet.getStyleRules();
      assertNotNull( rules );
      assertTrue( rules.length > 0 );
      ConditionalValue[] values = styleSheet.getValues( "Button", "font" );
      assertNotNull( values );
      assertEquals( 1, values.length );
      assertNotNull( values[ 0 ].value );
    } finally {
      inStream.close();
    }

    // check for problems
    assertTrue( stderr.size() > 0 );

    CSSException[] problems = reader.getProblems();
    assertNotNull( problems );
    assertTrue( problems.length > 0 );
    assertTrue( containsProblem( problems, "property font" ) );
    assertTrue( containsProblem( problems, "property color" ) );
    assertTrue( containsProblem( problems, "property padding" ) );
  }

  private static InputStream getInputStream( final String fileName ) {
    ClassLoader classLoader = CssFileReader_Test.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( PACKAGE + fileName );
    return inStream;
  }

  private boolean containsProblem( final Object[] array, final String part ) {
    boolean result = false;
    for( int i = 0; i < array.length && !result; i++ ) {
      result |= array[ i ].toString().indexOf( part ) != -1;
    }
    return result ;
  }
}
