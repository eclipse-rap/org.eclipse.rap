/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.MenuHolder.IMenuHolderAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MenuHolder_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testMenuHolder() {
    Object menuHolder = shell.getAdapter( IMenuHolderAdapter.class );
    assertNotNull( menuHolder );
    Object menuHolder2 = shell.getAdapter( IMenuHolderAdapter.class );
    assertSame( menuHolder2, menuHolder );
  }

  @Test
  public void testAddRemove() {
    assertEquals( 0, MenuHolder.getMenuCount( shell ) );
    Menu menu = new Menu( shell );
    assertEquals( 1, MenuHolder.getMenuCount( shell ) );
    assertEquals( menu, MenuHolder.getMenus( shell )[ 0 ] );
    Text text = new Text( shell, SWT.NONE );
    Menu anotherMenu = new Menu( text );
    assertEquals( 2, MenuHolder.getMenuCount( shell ) );
    assertEquals( anotherMenu, MenuHolder.getMenus( shell )[ 1 ] );
    menu.dispose();
    assertEquals( 1, MenuHolder.getMenuCount( shell ) );
    assertEquals( anotherMenu, MenuHolder.getMenus( shell )[ 0 ] );
  }

  @Test
  public void testSerialize() throws Exception {
    new Menu( shell );

    Shell deserializedShell = Fixture.serializeAndDeserialize( shell );

    assertEquals( 1, MenuHolder.getMenuCount( deserializedShell ) );
  }

}
