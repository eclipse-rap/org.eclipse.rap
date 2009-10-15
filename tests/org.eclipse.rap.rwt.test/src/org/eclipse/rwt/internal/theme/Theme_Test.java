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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class Theme_Test extends TestCase {

  private static final String TEST_SYNTAX_CSS = "TestExample.css";

  public void testCreate() throws Exception {
    String jsId = "some.nifty.js.id";
    String name = "TestTheme";
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    assertNotNull( styleSheet );
    try {
      new Theme( null, name, styleSheet, new ThemeableWidget[ 0 ] );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Theme( jsId, null, styleSheet, new ThemeableWidget[ 0 ] );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Theme( jsId, name, null, new ThemeableWidget[ 0 ] );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Theme( jsId, name, styleSheet, null );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
    Theme theme = new Theme( jsId, name, styleSheet, new ThemeableWidget[ 0 ] );
    assertEquals( jsId, theme.getJsId() );
    assertEquals( name, theme.getName() );
    assertNotNull( theme.getValuesMap() );
  }
}
