/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test.fancy;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.graphics.Color;


public class ToolTipFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testToolTipColor() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "ToolTip", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolTipBackgroundColor() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "ToolTip", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "e9f3d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolTipBorder() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "ToolTip", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#abe033", border.color );
  }
  
  public void testToolTipBorderRadius() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "ToolTip", 
                                             "border-radius", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testToolTipFont() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "ToolTip", 
                                             "font", 
                                             selector );
    QxFont font = ( QxFont ) cssValue;
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertEquals( 12, font.size );
    assertFalse( font.bold );
  }
    
}
