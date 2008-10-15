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

  public void testParseSac() throws Exception {
    ClassLoader classLoader = CssFileReader_Test.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( PACKAGE
                                                            + TEST_SYNTAX_CSS );
    assertNotNull( inStream );
    CssFileReader reader = new CssFileReader();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( stderr ) );
    StyleSheet styleSheet = reader.parse( inStream, TEST_SYNTAX_CSS, null );
    assertTrue( stderr.size() > 0 );
    StyleRule[] rules = styleSheet.getStyleRules();
    CSSException[] problems = reader.getProblems();
    assertNotNull( rules );
    assertTrue( rules.length > 0 );
    assertNotNull( problems );
    assertTrue( problems.length > 0 );
    assertTrue( containsProblem( problems, "import rules not supported" ) );
    assertTrue( containsProblem( problems, "page rules not supported" ) );
    inStream.close();
  }

  private boolean containsProblem( final Object[] array, final String part ) {
    boolean result = false;
    for( int i = 0; i < array.length && !result; i++ ) {
      result |= array[ i ].toString().indexOf( part ) != -1;
    }
    return result ;
  }
}
