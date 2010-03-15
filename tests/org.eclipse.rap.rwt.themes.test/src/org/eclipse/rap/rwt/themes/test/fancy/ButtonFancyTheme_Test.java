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
import org.eclipse.rwt.internal.theme.QxCursor;
import org.eclipse.rwt.internal.theme.QxFloat;
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ButtonFancyTheme_Test extends TestCase {
 
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testFont() {    
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "font" );
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
  
  public void testBackgroundColor() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 255, color.blue );
    assertEquals( 255, color.green );
    assertEquals( 255, color.red );
  }
  
  public void testBorderForPushUnselected() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "border" );
    QxBorder border = ( QxBorder ) cssValue;
    String color = border.color;
    String style = border.style;
    int width = border.width;
    assertEquals( "#a4a4a4", color );
    assertEquals( "solid", style );
    assertEquals( 1, width );
  }
  
  public void testBorderForToggleUnselected() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "border" );
    QxBorder border = ( QxBorder ) cssValue;
    String color = border.color;
    String style = border.style;
    int width = border.width;
    assertEquals( "#a4a4a4", color );
    assertEquals( "solid", style );
    assertEquals( 1, width );
  }
  
  public void testBorderForBorderUnselected() {
    Button button = createButton( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "border" );   
    QxBorder border = ( QxBorder ) cssValue;
    String color = border.color;
    String style = border.style;
    int width = border.width;
    assertEquals( "#a4a4a4", color );
    assertEquals( "solid", style );
    assertEquals( 1, width );
  }
  
  public void testBorderRadiusForPushUnselected() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue
      = ThemesTestUtil.getCssValue( button, selector, "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 3, dim.bottom );
    assertEquals( 3, dim.left );
    assertEquals( 3, dim.right );
    assertEquals( 3, dim.top );
  }
  
  public void testBorderRadiusForToggleUnselected() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 3, dim.bottom );
    assertEquals( 3, dim.left );
    assertEquals( 3, dim.right );
    assertEquals( 3, dim.top );
  }
  
  public void testBorderRadiusForBorderUnselected() {
    Button button = createButton( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 3, dim.bottom );
    assertEquals( 3, dim.left );
    assertEquals( 3, dim.right );
    assertEquals( 3, dim.top );
  }
  
  public void testPadding() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;    
    assertEquals( 5, dim.left );
    assertEquals( 5, dim.right );
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.bottom );
  }
  
  public void testForeground() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    String hex = ThemesTestUtil.getHexStringFromColor( swtColor );
    assertEquals( "4a4a4a", hex );
  }
  
  public void testBackgroundImageForPushUnselected() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#ffffff", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#ffffff", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 48.0F, percents[ 1 ], 0.0F );
    assertEquals( 52.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForToggleUnselected() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#ffffff", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#ffffff", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 48.0F, percents[ 1 ], 0.0F );
    assertEquals( 52.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForBorderUnselected() {
    Button button = createButton( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#ffffff", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#ffffff", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 48.0F, percents[ 1 ], 0.0F );
    assertEquals( 52.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForToggleSelected() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#e0e0e0", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#b0b0b0", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 30.0F, percents[ 1 ], 0.0F );
    assertEquals( 70.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForToggleSelectedHover() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":selected", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 3, gradientColors.length );
    assertEquals( "#e0e0e0", gradientColors[ 0 ] );
    assertEquals( "#e0e0e0", gradientColors[ 1 ] );
    assertEquals( "#b0b0b0", gradientColors[ 2 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 3, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 52.0F, percents[ 1 ], 0.0F );
    assertEquals( 100.0F, percents[ 2 ], 0.0F );
  }
  
  public void testBackgroundImageForPushUnselectedHover() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#ffffff", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#cccccc", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 48.0F, percents[ 1 ], 0.0F );
    assertEquals( 52.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForToggleUnselectedHover() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 4, gradientColors.length );
    assertEquals( "#ffffff", gradientColors[ 0 ] );
    assertEquals( "#f0f0f0", gradientColors[ 1 ] );
    assertEquals( "#e0e0e0", gradientColors[ 2 ] );
    assertEquals( "#cccccc", gradientColors[ 3 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 4, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 48.0F, percents[ 1 ], 0.0F );
    assertEquals( 52.0F, percents[ 2 ], 0.0F );
    assertEquals( 100.0F, percents[ 3 ], 0.0F );
  }
  
  public void testBackgroundImageForPushPressed() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 3, gradientColors.length );
    assertEquals( "#e0e0e0", gradientColors[ 0 ] );
    assertEquals( "#e0e0e0", gradientColors[ 1 ] );
    assertEquals( "#b0b0b0", gradientColors[ 2 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 3, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 52.0F, percents[ 1 ], 0.0F );
    assertEquals( 100.0F, percents[ 2 ], 0.0F );
  }
  
  public void testBackgroundImageForTogglePressed() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    // test the gradient Colors
    String[] gradientColors = qxImage.gradientColors;
    assertNotNull( gradientColors );
    assertEquals( 3, gradientColors.length );
    assertEquals( "#e0e0e0", gradientColors[ 0 ] );
    assertEquals( "#e0e0e0", gradientColors[ 1 ] );
    assertEquals( "#b0b0b0", gradientColors[ 2 ] );
    // test the gradient percentage
    float[] percents = qxImage.gradientPercents;
    assertNotNull( percents );
    assertEquals( 3, percents.length );
    assertEquals( 0.0F, percents[ 0 ], 0.0F );
    assertEquals( 52.0F, percents[ 1 ], 0.0F );
    assertEquals( 100.0F, percents[ 2 ], 0.0F );
  }
  
  public void testBackgroundImageForCheckBorder() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", "[BORDER" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    assertTrue( qxImage.none );
  }
  
  public void testBackgroundImageForRadioBorder() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO", "[BORDER" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "background-image" );
    QxImage qxImage = ( QxImage ) cssValue;
    assertTrue( qxImage.none );
  }
  
  public void testBackgroundImageForCheckUnselected() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-unselected.png", qxImage.path );
  }
  
  public void testBackgroundImageForCheckSelected() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-selected.png", qxImage.path );
  }
  
  public void testBackgroundImageForCheckUnselectedHover() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-unselected-hover.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForCheckSelectedHover() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", ":selected", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-selected-hover.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForCheckSelectedGrayed() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", ":selected", ":grayed" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-grayed.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForCheckSelectedGrayedHover() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK", 
                                           ":selected", 
                                           ":grayed", 
                                           ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-CheckIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/check-grayed-hover.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForRadioUnselected() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-RadioIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/radio-unselected.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForRadioSelected() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-RadioIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/radio-selected.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForRadioUnselectedHover() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-RadioIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/radio-unselected-hover.png", 
                  qxImage.path );
  }
  
  public void testBackgroundImageForRadioSelectedHover() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO", ":selected", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( button, 
                                                             selector, 
                                                             "background-image", 
                                                             "Button-RadioIcon" 
                                                             );
    QxImage qxImage = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/radio-selected-hover.png", 
                  qxImage.path );
  }
  
  public void testCursorForPushUnselected() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "cursor" );
    QxCursor cursor = ( QxCursor ) cssValue;
    assertEquals( "pointer", cursor.value );
  }
  
  public void testCursorForToggleUnselected() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "cursor" );
    QxCursor cursor = ( QxCursor ) cssValue;
    assertEquals( "pointer", cursor.value );
  }
  
  public void testCursorForPushDisabled() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ":disabled" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "cursor" );
    QxCursor cursor = ( QxCursor ) cssValue;
    assertEquals( "default", cursor.value );
  }
  
  public void testCursorForToogleDisabled() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":disabled" } );
    QxType cssValue = ThemesTestUtil.getCssValue( button, selector, "cursor" );
    QxCursor cursor = ( QxCursor ) cssValue;
    assertEquals( "default", cursor.value );
  }
  
  public void testPaddingForPushPressed() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH", ":pressed" } );
    QxType cssValue     
      = ThemesTestUtil.getCssValue( button, selector, "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 4, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 7, dim.left );
  }
  
  public void testPaddingForTogglePressed() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValue( button, selector, "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 4, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 7, dim.left );
  }
  
  public void testFocusIndicatorBackgroundColorForPush() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "background-color", 
                                               "Button-FocusIndicator" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testFocusIndicatorBorderForPush() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border", 
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "dotted", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#b8b8b8", border.color );
  }
  
  public void testFocusIndicatorMarginForPush() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "margin", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.top );
  }
  
  public void testFocusIndicatorPaddingForPush() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "padding", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.top );
  }

  public void testFocusIndicatorOpacityForPush() {
    Button button = createButton( SWT.PUSH );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[PUSH" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "opacity", 
                                               "Button-FocusIndicator" );
    QxFloat opacity = ( QxFloat ) cssValue;
    assertEquals( 1.0, opacity.value, 0.0 );
  }
  
  public void testFocusIndicatorBackgroundColorForToggle() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "background-color", 
                                               "Button-FocusIndicator" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testFocusIndicatorBorderForToggle() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border", 
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "dotted", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#b8b8b8", border.color );
  }
  
  public void testFocusIndicatorMarginForToggle() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "margin", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.top );
  }
  
  public void testFocusIndicatorPaddingForToggle() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "padding", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.top );
  }

  public void testFocusIndicatorOpacityForToggle() {
    Button button = createButton( SWT.TOGGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TOGGLE" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "opacity", 
                                               "Button-FocusIndicator" );
    QxFloat opacity = ( QxFloat ) cssValue;
    assertEquals( 1.0, opacity.value, 0.0 );
  }
  
  public void testFocusIndicatorBackgroundColorForCheck() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "background-color", 
                                               "Button-FocusIndicator" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testFocusIndicatorBorderForCheck() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border", 
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "dotted", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#b8b8b8", border.color );
  }
  
  public void testFocusIndicatorMarginForCheck() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "margin", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.top );
  }
  
  public void testFocusIndicatorPaddingForCheck() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "padding", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 1, dim.left );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.top );
  }

  public void testFocusIndicatorOpacityForCheck() {
    Button button = createButton( SWT.CHECK );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[CHECK" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "opacity", 
                                               "Button-FocusIndicator" );
    QxFloat opacity = ( QxFloat ) cssValue;
    assertEquals( 1.0, opacity.value, 0.0 );
  }
  
  public void testFocusIndicatorBackgroundColorForRadio() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "background-color", 
                                               "Button-FocusIndicator" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testFocusIndicatorBorderForRadio() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "border", 
                                               "Button-FocusIndicator" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "dotted", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#b8b8b8", border.color );
  }
  
  public void testFocusIndicatorMarginForRadio() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "margin", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.top );
  }
  
  public void testFocusIndicatorPaddingForRadio() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "padding", 
                                               "Button-FocusIndicator" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 1, dim.left );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.top );
  }

  public void testFocusIndicatorOpacityForRadio() {
    Button button = createButton( SWT.RADIO );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[RADIO" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( button, 
                                               selector, 
                                               "opacity", 
                                               "Button-FocusIndicator" );
    QxFloat opacity = ( QxFloat ) cssValue;
    assertEquals( 1.0, opacity.value, 0.0 );
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
