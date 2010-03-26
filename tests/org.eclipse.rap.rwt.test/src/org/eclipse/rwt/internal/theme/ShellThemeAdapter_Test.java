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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
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
    ShellThemeAdapter themeAdapter
      = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    assertEquals( 1, themeAdapter.getBorderWidth( shell ) );
    assertEquals( defFgColor, themeAdapter.getForeground( shell ) );
    assertEquals( defBgColor, themeAdapter.getBackground( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, themeAdapter.getBorderWidth( shell ) );
  }

  public void testShellWithBorder() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.BORDER );
    ShellThemeAdapter themeAdapter
      = ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
    assertEquals( 2, themeAdapter.getBorderWidth( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, themeAdapter.getBorderWidth( shell ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
