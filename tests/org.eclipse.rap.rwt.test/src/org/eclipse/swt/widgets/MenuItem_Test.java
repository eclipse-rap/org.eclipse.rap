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

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class MenuItem_Test extends TestCase {

  public void testConstructor() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menu = new Menu( shell );
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    assertEquals( "", item.getText() );
    assertSame( display, item.getDisplay() );
    assertSame( menu, item.getParent() );
    try {
      new MenuItem( null, SWT.CASCADE );
      fail( "Must not allow null-parent" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testSetMenu() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileMenuItem = new MenuItem( menuBar, SWT.CASCADE );
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
    Shell anotherShell = new Shell( display , SWT.NONE );
    Menu anotherMenu = new Menu( anotherShell );
    try {
      fileMenuItem.setMenu( anotherMenu );
      fail( "setMenu must not accept menu from different shell." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // setMenu may only be called on MenuItems with style SWT.CASCADE
    try {
      MenuItem nonCascadingMenuItem = new MenuItem( fileMenu, SWT.PUSH );
      nonCascadingMenuItem.setMenu( new Menu( shell, SWT.DROP_DOWN ) );
      fail( "setMenu can only be called on MenuItems with style SWT.CASCADE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // setMenu accepts only menu with style SWT.DROP_DOWN
    try {
      MenuItem cascadingMenuItem = new MenuItem( fileMenu, SWT.CASCADE );
      cascadingMenuItem.setMenu( new Menu( shell, SWT.POP_UP ) );
      fail( "setMenu allows only menus with style SWT.DROP_DOWN" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSelection() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    MenuItem pushItem = new MenuItem( menu, SWT.PUSH );
    MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    MenuItem checkItem = new MenuItem( menu, SWT.CHECK );

    // Initial state
    assertEquals( false, pushItem.getSelection() );
    assertEquals( false, checkItem.getSelection() );
    assertEquals( false, radioItem1.getSelection() );
    assertEquals( false, radioItem2.getSelection() );

    // MenuItems with style PUSH must ignore selection changes
    pushItem.setSelection( true );
    assertEquals( false, pushItem.getSelection() );

    // MenuItems with style CHECK or RADIO must allow selection changes
    checkItem.setSelection( true );
    assertEquals( true, checkItem.getSelection() );
    radioItem1.setSelection( true );
    assertEquals( true, radioItem1.getSelection() );

    // When selecting MenuItem with style RADIO programatically, there is no
    // automatic deselection of sibling radio items
    radioItem1.setSelection( true );
    radioItem2.setSelection( true );
    assertEquals( true, radioItem1.getSelection() );
    assertEquals( true, radioItem2.getSelection() );
  }

  public void testImage() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    MenuItem separator = new MenuItem( menu, SWT.SEPARATOR );

    // Don't allow an image to be set on a separator menu item
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    assertNotNull( image );
    separator.setImage( image );
    assertEquals( null, separator.getImage() );
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem fileMenuItem = new MenuItem( menu, SWT.CASCADE );
    Menu fileMenu = new Menu( fileMenuItem );
    fileMenuItem.setMenu( fileMenu );
    MenuItem exitMenuItem = new MenuItem( fileMenu, SWT.PUSH );
    fileMenuItem.dispose();
    assertEquals( true, fileMenuItem.isDisposed() );
    assertEquals( true, fileMenu.isDisposed() );
    assertEquals( true, exitMenuItem.isDisposed() );
  }

  public void testDisplay() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    assertSame( display, item.getDisplay() );
    assertSame( menu.getDisplay(), item.getDisplay() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
