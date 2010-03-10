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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class TableBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testTableBackgroundColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( table, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTableColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( table, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTableColumnColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableColumn" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "666666", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTableColumnBackgroundImage() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "TableColumn" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/table-column.png", image.path );
  }
  
  public void testTableColumnFont() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "font", 
                                                             "TableColumn" );
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
  
  public void testTableColumnBorderBottom() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "border-bottom", 
                                                             "TableColumn" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testTableColumnBackgroundImageHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "TableColumn" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/table-column-hover.png", image.path );
  }
  
  public void testTableColumnBorderBottomHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "border-bottom", 
                                                             "TableColumn" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testTableCellSpacing() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "spacing", 
                                                             "Table-Cell" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 3, dim.value );
  }
  
  public void testTableCellPadding() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "padding", 
                                                             "Table-Cell" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 5, dim.bottom );
    assertEquals( 5, dim.left );
    assertEquals( 5, dim.right );
    assertEquals( 5, dim.top );
  }
  
  public void testTableItemBackgroundColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testTableItemColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "666666", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTableItemBackgroundColorHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "dae9f7", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorSelected() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00589f", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemColorSelected() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorSelectedUnfocused() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemColorSelectedUnfocused() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorEven() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "f3f3f4", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorEvenHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "dae9f7", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorEvenSelected() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00589f", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemColorEvenSelected() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemBackgroundColorEvenSelectedUnfocused() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", 
                                           ":selected", 
                                           ":unfocused" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableItemColorEvenSelectedUnfocused() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", 
                                           ":selected", 
                                           ":unfocused" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "TableItem" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );       
  }
  
  public void testTableGridlineColor() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "color", 
                                                             "Table-GridLine" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "dedede", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTableCheckboxWidth() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "width", 
                                                             "Table-Checkbox" );
    QxDimension dim = ( QxDimension ) cssValue;
    assertEquals( 21, dim.value );
  }
  
  public void testTableCheckboxBackgroundImage() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-unselected.png", image.path );
  }
  
  public void testTableCheckboxBackgroundImageHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-unselected-hover.png", 
                  image.path );
  }
  
  public void testTableCheckboxBackgroundImageChecked() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":checked" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-selected.png", image.path );
  }
  
  public void testTableCheckboxBackgroundImageCheckedHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector     
      = new SimpleSelector( new String[] { ":checked", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-selected-hover.png", image.path );
  }
  
  public void testTableCheckboxBackgroundImageCheckedGrayed() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector     
      = new SimpleSelector( new String[] { ":checked", ":grayed" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-grayed.png", image.path );
  }
  
  public void testTableCheckboxBackgroundImageCheckedGrayedHover() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector     
      = new SimpleSelector( new String[] { ":checked", ":grayed", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( table, 
                                                             selector, 
                                                             "background-image", 
                                                             "Table-Checkbox" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/check-grayed-hover.png", image.path );
  }
  
  public void testTableColumnSortIndicatorUp() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector     
      = new SimpleSelector( new String[] { ":up" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( table, 
                                               selector, 
                                               "background-image", 
                                               "TableColumn-SortIndicator" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/sort-indicator-up.png", image.path );
  }
  
  public void testTableColumnSortIndicatorDown() {
    Table table = createTable( SWT.SINGLE );
    SimpleSelector selector     
      = new SimpleSelector( new String[] { ":down" } );
    QxType cssValue 
      = ThemesTestUtil.getCssValueForElement( table, 
                                               selector, 
                                               "background-image", 
                                               "TableColumn-SortIndicator" );
    QxImage image = ( QxImage ) cssValue;
    assertEquals( "theme/business/icons/sort-indicator-down.png", image.path );
  }  
  
  /*
   * Little Helper Method to create Tables
   */
  private Table createTable( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Table table = new Table( shell, style );
    for( int i = 0; i < 10; i++ ) {
      TableItem item = new TableItem( table, SWT.NONE );
      item.setText( "I'm item " + i );
    }
    return table;
  }
  
}
