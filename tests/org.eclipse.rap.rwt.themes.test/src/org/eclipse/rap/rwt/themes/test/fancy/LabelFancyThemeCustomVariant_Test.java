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
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;


public class LabelFancyThemeCustomVariant_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }  
  
  public void testLabelMenuBorderBackgroundColor() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "menuBorder" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".menuBorder" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 221, color.red );
    assertEquals( 221, color.green );
    assertEquals( 221, color.blue );
  }
  
  public void testLabelStackBorderBackgroundColor() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "stackBorder" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".stackBorder" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 235, color.red );
    assertEquals( 235, color.green );
    assertEquals( 235, color.blue );
  }
    
  public void testLabelStandaloneViewBackgroundColor() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "standaloneView" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".standaloneView" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  public void testLabelStandaloneViewBorder() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "standaloneView" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".standaloneView" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testLabelStandaloneViewColor() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "standaloneView" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".standaloneView" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 102, color.red );
    assertEquals( 102, color.green );
    assertEquals( 102, color.blue );
  }
  
  public void testLabelStandaloneViewFont() {
    Label label = createLabel( SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "standaloneView" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".standaloneView" } );
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "font" );
    QxFont font = ( QxFont ) cssValue;
    String[] family = font.family;
    assertEquals( 5, family.length );
    assertEquals( "Verdana", family[ 0 ] );
    assertEquals( "Lucida Sans", family[ 1 ] );
    assertEquals( "Arial", family[ 2 ] );
    assertEquals( "Helvetica", family[ 3 ] );
    assertEquals( "sans-serif", family[ 4 ] );
    assertEquals( 11, font.size );
    assertFalse( font.bold );
  }
  
  /*
   * Little Helper Method to create Links
   */
  private Label createLabel( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Label label = new Label( shell, style );
    label.setText( "I'm a label" );
    return label;
  }
  
}
