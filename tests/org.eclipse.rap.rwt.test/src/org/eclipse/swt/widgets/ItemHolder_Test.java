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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.internal.widgets.ItemHolder;
import org.junit.Before;
import org.junit.Test;


public class ItemHolder_Test {

  private ItemHolder<TestItem> itemHolder;

  @Before
  public void setUp() {
    itemHolder = new ItemHolder<TestItem>( TestItem.class );
  }

  @Test
  public void testInitiallyEmpty() {
    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
  }

  @Test
  public void testAdd() {
    TestItem item = mock( TestItem.class );

    itemHolder.add( item );

    assertEquals( 1, itemHolder.size() );
    // Do not inline to ensure type safety
    TestItem[] items = itemHolder.getItems();
    assertSame( item, items[ 0 ] );
    assertSame( item, itemHolder.getItem( 0 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAdd_failsWithNull() {
    itemHolder.add( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAdd_failsWithExistingItem() {
    TestItem item = mock( TestItem.class );

    itemHolder.add( item );
    itemHolder.add( item );
  }

  @Test
  public void testInsert() {
    TestItem item1 = mock( TestItem.class );
    TestItem item2 = mock( TestItem.class );

    itemHolder.add( item1 );
    itemHolder.insert( item2, 0 );

    assertSame( item2, itemHolder.getItem( 0 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testInsert_failsWithNull() {
    itemHolder.insert( null, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testInsert_failsWithNegativeIndex() {
    itemHolder.insert( null, -1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testInsert_failsWithOutOfRangeIndex() {
    itemHolder.insert( null, 1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testInsert_failsWithExistingItem() {
    TestItem item = mock( TestItem.class );

    itemHolder.add( item );
    itemHolder.insert( item, 0 );
  }

  @Test
  public void testRemove() {
    TestItem item = mock( TestItem.class );
    itemHolder.add( item );

    itemHolder.remove( item );

    assertEquals( 0, itemHolder.size() );
    assertEquals( 0, itemHolder.getItems().length );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemove_failsWithNull() {
    itemHolder.remove( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemove_failsWithMissingItem() {
    TestItem item = mock( TestItem.class );

    itemHolder.remove( item );
  }

  @Test
  public void testSerialize() throws Exception {
    TestItem item = mock( TestItem.class );
    itemHolder.add( item );

    ItemHolder<TestItem> deserializedItemHolder = serializeAndDeserialize( itemHolder );

    assertEquals( 1, deserializedItemHolder.getItems().length );
  }

  private static class TestItem extends Item {
    public TestItem( Widget parent ) {
      super( parent, 0 );
    }
  }

}
