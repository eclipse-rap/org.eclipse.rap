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
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Shell_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetAdapterWithShellAdapter() {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    assertNotNull( adapter );
  }

  @Test
  public void testMenuBar() {
    Shell shell2 = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menu );
    // Ensure that getMenuBar returns the very same shell that was set
    assertSame( menu, shell.getMenuBar() );
    // Allow to 'reset' the menuBar
    shell.setMenuBar( null );
    assertEquals( null, shell.getMenuBar() );
    // Ensure that shell does not return a disposed of menuBar
    shell.setMenuBar( menu );
    menu.dispose();
    assertNull( shell.getMenuBar() );
    // Ensure that the shell does not 'react' when disposing of a formerly
    // owned menuBar
    Menu shortTimeMenu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( shortTimeMenu );
    Menu replacementMenu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( replacementMenu );
    shortTimeMenu.dispose();
    assertSame( replacementMenu, shell.getMenuBar() );
    // Shell must initially have no menu bar
    assertEquals( null, shell2.getMenuBar() );
    // setMenuBar allows only a menu that whose parent is *this* shell
    try {
      Menu shell1Menu = new Menu( shell, SWT.BAR );
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

  @Test
  public void testClientArea() {
    Rectangle clientAreaWithoutMenuBar = shell.getClientArea();
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Rectangle clientAreaWithMenuBar = shell.getClientArea();
    assertTrue( clientAreaWithoutMenuBar.y < clientAreaWithMenuBar.y );
  }

  @Test
  public void testConstructor() throws Exception {
    Shell shell = new Shell();
    assertSame( display, shell.getDisplay() );
    shell = new Shell( ( Display )null, SWT.NONE );
    assertSame( display, shell.getDisplay() );
    shell = new Shell( ( Display )null );
    assertEquals( SWT.SHELL_TRIM | SWT.LEFT_TO_RIGHT, shell.getStyle() );
    shell = new Shell( display, SWT.NO_TRIM | SWT.CLOSE );
    assertTrue( ( shell.getStyle() & SWT.CLOSE ) == 0 );
    shell = new Shell( ( Shell )null );
    assertEquals( SWT.DIALOG_TRIM | SWT.LEFT_TO_RIGHT, shell.getStyle() );
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
    final Shell[] backgroundShell = { null };
    final boolean[] failed = { false };
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          backgroundShell[ 0 ] = new Shell();
        } catch( Exception e ) {
          failed[ 0 ] = true;
        }
      }
    } );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    assertNull( backgroundShell[ 0 ] );
    assertTrue( failed[ 0 ] );
  }

  @Test
  public void testInitialValues() {
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
    assertTrue( shell.getEnabled() );
    // Shell is visible after open(), but not directly after creation
    assertFalse( shell.getVisible() );
    assertFalse( shell.isVisible() );
    // The Shell(Display) constructor must use style SHELL_TRIM
    Shell trimShell = new Shell( display );
    assertEquals( SWT.SHELL_TRIM | SWT.LEFT_TO_RIGHT, trimShell.getStyle() );
  }

  @Test
  public void testInitialSize() {
    Point empty = new Point( 0, 0 );
    assertFalse( empty.equals( shell.getSize() ) );
  }

  @Test
  public void testAlpha() {
    assertEquals( 255, shell.getAlpha() );
    shell.setAlpha( 23 );
    assertEquals( 23, shell.getAlpha() );
    shell.setAlpha( 0 );
    assertEquals( 0, shell.getAlpha() );
  }

  @Test
  public void testOpen() {
    shell.open();
    assertTrue( shell.getVisible() );
    assertTrue( shell.isVisible() );
  }

  @Test
  public void testLayoutOnSetVisible() {
    // ensure that layout is trigered while opening a shell, more specifically
    // during setVisible( true )
    final StringBuilder log = new StringBuilder();
    shell.setLayout( new Layout() {
      @Override
      protected Point computeSize( Composite composite, int hint, int hint2, boolean flushCache ) {
        return null;
      }
      @Override
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

  @Test
  public void testCloseNotifiesShellListeners() {
    final ShellEvent[] shellEvent = { null };
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent event ) {
        shellEvent[ 0 ] = event;
      }
    } );
    shell.open();
    shell.close();
    assertNotNull( shellEvent );
    assertSame( shell, shellEvent[ 0 ].widget );
    assertSame( shell, shellEvent[ 0 ].getSource() );
    assertSame( shell.getDisplay(), shellEvent[ 0 ].display );
  }

  @Test
  public void testCloseChildShells() {
    shell.open();
    Shell childShell = new Shell( shell );
    shell.close();
    assertTrue( childShell.isDisposed() );
  }

  @Test
  public void testCloseDisposesShell() {
    shell.open();

    shell.close();

    assertTrue( shell.isDisposed() );
  }

  @Test
  public void testDisposeChildShell() {
    shell.open();
    Shell childShell = new Shell( shell );
    childShell.dispose();
    assertTrue( childShell.isDisposed() );
    childShell = new Shell( shell );
    shell.dispose();
    assertTrue( childShell.isDisposed() );
  }

  @Test
  public void testDisposeMenu() {
    Menu menu = new Menu( shell, SWT.BAR );
    shell.dispose();
    assertTrue( menu.isDisposed() );
  }

  @Test
  public void testCreateDescendantShell() {
    Shell descendantShell = new Shell( shell );
    assertEquals( 0, shell.getChildren().length );
    assertSame( shell, descendantShell.getParent() );
  }

  @Test
  public void testFocusAfterReEnable() {
    Control focusedWhileDisabled = new Button( shell, SWT.PUSH );
    Control focusedControl = new Button( shell, SWT.PUSH );
    shell.open();
    focusedWhileDisabled.forceFocus();
    shell.setEnabled( false );
    focusedControl.forceFocus();
    shell.setEnabled( true );
    assertEquals( focusedControl, display.getFocusControl() );
  }

  @Test
  public void testSavedFocus() {
    Control control = new Button( shell, SWT.PUSH );
    shell.open();
    control.setFocus();
    assertSame( shell.getSavedFocus(), control );  // ensure precondition
    control.dispose();
    assertNotSame( control, shell.getSavedFocus() );
    assertNull( shell.getSavedFocus() );
  }

  @Test
  public void testInvalidDefaultButton() {
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

  @Test
  public void testSaveDefaultButton() {
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

  @Test
  public void testDefaultButtonDisposed() {
    Button defaultButton = new Button( shell, SWT.PUSH );
    shell.setDefaultButton( defaultButton );
    defaultButton.dispose();
    assertNull( shell.getDefaultButton() );
  }

  @Test
  public void testResetDefaultButton() {
    Button defaultButton = new Button( shell, SWT.PUSH );
    shell.setDefaultButton( defaultButton );
    shell.setDefaultButton( null );
    assertSame( defaultButton, shell.getDefaultButton() );
    shell.setDefaultButton( null );
    assertNull( shell.getDefaultButton() );
  }

  @Test
  public void testSetDefaultButtonOnFocus() {
    shell.open();
    assertNull( shell.getDefaultButton() );
    Button button = new Button( shell, SWT.PUSH );
    Combo combo = new Combo( shell, SWT.NONE );
    button.setFocus();
    assertSame( button, shell.getDefaultButton() );
    combo.setFocus();
    assertNull( shell.getDefaultButton() );
  }

  @Test
  public void testSetDefaultButtonOnFocus_EventOrder() {
    final ArrayList<Button> log = new ArrayList<Button>();
    shell.open();
    assertNull( shell.getDefaultButton() );
    Button button = new Button( shell, SWT.PUSH );
    Combo combo = new Combo( shell, SWT.NONE );
    button.addFocusListener( new FocusListener() {
      public void focusGained( FocusEvent e ) {
        log.add( shell.getDefaultButton() );
      }
      public void focusLost( FocusEvent e ) {
        log.add( shell.getDefaultButton() );
      }
    } );
    button.setFocus();
    combo.setFocus();
    assertEquals( 2, log.size() );
    assertNull( log.get( 0 ) );
    assertSame( button, log.get( 1 ) );
  }

  @Test
  public void testForceActive() {
    Shell secondShell = new Shell( display );
    shell.open();
    secondShell.open();
    assertSame( secondShell, display.getActiveShell() );
    shell.forceActive();
    assertSame( shell, display.getActiveShell() );
  }

  @Test
  public void testActivateInvisible() {
    shell.setSize( 50, 50 );
    shell.setVisible( false );
    shell.setActive();
    assertNull( display.getActiveShell() );
  }

  @Test
  public void testSetActive() {
    final java.util.List<ShellEvent> log = new ArrayList<ShellEvent>();
    shell.open();
    assertSame( shell, display.getActiveShell() );
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellActivated( ShellEvent event ) {
        log .add( event );
      }
      @Override
      public void shellDeactivated( ShellEvent event ) {
        log .add( event );
      }
    } );
    shell.setActive();
    shell.setActive();
    assertEquals( 0, log.size() );
  }

  @Test
  public void testActiveShellOnFocusControl() {
    final java.util.List<String> log = new ArrayList<String>();
    Shell secondShell = new Shell( display );
    shell.open();
    secondShell.open();
    assertSame( secondShell, display.getActiveShell() );
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellActivated( ShellEvent event ) {
        log.add( "shellActivated" );
      }
    } );
    Button button = new Button( shell, SWT.PUSH );
    button.addFocusListener( new FocusAdapter() {
      @Override
      public void focusGained( FocusEvent event ) {
        log.add( "buttonFocusGained" );
      }
    } );
    button.addListener( SWT.Activate, new Listener() {
      public void handleEvent( Event event ) {
        log.add( "buttonActivated" );
      }
    } );
    button.setFocus();
    assertSame( shell, display.getActiveShell() );
    assertEquals( 3, log.size() );
    assertEquals( "shellActivated", log.get( 0 ) );
    assertEquals( "buttonFocusGained", log.get( 1 ) );
    assertEquals( "buttonActivated", log.get( 2 ) );
  }

  /* test case to simulate the scenario reported in this bug:
   * 278996: [Shell] Stackoverflow when closing child shell
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=278996
   */
  @Test
  public void testCloseOnDeactivateWithSingleShell() {
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellDeactivated( ShellEvent event ) {
        shell.close();
      }
    } );
    shell.open();
    shell.setActive();
    // no assert: test case is to ensure that no stack overflow occurs
  }

  /*
   * Bug 282506: [Shell] StackOverflow when calling Shell#close in Deactivate
   *             listener
   */
  @Test
  public void testCloseOnDeactivateWithMultipleShells() {
    shell.open();
    final Shell dialog = new Shell( shell );
    dialog.setLayout( new FillLayout() );
    dialog.addShellListener( new ShellAdapter() {
      @Override
      public void shellDeactivated( ShellEvent event ) {
        dialog.close();
      }
    } );
    dialog.open();
    dialog.close();
    // no assert: test case is to ensure that no stack overflow occurs
  }

  @Test
  public void testNoDeactivateEventOnDispose() {
    final StringBuilder log = new StringBuilder();
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellActivated( ShellEvent event ) {
        log.append( "shell activated" );
      }
      @Override
      public void shellDeactivated( ShellEvent event ) {
        log.append( "shell deactivated" );
      }
    } );
    shell.open();
    shell.dispose();
    assertEquals( "shell activated", log.toString() );
  }

  @Test
  public void testMaximized() {
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMaximized( true );
    assertTrue( shell.getMaximized() );
    assertEquals( shell.getBounds(), display.getBounds() );
  }

  @Test
  public void testSetBoundsResetMaximized() {
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMaximized( true );
    Rectangle bounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( bounds );
    assertFalse( shell.getMaximized() );
    assertEquals( bounds, shell.getBounds() );
  }

  @Test
  public void testSetLocationResetMaximized() {
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMaximized( true );
    shell.setLocation( 10, 10 );
    assertFalse( shell.getMaximized() );
  }

  @Test
  public void testSetSizeResetMaximized() {
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMaximized( true );
    shell.setSize( 6, 6 );
    assertFalse( shell.getMaximized() );
  }

  @Test
  public void testSetBoundsResetMaximizedEventOrder() {
    final boolean[] maximized = {
      true
    };
    shell.setBounds( 1, 2, 3, 4 );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        maximized[ 0 ] = shell.getMaximized();
      }
    } );
    shell.setMaximized( true );
    shell.setSize( 6, 6 );
    assertFalse( maximized[ 0 ] );
  }

  @Test
  public void testShellAdapterSetBounds() {
    final java.util.List<ControlEvent> log = new ArrayList<ControlEvent>();
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMaximized( true );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        log.add( event );
      }
    } );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setBounds( new Rectangle( 5, 6, 7, 8 ) );
    assertEquals( new Rectangle( 5, 6, 7, 8 ), shell.getBounds() );
    assertTrue( shell.getMaximized() );
    assertEquals( 1, log.size() );
  }

  @Test
  public void testSetBoundsResetMinimized() {
    shell.setBounds( 1, 2, 3, 4 );
    shell.setMinimized( true );
    Rectangle bounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( bounds );
    assertFalse( shell.getMinimized() );
    assertEquals( bounds, shell.getBounds() );
  }

  @Test
  public void testModified() {
    assertFalse( shell.getModified() );
    shell.setModified( true );
    assertTrue( shell.getModified() );
    shell.setModified( false );
    assertFalse( shell.getModified() );
  }

  @Test
  public void testMinimumSize() {
    final java.util.List<ControlEvent> log = new ArrayList<ControlEvent>();
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        log.add( event );
      }
    } );
    assertEquals( new Point( 80, 40 ), shell.getMinimumSize() );
    shell.setSize( 10, 10 );
    assertEquals( new Point( 80, 40 ), shell.getSize() );
    shell.setSize( 100, 100 );
    log.clear();
    assertEquals( new Point( 100, 100 ), shell.getSize() );
    shell.setMinimumSize( 150, 150 );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 150, 150 ), shell.getMinimumSize() );
    assertEquals( new Point( 150, 150 ), shell.getSize() );
    shell.setMinimumSize( 10, 10 );
    assertEquals( new Point( 80, 40 ), shell.getMinimumSize() );
    shell.setMinimumSize( new Point( 150, 150 ) );
    assertEquals( new Point( 150, 150 ), shell.getMinimumSize() );
    shell.setBounds( 10, 10, 100, 100 );
    assertEquals( new Point( 150, 150 ), shell.getSize() );
    assertEquals( new Rectangle( 10, 10, 150, 150 ), shell.getBounds() );
    try {
      shell.setMinimumSize( null );
      fail( "Must not allow null value" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testFullScreen() {
    final java.util.List<String> log = new ArrayList<String>();
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.addControlListener( new ControlListener() {
      public void controlMoved( ControlEvent event ) {
        log.add( "controlMoved" );
      }
      public void controlResized( ControlEvent event ) {
        log.add( "controlResized" );
      }
    } );
    shell.open();
    assertFalse( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );

    log.clear();
    shell.setMaximized( true );
    assertFalse( shell.getFullScreen() );
    assertTrue( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 2, log.size() );
    assertEquals( "controlMoved", log.get( 0 ) );
    assertEquals( "controlResized", log.get( 1 ) );
    assertEquals( displayBounds, shell.getBounds() );

    shell.setMinimized( true );
    assertFalse( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertTrue( shell.getMinimized() );

    shell.setMinimized( false );
    assertFalse( shell.getFullScreen() );
    assertTrue( shell.getMaximized() );
    assertFalse( shell.getMinimized() );

    log.clear();
    shell.setFullScreen( true );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 0, log.size() );
    assertEquals( displayBounds, shell.getBounds() );

    log.clear();
    shell.setMaximized( true );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 0, log.size() );
    assertEquals( displayBounds, shell.getBounds() );

    log.clear();
    shell.setMaximized( false );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 0, log.size() );
    assertEquals( displayBounds, shell.getBounds() );

    log.clear();
    shell.setMinimized( true );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertTrue( shell.getMinimized() );
    assertEquals( 0, log.size() );

    log.clear();
    shell.setMinimized( false );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 0, log.size() );

    log.clear();
    shell.setFullScreen( false );
    assertFalse( shell.getFullScreen() );
    assertTrue( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 0, log.size() );
    assertEquals( displayBounds, shell.getBounds() );

    shell.setMaximized( false );
    shell.setMinimized( true );
    log.clear();
    shell.setFullScreen( true );
    assertTrue( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 2, log.size() );
    assertEquals( "controlMoved", log.get( 0 ) );
    assertEquals( "controlResized", log.get( 1 ) );
    assertEquals( displayBounds, shell.getBounds() );

    log.clear();
    shell.setFullScreen( false );
    assertFalse( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 2, log.size() );
    assertEquals( "controlMoved", log.get( 0 ) );
    assertEquals( "controlResized", log.get( 1 ) );
    assertEquals( shellBounds, shell.getBounds() );

    shell.setFullScreen( true );
    log.clear();
    shell.setBounds( 20, 20, 200, 200 );
    assertFalse( shell.getFullScreen() );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    assertEquals( 2, log.size() );
    assertEquals( "controlMoved", log.get( 0 ) );
    assertEquals( "controlResized", log.get( 1 ) );
  }

  @Test
  public void testActiveShellOnFullScreen() {
    shell.setBounds( 20, 20, 200, 200 );
    shell.open();
    Shell shell2 = new Shell( display );
    shell2.setBounds( 20, 20, 200, 200 );
    shell2.open();
    assertEquals( shell2, display.getActiveShell() );
    shell.setFullScreen( true );
    assertEquals( shell, display.getActiveShell() );
  }

  @Test
  public void testGetToolTipsWhenNoToolTipWasCreated() {
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    assertNotNull( adapter.getToolTips() );
    assertEquals( 0, adapter.getToolTips().length );
  }

  @Test
  public void testGetToolTipsWhenToolTipWasCreated() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    assertEquals( 1, adapter.getToolTips().length );
    assertEquals( toolTip, adapter.getToolTips()[ 0 ] );
  }

  @Test
  public void testGetToolTipsAfterToolTipWasDisposed() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    toolTip.dispose();
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    assertNotNull( adapter.getToolTips() );
    assertEquals( 0, adapter.getToolTips().length );
  }

  @Test
  public void testGetToolBar() {
    assertNull( shell.getToolBar() );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

  @Test
  public void testIsSerializable() throws Exception {
    shell.setText( "text" );

    Shell deserializedShell = Fixture.serializeAndDeserialize( shell );

    assertEquals( shell.getText(), deserializedShell.getText() );
  }

  @Test
  public void testAddShellListener() {
    shell.addShellListener( mock( ShellListener.class ) );

    assertTrue( shell.isListening( SWT.Activate ) );
    assertTrue( shell.isListening( SWT.Deactivate ) );
    assertTrue( shell.isListening( SWT.Close ) );
  }

  @Test
  public void testRemoveShellListener() {
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );

    shell.removeShellListener( listener );

    assertFalse( shell.isListening( SWT.Activate ) );
    assertFalse( shell.isListening( SWT.Deactivate ) );
    assertFalse( shell.isListening( SWT.Close ) );
  }

  @Test
  public void testAddShellListenerWithNullArgument() {
    try {
      shell.addShellListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveShellListenerWithNullArgument() {
    try {
      shell.removeShellListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testActivateEventNotFiredOnShellInDispose() {
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );
    Shell childShell = new Shell( shell );
    childShell.open();

    display.dispose();

    verify( listener, times( 0 ) ).shellActivated( any( ShellEvent.class ) );
  }

}
