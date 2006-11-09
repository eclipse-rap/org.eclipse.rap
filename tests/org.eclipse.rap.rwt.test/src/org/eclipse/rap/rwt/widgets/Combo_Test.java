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

public class Combo_Test extends TestCase {

  public void testGetItemsAndGetItemCount() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Combo combo = new Combo( shell, RWT.NONE );
    assertEquals( 0, combo.getItemCount() );
    assertEquals( 0, combo.getItems().length );
    // add(String)
    combo.add( "test" );
    combo.add( "test2" );
    assertEquals( 2, combo.getItemCount() );
    assertEquals( 2, combo.getItems().length );
    assertSame( "test", combo.getItem( 0 ) );
    // add(String,int)
    combo.add( "test3", 0 );
    assertEquals( 3, combo.getItemCount() );
    assertSame( "test3", combo.getItem( 0 ) );
    assertSame( "test2", combo.getItem( 2 ) );
    // setItem
    combo.setItem( 0, "test" );
    combo.setItem( 1, "test2" );
    combo.setItem( 2, "test3" );
    assertEquals( 3, combo.getItemCount() );
    assertSame( "test", combo.getItem( 0 ) );
    // getItems
    assertSame( "test", combo.getItems()[ 0 ] );
    assertSame( "test2", combo.getItems()[ 1 ] );
    assertSame( "test3", combo.getItems()[ 2 ] );
    // remove(int)
    combo.remove( 1 );
    assertSame( "test3", combo.getItem( 1 ) );
    assertEquals( 2, combo.getItemCount() );
    // setItems
    combo.setItems( new String[] { "test", "test2", "test3" } );
    assertEquals( 3, combo.getItemCount() );
    assertSame( "test2", combo.getItem( 1 ) );
    // remove(int,int)
    combo.remove( 0, 1 );
    assertEquals( 1, combo.getItemCount() );
    assertSame( "test3", combo.getItem( 0 ) );
    // remove(String)
    combo.setItems( new String[]{ "test", "test2", "test3", "test" } );
    combo.remove( "test" );
    assertEquals( 3, combo.getItemCount() );
    assertSame( "test2", combo.getItem( 0 ) );
    // removeAll()
    combo.removeAll();
    assertEquals( 0, combo.getItemCount() );
  }

  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Combo combo = new Combo( shell, RWT.NONE );
    combo.add( "test" );
    combo.dispose();
    assertTrue( combo.isDisposed() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
