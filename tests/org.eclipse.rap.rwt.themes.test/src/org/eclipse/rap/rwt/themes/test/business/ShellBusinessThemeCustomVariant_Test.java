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
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellBusinessThemeCustomVariant_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testShellShellGrayBackgroundColor() {
    Shell shell = createShell( SWT.TITLE );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "shellGray" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".shellGray" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 235, color.red );
    assertEquals( 235, color.green );
    assertEquals( 235, color.blue );
  }
  
  public void testShellToolbarLayerBackgroundColor() {
    Shell shell = createShell( SWT.TITLE );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarLayer" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".toolbarLayer" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertEquals( 255, color.red );
    assertEquals( 255, color.green );
    assertEquals( 255, color.blue );
  }
  
  public void testShellToolbarLayerBorder() {
    Shell shell = createShell( SWT.TITLE );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarLayer" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".toolbarLayer" } );
    QxType cssValue = ThemesTestUtil.getCssValue( shell, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertNull( border.style );
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
