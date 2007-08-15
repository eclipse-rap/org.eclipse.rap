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

package org.eclipse.swt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.MenuHolder.IMenuHolderAdapter;
import org.eclipse.swt.widgets.*;

public class MenuHolder_Test extends TestCase {

  public void testMenuHolder() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Object menuHolder = shell.getAdapter( IMenuHolderAdapter.class );
    assertNotNull( menuHolder );
    Object menuHolder2 = shell.getAdapter( IMenuHolderAdapter.class );
    assertSame( menuHolder2, menuHolder );
  }

  public void testAddRemove() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
