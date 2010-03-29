/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.StyleRule;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.w3c.css.sac.CSSException;


public class StyleSheetBuilder_Test extends TestCase {

  public void testEmpty() {
    StyleSheetBuilder builder = new StyleSheetBuilder();
    StyleSheet styleSheet = builder.getStyleSheet();
    assertEquals( 0, styleSheet.getStyleRules().length );
  }

  public void testAddStyleSheet() throws CSSException, IOException {
    StyleSheetBuilder builder = new StyleSheetBuilder();
    String css1 = "Button { color: red; }";
    builder.addStyleSheet( ThemeTestUtil.createStyleSheet( css1 ) );
    String css2 = "Label { color: blue; }";
    builder.addStyleSheet( ThemeTestUtil.createStyleSheet( css2 ) );
    StyleSheet styleSheet = builder.getStyleSheet();
    assertEquals( 2, styleSheet.getStyleRules().length );
  }

  public void testAddStyleRule() throws CSSException, IOException {
    StyleSheetBuilder builder = new StyleSheetBuilder();
    String css1 = "Button { color: red; }";
    StyleSheet styleSheet1 = ThemeTestUtil.createStyleSheet( css1 );
    StyleRule styleRule1 = styleSheet1.getStyleRules()[0];
    builder.addStyleRule( styleRule1 );
    String css2 = "Label { color: blue; }";
    StyleSheet styleSheet2 = ThemeTestUtil.createStyleSheet( css2 );
    StyleRule styleRule2 = styleSheet2.getStyleRules()[0];
    builder.addStyleRule( styleRule2 );
    StyleSheet styleSheet = builder.getStyleSheet();
    assertEquals( 2, styleSheet.getStyleRules().length );
    assertSame( styleRule1, styleSheet.getStyleRules()[0] );
    assertSame( styleRule2, styleSheet.getStyleRules()[1] );
  }

}
