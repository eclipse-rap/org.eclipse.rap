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
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ButtonFancyThemeCustomVariant_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testClearButtonColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testClearButtonBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testClearButtonBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testClearButtonBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".clearButton", 
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testClearButtonBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".clearButton", 
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testClearButtonBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testClearButtonPadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.top );
  }
  
  public void testClearButtonFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".clearButton" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testConfigMenuButtonColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testConfigMenuButtonBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testConfigMenuButtonBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testConfigMenuButtonBackgroundImageSelected() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".configMenuButton",     
                                           ":selected" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testConfigMenuButtonBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".configMenuButton",     
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testConfigMenuButtonBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".configMenuButton",     
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testConfigMenuButtonBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testConfigMenuButtonPadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 1, dim.bottom );
    assertEquals( 1, dim.top );
    assertEquals( 1, dim.left );
    assertEquals( 1, dim.right );
  }
  
  public void testConfigMenuButtonFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".configMenuButton" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testViewCloseColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testViewCloseFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testViewCloseBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testViewCloseBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_active_close_active.png", 
                  image.path );
  }
  
  public void testViewCloseBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".viewClose", 
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_active_close_active.png", 
                  image.path );
  }
  
  public void testViewCloseBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".viewClose", 
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_active_close_active_hover.png", 
                  image.path );
  }
  
  public void testViewClosePadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testViewCloseBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewClose" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewClose" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );   
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testViewCloseInactiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewCloseInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testViewCloseInactiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewCloseInactive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testViewCloseInactiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewCloseInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testViewCloseInactiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewCloseInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_inactive_close_active.png", 
                  image.path );
  }
  
  public void testViewCloseInactiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".viewCloseInactive", 
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_inactive_close_active.png", 
                  image.path );
  }
  
  public void testViewCloseInactiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".viewCloseInactive", 
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_inactive_close_active_hover.png", 
                  image.path );
  }
  
  public void testViewCloseInactiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".viewCloseInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testToolbarOverflowInactiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolbarOverflowInactiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolbarOverflowInactiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow.png", image.path );
  }
  
  public void testToolbarOverflowInactiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow.png", image.path );
  }
  
  public void testToolbarOverflowInactiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_hover.png", image.path );
  }
  
  public void testToolbarOverflowInactiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testToolbarOverflowInactivePadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testToolbarOverflowInactiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".toolbarOverflowInactive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testToolbarOverflowActiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testToolbarOverflowActiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testToolbarOverflowActiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_active.png", image.path );
  }
  
  public void testToolbarOverflowActiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_active.png", image.path );
  }
  
  public void testToolbarOverflowActiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/toolbar_overflow_hover_active.png", 
                  image.path );
  }
  
  public void testToolbarOverflowActiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testToolbarOverflowActivePadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".toolbarOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testToolbarOverflowActiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".toolbarOverflowActive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartActiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 62, color.red );
    assertEquals( 64, color.green );
    assertEquals( 66, color.blue );
  }
  
  public void testPartActiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testPartActiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartActiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartActiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartActiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartActiveFont() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertFalse( font.bold );
  }
  
  public void testPartActiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".partActive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartInactiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 255, color.red );
    assertEquals( 255, color.green );
    assertEquals( 255, color.blue );
  }
  
  public void testPartInactiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testPartInactiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInactiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInactiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInactiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartInactiveFont() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertFalse( font.bold );
  }
  
  public void testPartInactiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".partInactive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartInActiveActiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 73, color.red );
    assertEquals( 95, color.green );
    assertEquals( 21, color.blue );
  }
  
  public void testPartInActiveActiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testPartInActiveActiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInActiveActiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInActiveActiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPartInActiveActiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPartInActiveActiveFont() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".partInActiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertFalse( font.bold );
  }
  
  public void testPartInActiveActiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "partInActiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".partInActiveActive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPerspectiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 204, color.red );
    assertEquals( 204, color.green );
    assertEquals( 204, color.blue );
  }
  
  public void testPerspectiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testPerspectiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPerspectiveFont() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertFalse( font.bold );
  }
  
  public void testPerspectiveFontHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspective",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertTrue( font.bold );
  }
  
  public void testPerspectiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspective" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".perspective" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPerspectiveActiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 112, color.red );
    assertEquals( 198, color.green );
    assertEquals( 243, color.blue );
  }
  
  public void testPerspectiveActiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testPerspectiveActiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveActiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveActiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertNull( image.path );
  }
  
  public void testPerspectiveActiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testPerspectiveActiveFont() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".perspectiveActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    assertEquals( 11, font.size );
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertFalse( font.bold );
  }
  
  public void testPerspectiveActiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "perspectiveActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".perspectiveActive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTabOverflowActiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabOverflowActiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testTabOverflowActiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_active.png", image.path );
  }
  
  public void testTabOverflowActiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_active.png", image.path );
  }
  
  public void testTabOverflowActiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_active_hover.png", 
                  image.path );
  }
  
  public void testTabOverflowActiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowActive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTabOverflowActivePadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".tabOverflowActive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testTabOverflowActiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".tabOverflowActive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTabOverflowInactiveColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabOverflowInactiveBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testTabOverflowInactiveBackgroundImage() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_inactive.png", image.path );
  }
  
  public void testTabOverflowInactiveBackgroundImagePressed() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive",
                                           ":pressed" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_inactive.png", image.path );
  }
  
  public void testTabOverflowInactiveBackgroundImageHover() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive",
                                           ":hover" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "img/fancy/stack_tab_overflow_inactive_hover.png", 
                  image.path );
  }
  
  public void testTabOverflowInactiveBorder() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
                                           ".tabOverflowInactive" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTabOverflowInactivePadding() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ".tabOverflowInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testTabOverflowInactiveFocusIndicator() {
    Button button = createButton( SWT.PUSH );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "tabOverflowInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", 
        ".tabOverflowInactive" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border",
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  
  /*
   * Little Helper Method to create buttons
   */
  private Button createButton( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display );
    assertNotNull( shell );
    
    Button button = new Button( shell, style );
    button.setText( "a test text" );
    return button;
  } 
  
}
