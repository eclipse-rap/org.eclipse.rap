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
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ItemHolder;

public class ItemHolder_Test extends TestCase {

  public void testItemHolder() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    ItemHolder itemHolder = new ItemHolder( TreeItem.class );
    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
    itemHolder.add( item );
    assertEquals( 1, itemHolder.size() );
    // Do not inline to ensure type safety
    TreeItem[] items = (org.eclipse.swt.widgets.TreeItem[] )itemHolder.getItems();
    assertSame( item, items[ 0 ] );
    assertSame( item, itemHolder.getItem( 0 ) );
    itemHolder.remove( item );
    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
    itemHolder.add( item );
    try {
      itemHolder.add( item );
      fail( "The same item must not be added twice." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    try {
      itemHolder.add( null );
      fail( "Parameter item must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    itemHolder.remove( item );
    try {
      itemHolder.remove( item );
      fail( "Only items that are contained in the item list must be removed." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    try {
      itemHolder.remove( null );
      fail( "Parameter item must not be null" );
    } catch( final NullPointerException npe ) {
      // expected
    }
  }

  public void testItemHolderAccessors() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    try {
      ItemHolder.getItems( shell );
      fail( "Shell is not an item holder widget" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    Item[] items = ItemHolder.getItems( tree );
    assertEquals( 0, items.length );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    items = ItemHolder.getItems( tree );
    assertEquals( 1, items.length );
    assertEquals( item, items[ 0 ] ); 
    assertEquals( 1, tree.getItemCount() );
    assertEquals( 0, tree.indexOf( item ) );
    item.dispose();
    items = ItemHolder.getItems( tree );
    assertEquals( 0, items.length );
    assertEquals( 0, tree.getItemCount() );
    Tree anotherTree = new Tree( shell, SWT.NONE );
    TreeItem anotherItem = new TreeItem( anotherTree, SWT.NONE );
    assertEquals( -1, tree.indexOf( anotherItem ) );
    try {
      tree.getItem( 0 );
      fail( "Index out of bounds expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
