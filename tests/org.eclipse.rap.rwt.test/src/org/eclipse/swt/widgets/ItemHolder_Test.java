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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ItemHolder_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testItemHolder() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    ItemHolder<TreeItem> itemHolder = new ItemHolder<TreeItem>( TreeItem.class );
    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
    itemHolder.add( item );
    assertEquals( 1, itemHolder.size() );
    // Do not inline to ensure type safety
    TreeItem[] items = itemHolder.getItems();
    assertSame( item, items[ 0 ] );
    assertSame( item, itemHolder.getItem( 0 ) );
    itemHolder.remove( item );
    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
    itemHolder.add( item );
    try {
      itemHolder.add( item );
      fail( "The same item must not be added twice." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    try {
      itemHolder.add( null );
      fail( "Parameter item must not be null." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    itemHolder.remove( item );
    try {
      itemHolder.remove( item );
      fail( "Only items that are contained in the item list must be removed." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    try {
      itemHolder.remove( null );
      fail( "Parameter item must not be null" );
    } catch( IllegalArgumentException npe ) {
      // expected
    }
  }

  @Test
  public void testItemHolderAccessors() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    try {
      ItemHolder.getItemHolder( shell ).getItems();
      fail( "Shell is not an item holder widget" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    Item[] items = ItemHolder.getItemHolder( tree ).getItems();
    assertEquals( 0, items.length );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    items = ItemHolder.getItemHolder( tree ).getItems();
    assertEquals( 1, items.length );
    assertEquals( item, items[ 0 ] );
    assertEquals( 1, tree.getItemCount() );
    assertEquals( 0, tree.indexOf( item ) );
    item.dispose();
    items = ItemHolder.getItemHolder( tree ).getItems();
    assertEquals( 0, items.length );
    assertEquals( 0, tree.getItemCount() );
    Tree anotherTree = new Tree( shell, SWT.NONE );
    TreeItem anotherItem = new TreeItem( anotherTree, SWT.NONE );
    assertEquals( -1, tree.indexOf( anotherItem ) );
    try {
      tree.getItem( 0 );
      fail( "Index out of bounds expected" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSerialize() throws Exception {
    ItemHolder<TestItem> itemHolder = new ItemHolder<TestItem>( TestItem.class );
    itemHolder.add( new TestItem( new Shell( new Display() ) ) );

    ItemHolder<TestItem> deserializedItemHolder = Fixture.serializeAndDeserialize( itemHolder );

    assertEquals( 1, deserializedItemHolder.getItems().length );
  }

  private static class TestItem extends Item {
    public TestItem( Widget parent ) {
      super( parent, 0 );
    }
  }

}
