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
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;


public class SliderBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }  
  
  public void testSliderBackgroundColor() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( slider, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "f3f3f4", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testSliderDownButtonBackgroundColorHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-color",
                                               "Slider-DownButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSliderDownButtonBorderHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border",
                                               "Slider-DownButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSliderDownButtonBorderRadiusHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border-radius",
                                               "Slider-DownButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 0, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testSliderDownButtonBackgroundImageHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton" );
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
  
  public void testSliderDownButtonBackgroundImageHorizontalPressed() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testSliderDownButtonIconBackgroundImageHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/left.png", image.path );
  }
  
  public void testSliderDownButtonIconBackgroundImageHorizontalHover() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/left-hover.png", image.path );
  }
  
  public void testSliderUpButtonBackgroundColorHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-color",
                                               "Slider-UpButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSliderUpButtonBorderHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border",
                                               "Slider-UpButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSliderUpButtonBorderRadiusHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border-radius",
                                               "Slider-UpButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testSliderUpButtonBackgroundImageHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton" );
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
  
  public void testSliderUpButtonBackgroundImageHorizontalPressed() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testSliderUpButtonIconBackgroundImageHorizontal() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/right.png", image.path );
  }
  
  public void testSliderUpButtonIconBackgroundImageHorizontalHover() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":horizontal", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/right-hover.png", image.path );
  }
  
  public void testSliderDownButtonBackgroundColorVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-color",
                                               "Slider-DownButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSliderDownButtonBorderVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border",
                                               "Slider-DownButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSliderDownButtonBorderRadiusVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border-radius",
                                               "Slider-DownButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testSliderDownButtonBackgroundImageVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton" );
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
  
  public void testSliderDownButtonBackgroundImageVerticalPressed() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testSliderDownButtonIconBackgroundImageVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up.png", image.path );
  }
  
  public void testSliderDownButtonIconBackgroundImageVerticalHover() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up-hover.png", image.path );
  }
  
  public void testSliderUpButtonBackgroundColorVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-color",
                                               "Slider-UpButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSliderUpButtonBorderVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border",
                                               "Slider-UpButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSliderUpButtonBorderRadiusVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border-radius",
                                               "Slider-UpButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 0, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testSliderUpButtonBackgroundImageVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#cccccc", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#f0f0f0", colors[ 2 ] );
    assertEquals( "#ffffff", colors[ 3 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 56.0, percents[ 2 ], 0.0 );
    assertEquals( 100.0, percents[ 3 ], 0.0 );
  }
  
  public void testSliderUpButtonBackgroundImageVerticalPressed() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical", ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testSliderUpButtonIconBackgroundImageVertical() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down.png", image.path );
  }
  
  public void testSliderUpButtonIconBackgroundImageVerticalHover() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":vertical", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down-hover.png", image.path );
  }
  
  public void testSliderThumbBackgroundColor() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-color",
                                               "Slider-Thumb" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSliderThumbBorder() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border",
                                               "Slider-Thumb" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSliderThumbBorderRadius() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "border-radius",
                                               "Slider-Thumb" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testSliderThumbBackgroundImage() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-Thumb" );
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
  
  public void testSliderThumbBackgroundImagePressed() {
    Slider slider = createSlider( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( slider, 
                                               selector, 
                                               "background-image",
                                               "Slider-Thumb" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#b0b0b0", colors[ 2 ] );
    float[] percents = image.gradientPercents;
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 52.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
    
  /*
   * Little Helper Method to create Sliders
   */
  private Slider createSlider( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Slider slider = new Slider( shell, style );
    return slider;
  }
  
}
