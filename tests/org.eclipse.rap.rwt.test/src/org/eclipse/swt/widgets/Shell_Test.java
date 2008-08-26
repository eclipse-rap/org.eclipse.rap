/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IShellAdapter;

public class Shell_Test extends TestCase {

  public void testMenuBar() {
    Display display = new Display();
    Shell shell1 = new Shell( display, SWT.NONE );
    Shell shell2 = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell1, SWT.BAR );
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
    Menu shortTimeMenu = new Menu( shell1, SWT.BAR );
    shell1.setMenuBar( shortTimeMenu );
    Menu replacementMenu = new Menu( shell1, SWT.BAR );
    shell1.setMenuBar( replacementMenu );
    shortTimeMenu.dispose();
    assertSame( replacementMenu, shell1.getMenuBar() );
    // Shell must initially have no menu bar
    assertEquals( null, shell2.getMenuBar() );
    // setMenuBar allows only a menu that whose parent is *this* shell
    try {
      Menu shell1Menu = new Menu( shell1, SWT.BAR );
      shell2.setMenuBar( shell1Menu );
      fail( "Must not allow to set menu from different shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Ensure that setMenuBar does not accept disposed of Menus
    try {
      Menu disposedMenu = new Menu( shell2, SWT.BAR );
      disposedMenu.dispose();
      shell2.setMenuBar( disposedMenu );
      fail( "Must not allow to set disposed of menu." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Ensure that setMenuBar does not accept menus other than those constructed
    // with SWT.BAR
    try {
      Shell shell3 = new Shell( display, SWT.NONE );
      Menu popupMenu = new Menu( shell3, SWT.POP_UP );
      shell3.setMenuBar( popupMenu );
      fail( "Must only accept menus with style SWT.BAR" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testClientArea() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Rectangle clientAreaWithoutMenuBar = shell.getClientArea();
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Rectangle clientAreaWithMenuBar = shell.getClientArea();
    assertTrue( clientAreaWithoutMenuBar.y < clientAreaWithMenuBar.y );
  }

  public void testConstructor() {
    Display display = new Display();
    Shell shell = new Shell( ( Display )null, SWT.NONE );
    assertSame( display, shell.getDisplay() );

    shell = new Shell( ( Display )null );
    assertEquals( SWT.SHELL_TRIM, shell.getStyle() );

    shell = new Shell( display, SWT.NO_TRIM | SWT.CLOSE );
    assertTrue( ( shell.getStyle() & SWT.CLOSE ) == 0 );

    shell = new Shell( ( Shell )null );
    assertEquals( SWT.DIALOG_TRIM, shell.getStyle() );

    shell = new Shell( display, SWT.MIN );
    assertTrue( ( shell.getStyle() & SWT.CLOSE ) != 0 );

    try {
      Shell disposedShell = new Shell( display, SWT.NONE );
      disposedShell.dispose();
      shell = new Shell( disposedShell );
      fail( "The constructor mut not accept a disposed shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    // Shell is visible after open(), but not directly after creation
    assertEquals( false, shell.getVisible() );
    assertEquals( false, shell.isVisible() );
    // The Shell(Display) constructor must use style SHELL_TRIM
    Shell trimShell = new Shell( display );
    assertEquals( SWT.SHELL_TRIM, trimShell.getStyle() );
  }

  public void testInitialSize() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Rectangle empty = new Rectangle( 0, 0, 0, 0 );
    assertFalse( empty.equals( shell.getBounds() ) );
  }

  public void testAlpha() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    assertEquals( 255, shell.getAlpha() );
    shell.setAlpha( 23 );
    assertEquals( 23, shell.getAlpha() );
    shell.setAlpha( 0 );
    assertEquals( 0, shell.getAlpha() );
  }

  public void testOpen() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.open();
    assertEquals( true, shell.getVisible() );
    assertEquals( true, shell.isVisible() );
  }

  public void testLayoutOnSetVisible() {
    // ensure that layout is trigered while opening a shell, more specifically
    // during setVisible( true )
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.setLayout( new Layout() {
      protected Point computeSize( Composite composite,
                                   int hint,
                                   int hint2,
                                   boolean flushCache )
      {
        return null;
      }
      protected void layout( Composite composite, boolean flushCache ) {
        log.append( "layout" );
      }
    } );
    shell.setVisible( true );
    assertEquals( "layout", log.toString() );
    // don't re-layout when shell is laready visible
    log.setLength( 0 );
    shell.setVisible( true );
    assertEquals( "", log.toString() );
    // make sure, layout is not triggered when shell gets hidden
    log.setLength( 0 );
    shell.setVisible( false );
    assertEquals( "", log.toString() );
  }

  public void testDisposeChildShell() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.open();
    Shell childShell = new Shell( shell );
    childShell.dispose();
    assertTrue( childShell.isDisposed() );
  }

  public void testCreateDescendantShell() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Shell descendantShell = new Shell( shell );
    assertEquals( 0, shell.getChildren().length );
    assertSame( shell, descendantShell.getParent() );
  }

  public void testFocusAfterReEnable() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Control focusedWhileDisabled = new Button( shell, SWT.PUSH );
    Control focusedControl = new Button( shell, SWT.PUSH );
    shell.open();
    focusedWhileDisabled.forceFocus();
    shell.setEnabled( false );
    focusedControl.forceFocus();
    shell.setEnabled( true );
    assertEquals( focusedControl, display.getFocusControl() );
  }

  public void testInvalidDefaultButton() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Shell anotherShell = new Shell( display );
    Button anotherButton = new Button( anotherShell, SWT.PUSH );

    // Setting button that belongs to different shell causes exception
    try {
      shell.setDefaultButton( anotherButton );
      fail( "Not allowed to set default button that belongs to another shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Setting button that is disposed causes exception
    try {
      Button disposedButton = new Button( shell, SWT.PUSH );
      disposedButton.dispose();
      shell.setDefaultButton( disposedButton );
      fail( "Not allowed to set default button that is disposed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Set a default button for the following tests
    Button defaultButton = new Button( shell, SWT.PUSH );
    shell.setDefaultButton( defaultButton );
    assertSame( defaultButton, shell.getDefaultButton() );

    // Try to set radio-button as default is ignored
    Button radio = new Button( shell, SWT.RADIO );
    shell.setDefaultButton( radio );
    assertSame( defaultButton, shell.getDefaultButton() );

    // Try to set check-box as default is ignored
    Button check = new Button( shell, SWT.RADIO );
    shell.setDefaultButton( check );
    assertSame( defaultButton, shell.getDefaultButton() );
  }

  public void testSaveDefaultButton() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Button button1 = new Button( shell, SWT.PUSH );
    Button button2 = new Button( shell, SWT.PUSH );

    shell.setDefaultButton( button1 );
    assertSame( button1, shell.getDefaultButton() );
    shell.setDefaultButton( null );
    assertSame( button1, shell.getDefaultButton() );

    shell.setDefaultButton( button1 );
    shell.setDefaultButton( button2 );
    assertSame( button2, shell.getDefaultButton() );
    shell.setDefaultButton( null );
    assertSame( button2, shell.getDefaultButton() );

    button2.dispose();
    shell.setDefaultButton( null );
    assertEquals( null, shell.getDefaultButton() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
