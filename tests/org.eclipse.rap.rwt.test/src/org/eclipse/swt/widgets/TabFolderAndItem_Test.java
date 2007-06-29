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

import java.util.ArrayList;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;

import com.w4t.engine.lifecycle.PhaseId;

public class TabFolderAndItem_Test extends TestCase {

  public void testGetItemsAndGetItemCount() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    assertEquals( 0, folder.getItemCount() );
    assertEquals( 0, folder.getItems().length );
    TabItem item = new TabItem( folder, SWT.NONE );
    assertEquals( 1, folder.getItemCount() );
    assertEquals( 1, folder.getItems().length );
    assertSame( item, folder.getItems()[ 0 ] );
  }

  public void testInitialSelection() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        log.add( e );
      }
    } );
    
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( 0, folder.getSelection().length );
    
    // add first item which must become selected and fire a SelectionEvent
    TabItem item = new TabItem( folder, SWT.NONE );
    assertEquals( 0, folder.getSelectionIndex() );
    assertEquals( 1, folder.getSelection().length );
    assertSame( item, folder.getSelection()[ 0 ] );
    assertEquals( 1, log.size() );
    SelectionEvent event = ( SelectionEvent )log.get( 0 );
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
  }
  
  public void testIndexOf() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    
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
  
  public void testSelection() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
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
    } catch( NullPointerException e ) {
      // expected
    }
    
    try {
      folder.setSelection( ( TabItem[] )null );
      fail( "Parameter items must not be null." );
    } catch( final NullPointerException npe ) {
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
    assertEquals( 0, selection.length );
    assertEquals( -1, folder.getSelectionIndex() );
    
    // Ensure that no event is fired when selection is changed programmatically
    final boolean[] eventOccured = new boolean[] { false };
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        eventOccured[ 0 ] = true;
      }
    };
    folder.setSelection( 0 );
    folder.addSelectionListener( listener );
    folder.setSelection( 1 );
    assertEquals( 1, folder.getSelectionIndex() );
    assertEquals( false, eventOccured[ 0 ] );
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
  
  public void testImages() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    item0.setImage(Image.find( RWTFixture.IMAGE1 ) );
    assertSame( Image.find( RWTFixture.IMAGE1 ), item0.getImage() );
    assertEquals( 1, Image.size() );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    item1.setImage(Image.find( RWTFixture.IMAGE2 ) );
    assertSame( Image.find( RWTFixture.IMAGE2 ), item1.getImage() );
    assertEquals( 2, Image.size() );
  }

  public void testHierarchy() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
    Control control = new Label( folder, SWT.NONE );
    item.setControl( control );
    assertSame( control, item.getControl() );
    try {
      item.setControl( shell );
      fail( "Wrong parent." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    folder.dispose();
    assertEquals( true, item.isDisposed() );
    assertEquals( 0, folder.getItemCount() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
