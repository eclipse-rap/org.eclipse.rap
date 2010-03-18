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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testShellTitleBarColor() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "color", 
                                                             "Shell-Titlebar" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testShellTitleBarBackgroundImage() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "background-image", 
                                                             "Shell-Titlebar" );
    QxImage image = ( QxImage ) cssValue;
    String[] gradienColors = image.gradientColors;
    assertEquals( 2, gradienColors.length );
    assertEquals( "#005fac", gradienColors[ 0 ] );
    assertEquals( "#005092", gradienColors[ 1 ] );
  }
  
  public void testShellTitleBarPadding() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "padding", 
                                                             "Shell-Titlebar" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.top );
    assertEquals( 5, dim.left );
    assertEquals( 5, dim.right );
  }
  
  public void testShellTitleBarMargin() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "margin", 
                                                             "Shell-Titlebar" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.top );
    assertEquals( 0, dim.left );
    assertEquals( 0, dim.right );
  }
  
  public void testShellTitleBarHeight() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "height", 
                                                             "Shell-Titlebar" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 22, dim.value );
  }
  
  public void testShellTitleBarFont() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "font", 
                                                             "Shell-Titlebar" );
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
  
  public void testShellTitleBarBorder() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "border", 
                                                             "Shell-Titlebar" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testShellTitleBarBorderRadius() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "border-radius", 
                                                             "Shell-Titlebar" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 3, dim.top );
    assertEquals( 0, dim.left );
    assertEquals( 3, dim.right );
    assertEquals( 0, dim.bottom );
  }
  
  public void testShellTitleBarColorInactive() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":inactive" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "color", 
                                                             "Shell-Titlebar" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "aaaaaa", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testShellTitleBarBackgroundImageInactive() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":inactive" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( shell, 
                                                             selector, 
                                                             "background-image", 
                                                             "Shell-Titlebar" );
    QxImage image = ( QxImage ) cssValue;
    String[] gradienColors = image.gradientColors;
    assertEquals( 2, gradienColors.length );
    assertEquals( "#595959", gradienColors[ 0 ] );
    assertEquals( "#4b4b4b", gradienColors[ 1 ] );
  }
  
  public void testShellBackgroundColor() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testShellTitleBackgroundColor() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TITLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testShellTitleBorder() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TITLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 2, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "#005092", border.color );
  }
  
  public void testShellTitleBorderRadius() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TITLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 6, dim.top );
    assertEquals( 6, dim.right );
    assertEquals( 6, dim.bottom );
    assertEquals( 6, dim.left );
  }
  
  public void testShellTitlePadding() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[TITLE" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 5, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 5, dim.bottom );
    assertEquals( 5, dim.left );
  }
  
  public void testShellBorderBackgroundColor() {
    Shell shell = createShell( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testShellBorderBorder() {
    Shell shell = createShell( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 2, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "#005092", border.color );
  }
  
  public void testShellBorderBorderRadius() {
    Shell shell = createShell( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 6, dim.top );
    assertEquals( 6, dim.right );
    assertEquals( 6, dim.bottom );
    assertEquals( 6, dim.left );
  }
  
  public void testShellBorderPadding() {
    Shell shell = createShell( SWT.BORDER );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "padding" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 5, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 5, dim.bottom );
    assertEquals( 5, dim.left );
  }
  
  public void testShellTitleBorderInactive() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[TITLE", ":inactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 2, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "#4b4b4b", border.color );
  }
  
  public void testShellBorderBorderInactive() {
    Shell shell = createShell( SWT.BORDER );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { "[BORDER", ":inactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( 2, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "#4b4b4b", border.color );
  }
  
  public void testShellCloseButtonBackgroundImage() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-CloseButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-close.png", image.path );
  }
  
  public void testShellCloseButtonBackgroundImageHover() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-CloseButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-close-hover.png", image.path );
  }
  
  public void testShellCloseButtonMargin() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "margin", 
                                               "Shell-CloseButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 5, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testShellMaxButtonBackgroundImage() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-MaxButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-max.png", image.path );
  }
  
  public void testShellMaxButtonBackgroundImageHover() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-MaxButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-max-hover.png", image.path );
  }
  
  public void testShellMaxButtonMargin() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "margin", 
                                               "Shell-MaxButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 6, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testShellMinButtonBackgroundImage() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-MinButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-min.png", image.path );
  }
  
  public void testShellMinButtonBackgroundImageHover() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "background-image", 
                                               "Shell-MinButton" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/shell-min-hover.png", image.path );
  }
  
  public void testShellMinButtonMargin() {
    Shell shell = createShell( SWT.TITLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( shell, 
                                               selector, 
                                               "margin", 
                                               "Shell-MinButton" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 6, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
    
  /*
   * Little Helper Method to create Shells
   */
  private Shell createShell( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    shell.setText( "a shell's title" );
    assertNotNull( shell );
    return shell;
  } 
  
}
