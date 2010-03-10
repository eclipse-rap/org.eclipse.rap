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

import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import junit.framework.TestCase;


public class ListFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testListColor() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( list, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListBackgroundColor() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( list, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemColor() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColor() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorHover() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "e9f3d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorSelected() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemColorSelected() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector = new SimpleSelector( new String[] { ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemColorSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":selected", ":unfocused" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorEven() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "f3f3f4", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorEvenHover() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":hover" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "e9f3d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorEvenSelected() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemColorEvenSelected() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", ":selected" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemBackgroundColorEvenSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even", 
                                           ":selected", 
                                           ":unfocused" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testListItemColorEvenSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":even",
                                           ":selected", 
                                           ":unfocused" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  /*
   * Little Helper Method to create Lists
   */
  private List createList( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    List list = new List( shell, style );
    for( int i = 0; i < 10; i++ ) {
      list.add( "I'm item " + i );
    }
    return list;
  }
  
}
