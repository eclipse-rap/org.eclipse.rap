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

/**
 * @author georg
 */

public class List_Test extends TestCase {

  public void testGetItemsAndGetItemCount() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    List list = new List( shell, RWT.NONE );
    assertEquals( 0, list.getItemCount() );
    assertEquals( 0, list.getItems().length );
    // add(String)
    list.add( "test" );
    list.add( "test2" );
    assertEquals( 2, list.getItemCount() );
    assertEquals( 2, list.getItems().length );
    assertSame( "test", list.getItem( 0 ) );
    // add(String,int)
    list.add( "test3", 0 );
    assertEquals( 3, list.getItemCount() );
    assertSame( "test3", list.getItem( 0 ) );
    assertSame( "test2", list.getItem( 2 ) );
    // setItem
    list.setItem( 0, "test" );
    list.setItem( 1, "test2" );
    list.setItem( 2, "test3" );
    assertEquals( 3, list.getItemCount() );
    assertSame( "test", list.getItem( 0 ) );
    // getItems
    assertSame( "test", list.getItems()[ 0 ] );
    assertSame( "test2", list.getItems()[ 1 ] );
    assertSame( "test3", list.getItems()[ 2 ] );
    // remove(int)
    list.remove( 1 );
    assertSame( "test3", list.getItem( 1 ) );
    assertEquals( 2, list.getItemCount() );
    // setItems
    list.setItems( new String[] { "test", "test2", "test3" } );
    assertEquals( 3, list.getItemCount() );
    assertSame( "test2", list.getItem( 1 ) );
    // remove(int,int)
    list.remove( 0, 1 );
    assertEquals( 1, list.getItemCount() );
    assertSame( "test3", list.getItem( 0 ) );
    // remove(String)
    list.setItems( new String[]{ "test", "test2", "test3", "test" } );
    list.remove( "test" );
    assertEquals( 3, list.getItemCount() );
    assertSame( "test2", list.getItem( 0 ) );
    // removeAll()
    list.removeAll();
    assertEquals( 0, list.getItemCount() );
  }

  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    List list = new List( shell, RWT.NONE );
    list.add( "test" );
    list.dispose();
    assertTrue( list.isDisposed() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
