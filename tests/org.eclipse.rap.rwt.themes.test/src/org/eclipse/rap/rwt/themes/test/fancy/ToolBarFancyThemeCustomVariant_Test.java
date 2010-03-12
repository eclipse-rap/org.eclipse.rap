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
import org.eclipse.rwt.internal.theme.QxDimension;
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.graphics.Color;


public class ToolBarFancyThemeCustomVariant_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testToolBarHeaderToolbarColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolBarHeaderToolbarPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 25, dim.left );
  }
  
  public void testToolBarHeaderToolbarSpacing() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "spacing", 
                                             selector );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 25, dim.value );
  }
  
  public void testToolItemHeaderToolbarColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemHeaderToolbarBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemHeaderToolbarPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 5, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 5, dim.bottom );
    assertEquals( 5, dim.left );
  }
  
  public void testToolItemHeaderToolbarSpacing() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "spacing", 
                                             selector );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 10, dim.value );
  }
  
  public void testToolItemHeaderToolbarColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemHeaderToolbarBackgroundColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolItemHeaderToolbarBackgroundImageHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testToolItemHeaderToolbarBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#3b8fc2", border.color );
  }
  
  public void testToolItemHeaderToolbarPaddingHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 5, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 5, dim.bottom );
    assertEquals( 5, dim.left );
  }
  
  public void testToolItemDropDownIconHeaderToolbarBackgroundImage() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_arrow.png", image.path );
  }
  
  public void testToolItemDropDownIconHeaderToolbarBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemDropDownIconHeaderToolbarBackgroundImageHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_arrow.png", image.path );
  }
  
  public void testToolItemDropDownIconHeaderToolbarBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-toolbar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#3b8fc2", border.color );
  }
  
  public void testToolItemHeaderOverflowColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "656565", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemHeaderOverflowBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemHeaderOverflowPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 25, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testToolItemHeaderOverflowColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "3b8fc2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemHeaderOverflowBackgroundColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolItemHeaderOverflowBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemDropDownIconHeaderOverflowBackgroundImage() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_arrow.png", image.path );
  }
  
  public void testToolItemDropDownIconHeaderOverflowBackgroundImageHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_arrow.png", image.path );
  }
  
  public void testToolItemDropDownIconHeaderOverflowBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".header-overflow", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolBarMenuBarColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "656565", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolBarMenuBarBackgroundColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolBarMenuBarPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testToolBarMenuBarSpacing() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "spacing", 
                                             selector );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 25, dim.value );
  }
  
  public void testToolItemMenuBarColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "656565", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemMenuBarBackgroundColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolItemMenuBarBackgroundImage() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testToolItemMenuBarBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemMenuBarPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 10, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 10, dim.left );
  }
  
  public void testToolItemMenuBarSpacing() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "spacing", 
                                             selector );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 5, dim.value );
  }
  
  public void testToolItemMenuBarColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolItemMenuBarBackgroundColorHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolItemMenuBarBackgroundImageHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#9acd2b", colors[ 0 ] );
    assertEquals( "#8fbe29", colors[ 1 ] );
  }
  
  public void testToolItemMenuBarBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#83a438", border.color );
  }
  
  public void testToolItemMenuBarBorderRadiusHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border-radius", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 3, dim.top );
    assertEquals( 3, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testToolItemMenuBarPaddingHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 10, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 10, dim.left );
  }
  
  public void testToolItemDropDownIconMenuBarBackgroundImage() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/menu_arrow.png", image.path );
  }
  
  public void testToolItemDropDownIconMenuBarBackgroundImageHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/menu_arrow_hover.png", image.path );
  }
  
  public void testToolItemDropDownIconMenuBarBorderHover() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBar", ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#83a438", border.color );
  }
  
  public void testToolBarViewToolbarBackgroundColor() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolBarViewToolbarBackgroundPadding() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testToolBarViewToolbarSpacing() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolBar", 
                                             "spacing", 
                                             selector );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 2, dim.value );
  }
  
  public void testToolItemViewToolbarBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testToolItemViewToolbarBorderSelected() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar", ":selected" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#8fbe29", border.color );
  }
  
  public void testToolItemViewToolbarPaddingSelected() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar", ":selected" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem", 
                                             "padding", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testToolItemDropDownIconViewToolbarBackgroundImage() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar", ":selected" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "background-image", 
                                             selector );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/viewMenu.png", image.path );
  }
  
  public void testToolItemDropDownIconViewToolbarBorder() {
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".viewToolbar", ":selected" } );
    QxType cssValue = ThemeUtil.getCssValue( "ToolItem-DropDownIcon", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
}
