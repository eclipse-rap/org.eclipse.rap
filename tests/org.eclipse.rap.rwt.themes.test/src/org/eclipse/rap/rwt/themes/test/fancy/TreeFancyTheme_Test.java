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
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class TreeFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testTreeBackgroundColor() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( tree, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeColor() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( tree, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeBorder() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( tree, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTreeItemBackgroundColor() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "background-color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeItemColor() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeItemBackgroundColorSelected() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "background-color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeItemColorSelected() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeItemBackgroundColorSelectedUnfocused() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "background-color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeItemColorSelectedUnfocused() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", "unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "color", 
                                                             "TreeItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTreeColumnBackgroundImage() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "background-image", 
                                                             "TreeColumn" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/table-column.png", image.path );
  }
  
  public void testTreeColumnFont() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "font", 
                                                             "TreeColumn" );
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
  
  public void testTreeColumnBorderBottom() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "border-bottom", 
                                                             "TreeColumn" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTreeColumnPadding() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "padding", 
                                                             "TreeColumn" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 1, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 1, dim.bottom );
    assertEquals( 2, dim.left );
  }
  
  public void testTreeColumnBackgroundImageHover() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "background-image", 
                                                             "TreeColumn" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/table-column-hover.png", image.path );
  }
  
  public void testTreeColumnBorderBottomHover() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( tree, 
                                                             selector, 
                                                             "border-bottom", 
                                                             "TreeColumn" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
  }
  
  public void testTreeColumnSortIndicatorBackgroundImage() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( tree, 
                                               selector, 
                                               "background-image", 
                                               "TreeColumn-SortIndicator" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testTreeColumnSortIndicatorBackgroundImageUp() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":up" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( tree, 
                                               selector, 
                                               "background-image", 
                                               "TreeColumn-SortIndicator" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/sort-indicator-up.png", image.path );
  }
  
  public void testTreeColumnSortIndicatorBackgroundImageDown() {
    Tree tree = createTree( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":down" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( tree, 
                                               selector, 
                                               "background-image", 
                                               "TreeColumn-SortIndicator" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/fancy/icons/sort-indicator-down.png", image.path );
  }
  
  /*
   * Little Helper Method to create Trees
   */
  private Tree createTree( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Tree tree = new Tree( shell, style );
    for( int i = 0; i < 10; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "I'm item " + i );
    }
    return tree;
  }
  
}
