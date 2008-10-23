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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.css.StyleSheet.SelectorWrapper;


public class StyleSheet_Test extends TestCase {

  private static final String TEST_SELECTORS_CSS = "TestSelectors.css";
  private static final String TEST_EXAMPLE_CSS = "TestExample.css";

  private static final int ALL_RULE = 0;
  private static final int ELEMENT_RULE = 1;
  private static final int CLASS_RULE = 2;
  private static final int PSEUDO_CLASS_RULE = 3;
  private static final int ATTRIBUTE_RULE = 4;
  private static final int ATTRIBUTE_VALUE_RULE = 5;
  private static final int ONE_OF_ATTRIBUTE_RULE = 6;
  private static final int COMBINED_ATTRIBUTE_RULE = 7;
  private static final int SELECTOR_LIST_RULE = 8;

  // == Test that style rules match the correct elements ==

  public void testMatchAll() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ ALL_RULE ];
    StylableElement label = new StylableElement( "Label" );
    assertTrue( matchingRule.matches( label ) );
    StylableElement tree = new StylableElement( "Tree" );
    assertTrue( matchingRule.matches( tree ) );
    StylableElement list = new StylableElement( "NONE" );
    assertTrue( matchingRule.matches( list ) );
  }

  public void testMatchElement() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ ELEMENT_RULE ];
    StylableElement label = new StylableElement( "Label" );
    assertTrue( matchingRule.matches( label ) );
    StylableElement button = new StylableElement( "Button" );
    assertFalse( matchingRule.matches( button ) );
  }

  public void testMatchClass() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ CLASS_RULE ];
    StylableElement button = new StylableElement( "Button" );
    assertFalse( matchingRule.matches( button ) );
    button.setClass( "special" );
    assertTrue( matchingRule.matches( button ) );
    StylableElement label = new StylableElement( "Label" );
    assertFalse( matchingRule.matches( label ) );
    label.setClass( "special" );
    assertFalse( matchingRule.matches( label ) );
  }

  public void testMatchPseudoClass() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ PSEUDO_CLASS_RULE ];
    StylableElement list = new StylableElement( "List" );
    assertFalse( matchingRule.matches( list ) );
    list.setPseudoClass( "selected" );
    assertTrue( matchingRule.matches( list ) );
    StylableElement button = new StylableElement( "Button" );
    assertFalse( matchingRule.matches( button ) );
    button.setPseudoClass( "selected" );
    assertFalse( matchingRule.matches( button ) );
  }

  public void testMatchAttribute() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ ATTRIBUTE_RULE ];
    StylableElement text = new StylableElement( "Text" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "SIMPLE", "" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "SIMPLE", "x" );
    assertTrue( matchingRule.matches( text ) );
  }

  public void testMatchAttributeValue() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ ATTRIBUTE_VALUE_RULE ];
    StylableElement text = new StylableElement( "Text" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "SIMPLE" );
    assertTrue( matchingRule.matches( text ) );
    text.setAttribute( "style", "SIMPLE BORDER" );
    assertFalse( matchingRule.matches( text ) );
  }

  public void testMatchOneOfAttribute() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ ONE_OF_ATTRIBUTE_RULE ];
    StylableElement text = new StylableElement( "Text" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "x" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "BORDER", "x" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "BORDER" );
    // false as one-of attributes are currently not supported
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "style", "SIMPLE BORDER" );
    // false as one-of attributes are currently not supported
    assertFalse( matchingRule.matches( text ) );
  }

  public void testCombinedAttributes() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ COMBINED_ATTRIBUTE_RULE ];
    StylableElement text = new StylableElement( "Text" );
    text.setClass( "special" );
    text.setAttribute( "SIMPLE" );
    assertFalse( matchingRule.matches( text ) );
    text.setAttribute( "BORDER" );
    assertTrue( matchingRule.matches( text ) );
    text.resetClass( "special" );
    assertFalse( matchingRule.matches( text ) );
  }

  public void testSelectorList() throws Exception {
    StyleRule[] rules = ThemeTestUtil.getStyleSheet( TEST_SELECTORS_CSS ).getStyleRules();
    ElementMatcher matchingRule = rules[ SELECTOR_LIST_RULE ];
    StylableElement tree = new StylableElement( "Tree" );
    assertFalse( matchingRule.matches( tree ) );
    StylableElement label = new StylableElement( "Label" );
    assertTrue( matchingRule.matches( label ) );
    StylableElement button = new StylableElement( "Button" );
    button.setClass( "special" );
    assertTrue( matchingRule.matches( button ) );
    StylableElement list = new StylableElement( "List" );
    list.setPseudoClass( "selected" );
    assertTrue( matchingRule.matches( list ) );
    StylableElement table = new StylableElement( "Table" );
    assertFalse( matchingRule.matches( table ) );
  }

  // == Test other StyleSheet methods ==

  public void testGetValue() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    StylableElement button = new StylableElement( "Button" );
    button.setAttribute( "PUSH" );
    button.setAttribute( "BORDER" );
    QxType bgColor = styleSheet.getValue( "background-color", button );
    assertNotNull( bgColor );
    assertEquals( QxColor.valueOf( "#9dd0ea" ), bgColor );
    QxType color = styleSheet.getValue( "color", button );
    assertNotNull( color );
    assertEquals( QxColor.valueOf( "#705e42" ), color );
    button.setClass( "special" );
    QxType specialColor = styleSheet.getValue( "color", button );
    assertNotNull( specialColor );
    assertEquals( QxColor.valueOf( "red" ), specialColor );
    QxType specialBgCol = styleSheet.getValue( "background-color", button );
    assertNotNull( specialBgCol );
    assertEquals( QxColor.TRANSPARENT, specialBgCol );
  }

  public void testGetVariants() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    String[] variants = styleSheet.getVariants( "Button" );
    assertNotNull( variants );
    assertTrue( variants.length > 0 );
  }

  public void testGetMatchingStyleRules() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    SelectorWrapper[] styleRules = styleSheet.getMatchingStyleRules( "Button" );
    assertNotNull( styleRules );
    assertEquals( 13, styleRules.length );
    // ensure decreasing specificity
    int lastSpecificity = Specific.ID_SPEC * 1000;
    for( int i = 0; i < styleRules.length; i++ ) {
      int specificity = styleRules[ i ].selectorExt.getSpecificity();
      assertTrue( specificity <= lastSpecificity );
      lastSpecificity = specificity;
    }
  }

  public void testGetConditionalValues() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    ConditionalValue[] values = styleSheet.getValues( "Button",
                                                      "border" );
    assertNotNull( values );
    assertEquals( 5, values.length );
  }

  public void testNamespaces() {
//    Doesn't work with Batik parser
//    StyleRule[] rules = getStyleSheet( "TestNamespaces.css" ).getStyleRules();
//    assertEquals( 3, rules.length );
  }
}
