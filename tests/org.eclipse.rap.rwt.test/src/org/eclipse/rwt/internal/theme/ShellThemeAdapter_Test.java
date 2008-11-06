/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellThemeAdapter_Test extends TestCase {


  public void testPlainShell() {
    Color defFgColor = Graphics.getColor( 0, 0, 0 );
    Color defBgColor = Graphics.getColor( 248, 248, 255 );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    IThemeAdapter themeAdapter = themeManager.getThemeAdapter( Shell.class );
    assertTrue( themeAdapter instanceof ShellThemeAdapter );
    ShellThemeAdapter sta = ( ShellThemeAdapter )themeAdapter;
    assertEquals( 1, sta.getBorderWidth( shell ) );
    assertEquals( defFgColor, sta.getForeground( shell ) );
    assertEquals( defBgColor, sta.getBackground( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, sta.getBorderWidth( shell ) );
  }

  public void testShellWithBorder() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.BORDER );
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    IThemeAdapter themeAdapter = themeManager.getThemeAdapter( Shell.class );
    ShellThemeAdapter sta = ( ShellThemeAdapter )themeAdapter;
    assertEquals( 2, sta.getBorderWidth( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, sta.getBorderWidth( shell ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    RWTFixture.tearDown();
  }
}
