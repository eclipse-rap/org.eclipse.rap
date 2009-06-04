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

import org.eclipse.swt.widgets.Widget;

public class ThemeDefinitionReader_Test extends TestCase {

  private static final String BUTTON_THEME_FILE
    = "org/eclipse/swt/internal/widgets/buttonkit/Button.theme.xml";
  
  private static final String SHELL_THEME_FILE
  = "org/eclipse/swt/internal/widgets/shellkit/Shell.theme.xml";

  public void testReadCss() throws Exception {
    ClassLoader loader = Widget.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( BUTTON_THEME_FILE );
    ThemeDefinitionReader reader = new ThemeDefinitionReader( is, "test" );
    try {
      reader.read();
    } finally {
      is.close();
    }
    IThemeCssElement[] elements = reader.getThemeCssElements();
    assertNotNull( elements );
    assertTrue( elements.length > 0 );
    assertEquals( "Button", elements[ 0 ].getName() );
    assertNotNull( elements[ 0 ].getDescription() );
    IThemeCssProperty[] properties = elements[ 0 ].getProperties();
    assertNotNull( properties );
    assertTrue( properties.length > 0 );
    IThemeCssAttribute[] styles = elements[ 0 ].getStyles();
    assertNotNull( styles );
    assertTrue( styles.length > 0 );
    IThemeCssAttribute[] states = elements[ 0 ].getStates();
    assertNotNull( states );
    assertTrue( states.length > 0 );
    assertTrue( properties.length > 0 );
    assertEquals( "color", properties[ 0 ].getName() );
    assertNotNull( properties[ 0 ].getDescription() );
  }

  public void testNestedElements() throws Exception {
    ClassLoader loader = Widget.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( SHELL_THEME_FILE );
    ThemeDefinitionReader reader = new ThemeDefinitionReader( is, "test" );
    try {
      reader.read();
    } finally {
      is.close();
    }
    IThemeCssElement[] elements = reader.getThemeCssElements();
    assertNotNull( elements );
    assertTrue( elements.length > 1 );
    assertEquals( "Shell", elements[ 0 ].getName() );
    assertEquals( "Shell-Titlebar", elements[ 1 ].getName() );
  }
}
