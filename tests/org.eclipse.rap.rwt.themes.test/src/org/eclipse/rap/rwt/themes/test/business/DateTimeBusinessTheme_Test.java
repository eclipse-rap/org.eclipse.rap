/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test.business;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class DateTimeBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testDateTimeColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "464a4e", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeBackgroundColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 252, color.red );
    assertEquals( 252, color.green );
    assertEquals( 252, color.blue );
  }
  
  public void testDateTimeBorder() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 1, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testDateTimeBorderRadius() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testDateTimePading() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 1, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 1, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testDateTimeFont() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( time, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertEquals( 11, font.size );
    assertFalse( font.bold );
  }
  
  public void testDateTimeFieldColorSelected() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "color",
                                               "DateTime-Field" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeFieldBackgroundColorSelected() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Field" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "d2d2d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayColorSelected() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayBackgroundColorSelected() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "d2d2d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayColorSelectedHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayBackgroundColorSelectedHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00569c", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayColorOtherMonth() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":otherMonth" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "808080", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarDayBackgroundColorOtherMonth() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":otherMonth" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testDateTimeCalendarDayBackgroundColorHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Calendar-Day" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 217, color.red );
    assertEquals( 227, color.green );
    assertEquals( 243, color.blue );
  }
  
  public void testDateTimeCalendarNavbarColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "color",
                                               "DateTime-Calendar-Navbar" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarNavbarBackgroundColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               "DateTime-Calendar-Navbar" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00569c", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testDateTimeCalendarNavbarFont() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "font",
                                               "DateTime-Calendar-Navbar" );
    QxFont font = ( QxFont ) cssValue;
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertEquals( 11, font.size );
    assertTrue( font.bold );
  }
  
  public void testDateTimeCalendarPreviousMonthButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-Calendar-PreviousMonthButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/lastMonth.png", image.path );
  }
  
  public void testDateTimeCalendarPreviousMonthButtonBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-Calendar-PreviousMonthButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/lastMonth-hover.png", image.path );
  }
  
  public void testDateTimeCalendarNextMonthButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-Calendar-NextMonthButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/nextMonth.png", image.path );
  }
  
  public void testDateTimeCalendarNextMonthButtonBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-Calendar-NextMonthButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/nextMonth-hover.png", image.path );
  }
  
  public void testDateTimeCalendarPreviousYearButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-Calendar-PreviousYearButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/lastYear.png", image.path );
  }
  
  public void testDateTimeCalendarPreviousYearButtonBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-Calendar-PreviousYearButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/lastYear-hover.png", image.path );
  }
  
  public void testDateTimeCalendarNextYearButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-Calendar-NextYearButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/nextYear.png", image.path );
  }
  
  public void testDateTimeCalendarNextYearButtonBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-Calendar-NextYearButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/nextYear-hover.png", image.path );
  }
  
  public void testDateTimeDownButtonIconBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down.png", image.path );
  }
  
  public void testDateTimeDownButtonIconBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-DownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down-hover.png", image.path );
  }
  
  public void testDateTimeUpButtonIconBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up.png", image.path );
  }
  
  public void testDateTimeUpButtonIconBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-UpButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up-hover.png", image.path );
  }
  
  public void testDateTimeDownButtonBackgroundColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               element );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testDateTimeDownButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#ffffff", colors[ 1 ] );
  }
  
  public void testDateTimeDownButtonBackgroundImagePressed() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":pressed" } );
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#b0b0b0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );    
    assertEquals( "#ffffff", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testDateTimeDownButtonWidth() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "width",
                                               element );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 18, dim.value );
  }
  
  public void testDateTimeDownButtonBorder() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border",
                                               element );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testDateTimeDownButtonBorderRadius() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border-radius",
                                               element );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( dim.top, 0 );
    assertEquals( dim.right, 0 );
    assertEquals( dim.bottom, 2 );
    assertEquals( dim.left, 0 );
  }
  
  public void testDateTimeUpButtonBackgroundColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               element );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testDateTimeUpButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#ffffff", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
  }
  
  public void testDateTimeUpButtonBackgroundImagePressed() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":pressed" } );
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#ffffff", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );    
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testDateTimeUpButtonWidth() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "width",
                                               element );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 18, dim.value );
  }
  
  public void testDateTimeUpButtonBorder() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border",
                                               element );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testDateTimeUpButtonBorderRadius() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-UpButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border-radius",
                                               element );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( dim.top, 0 );
    assertEquals( dim.right, 2 );
    assertEquals( dim.bottom, 0 );
    assertEquals( dim.left, 0 );
  }
  
  public void testDateTimeDropDownButtonIconBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down.png", image.path );
  }
  
  public void testDateTimeDropDownButtonIconBackgroundImageHover() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "DateTime-DropDownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down-hover.png", image.path );
  }
  
  public void testDateTimeDropDownButtonBackgroundColor() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-color",
                                               element );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testDateTimeDropDownButtonBackgroundImage() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#ffffff", colors[ 0 ] );
    assertEquals( "#f0f0f0", colors[ 1 ] );
    assertEquals( "#e0e0e0", colors[ 2 ] );
    assertEquals( "#cccccc", colors[ 3 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 48.0, percents[ 1 ], 0.0 );
    assertEquals( 52.0, percents[ 2 ], 0.0 );
    assertEquals( 100.0, percents[ 3 ], 0.0 );
  }
  
  public void testDateTimeDropDownButtonBorder() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border",
                                               element );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testDateTimeDropDownButtonBorderRadius() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "border-radius",
                                               element );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testDateTimeDropDownButtonWidth() {
    DateTime time = createDateTime( SWT.DATE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "DateTime-DropDownButton";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( time, 
                                               selector, 
                                               "width",
                                               element );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 18, dim.value );
  }
  
  

    
  /*
   * Little Helper Method to create DateTimes
   */
  private DateTime createDateTime( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    DateTime time = new DateTime( shell, style );
    return time;
  }
  
}
