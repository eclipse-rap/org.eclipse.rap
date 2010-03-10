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
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class ExpandBarFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testExpandBarColor() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testExpandBarBackgroundColor() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 248, color.red );
    assertEquals( 248, color.green );
    assertEquals( 255, color.blue );
  }
  
  public void testExpandBarBorderBorder() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( bar, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 1, border.width );
    assertEquals( "#c1c1c1", border.color );
    assertEquals( "solid", border.style );    
  }
  
  public void testExpandItemHeaderBackgroundImage() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ExpandItem-Header");
    QxImage image = ( QxImage ) cssValue;   
    String[] colors = image.gradientColors;
    float[] percents = image.gradientPercents;
    assertEquals( 3, colors.length );
    assertEquals( 3, percents.length );
    assertEquals( "#ffffff", colors[ 0 ] );
    assertEquals( "#e0e0e0", colors[ 1 ] );
    assertEquals( "#f0f0f0", colors[ 2 ] );
    assertEquals( 0.0, percents[ 0 ], 0.0 );
    assertEquals( 55.0, percents[ 1 ], 0.0 );
    assertEquals( 100.0, percents[ 2 ], 0.0 );
  }
  
  public void testExpandItemBorder() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( bar, 
                                                             selector, 
                                                             "border",
                                                             "ExpandItem" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 1, border.width );
    assertEquals( "#c1c1c1", border.color );
    assertEquals( "solid", border.style );    
  }
  
  public void testExpandItemButtonBackgroundImage() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ExpandItem-Button");
    QxImage image = ( QxImage ) cssValue;   
    assertEquals( "theme/fancy/icons/expanditem-expand.png", image.path );
  }
  
  public void testExpandItemButtonBackgroundImageHover() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ExpandItem-Button");
    QxImage image = ( QxImage ) cssValue;   
    assertEquals( "theme/fancy/icons/expanditem-expand-hover.png", 
                  image.path );
  }
  
  public void testExpandItemButtonBackgroundImageExpanded() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":expanded" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ExpandItem-Button");
    QxImage image = ( QxImage ) cssValue;   
    assertEquals( "theme/fancy/icons/expanditem-collapse.png", 
                  image.path );
  }
  
  public void testExpandItemButtonBackgroundImageExpandedHover() {
    ExpandBar bar = createExpandBar( SWT.V_SCROLL );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":expanded", ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( bar, 
                                               selector, 
                                               "background-image",
                                               "ExpandItem-Button");
    QxImage image = ( QxImage ) cssValue;   
    assertEquals( "theme/fancy/icons/expanditem-collapse-hover.png", 
                  image.path );
  }
  
  /*
   * Little Helper Method to create ExpandBars
   */
  private ExpandBar createExpandBar( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    ExpandBar bar = new ExpandBar( shell, style );
    for( int i = 0; i < 10; i++ ) {
      ExpandItem item = new ExpandItem( bar, SWT.NONE );
      item.setText( "I'm item " + i );
    }
    return bar;
  }
}
