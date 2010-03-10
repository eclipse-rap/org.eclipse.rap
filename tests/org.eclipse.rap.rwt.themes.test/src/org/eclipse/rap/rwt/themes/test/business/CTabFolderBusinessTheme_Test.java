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
import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxDimension;
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CTabFolderBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testCTabFolderBorderColor() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-color",
                                               "CTabFolder" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "a4a4a4", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabFolderBorderRadius() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-radius",
                                               "CTabFolder" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testCTabItemFont() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "font",
                                               "CTabItem" );
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
  
  public void testCTabItemColor() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "color",
                                               "CTabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabItemBackgroundColor() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-color",
                                               "CTabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabItemBackgroundImage() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               "CTabItem" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testCTabItemPadding() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "padding",
                                               "CTabItem" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 4, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 4, dim.left );
  }
  
  public void testCTabItemSpacing() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "spacing",
                                               "CTabItem" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 4, dim.value );
  }
  
  public void testCTabItemColorSelected() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "color",
                                               "CTabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabItemBackgroundColorSelected() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-color",
                                               "CTabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00589f", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabItemColorDisabled() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":disabled" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "color",
                                               "CTabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCTabFolderDropDownButtonIconBackgroundImage() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    String element = "CTabFolder-DropDownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/ctabfolder-dropdown.png", image.path );
  }
  
  public void testCTabFolderDropDownButtonIconBackgroundImageHover() {
    CTabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    String element = "CTabFolder-DropDownButton-Icon";
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               element );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/ctabfolder-dropdown-hover.png", 
                  image.path );
  }
  
  /*
   * Little Helper Method to create TabFolders
   */
  private CTabFolder createFolder( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    CTabFolder folder = new CTabFolder( shell, style );
    for( int i = 0; i < 10; i++ ) {
      CTabItem item = new CTabItem( folder, SWT.NONE );
      item.setText( "I'm a item" );
    }
    return folder;
  }
  
}
