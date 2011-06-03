/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.MenuHolder.IMenuHolderAdapter;
import org.eclipse.swt.widgets.*;

public class MenuHolder_Test extends TestCase {

  private Shell shell;

  public void testMenuHolder() {
    Object menuHolder = shell.getAdapter( IMenuHolderAdapter.class );
    assertNotNull( menuHolder );
    Object menuHolder2 = shell.getAdapter( IMenuHolderAdapter.class );
    assertSame( menuHolder2, menuHolder );
  }

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
  
  public void testSerialize() throws Exception {
    new Menu( shell );
    
    Shell deserializedShell = Fixture.serializeAndDeserialize( shell );

    assertEquals( 1, MenuHolder.getMenuCount( deserializedShell ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
