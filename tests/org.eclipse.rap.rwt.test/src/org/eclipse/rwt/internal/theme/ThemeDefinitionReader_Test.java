/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Widget;

public class ThemeDefinitionReader_Test extends TestCase {

  private static final String WIDGET_THEME_FILE
    = "org/eclipse/swt/internal/widgets/widgetkit/Widget.theme.xml";

  private static final String BUTTON_THEME_FILE
    = "org/eclipse/swt/internal/widgets/buttonkit/Button.theme.xml";
  
  private static final String SHELL_THEME_FILE
  = "org/eclipse/swt/internal/widgets/shellkit/Shell.theme.xml";

  public void testReadOld() throws Exception {
    final ClassLoader classLoader = Widget.class.getClassLoader();
    InputStream is = classLoader.getResourceAsStream( WIDGET_THEME_FILE );
    ResourceLoader loader = new ResourceLoader() {

      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( WIDGET_THEME_FILE );
      }
    };
    ThemeDefinitionReader reader
      = new ThemeDefinitionReader( is, "test", loader );
    try {
      reader.read();
    } finally {
      is.close();
    }
    ThemeProperty[] properties = reader.getThemeProperties();
    Map propertyMap = new HashMap();
    for( int i = 0; i < properties.length; i++ ) {
      ThemeProperty prop = properties[ i ];
      propertyMap.put( prop.name, prop.defValue );
    }
    Set keys = propertyMap.keySet();
    assertTrue( keys.size() > 0 );
    assertTrue( propertyMap.get( "widget.background" ) instanceof QxColor );
  }

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
