/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class MenuItem_Test {

  private Display display;
  private Shell shell;
  private Menu menuBar;
  private Menu menu;
  private MenuItem menuItem;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    menu = new Menu( shell, SWT.POP_UP );
    menuItem = new MenuItem( menu, SWT.PUSH );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructor() {
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

  @Test
  public void testSetMenu() {
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

  @Test
  public void testSelection() {
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    MenuItem pushItem = new MenuItem( menu, SWT.PUSH );
    MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    MenuItem checkItem = new MenuItem( menu, SWT.CHECK );

    // Initial state
    assertFalse( pushItem.getSelection() );
    assertFalse( checkItem.getSelection() );
    assertFalse( radioItem1.getSelection() );
    assertFalse( radioItem2.getSelection() );

    // MenuItems with style PUSH must ignore selection changes
    pushItem.setSelection( true );
    assertFalse( pushItem.getSelection() );

    // MenuItems with style CHECK or RADIO must allow selection changes
    checkItem.setSelection( true );
    assertTrue( checkItem.getSelection() );
    radioItem1.setSelection( true );
    assertTrue( radioItem1.getSelection() );

    // When selecting MenuItem with style RADIO programatically, there is no
    // automatic deselection of sibling radio items
    radioItem1.setSelection( true );
    radioItem2.setSelection( true );
    assertTrue( radioItem1.getSelection() );
    assertTrue( radioItem2.getSelection() );
  }

  @Test
  public void testImage() throws IOException {
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    MenuItem separator = new MenuItem( menu, SWT.SEPARATOR );

    // Don't allow an image to be set on a separator menu item
    Image image = TestUtil.createImage( display, Fixture.IMAGE1 );
    assertNotNull( image );
    separator.setImage( image );
    assertEquals( null, separator.getImage() );
  }

  @Test
  public void testDispose() {
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem fileMenuItem = new MenuItem( menu, SWT.CASCADE );
    Menu fileMenu = new Menu( fileMenuItem );
    fileMenuItem.setMenu( fileMenu );
    MenuItem exitMenuItem = new MenuItem( fileMenu, SWT.PUSH );
    fileMenuItem.dispose();
    assertTrue( fileMenuItem.isDisposed() );
    assertTrue( fileMenu.isDisposed() );
    assertTrue( exitMenuItem.isDisposed() );
  }

  @Test
  public void testDisplay() {
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    assertSame( display, item.getDisplay() );
    assertSame( menu.getDisplay(), item.getDisplay() );
  }

  @Test
  public void testDefaultId() {
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    assertEquals( 0, item.getID() );
  }

  @Test
  public void testId() {
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );
    item.setID( 123 );
    assertEquals( 123, item.getID() );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testId_InvalidValue() {
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );
    item.setID( -100 );
  }

  @Test
  public void testAddArmListener() {
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );

    item.addArmListener( mock( ArmListener.class ) );

    assertTrue( item.isListening( SWT.Arm ) );
  }

  @Test
  public void testRemoveArmListener() {
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );
    ArmListener listener = mock( ArmListener.class );
    item.addArmListener( listener );

    item.removeArmListener( listener );

    assertFalse( item.isListening( SWT.Arm ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddArmListenerWithNullArgument() {
    menuItem.addArmListener( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveArmListenerWithNullArgument() {
    menuItem.removeArmListener( null );
  }

  @Test
  public void testAddHelpListener() {
    menuItem.addHelpListener( mock( HelpListener.class ) );

    assertTrue( menuItem.isListening( SWT.Help ) );
  }

  @Test
  public void testRemoveHelpListener() {
    HelpListener listener = mock( HelpListener.class );
    menuItem.addHelpListener( listener );

    menuItem.removeHelpListener( listener );

    assertFalse( menuItem.isListening( SWT.Help ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddHelpListenerWithNullArgument() {
    menuItem.addHelpListener( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveHelpListenerWithNullArgument() {
    menuItem.removeHelpListener( null );
  }

  @Test
  public void testAddSelectionListener() {
    menuItem.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( menuItem.isListening( SWT.Selection ) );
    assertTrue( menuItem.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    menuItem.addSelectionListener( listener );

    menuItem.removeSelectionListener( listener );

    assertFalse( menuItem.isListening( SWT.Selection ) );
    assertFalse( menuItem.isListening( SWT.DefaultSelection ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSelectionListenerWithNullArgument() {
    menuItem.addSelectionListener( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveSelectionListenerWithNullArgument() {
    menuItem.removeSelectionListener( null );
  }

  @Test
  public void testGetAccelerator_Initial() {
    assertEquals( 0, menuItem.getAccelerator() );
  }

  @Test
  public void testSetAccelerator() {
    menuItem.setAccelerator( SWT.ALT | 'A' );

    assertEquals( SWT.ALT | 'A', menuItem.getAccelerator() );
  }

  @Test
  public void testSetAccelerator_OnSeparator() {
    menuItem = new MenuItem( menu, SWT.SEPARATOR );
    menuItem.setAccelerator( SWT.ALT | 'A' );

    assertEquals( SWT.ALT | 'A', menuItem.getAccelerator() );
  }

  @Test
  public void testSelectionEvent_TriggeredByAccelerator() {
    SelectionListener listener = mock( SelectionListener.class );
    menuItem.addSelectionListener( listener );
    menuItem.setAccelerator( SWT.ALT | 'A' );

    menuItem.handleAcceleratorActivation();

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertSame( menuItem, event.widget );
  }

  @Test
  public void testToggleSelection_TriggeredByAccelerator_OnCheck() {
    menuItem = new MenuItem( menu, SWT.CHECK );
    menuItem.setAccelerator( SWT.ALT | 'A' );
    shell.open();

    menuItem.handleAcceleratorActivation();

    assertTrue( menuItem.getSelection() );

    menuItem.handleAcceleratorActivation();

    assertFalse( menuItem.getSelection() );
  }

  @Test
  public void testSetSelection_TriggeredByAccelerator_OnRadio() {
    menuItem = new MenuItem( menu, SWT.RADIO );
    menuItem.setAccelerator( SWT.ALT | 'A' );
    shell.open();

    menuItem.handleAcceleratorActivation();

    assertTrue( menuItem.getSelection() );

    menuItem.handleAcceleratorActivation();

    assertTrue( menuItem.getSelection() );
  }

  @Test
  public void testDeselectOtherRadios_TriggeredByAccelerator() {
    menuItem = new MenuItem( menu, SWT.RADIO );
    menuItem.setAccelerator( SWT.ALT | 'A' );
    MenuItem menuItem1 = new MenuItem( menu, SWT.RADIO );
    menuItem1.setSelection( true );
    MenuItem menuItem2 = new MenuItem( menu, SWT.CHECK );
    menuItem2.setSelection( true );

    menuItem.handleAcceleratorActivation();

    assertFalse( menuItem1.getSelection() );
    assertTrue( menuItem2.getSelection() );
  }

  @Test
  public void testDeselectionEvent_TriggeredByAccelerator() {
    menuItem = new MenuItem( menu, SWT.RADIO );
    menuItem.setAccelerator( SWT.ALT | 'A' );
    MenuItem menuItem1 = new MenuItem( menu, SWT.RADIO );
    menuItem1.setSelection( true );
    SelectionListener listener = mock( SelectionListener.class );
    menuItem1.addSelectionListener( listener );

    menuItem.handleAcceleratorActivation();

    verify( listener ).widgetSelected( any( SelectionEvent.class ) );
  }

}
