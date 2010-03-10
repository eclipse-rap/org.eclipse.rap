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
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class TextFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testTextColor() {
    Text text = createText( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( text, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "464a4e", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testTextBackgroundColor() {
    Text text = createText( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( text, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 252, color.red );
    assertEquals( 252, color.green );
    assertEquals( 252, color.blue );
  }
  
  public void testTextBorderBorder() {
    Text text = createText( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( text, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#a4a4a4", border.color );
  }
  
  public void testTextBorderBorderRadius() {
    Text text = createText( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( text, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 2, dim.top );
    assertEquals( 2, dim.right );
    assertEquals( 2, dim.bottom );
    assertEquals( 2, dim.left );
  }
    
  /*
   * Little Helper Method to create Texts
   */
  private Text createText( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Text text = new Text( shell, style );
    text.setText( "I'm a text" );
    return text;
  }
  
}
