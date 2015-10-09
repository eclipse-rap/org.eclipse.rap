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
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Iterator;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class MenuHolder_Test {

  private Shell shell;

  @Rule
  public TestContext context = new TestContext();

  private MenuHolder menuHolder;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    menuHolder = new MenuHolder();
  }

  @Test
  public void testSize() {
    assertEquals( 0, menuHolder.size() );
  }

  @Test
  public void testAdd() {
    Menu menu = mock( Menu.class );

    menuHolder.addMenu( menu );

    assertEquals( 1, menuHolder.size() );
    assertEquals( menu, menuHolder.getMenus()[ 0 ] );
  }

  @Test
  public void testAdd_twice() {
    Menu menu1 = mock( Menu.class );
    Menu menu2 = mock( Menu.class );

    menuHolder.addMenu( menu1 );
    menuHolder.addMenu( menu2 );

    assertEquals( 2, menuHolder.size() );
    assertArrayEquals( new Menu[] { menu1, menu2 }, menuHolder.getMenus() );
  }

  @Test
  public void testRemove() {
    Menu menu1 = mock( Menu.class );
    Menu menu2 = mock( Menu.class );
    menuHolder.addMenu( menu1 );
    menuHolder.addMenu( menu2 );

    menuHolder.removeMenu( menu1 );

    assertEquals( 1, menuHolder.size() );
    assertArrayEquals( new Menu[] { menu2 }, menuHolder.getMenus() );
  }

  @Test
  public void testRemove_twice() {
    Menu menu1 = mock( Menu.class );
    Menu menu2 = mock( Menu.class );
    menuHolder.addMenu( menu1 );
    menuHolder.addMenu( menu2 );

    menuHolder.removeMenu( menu1 );
    menuHolder.removeMenu( menu2 );

    assertEquals( 0, menuHolder.size() );
    assertArrayEquals( new Menu[] {}, menuHolder.getMenus() );
  }

  @Test
  public void testIterator() {
    Menu menu = mock( Menu.class );
    menuHolder.addMenu( menu );

    Iterator<Menu> iterator = menuHolder.iterator();

    assertTrue( iterator.hasNext() );
    assertSame( menu, iterator.next() );
    assertFalse( iterator.hasNext() );
  }

  @Test
  public void testSerialize() throws Exception {
    menuHolder.addMenu( new Menu( shell ) );

    MenuHolder deserializedMenuHolder = serializeAndDeserialize( menuHolder );

    assertEquals( 1, deserializedMenuHolder.size() );
  }

}
