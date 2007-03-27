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
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;

public class Menu_Test extends TestCase {

  public void testMenuBarConstructor() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menuBar );
    assertSame( shell, menuBar.getParent() );
    assertSame( display, menuBar.getDisplay() );
    // Disallow null in constructor
    try {
      new Menu( ( Menu )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Menu( ( Control )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Menu( ( Shell )null );
      fail( "Menu must not accept null-parent in constructor" );
    } catch( NullPointerException e ) {
      // expected
    }
    // Test constructor for popup menus
    Label label = new Label( shell, RWT.NONE );
    Menu labelMenu = new Menu( label );
    assertSame( shell, labelMenu.getParent() );
    assertSame( display, labelMenu.getDisplay() );
    // Test Menu-constructor
    Menu subMenu1 = new Menu( menuBar );
    assertSame( menuBar.getParent(), subMenu1.getParent() );
    assertSame( menuBar.getDisplay(), subMenu1.getDisplay() );
    // Test MenuItem-constructor
    MenuItem item = new MenuItem( menuBar, RWT.CASCADE );
    Menu subMenu2 = new Menu( item );
    assertSame( menuBar.getParent(), subMenu2.getParent() );
    assertSame( menuBar.getDisplay(), subMenu2.getDisplay() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    assertEquals( RWT.BAR, menuBar.getStyle() );
    Menu menuDropDown = new Menu( shell, RWT.DROP_DOWN );
    assertEquals( RWT.DROP_DOWN, menuDropDown.getStyle() );
    Menu menuPopup = new Menu( shell, RWT.POP_UP );
    assertEquals( RWT.POP_UP, menuPopup.getStyle() );
  }
  
  public void testVisibility() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    assertFalse( menuBar.getVisible() );
    assertFalse( menuBar.isVisible() );
    shell.setMenuBar( menuBar );
    assertTrue( menuBar.getVisible() );
    assertTrue( menuBar.isVisible() );
    Menu popupMenu = new Menu( shell, RWT.POP_UP );
    assertFalse( popupMenu.getVisible() );
    assertFalse( popupMenu.isVisible() );
    popupMenu.setVisible( true );
    assertTrue( popupMenu.getVisible() );
    assertTrue( popupMenu.isVisible() );
    Menu dropdownMenu = new Menu( shell, RWT.DROP_DOWN );
    assertFalse( dropdownMenu.getVisible() );
    assertFalse( dropdownMenu.isVisible() );
  }

  public void testItems() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menu = new Menu( shell, RWT.BAR );
    assertEquals( 0, menu.getItemCount() );
    MenuItem item = new MenuItem( menu, RWT.CASCADE );
    assertEquals( 1, menu.getItemCount() );
    assertSame( item, menu.getItem( 0 ) );
    item.dispose();
    assertEquals( 0, menu.getItemCount() );
    item = new MenuItem( menu, RWT.CASCADE );
    assertEquals( 1, menu.getItemCount() );
    assertSame( item, menu.getItems()[ 0 ] );
    assertSame( item, menu.getItem( 0 ) );
    assertEquals( 0, menu.indexOf( item ) );
    new MenuItem( menu, RWT.CASCADE );
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

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menu = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menu );
    MenuItem fileMenuItem = new MenuItem( menu, RWT.CASCADE );
    final Menu fileMenu = new Menu( fileMenuItem );
    fileMenuItem.setMenu( fileMenu );
    MenuItem exitMenuItem = new MenuItem( fileMenu, RWT.PUSH );
    fileMenu.dispose();
    assertEquals( true, fileMenu.isDisposed() );
    assertEquals( true, exitMenuItem.isDisposed() );
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {

      public boolean doVisit( final Widget widget ) {
        assertTrue( widget != fileMenu );
        return true;
      }
    } );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
