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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class List_Test {

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
  public void testGetItemsAndGetItemCount() {
    List list = new List( shell, SWT.NONE );
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
    assertEquals( "test2", list.getItem( 1 ) );
    // remove(int,int)
    list.remove( 0, 1 );
    assertEquals( 1, list.getItemCount() );
    assertEquals( "test3", list.getItem( 0 ) );
    // remove(String)
    list.setItems( new String[]{ "test", "test2", "test3", "test" } );
    list.remove( "test" );
    assertEquals( 3, list.getItemCount() );
    assertEquals( "test2", list.getItem( 0 ) );
    // removeAll()
    list.removeAll();
    assertEquals( 0, list.getItemCount() );
  }

  @Test
  public void testAdd() {
    List list = new List( shell, SWT.NONE );

    // add item at end of list
    list.add( "test" );
    assertEquals( "test", list.getItem( 0 ) );

    // add duplicate items
    list.add( "test" );
    String[] expected = new String[] { "test", "test" };
    assertTrue( Arrays.equals( expected, list.getItems() ) );

    // ensure handling of invalid arguments
    int sizeBefore = list.getItemCount();
    try {
      list.add( null );
      fail( "add( null ) not allowed." );
    } catch( IllegalArgumentException e ) {
      assertEquals( sizeBefore, list.getItemCount() );
    }

    // insert item at invalid position
    list.removeAll();
    list.add( "item1" );
    try {
      list.add( "bad", 50 );
      fail( "Inserting an item at an invalid position must not be allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      list.add( "bad", -2 );
      fail( "Inserting an item at an invalid position must not be allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // insert item2 between item1 and item3
    list.removeAll();
    list.add( "item1" );
    list.add( "item3" );
    list.add( "item2", 1 );
    expected = new String[]{ "item1", "item2", "item3" };
    assertTrue( Arrays.equals( expected, list.getItems() ) );

    // insert item in empty list
    list.removeAll();
    list.add( "xyz", 0 );
    assertEquals( "xyz", list.getItem( 0 ) );
  }

  @Test
  public void testRemove() {
    List list = new List( shell, SWT.NONE );
    list.add( "item1" );

    // Test remove at specific position
    list.remove( 0 );
    assertEquals( 0, list.getItemCount() );
    list.add( "item1" );
    list.add( "item2" );
    list.remove( 0 );
    assertEquals( 1, list.getItemCount() );
    assertEquals( "item2", list.getItem( 0 ) );

    // Test remove with illegal arguments
    list.removeAll();
    try {
      list.remove( 0 );
      fail( "remove with invalid index must not be allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Test remove range
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    list.remove( 0, 2 );
    assertEquals( 1, list.getItemCount() );
    assertEquals( "item3", list.getItem( 0 ) );

    // Test remove range where start > end
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    list.remove( 1, 0 );
    assertEquals( 4, list.getItemCount() );

    // Test remove(int[])
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    list.remove( new int[] { 0, 2 } );
    assertEquals( 2, list.getItemCount() );
    assertEquals( "item1", list.getItem( 0 ) );
    assertEquals( "item3", list.getItem( 1 ) );

    // Test remove(int[]) with an invalid index
    list.removeAll();
    list.add( "item0" );
    try {
      list.remove( new int[] { 0, 2 } );
      fail( "remove(int[]) with an invalid index must throw exception" );
    } catch( IllegalArgumentException e ) {
      assertEquals( 1, list.getItemCount() );
    }

    // Test remove(int[]) with an empty array argument
    list.removeAll();
    list.add( "item0" );
    list.remove( new int[ 0 ] );
    assertEquals( 1, list.getItemCount() );

    // Test remove(int[]) with null-argument
    list.removeAll();
    list.add( "item0" );
    try {
      list.remove( ( int[] )null );
      fail( "remove(int[]) with an null-argument must throw exception" );
    } catch( IllegalArgumentException iae ) {
      assertEquals( 1, list.getItemCount() );
    }

    // Test remove(string)
    list.removeAll();
    list.add( "item0" );
    list.remove( "item0" );
    assertEquals( 0, list.getItemCount() );

    // Test remove(string) with null-argument
    list.removeAll();
    list.add( "item0" );
    try {
      list.remove( ( String )null );
      fail( "remove(String) with an null-argument must throw exception" );
    } catch( IllegalArgumentException e ) {
      assertEquals( 1, list.getItemCount() );
    }

    // Test remove(string) with non-existing string
    list.removeAll();
    list.add( "item0" );
    try {
      list.remove( "bad-item" );
      fail(   "remove(String) with an non-existing string as its argument "
            + "must throw exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Test removeAll
    list.add( "at least one item" );
    list.removeAll();
    assertEquals( 0, list.getItemCount() );
  }

  @Test
  public void testSelectionForSingle() {
    List list = new List( shell, SWT.SINGLE );

    // Test initials state of selection
    assertEquals( -1, list.getSelectionIndex() );
    assertEquals( 0, list.getSelectionCount() );
    assertEquals( 0, list.getSelection().length );

    // Test selecting single item
    list.add( "test1" );
    list.setSelection( 0 );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );
    assertEquals( 1, list.getSelection().length );
    assertEquals( "test1", list.getSelection()[ 0 ] );

    // Test selecting single item with setSelection(int[])
    list.removeAll();
    list.add( "test1" );
    list.setSelection( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // Test selecting multiple items: selection will be reset (list is SINGLE)
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1 );
    list.setSelection( new int[] { 0, 2 } );
    assertEquals( 0, list.getSelectionCount() );
    assertEquals( 0, list.getSelection().length );

    // Test setSelection(int[]) with invaid arguments
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    try {
      list.setSelection( ( int[] )null );
      fail( "setSelection(int[]) with null-argument is not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.setSelection( new int[]{ 10 } );
    assertEquals( -1, list.getSelectionIndex() );
    list.removeAll();
    list.add( "test1" );
    list.add( "test1" );
    list.setSelection( 0 );
    list.setSelection( new int[]{ 0, 1 } );
    assertEquals( -1, list.getSelectionIndex() );

    // Ensure that adding an item does not change selection
    list.removeAll();
    list.add( "test1" );
    list.setSelection( 0 );
    list.add( "test2" );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // Ensure that inserting an item does not change selection
    list.removeAll();
    list.add( "test1" );
    list.setSelection( 0 );
    list.add( "test2" );
    list.setSelection( 0 );
    list.add( "test1.5", 1 );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // removing selected item must remove the selection also
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.remove( 0 );
    assertEquals( -1, list.getSelectionIndex() );

    // Using setItems(String[]) must reset selection
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.setItems( new String[] { "a", "b" } );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(int,int)
    list.removeAll();
    list.add( "test1" );
    list.add( "test2" );
    list.add( "test3" );
    list.setSelection( 0, 0 );
    assertEquals( 0, list.getSelectionIndex() );
    // setSelection(int,int) must specify only one item in the range
    list.setSelection( 0, 1 );
    assertEquals( -1, list.getSelectionIndex() );
    // Test setSelection(int,int) with invalid ranges: must not select anything
    list.setSelection( 0 );
    list.setSelection( 7, 1 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( 7, 1 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( -2, -2 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( -1, 2 );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[])
    list.removeAll();
    list.add( "test1" );
    list.add( "test2" );
    list.add( "test3" );
    list.setSelection( -1 );
    list.setSelection( new String[] { "test1" } );
    assertEquals( 0, list.getSelectionIndex() );

    // Test setSelection(String[]) with empty array: deselect all
    list.setSelection( 0 );
    list.setSelection( new String[ 0 ] );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[]) with more than one String: deselect all
    list.setSelection( 0 );
    list.setSelection( new String[] { "test1", "test2" } );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( new String[] { null, "test2" } );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[]) with one non-existing String: deselect all
    list.setSelection( 0 );
    list.setSelection( new String[] { null } );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( new String[] { "not here" } );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[]) with invalid arguments
    try {
      list.setSelection( ( String[] )null );
      fail( "setSelection(String[]) must not allow null-argument" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    // Select last item and remove it
    list.removeAll();
    list.add( "item0" );
    list.add( "last item" );
    list.setSelection( list.getItemCount() - 1 );
    list.remove( list.getItemCount() - 1 );
    assertEquals( 1, list.getItemCount() );
    assertEquals( -1, list.getSelectionIndex() );

    // Select non-existing item
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1 );
    list.setSelection( 12345 );
    assertEquals( -1, list.getSelectionIndex() );

    // selectAll must be ignored for single-select Lists
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1 );
    list.selectAll();
    assertEquals( 1, list.getSelectionIndex() );
    assertEquals( 1, list.getSelectionCount() );

    // select must replace a current selection if called with valid argument
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( 1 );
    assertEquals( 1, list.getSelectionIndex() );
    list.select( -3 );
    assertEquals( 1, list.getSelectionIndex() );
    list.select( list.getItemCount() + 10 );
    assertEquals( 1, list.getSelectionIndex() );

    // test select( int[] )
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    // proper usage
    list.select( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // calling again must not change anything
    list.select( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // duplicate entries in argumenet are ignored
    list.select( new int[] { 0, 0, 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // calls with more than one entries are ignored
    list.select( new int[] { 1, 2 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // calls with empty array are ignored
    list.select( new int[] { } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // invalid argument
    try {
      list.select( null );
      fail( "Null argument not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // test select( int, int )
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    // proper usage
    list.select( 0, 0 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // invalid usage
    list.select( 1, 2 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    list.select( 11, 11 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    list.select( -3, -1 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // proper usage
    list.select( 1, 1 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 1, list.getSelectionIndices()[ 0 ] );

    // Test setSelection with selection listener
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    SelectionListener listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        String msg
          = "SelectionEvent must not be fired when selecting programmatically";
        fail( msg );
      }
    };
    list.addSelectionListener( listener );
    list.setSelection( 1 );
    list.removeSelectionListener( listener );

    // Test deselect
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( 0 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    list.deselect( 0 );
    assertEquals( 0, list.getSelectionCount() );

    list.select( 1 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 1, list.getSelectionIndices()[ 0 ] );
    list.deselect( new int[] { 0, 1 } );
    assertEquals( 0, list.getSelectionCount() );

    list.select( 1 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 1, list.getSelectionIndices()[ 0 ] );
    list.deselect( 0, 2 );
    assertEquals( 0, list.getSelectionCount() );
  }

  @Test
  public void testSelectionForMulti() {
    List list = new List( shell, SWT.MULTI );

    // Test initials state of selection
    assertEquals( -1, list.getSelectionIndex() );
    assertEquals( 0, list.getSelectionCount() );
    assertEquals( 0, list.getSelection().length );

    // Test selecting single item
    list.add( "test1" );
    list.setSelection( 0 );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );
    assertEquals( "test1", list.getSelection()[ 0 ] );

    // Test selecting single item with setSelection(int[])
    list.removeAll();
    list.add( "test1" );
    list.setSelection( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // Test selecting multiple items
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1 );
    int[] selection = new int[] { 0, 2 };
    list.setSelection( selection );
    assertEquals( 2, list.getSelectionCount() );
    assertTrue( Arrays.equals( selection, list.getSelectionIndices() ) );
    assertEquals( "item0", list.getSelection()[ 0 ] );
    assertEquals( "item2", list.getSelection()[ 1 ] );

    // Test setSelection(int[]) with (partially) invaid arguments
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    try {
      list.setSelection( ( int[] )null );
      fail( "setSelection(int[]) with null-argument is not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.setSelection( new int[]{ 10, 12 } );
    assertEquals( -1, list.getSelectionIndex() );
    assertEquals( 0, list.getSelection().length );
    list.removeAll();
    list.add( "test1" );
    list.add( "test2" );
    list.setSelection( 0 );
    list.setSelection( new int[]{ 1, 8 } );
    assertEquals( 1, list.getSelectionIndex() );
    assertEquals( "test2", list.getSelection()[ 0 ] );

    // Ensure that adding an item does not change selection
    list.removeAll();
    list.add( "test1" );
    list.setSelection( 0 );
    list.add( "test2" );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // Ensure that inserting an item does not change selection
    list.removeAll();
    list.add( "test1" );
    list.setSelection( 0 );
    list.add( "test2" );
    list.setSelection( 0 );
    list.add( "test1.5", 1 );
    assertEquals( 0, list.getSelectionIndex() );
    assertTrue( Arrays.equals( new int[] { 0 }, list.getSelectionIndices() ) );

    // removing selected item must remove the selection also
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.remove( 0 );
    assertEquals( -1, list.getSelectionIndex() );

    // Using setItems(String[]) must reset selection
    list.removeAll();
    list.add( "test" );
    list.setSelection( 0 );
    list.setItems( new String[] { "a", "b" } );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(int,int)
    list.removeAll();
    list.add( "test1" );
    list.add( "test2" );
    list.add( "test3" );
    list.setSelection( 0, 0 );
    assertEquals( 0, list.getSelectionIndex() );
    // setSelection(int,int) must specify only one item in the range
    list.setSelection( 0, 1 );
    assertEquals( 0, list.getSelectionIndex() );
    assertEquals( 2, list.getSelectionCount() );
    assertTrue( Arrays.equals( new int[] { 0, 1 },
                               list.getSelectionIndices() ) );
    // Test setSelection(int,int) with invalid ranges: must not select anything
    list.setSelection( 0 );
    list.setSelection( 7, 1 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( 7, 1 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( -2, -2 );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( -1, 2 );
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 3, list.getSelectionCount() );

    // Test setSelection(String[])
    list.removeAll();
    list.add( "test1" );
    list.add( "test2" );
    list.add( "test3" );
    list.setSelection( -1 );
    list.setSelection( new String[] { "test1" } );
    assertEquals( 0, list.getSelectionIndex() );

    // Test setSelection(String[])
    list.setSelection( 0 );
    list.setSelection( new String[] { "test1", "test2" } );
    assertEquals( 0, list.getSelectionIndex() );
    assertEquals( 2, list.getSelectionCount() );
    assertTrue( Arrays.equals( new int[] { 0, 1 },
                               list.getSelectionIndices() ) );
    list.setSelection( 0 );
    list.setSelection( new String[] { null, "test2" } );
    assertEquals( 1, list.getSelectionIndex() );

    // Test setSelection(String[]) with empty array: deselect all
    list.setSelection( 0 );
    list.setSelection( new String[ 0 ] );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[]) with one non-existing String: deselect all
    list.setSelection( 0 );
    list.setSelection( new String[] { null } );
    assertEquals( -1, list.getSelectionIndex() );
    list.setSelection( 0 );
    list.setSelection( new String[] { "not here" } );
    assertEquals( -1, list.getSelectionIndex() );

    // Test setSelection(String[]) with invalid arguments
    try {
      list.setSelection( ( String[] )null );
      fail( "setSelection(String[]) must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Select non-existing item
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1 );
    list.setSelection( 12345 );
    assertEquals( -1, list.getSelectionIndex() );

    // selectAll must be ignored for single-select Lists
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.selectAll();
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );

    // select must add to the current selection if called with valid argument
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( 0 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    list.select( 1 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    list.select( -1 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    list.select( list.getItemCount() + 10 );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );

    // test select( int[] )
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    // proper usage
    list.select( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // calling again must not change anything
    list.select( new int[] { 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // duplicate entries in argumenet are ignored
    list.select( new int[] { 0, 0, 0 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // propert usage with more than one entries
    list.select( new int[] { 1, 2 } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );
    // calls with empty array are ignored
    list.select( new int[] { } );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    // invalid argument
    try {
      list.select( null );
      fail( "Null argument not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // test select( int, int )
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    // proper usage
    list.select( 0, 0 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    list.select( 1, 2 );
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );
    // invalid usage
    list.select( 11, 11 );
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );
    list.select( -3, -1 );
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );
    // select an already selected index
    list.select( 1, 1 );
    assertEquals( 3, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    assertEquals( 2, list.getSelectionIndices()[ 2 ] );

    // Ensure that no selection indices after remove all items one by one
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( new int[] { 0, 1, 2 } );
    String[] listSelection = list.getSelection();
    for( int i = 0; i < listSelection.length; i++ ) {
      list.remove( listSelection[ i ] );
    }
    assertEquals( 0, list.getSelectionCount() );

    // Ensure that no selection indices after remove selected items one by one
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( new int[] { 0, 1 } );
    listSelection = list.getSelection();
    for( int i = 0; i < listSelection.length; i++ ) {
      list.remove( listSelection[ i ] );
    }
    assertEquals( 0, list.getSelectionCount() );

    // Test deselect
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.select( new int[] { 0, 1 } );
    listSelection = list.getSelection();
    assertEquals( 2, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );
    assertEquals( 1, list.getSelectionIndices()[ 1 ] );
    list.deselect( 1 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 0, list.getSelectionIndices()[ 0 ] );

    list.selectAll();
    assertEquals( 3, list.getSelectionCount() );
    list.deselect( new int[] { 0, 2 } );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 1, list.getSelectionIndices()[ 0 ] );

    list.selectAll();
    assertEquals( 3, list.getSelectionCount() );
    list.deselect( 0, 1 );
    assertEquals( 1, list.getSelectionCount() );
    assertEquals( 2, list.getSelectionIndices()[ 0 ] );

    // invalid argument
    try {
      list.deselect( null );
      fail( "Null argument not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetItem() {
    List list = new List( shell, SWT.NONE );

    // Test setItem
    list.add( "itemX" );
    list.add( "item1" );
    list.setItem( 0, "item0" );
    assertEquals( "item0", list.getItem( 0 ) );

    // Test setItem with invalid index
    list.removeAll();
    try {
      list.setItem( -4, "won't make it" );
      fail( "Must check valid range of index" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Test setItem with null argument
    list.removeAll();
    list.add( "abc" );
    try {
      list.setItem( 0, null );
      fail( "Must check valid range of index" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetItems() {
    List list = new List( shell, SWT.NONE );

    // Test setItems on empty list
    String[] itemsToSet = new String[] { "a", "b", "c" };
    list.setItems( itemsToSet );
    assertTrue( Arrays.equals( itemsToSet, list.getItems() ) );

    // Test setItems on empty list
    String[] otherItemsToSet = new String[] { "a", "b", "c", "d" };
    list.setItems( otherItemsToSet );
    assertTrue( Arrays.equals( otherItemsToSet, list.getItems() ) );

    // Test setItems with null-argument
    list.removeAll();
    list.add( "a" );
    try {
      list.setItems( null );
      fail( "setItems(null) is not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( 1, list.getItemCount() );
    }

    // Test setItems with an arrays that contains a null-string
    list.removeAll();
    list.add( "a" );
    try {
      list.setItems( new String[] { "a", null, "b" } );
      fail( "setItems(String[]) with a null-string is not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( 1, list.getItemCount() );
    }
  }

  @Test
  public void testGetItem() {
    List list = new List( shell, SWT.NONE );

    // Test getItem for existing item
    list.add( "item0" );
    assertEquals( "item0", list.getItem( 0 ) );

    // Test getItem for non-existing item
    try {
      list.getItem( -2 );
      fail( "Must not allow getItem with invalid index" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      list.getItem( 22 );
      fail( "Must not allow getItem with invalid index" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testGetItems() {
    List list = new List( shell, SWT.NONE );
    assertEquals( 0, list.getItems().length );
    list.add( "item1" );
    assertTrue( Arrays.equals( new String[] { "item1" }, list.getItems() ) );
  }

  @Test
  public void testStyle() {
    List list1 = new List( shell, SWT.NONE );
    assertTrue( ( list1.getStyle() & SWT.SINGLE ) != 0 );

    List list2 = new List( shell, SWT.SINGLE );
    assertTrue( ( list2.getStyle() & SWT.SINGLE ) != 0 );

    List list3 = new List( shell, SWT.MULTI );
    assertTrue( ( list3.getStyle() & SWT.MULTI ) != 0 );

    List list4 = new List( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( list4.getStyle() & SWT.SINGLE ) != 0 );
  }

  @Test
  public void testFocusIndexForSingle() {
    List list = new List( shell, SWT.SINGLE );

    // Initial focusIndex must be -1
    assertEquals( -1, list.getFocusIndex() );

    // focusIndex must be 0 after adding the first item
    list.removeAll();
    list.add( "item0" );
    assertEquals( 0, list.getFocusIndex() );

    // focusIndex must be -1 after removeing last item
    list.removeAll();
    list.add( "item0" );
    list.remove( 0 );
    assertEquals( -1, list.getFocusIndex() );

    // Test that focusIndex goes with the selection, if any
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 0 );
    assertEquals( 0, list.getFocusIndex() );
    list.remove( 1 );
    assertEquals( 0, list.getFocusIndex() );
    list.setSelection( 1 );
    assertEquals( 1, list.getFocusIndex() );

    // Setting a selection range must set the focusIndex to the selection start
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1, 1 );
    assertEquals( 1, list.getFocusIndex() );

    // Removing the last but not only item (when selected and focused)
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 2 );
    list.remove( 2 );
    assertEquals( 1, list.getFocusIndex() );
    assertEquals( -1, list.getSelectionIndex() );

    // Don't move focusIndex when selecting a non-existing item
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 0 );
    list.setSelection( 1234 );
    assertEquals( 0, list.getFocusIndex() );

    // Ensure that using List with ListViewer and 'empty' ContentProvider works
    list.removeAll();
    list.deselectAll();
    assertEquals( -1, list.getFocusIndex() );
  }

  @Test
  public void testFocusIndexForMulti() {
    List list = new List( shell, SWT.MULTI );

    // Initial focusIndex must be -1
    assertEquals( -1, list.getFocusIndex() );

    // focusIndex must be 0 after adding the first item
    list.removeAll();
    list.add( "item0" );
    assertEquals( 0, list.getFocusIndex() );

    // focusIndex must be -1 after removeing last item
    list.removeAll();
    list.add( "item0" );
    list.remove( 0 );
    assertEquals( -1, list.getFocusIndex() );

    // Test that focusIndex goes with the selection, if any
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 0 );
    assertEquals( 0, list.getFocusIndex() );
    list.remove( 1 );
    assertEquals( 0, list.getFocusIndex() );
    list.setSelection( 1 );
    assertEquals( 1, list.getFocusIndex() );
    list.setSelection( -1 );
    list.setSelection( 1, 2 );
    assertEquals( 1, list.getFocusIndex() );

    // Setting a selection range must set the focusIndex to the selection start
    list.removeAll();
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( 1, 2 );
    assertEquals( 1, list.getFocusIndex() );

    // Ensure that using List with ListViewer and 'empty' ContentProvider works
    list.removeAll();
    list.deselectAll();
    assertEquals( -1, list.getFocusIndex() );
  }

  @Test
  public void testTopIndex() {
    List list = new List( shell, SWT.NONE );
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    list.add( "item4" );
    list.add( "item5" );

    // Set a value which is out of bounds
    int previousTopIndex = list.getTopIndex();
    list.setTopIndex( 100 );
    assertEquals( previousTopIndex, list.getTopIndex() );
    list.setTopIndex( -1 );
    assertEquals( previousTopIndex, list.getTopIndex() );

    // Set topIndex to the last item
    list.setTopIndex( 5 );
    assertEquals( 5, list.getTopIndex() );

    // Remove last item (whose index equals topIndex) -> must adjust topIndex
    list.remove( 5 );
    assertEquals( 4, list.getTopIndex() );

    // Ensure that topIndex stays at least 0 even if all items are removed
    list.removeAll();
    list.add( "soleItem" );
    list.remove( list.indexOf( "soleItem" ) );
    assertEquals( 0, list.getTopIndex() );
  }

  @Test
  public void testDispose() {
    List list = new List( shell, SWT.NONE );
    list.add( "test" );
    list.dispose();
    assertTrue( list.isDisposed() );
  }

  @Test
  public void testComputeSize() {
    List list = new List( shell, SWT.NONE );
    Point expected = new Point( 64, 64 );
    assertEquals( expected, list.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    list.add( "test 1" );
    list.add( "test 22" );
    list.add( "test 333" );
    expected = new Point( 74, 78 );
    assertEquals( expected, list.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    list = new List( shell, SWT.V_SCROLL );
    list.add( "test 1" );
    list.add( "test 22" );
    list.add( "test 333" );
    expected = new Point( 84, 78 );
    assertEquals( expected, list.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    list = new List( shell, SWT.H_SCROLL );
    list.add( "test 1" );
    list.add( "test 22" );
    list.add( "test 333" );
    expected = new Point( 74, 88 );
    assertEquals( expected, list.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    list = new List( shell, SWT.BORDER );
    list.add( "test 1" );
    list.add( "test 22" );
    list.add( "test 333" );
    expected = new Point( 76, 80 );
    assertEquals( expected, list.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 102, 102 );
    assertEquals( expected, list.computeSize( 100, 100 ) );
  }

  @Test
  public void testShowSelectionEmptyList() {
    List list = new List( shell, SWT.NONE );
    list.showSelection();
    list.add( "1" );
    list.add( "2" );
    list.add( "3" );
    list.setSelection( new int[]{
      0, 1, 2
    } );
    list.showSelection();
    list.removeAll();
    list.showSelection();
  }

  @Test
  public void testShowSelectionBottom() {
    List list = new List( shell, SWT.NONE );
    list.setSize( 100, 26 );
    list.add( "1" );
    list.add( "2" );
    list.add( "3" );
    assertEquals( 0, list.getTopIndex() );
    list.setSelection( 2 );
    list.showSelection();
    assertEquals( 2, list.getTopIndex() );
    list.removeAll();
  }

  @Test
  public void testShowSelectionTop() {
    List list = new List( shell, SWT.NONE );
    list.setSize( 100, 20 );
    list.add( "1" );
    list.add( "2" );
    list.add( "3" );
    list.setSelection( 2 );
    list.showSelection();
    list.setSelection( 0 );
    list.showSelection();
    assertEquals( 0, list.getTopIndex() );
  }

  @Test
  public void testHasScrollBar() {
    List list = new List( shell, SWT.NONE );
    list.setSize( 10, 10 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertFalse( list.hasVScrollBar() );
    assertFalse( list.hasHScrollBar() );

    list = new List( shell, SWT.V_SCROLL );
    list.setSize( 10, 10 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertTrue( list.hasVScrollBar() );
    assertFalse( list.hasHScrollBar() );

    list = new List( shell, SWT.H_SCROLL );
    list.setSize( 10, 10 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertFalse( list.hasVScrollBar() );
    assertTrue( list.hasHScrollBar() );

    list = new List( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    list.setSize( 200, 10 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertTrue( list.hasVScrollBar() );
    assertFalse( list.hasHScrollBar() );

    list = new List( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    list.setSize( 10, 200 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertFalse( list.hasVScrollBar() );
    assertTrue( list.hasHScrollBar() );

    list = new List( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    list.setSize( 200, 200 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertFalse( list.hasVScrollBar() );
    assertFalse( list.hasHScrollBar() );
  }

  @Test
  public void testGetScrollBarWidth() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 10, 10 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertTrue( list.getVScrollBarWidth() > 0 );
    assertTrue( list.getHScrollBarHeight() > 0 );
    List noScrollList = new List( shell, SWT.NONE );
    noScrollList.setSize( 200, 200 );
    assertEquals( 0, noScrollList.getVScrollBarWidth() );
    assertEquals( 0, noScrollList.getHScrollBarHeight() );
  }

  @Test
  public void testUpdateScrollBarOnItemsChange() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 200, 26 );
    assertFalse( list.hasVScrollBar() );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertTrue( list.hasVScrollBar() );
    list.removeAll();
    assertFalse( list.hasVScrollBar() );
    list.add( "Item 1" );
    list.add( "Item 2" );
    assertTrue( list.hasVScrollBar() );
    list.remove( 0 );
    assertFalse( list.hasVScrollBar() );
    list.add( "Item 0", 0 );
    assertTrue( list.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnResize() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 20, 20 );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    assertTrue( list.hasVScrollBar() );
    assertTrue( list.hasHScrollBar() );
    list.setSize( 100, 100 );
    assertFalse( list.hasVScrollBar() );
    assertFalse( list.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemWidthChange() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 60, 60 );
    list.add( "" );
    assertFalse( list.hasHScrollBar() );
    list.setItem( 0, "Very long long long long long long long long text" );
    assertTrue( list.hasHScrollBar() );
    list.setItem( 0, "" );
    assertFalse( list.hasHScrollBar() );
    // change font
    list.setItem( 0, "short" );
    assertFalse( list.hasHScrollBar() );
    Font bigFont = new Font( display, "Helvetica", 50, SWT.BOLD );
    list.setFont( bigFont );
    assertTrue( list.hasHScrollBar() );
    list.setFont( null );
    assertFalse( list.hasHScrollBar() );
    list.setFont( bigFont );
    assertTrue( list.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyHFirst() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.add( "" );
    list.setSize( 30, list.getItemHeight() + 4 );
    assertFalse( list.needsVScrollBar() );
    assertFalse( list.needsHScrollBar() );
    assertFalse( list.hasHScrollBar() );
    assertFalse( list.hasVScrollBar() );
    list.setItem( 0, "Item 1001" );
    assertTrue( list.hasHScrollBar() );
    assertTrue( list.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyVFirst() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.add( "123" );
    list.setSize( 50, 26 );
    assertFalse( list.hasHScrollBar() );
    assertFalse( list.hasVScrollBar() );
    for( int i = 0; i < 10; i++ ) {
      list.add( "123456789" );
    }
    assertTrue( list.hasHScrollBar() );
    assertTrue( list.hasVScrollBar() );
  }

  @Test
  public void testItemDimensions() {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    assertEquals( new Point( 0, 0 ), list.getItemDimensions() );
    list.add( "123" );
    assertEquals( new Point( 40, 26 ), list.getItemDimensions() );
    list.setSize( 100, 100 );
    assertEquals( new Point( 100, 26 ), list.getItemDimensions() );
    for( int i = 0; i < 10; i++ ) {
      list.add( "123" );
    }
    assertEquals( new Point( 90, 26 ), list.getItemDimensions() );
    list.add( "Very long list item" );
    assertEquals( new Point( 148, 26 ), list.getItemDimensions() );
  }

  @Test
  public void testLimitedItemDimensions() {
    List list = new List( shell, SWT.V_SCROLL );

    list.setSize( 100, 100 );
    list.add( "Very long list item" );

    assertEquals( new Point( list.getClientArea().width, 26 ), list.getItemDimensions() );
  }

  @Test
  public void testIsSerialized() throws Exception {
    String listItem = "listItem";
    List list = new List( shell, SWT.NONE  );
    list.add( listItem );

    List deserializedList = Fixture.serializeAndDeserialize( list );

    assertEquals( listItem, deserializedList.getItem( 0 ) );
  }

  @Test
  public void testIsSelected_IdenticalItems() {
    List list = new List( shell, SWT.MULTI );
    String[] items = { "text1", "text2", "text2" }; // two identical
    list.setItems( items );

    list.setSelection( items );

    assertTrue( list.isSelected( 0 ) );
    assertTrue( list.isSelected( 1 ) );
    assertTrue( list.isSelected( 2 ) );
  }

  @Test
  public void testIsSelected_IdenticalItems2() {
    List list = new List( shell, SWT.MULTI );
    String[] items = { "text1", "text2", "text2" }; // two identical
    list.setItems( items );

    list.setSelection( new String[] { "text1", "text2" } );

    assertTrue( list.isSelected( 0 ) );
    assertTrue( list.isSelected( 1 ) );
    assertTrue( list.isSelected( 2 ) );
  }

  @Test
  public void testSelection_DuplicateSelectionIndices() {
    List list = new List( shell, SWT.MULTI );
    String[] items = { "text1", "text2", "text2", "text3" };
    list.setItems( items );

    list.setSelection( new String[] { "text1", "text1", "text2" } );


    assertFalse( hasDuplicateIndices( list.getSelectionIndices() ) );
  }

  @Test
  public void testSetInvalidSelectionForMulti() {
    List list = new List( shell, SWT.MULTI );
    list.setItems( new String[] { "text1", "text2", "text2" } );

    list.setSelection( new String[] { "non-existing", "text2" } );

    assertEquals( 2, list.getSelectionCount() );
  }

  @Test
  public void testSetCustomItemHeight() {
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    assertEquals( 123, list.getItemHeight() );
  }

  @Test
  public void testGetCustomItemHeight() {
    Integer itemHeight = new Integer( 123 );
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.CUSTOM_ITEM_HEIGHT, itemHeight );

    Object returnedItemHeight = list.getData( RWT.CUSTOM_ITEM_HEIGHT );

    assertEquals( itemHeight, returnedItemHeight );
  }

  @Test
  public void testResetCustomItemHeight() {
    List list = new List( shell, SWT.NONE );
    int calculatedItemHeight = list.getItemHeight();
    list.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    list.setData( RWT.CUSTOM_ITEM_HEIGHT, null );
    assertEquals( calculatedItemHeight, list.getItemHeight() );
  }

  @Test
  public void testDefaultCustomItemHeight() {
    List list = new List( shell, SWT.NONE );
    assertEquals( 26, list.getItemHeight() );
  }

  @Test
  public void testSetCustomItemHeightWithNegativeValue() {
    List list = new List( shell, SWT.NONE );
    try {
      list.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( -1 ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetCustomItemHeightWithNonIntegerValue() {
    List list = new List( shell, SWT.NONE );
    try {
      list.setData( RWT.CUSTOM_ITEM_HEIGHT, new Object() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    try {
      list.setItems( new String[] { "invalid xhtml: <<&>>" } );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testMarkupTextWithMarkupEnabled() {
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    try {
      list.setItems( new String[] { "invalid xhtml: <<&>>" } );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    list.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      list.setItems( new String[] { "invalid xhtml: <<&>>" } );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    List list = new List( shell, SWT.NONE );
    list.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    list.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertTrue( list.markupEnabled );
  }

  @Test
  public void testAddSelectionListener() {
    List list = new List( shell, SWT.NONE );

    list.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( list.isListening( SWT.Selection ) );
    assertTrue( list.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    List list = new List( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    list.addSelectionListener( listener );

    list.removeSelectionListener( listener );

    assertFalse( list.isListening( SWT.Selection ) );
    assertFalse( list.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    List list = new List( shell, SWT.NONE );

    try {
      list.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    List list = new List( shell, SWT.NONE );

    try {
      list.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private boolean hasDuplicateIndices( int[] indices ) {
    boolean result = false;
    for( int i = 0; i < indices.length && !result; i++ ) {
      for( int j = i + 1; j < indices.length && !result; j++ ) {
        if( indices[ i ] == indices[ j ] ) {
          result = true;
        }
      }
    }
    return result;
  }
}
