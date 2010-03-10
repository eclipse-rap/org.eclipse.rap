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

import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxDimension;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import junit.framework.TestCase;


public class SpinnerBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testSpinnerColor() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( spinner, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "464a4e", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testSpinnerBackgroundColor() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( spinner, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 252, color.red );
    assertEquals( 252, color.green );
    assertEquals( 252, color.blue );
  }
  
  public void testSpinnerBorder() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( spinner, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testSpinnerBorderRadius() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( spinner, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testSpinnerPadding() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( spinner, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 1, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 1, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testSpinnerDownButtonIconBackgroundImage() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down.png", image.path );
  }
  
  public void testSpinnerDownButtonIconBackgroundImageHover() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-DownButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/down-hover.png", image.path );
  }
  
  public void testSpinnerDownButtonBackgroundImage() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-DownButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#e0e0e0", colors[ 0 ] );
    assertEquals( "#ffffff", colors[ 1 ] );
  }
  
  public void testSpinnerDownButtonBackgroundColor() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-color",
                                               "Spinner-DownButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSpinnerDownButtonWidth() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "width",
                                               "Spinner-DownButton" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 18, dim.value );
  }
  
  public void testSpinnerDownButtonBorder() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "border",
                                               "Spinner-DownButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testSpinnerDownButtonBackgroundImagePressed() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-DownButton" );
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
  
  public void testSpinnerUpButtonIconBackgroundImage() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up.png", image.path );
  }
  
  public void testSpinnerUpButtonIconBackgroundImageHover() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-UpButton-Icon" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/up-hover.png", image.path );
  }
  
  public void testSpinnerUpButtonBackgroundImage() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-UpButton" );
    QxImage image = ( QxImage ) cssValue;
    String[] colors = image.gradientColors;
    assertEquals( "#ffffff", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
  }
  
  public void testSpinnerUpButtonBackgroundColor() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-color",
                                               "Spinner-UpButton" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testSpinnerUpButtonWidth() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "width",
                                               "Spinner-UpButton" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 18, dim.value );
  }
  
  public void testSpinnerUpButtonBorder() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "border",
                                               "Spinner-UpButton" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testSpinnerUpButtonBackgroundImagePressed() {
    Spinner spinner = createSpinner( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":pressed" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( spinner, 
                                               selector, 
                                               "background-image",
                                               "Spinner-UpButton" );
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
  

    
  /*
   * Little Helper Method to create Labels
   */
  private Spinner createSpinner( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Spinner spinner = new Spinner( shell, style );
    return spinner;
  }
  
}
