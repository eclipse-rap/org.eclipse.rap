/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.toolbarkit.ToolBarThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;


public class ToolBarThemeAdapter_Test extends TestCase {

  public void testGetItemPaddingAndBorderWidth() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.HORIZONTAL );
    ToolBarThemeAdapter themeAdapter
      = ( ToolBarThemeAdapter )toolBar.getAdapter( IThemeAdapter.class );
    assertEquals( 0, themeAdapter.getItemBorderWidth( toolBar ) );
    assertEquals( new Rectangle( 8, 8, 16, 16 ), themeAdapter.getItemPadding( toolBar ) );
    ToolBar flatToolBar = new ToolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    assertEquals( 0, themeAdapter.getItemBorderWidth( flatToolBar ) );
    assertEquals( new Rectangle( 8, 8, 16, 16 ), themeAdapter.getItemPadding( flatToolBar ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
