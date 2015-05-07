/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.toolbarkit.ToolBarThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolBarThemeAdapter_Test {

  private static BoxDimensions EIGHT = new BoxDimensions( 8, 8, 8, 8 );

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetItemPaddingAndBorderWidth() {
    ToolBar toolBar = new ToolBar( shell, SWT.HORIZONTAL );
    ToolBarThemeAdapter themeAdapter = getThemeAdapter( toolBar );

    assertEquals( new BoxDimensions( 0, 0, 0, 0 ), themeAdapter.getItemBorder( toolBar ) );
    assertEquals( EIGHT, themeAdapter.getItemPadding( toolBar ) );

    ToolBar flatToolBar = new ToolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    assertEquals( new BoxDimensions( 0, 0, 0, 0 ), themeAdapter.getItemBorder( flatToolBar ) );
    assertEquals( EIGHT, themeAdapter.getItemPadding( flatToolBar ) );
  }

  private static ToolBarThemeAdapter getThemeAdapter( ToolBar toolBar ) {
    return ( ToolBarThemeAdapter )toolBar.getAdapter( ThemeAdapter.class );
  }

}
