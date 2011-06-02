/*******************************************************************************
* Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.themes.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.IStylePropertyMap;
import org.eclipse.rwt.internal.theme.css.SelectorExt;
import org.eclipse.rwt.internal.theme.css.StyleRule;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;


@SuppressWarnings("restriction")
public abstract class ThemeTestCase extends TestCase {

  protected void setUp() {
    Fixture.setUp();
  }

  protected void tearDown() {
    Fixture.tearDown();
  }

  protected void processCssTestFile( final Class clazz, final String fileName )
    throws IOException
  {
    StyleRule[] rules = readTestCssFile( clazz, fileName );
    for( int i = 0; i < rules.length; i++ ) {
      StyleRule styleRule = rules[ i ];
      processTestStyleRule( styleRule );
    }
  }

  private static void processTestStyleRule( final StyleRule styleRule ) {
    SelectorList selectors = styleRule.getSelectors();
    IStylePropertyMap properties = styleRule.getProperties();
    int length = selectors.getLength();
    for( int i = 0; i < length; i++ ) {
      Selector selector = selectors.item( i );
      SelectorExt selectorExt = ( SelectorExt )selector;
      checkProperties( selectorExt, properties );
    }
  }

  private static void checkProperties( final SelectorExt selector,
                                       final IStylePropertyMap expectedValues )
  {
    String[] properties = expectedValues.getProperties();
    for( int i = 0; i < properties.length; i++ ) {
      String property = properties[ i ];
      QxType expected = expectedValues.getValue( property );
      SimpleSelector config = new SimpleSelector( selector.getConstraints() );
      QxType actual = ThemeUtil.getCssValue( selector.getElementName(),
                                             property,
                                             config );
      if( !actual.equals( expected ) ) {
        String message =   "Css test failed for "
                         + createSelectorString( selector )
                         + ", property "
                         + property;
        assertEquals( message, expected, actual );
      }
    }
  }

  private static String createSelectorString( final SelectorExt item ) {
    StringBuffer result = new StringBuffer();
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

  private static StyleRule[] readTestCssFile( final Class clazz,
                                              final String fileName )
    throws IOException
  {
    final ClassLoader classLoader = clazz.getClassLoader();
    String packageName = clazz.getPackage().getName().replace( '.', '/' );
    String filePath = packageName + "/" + fileName;
    InputStream inputStream = classLoader.getResourceAsStream( filePath );
    ResourceLoader loader = ThemesTestUtil.RESOURCE_LOADER;
    StyleSheet styleSheet = CssFileReader.readStyleSheet( inputStream,
                                                          filePath,
                                                          loader );
    StyleRule[] rules = styleSheet.getStyleRules();
    return rules;
  }
}
