/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ThemeAdapterManager_Test extends TestCase {
  
  private static class TestWidget extends Widget {
    public TestWidget( Widget parent ) {
      super( parent, SWT.NONE );
    }
  }

  private Display display;
  private Shell shell;

  public void testGetThemeAdapterForWidgetWithoutThemeAdapter() {
    Widget widget = new TestWidget( shell );
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();
    try {
      themeAdapterManager.getThemeAdapter( widget );
      fail();
    } catch( ThemeManagerException expected ) {
    }
  }
  
  public void testGetThemeAdapterForWidgetWithThemeAdapter() {
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();
    
    IThemeAdapter themeAdapter = themeAdapterManager.getThemeAdapter( shell );
    assertNotNull( themeAdapter );
  }

  public void testGetThemeAdapterReturnsSameAdapterForSameWidget() {
    ThemeAdapterManager themeAdapterManager = new ThemeAdapterManager();
    
    IThemeAdapter themeAdapter1 = themeAdapterManager.getThemeAdapter( shell );
    IThemeAdapter themeAdapter2 = themeAdapterManager.getThemeAdapter( shell );
    assertSame( themeAdapter1, themeAdapter2 );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }
  
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }
}
