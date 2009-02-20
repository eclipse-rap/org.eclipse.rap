/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
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

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.toolbarkit.ToolBarThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ToolBarThemeAdapter_Test extends TestCase {

  public void testGetItemPaddingAndBorderWidth() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    IThemeAdapter themeAdapter = themeManager.getThemeAdapter( ToolBar.class );
    assertTrue( themeAdapter instanceof ToolBarThemeAdapter );
    ToolBarThemeAdapter adapter = ( ToolBarThemeAdapter )themeAdapter;
    ToolBar toolBar = new ToolBar( shell, SWT.HORIZONTAL );
    assertEquals( 1, adapter.getItemBorderWidth( toolBar ) );
    assertEquals( new Rectangle( 3, 2, 6, 4 ),
                  adapter.getItemPadding( toolBar ) );
    ToolBar flatToolBar = new ToolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    assertEquals( 0, adapter.getItemBorderWidth( flatToolBar ) );
    assertEquals( new Rectangle( 4, 3, 8, 6 ),
                  adapter.getItemPadding( flatToolBar ) );
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
