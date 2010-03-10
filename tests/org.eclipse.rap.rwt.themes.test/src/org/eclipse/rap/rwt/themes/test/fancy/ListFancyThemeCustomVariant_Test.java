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
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;


public class ListFancyThemeCustomVariant_Test extends TestCase {  

  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testEXAMPLESListItemBackgroundColorEven() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even" } 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "f3f3f4", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testEXAMPLESListItemBackgroundColorEvenHover() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even",
                                           ":hover"} 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "e9f3d2", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testEXAMPLESListItemBackgroundColorEvenSelected() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even",
                                           ":selected"} 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "background-color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "abe033", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testEXAMPLESListItemColorEvenSelected() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even",
                                           ":selected"} 
      );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( list, 
                                                             selector, 
                                                             "color",
                                                             "List-Item");
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testEXAMPLESListItemBackgroundColorEvenSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even",
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
  
  public void testEXAMPLESListItemColorEvenSelectedUnfocused() {
    List list = createList( SWT.SINGLE );
    list.setData( WidgetUtil.CUSTOM_VARIANT, "EXAMPLES" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".EXAMPLES",
                                           ":even",
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
