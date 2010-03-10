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
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


public class TabFolderFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testTabItemBackgroundColor() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderTopColor() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-top-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderBottomColor() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-bottom-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBackgroundImage() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               "TabItem" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testTabItemBackgroundColorSelected() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderTopColorSelected() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-top-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderBottomColorSelected() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-bottom-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBackgroundImageSelected() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               "TabItem" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testTabItemBackgroundColorHover() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "e9f3d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderTopColorHover() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-top-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBorderBottomColorHover() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "border-bottom-color",
                                               "TabItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTabItemBackgroundImageHover() {
    TabFolder folder = createFolder( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( folder, 
                                               selector, 
                                               "background-image",
                                               "TabItem" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }  
    
  /*
   * Little Helper Method to create TabFolders
   */
  private TabFolder createFolder( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    TabFolder folder = new TabFolder( shell, style );
    for( int i = 0; i < 10; i++ ) {
      TabItem item = new TabItem( folder, SWT.NONE );
      item.setText( "I'm a item" );
    }
    return folder;
  }
  
}
