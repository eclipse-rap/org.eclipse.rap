/*******************************************************************************
* Copyright (c) 2010, 2014 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.themes.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.internal.theme.CssType;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rap.rwt.internal.theme.css.SelectorExt;
import org.eclipse.rap.rwt.internal.theme.css.StylePropertyMap;
import org.eclipse.rap.rwt.internal.theme.css.StyleRule;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;


@SuppressWarnings("restriction")
public abstract class ThemeTestBase {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    ThemesTestUtil.cleanupThemes();
    Fixture.tearDown();
  }

  protected void processCssTestFile( Class clazz, String fileName ) throws IOException {
    StyleRule[] rules = readTestCssFile( clazz, fileName );
    for( int i = 0; i < rules.length; i++ ) {
      StyleRule styleRule = rules[ i ];
      processTestStyleRule( styleRule );
    }
  }

  private static void processTestStyleRule( StyleRule styleRule ) {
    SelectorList selectors = styleRule.getSelectors();
    StylePropertyMap properties = styleRule.getProperties();
    int length = selectors.getLength();
    for( int i = 0; i < length; i++ ) {
      Selector selector = selectors.item( i );
      SelectorExt selectorExt = ( SelectorExt )selector;
      checkProperties( selectorExt, properties );
    }
  }

  private static void checkProperties( SelectorExt selector, StylePropertyMap expectedValues ) {
    String[] properties = expectedValues.getProperties();
    for( int i = 0; i < properties.length; i++ ) {
      String property = properties[ i ];
      CssType expected = expectedValues.getValue( property );
      SimpleSelector config = new SimpleSelector( selector.getConstraints() );
      CssType actual = ThemeUtil.getCssValue( selector.getElementName(), property, config );
      if( !actual.equals( expected ) ) {
        String message =   "Css test failed for "
                         + createSelectorString( selector )
                         + ", property "
                         + property;
        assertEquals( message, expected, actual );
      }
    }
  }

  private static String createSelectorString( SelectorExt item ) {
    StringBuilder result = new StringBuilder();
    String elementName = item.getElementName();
    result.append( elementName );
    String[] constraints = item.getConstraints();
    for( int i = 0; i < constraints.length; i++ ) {
      String constraint = constraints[ i ];
      result.append( constraint );
      if( constraint.startsWith( "[" ) ) {
        result.append( "]" );
      }
    }
    return result.toString();
  }

  private static StyleRule[] readTestCssFile( Class clazz, String fileName ) throws IOException {
    ClassLoader classLoader = clazz.getClassLoader();
    String packageName = clazz.getPackage().getName().replace( '.', '/' );
    String filePath = packageName + "/" + fileName;
    InputStream inputStream = classLoader.getResourceAsStream( filePath );
    StyleSheet styleSheet;
    try {
      ResourceLoader loader = ThemesTestUtil.RESOURCE_LOADER;
      styleSheet = CssFileReader.readStyleSheet( inputStream, filePath, loader );
    } finally {
      inputStream.close();
    }
    return styleSheet.getStyleRules();
  }

}
