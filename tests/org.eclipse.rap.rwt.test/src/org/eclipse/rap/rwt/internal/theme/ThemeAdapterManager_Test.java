/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ThemeAdapterManager_Test {

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
  public void testGetThemeAdapterForWidgetWithoutThemeAdapter() {
    Widget widget = new TestWidget( shell );
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();
    try {
      themeAdapterManager.getThemeAdapter( widget );
      fail();
    } catch( ThemeManagerException expected ) {
    }
  }

  @Test
  public void testGetThemeAdapterForWidgetWithThemeAdapter() {
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();

    IThemeAdapter themeAdapter = themeAdapterManager.getThemeAdapter( shell );
    assertNotNull( themeAdapter );
  }

  @Test
  public void testGetThemeAdapterReturnsSameAdapterForSameWidget() {
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();

    IThemeAdapter themeAdapter1 = themeAdapterManager.getThemeAdapter( shell );
    IThemeAdapter themeAdapter2 = themeAdapterManager.getThemeAdapter( shell );
    assertSame( themeAdapter1, themeAdapter2 );
  }

  private static class TestWidget extends Widget {
    private static final long serialVersionUID = 1L;
    public TestWidget( Widget parent ) {
      super( parent, SWT.NONE );
    }
  }

}
