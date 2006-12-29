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

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import com.w4t.engine.lifecycle.PhaseId;

public class MenuItem_Test extends TestCase {

  public void testConstructor() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menu = new Menu( shell );
    MenuItem item = new MenuItem( menu, RWT.CASCADE );
    assertEquals( "", item.getText() );
    assertSame( display, item.getDisplay() );
    assertSame( menu, item.getParent() );
    try {
      new MenuItem( null, RWT.CASCADE );
      fail( "Must not allow null-parent" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testSetMenu() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileMenuItem = new MenuItem( menuBar, RWT.CASCADE );
    Menu fileMenu = new Menu( menuBar );
    // Test 'normal' usage of setMenu
    fileMenuItem.setMenu( fileMenu );
    assertSame( fileMenu, fileMenuItem.getMenu() );
    // Dispose the above set menu
    // -> the item must track this and getMenu must return null
    fileMenu.dispose();
    assertNull( fileMenuItem.getMenu() );
    // Ensure no disposed of menu can be set
    try {
      fileMenuItem.setMenu( fileMenu );
      fail( "setMenu must to allow to set disposed of menu" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Ensure that no menu that belongs to a different shell can be set
    Shell anotherShell = new Shell( display , RWT.NONE );
    Menu anotherMenu = new Menu( anotherShell );
    try {
      fileMenuItem.setMenu( anotherMenu );
      fail( "setMenu must not accept menu from different shell." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // setMenu may only be called on MenuItems with style RWT.CASCADE
    try {
      MenuItem nonCascadingMenuItem = new MenuItem( fileMenu, RWT.PUSH );
      nonCascadingMenuItem.setMenu( new Menu( shell, RWT.DROP_DOWN ) );
      fail( "setMenu can only be called on MenuItems with style RWT.CASCADE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // setMenu accepts only menu with style RWT.DROP_DOWN
    try {
      MenuItem cascadingMenuItem = new MenuItem( fileMenu, RWT.CASCADE );
      cascadingMenuItem.setMenu( new Menu( shell, RWT.POP_UP ) );
      fail( "setMenu allows only menus with style RWT.DROP_DOWN" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menu = new Menu( shell, RWT.BAR );
    MenuItem fileMenuItem = new MenuItem( menu, RWT.CASCADE );
    Menu fileMenu = new Menu( fileMenuItem );
    fileMenuItem.setMenu( fileMenu );
    MenuItem exitMenuItem = new MenuItem( fileMenu, RWT.PUSH );
    fileMenuItem.dispose();
    assertEquals( true, fileMenuItem.isDisposed() );
    assertEquals( true, fileMenu.isDisposed() );
    assertEquals( true, exitMenuItem.isDisposed() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
