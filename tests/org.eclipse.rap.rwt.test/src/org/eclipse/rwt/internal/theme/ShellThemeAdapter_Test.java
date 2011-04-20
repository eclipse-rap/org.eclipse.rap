/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellThemeAdapter_Test extends TestCase {

  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    display = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPlainShell() {
    Color defFgColor = Graphics.getColor( 74, 74, 74 );
    Color defBgColor = Graphics.getColor( 255, 255, 255 );
    Shell shell = new Shell( display, SWT.NONE );
    ShellThemeAdapter themeAdapter = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    assertEquals( 1, themeAdapter.getBorderWidth( shell ) );
    assertEquals( defFgColor, themeAdapter.getForeground( shell ) );
    assertEquals( defBgColor, themeAdapter.getBackground( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, themeAdapter.getBorderWidth( shell ) );
  }

  public void testShellWithBorder() {
    Shell shell = new Shell( display, SWT.BORDER );
    ShellThemeAdapter themeAdapter = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    assertEquals( 2, themeAdapter.getBorderWidth( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, themeAdapter.getBorderWidth( shell ) );
  }

  public void testTitleBarHeightFromCustomVariant() throws IOException {
    String css = "Shell-Titlebar.special { height: 50px }";
    ThemeTestUtil.registerCustomTheme( "custom", css, null );
    ThemeUtil.setCurrentThemeId( "custom" );
    Shell shell = new Shell( display, SWT.TITLE );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "special" );
    ShellThemeAdapter themeAdapter = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    assertEquals( 50, themeAdapter.getTitleBarHeight( shell ) );
  }

  public void testTitleBarMarginFromCustomVariant() throws IOException {
    String css = "Shell-Titlebar.special { margin: 1px 2px 3px 4px }";
    ThemeTestUtil.registerCustomTheme( "custom", css, null );
    ThemeUtil.setCurrentThemeId( "custom" );
    Shell shell = new Shell( display, SWT.TITLE );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "special" );
    ShellThemeAdapter themeAdapter = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    Rectangle expected = new Rectangle( 4, 1, 6, 4 );
    Rectangle margin = themeAdapter.getTitleBarMargin( shell );    
    assertEquals( expected, margin );
  }
}
