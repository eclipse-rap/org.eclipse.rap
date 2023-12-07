/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.internal.widgets.MenuHolder;
import org.eclipse.swt.internal.widgets.menukit.MenuLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Menu_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Menu menu;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    menu = new Menu( shell, SWT.NONE );
  }

  @Test
  public void testMenuBarConstructor() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    assertSame( shell, menuBar.getParent() );
    assertSame( display, menuBar.getDisplay() );
    // Disallow null in constructor
    try {
      new Menu( ( Menu )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new Menu( ( Control )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new Menu( ( Shell )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Test constructor for popup menus
    Label label = new Label( shell, SWT.NONE );
    Menu labelMenu = new Menu( label );
    assertSame( shell, labelMenu.getParent() );
    assertSame( display, labelMenu.getDisplay() );
    // Test Menu-constructor
    Menu subMenu1 = new Menu( menuBar );
    assertSame( menuBar.getParent(), subMenu1.getParent() );
    assertSame( menuBar.getDisplay(), subMenu1.getDisplay() );
    // Test MenuItem-constructor
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );
    Menu subMenu2 = new Menu( item );
    assertSame( menuBar.getParent(), subMenu2.getParent() );
    assertSame( menuBar.getDisplay(), subMenu2.getDisplay() );
  }

  @Test
  public void testStyle() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    assertEquals( SWT.BAR | SWT.LEFT_TO_RIGHT, menuBar.getStyle() );

    Menu menuDropDown = new Menu( shell, SWT.DROP_DOWN );
    assertEquals( SWT.DROP_DOWN | SWT.LEFT_TO_RIGHT, menuDropDown.getStyle() );

    Menu menuPopup = new Menu( shell, SWT.POP_UP );
    assertEquals( SWT.POP_UP | SWT.LEFT_TO_RIGHT, menuPopup.getStyle() );
  }

  @Test
  public void testVisibility() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    assertFalse( menuBar.getVisible() );
    assertFalse( menuBar.isVisible() );
    shell.setMenuBar( menuBar );
    assertTrue( menuBar.getVisible() );
    assertTrue( menuBar.isVisible() );
    Menu popupMenu = new Menu( shell, SWT.POP_UP );
    assertFalse( popupMenu.getVisible() );
    assertFalse( popupMenu.isVisible() );
    popupMenu.setVisible( true );
    assertTrue( popupMenu.getVisible() );
    assertTrue( popupMenu.isVisible() );
    Menu dropdownMenu = new Menu( shell, SWT.DROP_DOWN );
    assertFalse( dropdownMenu.getVisible() );
    assertFalse( dropdownMenu.isVisible() );
  }

  @Test
  public void testItems() {
    Menu menu = new Menu( shell, SWT.BAR );
    assertEquals( 0, menu.getItemCount() );
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    assertEquals( 1, menu.getItemCount() );
    assertSame( item, menu.getItem( 0 ) );
    item.dispose();
    assertEquals( 0, menu.getItemCount() );
    item = new MenuItem( menu, SWT.CASCADE );
    assertEquals( 1, menu.getItemCount() );
    assertSame( item, menu.getItems()[ 0 ] );
    assertSame( item, menu.getItem( 0 ) );
    assertEquals( 0, menu.indexOf( item ) );
    new MenuItem( menu, SWT.CASCADE );
    assertEquals( 2, menu.getItemCount() );
    item.dispose();
    try {
      menu.indexOf( item );
      fail( "Obtaining indexOf disposed menuItem is illegal" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      menu.indexOf( null );
      fail( "indexOf( null ) not allowed" );
    } catch( RuntimeException e ) {
      // expected
    }
  }

  @Test
  public void testDispose_disposesItems() {
    MenuItem menuItem = new MenuItem( menu, SWT.CASCADE );
    Menu subMenu = new Menu( menuItem );
    menuItem.setMenu( subMenu );

    menu.dispose();

    assertTrue( menuItem.isDisposed() );
    assertTrue( subMenu.isDisposed() );
  }

  @Test
  public void testDispose_removesFromParent() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileMenuItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu fileMenu = new Menu( fileMenuItem );
    fileMenuItem.setMenu( fileMenu );

    fileMenu.dispose();

    assertArrayEquals( new Menu[] { menu, menuBar }, shell.getAdapter( MenuHolder.class ).getMenus() );
  }

  @Test
  public void testUntypedShowEvent() {
    final java.util.List<Event> log = new ArrayList<Event>();
    Listener listener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.add( event );
      }
    };
    // popup menus fire show events
    Menu popupMenu = new Menu( shell, SWT.POP_UP );
    popupMenu.addListener( SWT.Show, listener );
    popupMenu.setVisible( true );
    assertEquals( 1, log.size() );
    assertSame( popupMenu, log.get( 0 ).widget );
    assertEquals( SWT.Show, log.get( 0 ).type );
    // BAR menus must not fire show events
    log.clear();
    Menu barMenu = new Menu( shell, SWT.BAR );
    barMenu.addListener( SWT.Show, listener );
    barMenu.setVisible( true );
    assertEquals( 0, log.size() );
    // DROP_DOWN menus must not fire show events
    log.clear();
    Menu dropDownMenu = new Menu( shell, SWT.DROP_DOWN );
    dropDownMenu.addListener( SWT.Show, listener );
    dropDownMenu.setVisible( true );
    assertEquals( 0, log.size() );
  }

  @Test
  public void testGetOrientation_default() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    assertEquals( SWT.LEFT_TO_RIGHT, menu.getOrientation() );
  }

  @Test
  public void testGetOrientation_RTL() {
    Menu menu = new Menu( shell, SWT.POP_UP | SWT.RIGHT_TO_LEFT );
    assertEquals( SWT.RIGHT_TO_LEFT, menu.getOrientation() );
  }

  @Test
  public void testGetOrientation_onPopupMenu() {
    Menu menu = new Menu( shell, SWT.POP_UP );

    menu.setOrientation( SWT.RIGHT_TO_LEFT );

    assertEquals( SWT.RIGHT_TO_LEFT, menu.getOrientation() );
  }

  @Test
  public void testGetOrientation_onDropDownMenu() {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );

    menu.setOrientation( SWT.RIGHT_TO_LEFT );

    assertEquals( SWT.LEFT_TO_RIGHT, menu.getOrientation() );
  }

  @Test
  public void testDefaultItem() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    menu.setDefaultItem( item );
    assertNull( menu.getDefaultItem() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    new MenuItem( menu, SWT.PUSH );

    Menu deserializedMenu = serializeAndDeserialize( menu );

    assertEquals( 1, deserializedMenu.getItemCount() );
  }

  @Test
  public void testAddHelpListener() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    menu.addHelpListener( mock( HelpListener.class ) );

    assertTrue( menu.isListening( SWT.Help ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddHelpListener_withNullArgument() {
    menu.addHelpListener( null );
  }

  @Test
  public void testRemoveHelpListener() {
    HelpListener listener = mock( HelpListener.class );
    menu.addHelpListener( listener );

    menu.removeHelpListener( listener );

    assertFalse( menu.isListening( SWT.Help ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveHelpListener_withNullArgument() {
    menu.removeHelpListener( null );
  }

  @Test
  public void testAddMenuListener() {
    menu.addMenuListener( mock( MenuListener.class ) );

    assertTrue( menu.isListening( SWT.Show ) );
    assertTrue( menu.isListening( SWT.Hide ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddMenuListener_withNullArgument() {
    menu.addMenuListener( null );
  }

  @Test
  public void testRemoveMenuListener() {
    MenuListener listener = mock( MenuListener.class );
    menu.addMenuListener( listener );

    menu.removeMenuListener( listener );

    assertFalse( menu.isListening( SWT.Show ) );
    assertFalse( menu.isListening( SWT.Hide ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveMenuListener_withNullArgument() {
    menu.removeMenuListener( null );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( menu.getAdapter( WidgetLCA.class ) instanceof MenuLCA );
    assertSame( menu.getAdapter( WidgetLCA.class ), menu.getAdapter( WidgetLCA.class ) );
  }

  @Test
  public void testLocation() {
    Menu popupMenu = spy( new Menu( shell, SWT.POP_UP ) );
    popupMenu.setLocation( 10, 10 );
    popupMenu.setVisible( true );
    verify( popupMenu ).setLocation( 10, 10 );

    popupMenu.setVisible( true );
    verify( popupMenu ).setLocation( anyInt(), anyInt() );
  }
}
