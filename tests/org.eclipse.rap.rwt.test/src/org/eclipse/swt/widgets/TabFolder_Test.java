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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TabFolder_Test {

  private Display display;
  private Shell shell;
  private TabFolder folder;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    folder = new TabFolder( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetItems() {
    assertEquals( 0, folder.getItems().length );

    TabItem item = new TabItem( folder, SWT.NONE );

    assertEquals( 1, folder.getItems().length );
    assertSame( item, folder.getItems()[ 0 ] );
  }

  @Test
  public void testInitialSelection() {
    final java.util.List<SelectionEvent> log = new ArrayList<SelectionEvent>();
    folder.setSize( 100, 100 );
    SelectionListener selectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        log.add( e );
      }
    };
    folder.addSelectionListener( selectionListener );

    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( 0, folder.getSelection().length );

    // add first item which must become selected and fire a SelectionEvent
    TabItem item = new TabItem( folder, SWT.NONE );
    assertEquals( 0, folder.getSelectionIndex() );
    assertEquals( 1, folder.getSelection().length );
    assertSame( item, folder.getSelection()[ 0 ] );
    assertEquals( 1, log.size() );
    SelectionEvent event = log.get( 0 );
    assertSame( folder, event.widget );
    assertSame( item, event.item );
    assertTrue( event.doit );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertNull( event.data );
    assertEquals( SWT.NONE, event.detail );
    assertNull( event.text );

    // ... and the same without a SelectionListener
    folder.removeSelectionListener( selectionListener );
    item.dispose();
    item = new TabItem( folder, SWT.NONE );
    assertEquals( 0, folder.getSelectionIndex() );
    assertEquals( 1, folder.getSelection().length );
    assertSame( item, folder.getSelection()[ 0 ] );
  }

  @Test
  public void testIndexOf() {
    TabItem item0 = new TabItem( folder, SWT.NONE );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    assertEquals( 0, folder.indexOf( item0 ) );
    assertEquals( 1, folder.indexOf( item1 ) );

    item0.dispose();
    assertEquals( 0, folder.indexOf( item1 ) );

    TabFolder anotherTabFolder = new TabFolder( shell, SWT.NONE );
    TabItem anotherItem = new TabItem( anotherTabFolder, SWT.NONE );
    assertEquals( -1, folder.indexOf( anotherItem ) );
  }

  @Test
  public void testSelection() {
    TabItem item0 = new TabItem( folder, SWT.NONE );
    TabItem item1 = new TabItem( folder, SWT.NONE );

    folder.setSelection( new TabItem[]{ item0 } );
    TabItem[] selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item0, selection[ 0 ] );
    assertEquals( 0, folder.getSelectionIndex() );

    folder.setSelection( new TabItem[]{ item1, item0 } );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    assertEquals( 1, folder.getSelectionIndex() );

    folder.setSelection( new TabItem[ 0 ] );
    selection = folder.getSelection();
    assertEquals( 0, selection.length );
    assertEquals( -1, folder.getSelectionIndex() );

    try {
      folder.setSelection( ( TabItem )null );
      fail( "No exception thrown for selection == null" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    try {
      folder.setSelection( ( TabItem[] )null );
      fail( "Parameter items must not be null." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    folder.setSelection( 1 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    assertEquals( 1, folder.getSelectionIndex() );

    folder.setSelection( 3 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    assertEquals( 1, folder.getSelectionIndex() );

    folder.setSelection( -2 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    assertEquals( 1, folder.getSelectionIndex() );

    folder.setSelection( -1 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    assertEquals( 1, folder.getSelectionIndex() );

    // Ensure that no event is fired when selection is changed programmatically
    final boolean[] eventOccured = new boolean[] { false };
    SelectionListener listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        eventOccured[ 0 ] = true;
      }
    };
    folder.setSelection( 0 );
    folder.addSelectionListener( listener );
    folder.setSelection( 1 );
    assertEquals( 1, folder.getSelectionIndex() );
    assertFalse( eventOccured[ 0 ] );
    folder.removeSelectionListener( listener );

    // test change of selection index in case of disposing the item thats
    // currently selected
    // TODO: [fappel] note that this is only a preliminarily implementation
    // since SWT behaves different in case that the selected
    // tab is disposed.
    folder.setSelection( 1 );
    item1.dispose();
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item0, folder.getSelection()[ 0 ] );
    item1 = new TabItem( folder, SWT.NONE );
    folder.setSelection( 0 );
    item1.dispose();
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item0, folder.getSelection()[ 0 ] );
  }

  @Test
  public void testDispose() {
    TabItem item = new TabItem( folder, SWT.NONE );
    folder.dispose();
    assertTrue( item.isDisposed() );
    assertEquals( 0, ItemHolder.getItemHolder( folder ).getItems().length );
  }

  @Test
  public void testGetItemAtPoint() {
    // Test with bar on top
    folder.setSize( 400, 400 );
    createItems( folder, 3 );
    Rectangle expected = new Rectangle( 0, 0, 74, 33 );
    assertEquals( expected, folder.getItem( 0 ).getBounds() );
    expected = new Rectangle( 74, 3, 73, 30 );
    assertEquals( expected, folder.getItem( 1 ).getBounds() );
    expected = new Rectangle( 148, 3, 74, 30 );
    assertEquals( expected, folder.getItem( 2 ).getBounds() );
    assertEquals( folder.getItem( 0 ), folder.getItem( new Point( 10, 2 ) ) );
    assertEquals( folder.getItem( 0 ), folder.getItem( new Point( 10, 10 ) ) );
    assertNull( folder.getItem( new Point( 95, 2 ) ) );
    assertEquals( folder.getItem( 1 ), folder.getItem( new Point( 95, 10 ) ) );
    assertNull( folder.getItem( new Point( 130, 2 ) ) );
    assertEquals( folder.getItem( 2 ), folder.getItem( new Point( 160, 10 ) ) );

    // Test with bar on bottom
    folder = new TabFolder( shell, SWT.BOTTOM );
    folder.setSize( 400, 400 );
    for( int i = 0; i < 3; i++ ) {
      TabItem tabItem = new TabItem( folder, SWT.NONE );
      tabItem.setText( "TabItem " + i );
    }
    expected = new Rectangle( 0, 367, 74, 33 );
    assertEquals( expected, folder.getItem( 0 ).getBounds() );
    expected = new Rectangle( 74, 367, 73, 30 );
    assertEquals( expected, folder.getItem( 1 ).getBounds() );
    expected = new Rectangle( 148, 367, 74, 30 );
    assertEquals( expected, folder.getItem( 2 ).getBounds() );
    assertEquals( folder.getItem( 0 ), folder.getItem( new Point( 10, 398 ) ) );
    assertEquals( folder.getItem( 0 ), folder.getItem( new Point( 10, 390 ) ) );
    assertNull( folder.getItem( new Point( 95, 398 ) ) );
    assertEquals( folder.getItem( 1 ), folder.getItem( new Point( 95, 390 ) ) );
    assertNull( folder.getItem( new Point( 130, 398 ) ) );
    assertEquals( folder.getItem( 2 ), folder.getItem( new Point( 160, 390 ) ) );

    assertNull( folder.getItem( new Point( 200, 200 ) ) );

    try {
      folder.getItem( null );
      fail( "Null argument" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testClientArea() {
    folder.setSize( 100, 100 );
    Rectangle expected = new Rectangle( 1, 31, 98, 68 );
    assertEquals( expected, folder.getClientArea() );

    folder = new TabFolder( shell, SWT.BORDER );
    folder.setSize( 100, 100 );
    expected = new Rectangle( 2, 32, 96, 66 );
    assertEquals( expected, folder.getClientArea() );

    folder = new TabFolder( shell, SWT.BOTTOM );
    folder.setSize( 100, 100 );
    expected = new Rectangle( 1, 1, 98, 68 );
    assertEquals( expected, folder.getClientArea() );

    folder = new TabFolder( shell, SWT.BOTTOM | SWT.BORDER );
    folder.setSize( 100, 100 );
    expected = new Rectangle( 2, 2, 96, 66 );
    assertEquals( expected, folder.getClientArea() );
  }

  @Test
  public void testComputeTrim() {
    Rectangle expected = new Rectangle( -1, -31, 2, 32 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder = new TabFolder( shell, SWT.BORDER );
    expected = new Rectangle( -2, -32, 4, 34 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder = new TabFolder( shell, SWT.BOTTOM );
    expected = new Rectangle( -1, -1, 2, 32 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder = new TabFolder( shell, SWT.BOTTOM | SWT.BORDER );
    expected = new Rectangle( -2, -2, 4, 34 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    createItems( folder, 2 );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    folder.setFont( font );
    folder.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    folder.dispose();
  }

  @Test
  public void testIsSerializable() throws Exception {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "item1" );

    TabFolder deserializedFolder = Fixture.serializeAndDeserialize( folder );

    assertEquals( folder.getItemCount(), deserializedFolder.getItemCount() );
    assertSame( deserializedFolder, deserializedFolder.getItem( 0 ).getParent() );
    assertEquals( item.getText(), deserializedFolder.getItem( 0 ).getText() );
  }

  @Test
  public void testAddSelectionListener() {
    TabFolder tabFolder = new TabFolder( shell, SWT.NONE );

    tabFolder.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( tabFolder.isListening( SWT.Selection ) );
    assertTrue( tabFolder.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    TabFolder tabFolder = new TabFolder( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    tabFolder.addSelectionListener( listener );

    tabFolder.removeSelectionListener( listener );

    assertFalse( tabFolder.isListening( SWT.Selection ) );
    assertFalse( tabFolder.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    TabFolder tabFolder = new TabFolder( shell, SWT.NONE );

    try {
      tabFolder.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    TabFolder tabFolder = new TabFolder( shell, SWT.NONE );

    try {
      tabFolder.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSelection_onFirstCreatedItem() {
    createItems( folder, 3 );

    assertEquals( 0, folder.getSelectionIndex() );
  }

  @Test
  public void testSelection_addItem() {
    createItems( folder, 3 );

    new TabItem( folder, SWT.NONE, 0 );

    assertEquals( 1, folder.getSelectionIndex() );
  }

  @Test
  public void testSelection_removeSelectedItem() {
    createItems( folder, 3 );
    folder.setSelection( 1 );

    folder.getItem( 1 ).dispose();

    assertEquals( 0, folder.getSelectionIndex() );
  }

  @Test
  public void testSelection_removeSelectedItemAtPositionZero() {
    createItems( folder, 3 );
    folder.setSelection( 0 );

    folder.getItem( 0 ).dispose();

    assertEquals( 0, folder.getSelectionIndex() );
  }

  @Test
  public void testSelection_removeItemBeforeSelected() {
    createItems( folder, 3 );
    folder.setSelection( 1 );

    folder.getItem( 0 ).dispose();

    assertEquals( 0, folder.getSelectionIndex() );
  }

  @Test
  public void testSelection_removeAllItems() {
    createItems( folder, 3 );

    for( int i = 0; i < 3; i++ ) {
      folder.getItem( 0 ).dispose();
    }

    assertEquals( -1, folder.getSelectionIndex() );
  }

  @Test
  public void testGetItemCount() {
    createItems( folder, 3 );

    assertEquals( 3, folder.getItemCount() );
  }

  @Test
  public void testGetItemCount_initial() {
    assertEquals( 0, folder.getItemCount() );
  }

  @Test
  public void testGetItemCount_afterItemDispose() {
    createItems( folder, 3 );

    folder.getItem( 0 ).dispose();

    assertEquals( 2, folder.getItemCount() );
  }

  private void createItems( TabFolder folder, int number ) {
    for( int i = 0; i < number; i++ ) {
      TabItem item = new TabItem( folder, SWT.NONE );
      item.setText( "TabItem " + i );
    }
  }

}
