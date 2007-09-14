/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonThemeAdapter;
import org.eclipse.swt.internal.widgets.listkit.ListThemeAdapter;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ThemeManager_Test extends TestCase {

  public void testThemeAdapters() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    IThemeAdapter themeAdapter;
    // Control
    themeAdapter = themeManager.getThemeAdapter( Control.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof IControlThemeAdapter );
    // List
    themeAdapter = themeManager.getThemeAdapter( List.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ListThemeAdapter );
    // Button
    themeAdapter = themeManager.getThemeAdapter( Button.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ButtonThemeAdapter );
    // Shell
    themeAdapter = themeManager.getThemeAdapter( Shell.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ShellThemeAdapter );
  }

  public void testRegister() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    try {
      themeManager.registerTheme( null, "foo", null, null );
      fail( "Null id must throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      themeManager.registerTheme( "", "foo", null, null );
      fail( "Empty id must throw IlleaglArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDeregister() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String id = manager.getDefaultThemeId();
    assertEquals( "org.eclipse.swt.theme.Default", id );
    manager.deregisterAll();
    try {
      manager.hasTheme( "foo" );
      fail( "Theme manager de-initialized, should throw IllegalStateException" );
    } catch( IllegalStateException e ) {
      // expected
    }
    manager.initialize();
    id = manager.getDefaultThemeId();
    assertEquals( "org.eclipse.swt.theme.Default", id );
    Theme theme = manager.getTheme( id );
    assertNotNull( theme );
    assertEquals( "RAP Default Theme", theme.getName() );
  }

  public void testRegisterResources() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testStripTemplate() throws Exception {
    String content;
    String template;
    content = "Line 1\r\n// BEGIN TEMPLATE (bla)\r\nLine3\r\n";
    template = ThemeManager.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
    content = "Line 1\r// BEGIN TEMPLATE (bla)\rLine3\r";
    template = ThemeManager.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
    content = "Line 1\n// BEGIN TEMPLATE (bla)\nLine3\n";
    template = ThemeManager.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
