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
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


public class ProgressBarFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }  
  
  public void testProgressBarBackgroundColor() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testProgressBarBackgroundImage() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testProgressBarBorder() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testProgressBarBorderRadius() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testProgressBarIndicatorBackgroundColor() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-color",
                                               "ProgressBar-Indicator" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testProgressBarIndicatorBackgroundColorPaused() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":paused" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-color",
                                               "ProgressBar-Indicator" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ebebeb", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testProgressBarIndicatorBackgroundColorError() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":error" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-color",
                                               "ProgressBar-Indicator" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 203, color.red );
    assertEquals( 32, color.green );
    assertEquals( 32, color.blue );
  }
  
  public void testProgressBarIndicatorBackgroundImage() {
    ProgressBar bar = createBar( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ProgressBar-Indicator" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/progress.png", image.path );
  }
    
  /*
   * Little Helper Method to create ProgressBars
   */
  private ProgressBar createBar( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    ProgressBar bar = new ProgressBar( shell, style );
    return bar;
  }
  
}
