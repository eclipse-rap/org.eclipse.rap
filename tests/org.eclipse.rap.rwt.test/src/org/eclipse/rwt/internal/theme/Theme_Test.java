/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class Theme_Test extends TestCase {

  private static final String PACKAGE = "resources/theme/";

  private static final String TEST_SYNTAX_CSS = "TestExample.css";

  public void testNothing() throws Exception {
    // TODO wrte some tests
    CssFileReader reader = new CssFileReader();
    InputStream inStream = getInputStream( TEST_SYNTAX_CSS );
    assertNotNull( inStream );
    StyleSheet styleSheet = reader.parse( inStream, TEST_SYNTAX_CSS, null );
    assertNotNull( styleSheet );
    String name = "TestTheme";
    Theme theme = new Theme( name, styleSheet );
    assertEquals( name, theme.getName() );
    assertEquals( styleSheet, theme.getStyleSheet() );
    assertNotNull( theme.getValues() );
    assertTrue( theme.getValues().length > 0 );
    assertNull( theme.getValuesMap() );
    assertNull( theme.getJsId() );
    String jsId = "some.nifty.js.id";
    theme.setJsId( jsId );
    assertEquals( jsId, theme.getJsId() );
    theme.initValuesMap( new ThemeableWidget[ 0 ]  );
    assertNotNull( theme.getValuesMap() );
  }

  private static InputStream getInputStream( final String fileName ) {
    ClassLoader classLoader = Theme_Test.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( PACKAGE + fileName );
    return inStream;
  }
}
