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
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IShellAdapter;
import com.w4t.engine.lifecycle.PhaseId;

public class Shell_Test extends TestCase {

  public void testMenuBar() {
    Display display = new Display();
    Shell shell1 = new Shell( display, RWT.NONE );
    Shell shell2 = new Shell( display, RWT.NONE );
    Menu menu = new Menu( shell1, RWT.BAR );
    shell1.setMenuBar( menu );
    // Ensure that getMenuBar returns the very same shell that was set
    assertSame( menu, shell1.getMenuBar() );
    // Allow to 'reset' the menuBar
    shell1.setMenuBar( null );
    assertEquals( null, shell1.getMenuBar() );
    // Ensure that shell does not return a disposed of menuBar
    shell1.setMenuBar( menu );
    menu.dispose();
    assertNull( shell1.getMenuBar() );
    // Ensure that the shell does not 'react' when disposing of a formerly
    // owned menuBar
    Menu shortTimeMenu = new Menu( shell1, RWT.BAR );
    shell1.setMenuBar( shortTimeMenu );
    Menu replacementMenu = new Menu( shell1, RWT.BAR );
    shell1.setMenuBar( replacementMenu );
    shortTimeMenu.dispose();
    assertSame( replacementMenu, shell1.getMenuBar() );
    // Shell must initially have no menu bar
    assertEquals( null, shell2.getMenuBar() );
    // setMenuBar allows only a menu that whose parent is *this* shell
    try {
      Menu shell1Menu = new Menu( shell1, RWT.BAR );
      shell2.setMenuBar( shell1Menu );
      fail( "Must not allow to set menu from different shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Ensure that setMenuBar does not accept disposed of Menus
    try {
      Menu disposedMenu = new Menu( shell2, RWT.BAR );
      disposedMenu.dispose();
      shell2.setMenuBar( disposedMenu );
      fail( "Must not allow to set disposed of menu." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Ensure that setMenuBar does not accept menus other than those constructed
    // with RWT.BAR
    try {
      Shell shell3 = new Shell( display , RWT.NONE );
      Menu popupMenu = new Menu( shell3, RWT.POP_UP );
      shell3.setMenuBar( popupMenu );
      fail( "Must only accept menus with style RWT.BAR" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testClientArea() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Rectangle clientAreaWithoutMenuBar = shell.getClientArea();
    Menu menuBar = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menuBar );
    Rectangle clientAreaWithMenuBar = shell.getClientArea();
    assertTrue( clientAreaWithoutMenuBar.y < clientAreaWithMenuBar.y );
  }
  
  public void testConstructor() {
    Display display = new Display();
    Shell shell = new Shell( ( Display )null, RWT.NONE );
    assertSame( display, shell.getDisplay() );

    shell = new Shell( ( Display )null );
    assertEquals( RWT.SHELL_TRIM, shell.getStyle() );

    shell = new Shell( display, RWT.NO_TRIM | RWT.CLOSE );
    assertTrue( ( shell.getStyle() & RWT.CLOSE ) == 0 );
    
    shell = new Shell( ( Shell )null );
    assertEquals( RWT.DIALOG_TRIM, shell.getStyle() );

    shell = new Shell( display, RWT.MIN );
    assertTrue( ( shell.getStyle() & RWT.CLOSE ) != 0 );
    
    try {
      Shell disposedShell = new Shell( display, RWT.NONE );
      disposedShell.dispose();
      shell = new Shell( disposedShell );
      fail( "The constructor mut not accept a disposed shell" );
    } catch( IllegalArgumentException e ) {
      // expected
  }
  }
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    // Must return the display it was created with
    assertSame( display, shell.getDisplay() );
    // Active control must be null
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    assertEquals( null, shellAdapter.getActiveControl() );
    // Shell initially has no layout
    assertEquals( null, shell.getLayout() );
    // Text must be an empty string 
    assertEquals( "", shell.getText() );
    // Enabled
    assertEquals( true, shell.getEnabled() );
    // Shell is visible after open(), but not direectly after creation
    assertEquals( false, shell.getVisible() );
    assertEquals( false, shell.isVisible() );
    // The Shell(Display) constructor must use style SHELL_TRIM
    Shell trimShell = new Shell( display );
    assertEquals( RWT.SHELL_TRIM, trimShell.getStyle() );
  }
  
  public void testOpen() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    shell.open();
    assertEquals( true, shell.getVisible() );
    assertEquals( true, shell.isVisible() );
  }
  
  public void testDisposeChildShell() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    shell.open();
    Shell childShell = new Shell( shell );
    childShell.dispose();
    assertTrue( childShell.isDisposed() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
