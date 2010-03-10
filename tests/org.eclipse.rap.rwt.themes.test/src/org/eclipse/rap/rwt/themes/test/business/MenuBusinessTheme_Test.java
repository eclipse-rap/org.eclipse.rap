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
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class MenuBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testMenuColor() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "Menu", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 0, color.red );
    assertEquals( 89, color.green );
    assertEquals( 165, color.blue );
  }
  
  public void testMenuBackgroundColor() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "Menu", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 255, color.red );
    assertEquals( 255, color.green );
    assertEquals( 255, color.blue );
  }
  
  public void testMenuBorder() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "Menu", 
                                             "border", 
                                             selector );
    QxBorder border = ( QxBorder ) cssValue;
    String hexColor = 
      ThemesTestUtil.getHexStringFromColor( Graphics.getColor( 0, 89, 165 ) );
    assertEquals( "#" + hexColor, border.color );
    assertEquals( 1, border.width );
    assertEquals( "solid", border.style );
  }
  
  public void testMenuBorderRadius() {
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "Menu", 
                                             "border-radius", 
                                             selector );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.left );
    assertEquals( 2, dim.right );
  }
  
  public void testMenuItemColorHover() {
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "MenuItem", 
                                             "color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testMenuItemBackgroundColorHover() {
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemeUtil.getCssValue( "MenuItem", 
                                             "background-color", 
                                             selector );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "dae9f7", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
}
