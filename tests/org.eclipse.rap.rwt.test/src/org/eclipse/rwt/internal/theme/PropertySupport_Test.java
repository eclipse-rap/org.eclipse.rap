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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.StyleRule;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class PropertySupport_Test extends TestCase {

  private static final String CUSTOM_THEME_ID = "Custom";
  private ThemeManager manager;

  protected void setUp() throws Exception {
    final ClassLoader classLoader = PropertySupport_Test.class.getClassLoader();
    ResourceLoader loader = new ResourceLoader() {
      
      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( "resources/theme/"
                                                + resourceName );
      }
    };
    manager = ThemeManager.getInstance();
    manager.initialize();
    String file = "TestExample.css";
    manager.registerTheme( CUSTOM_THEME_ID, "Custom Theme", file, loader );
  }

  protected void tearDown() throws Exception {
    manager.reset();
  }

  public void testGetVariants() {
    Theme theme = manager.getTheme( CUSTOM_THEME_ID );
    String[] variants = PropertySupport.getVariants( theme );
    assertEquals( 2, variants.length );
    assertTrue( "special".equals( variants[ 0 ] )
                || "special".equals( variants[ 1 ] ) );
    assertTrue( "special-blue".equals( variants[ 0 ] )
                || "special-blue".equals( variants[ 1 ] ) );
  }

  public void testCreateStyleSheet() {
    Theme theme = manager.getTheme( CUSTOM_THEME_ID );
    ThemeProperty[] properties = manager.getThemeProperties();
    StyleSheet styleSheet
      = PropertySupport.createStyleSheetFromProperties( properties, theme );
    assertNotNull( styleSheet );
    StyleRule[] styleRules = styleSheet.getStyleRules();
    assertTrue( styleRules.length > 0 );
    String[] buttonVariants = styleSheet.getVariants( "Button" );
    assertTrue( buttonVariants.length > 0 );
    String[] listVariants = styleSheet.getVariants( "List" );
    assertEquals( 2, listVariants.length );
  }
}
