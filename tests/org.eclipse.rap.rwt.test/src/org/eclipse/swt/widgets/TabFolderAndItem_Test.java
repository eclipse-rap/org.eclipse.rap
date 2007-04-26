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
import org.eclipse.swt.graphics.Image;

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
    Display display = new Display();
    Shell shell = new Shell( display );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( 0, folder.getSelection().length );
    
    TabItem item = new TabItem( folder, SWT.NONE );
    assertEquals( 0, folder.getSelectionIndex() );
    assertEquals( 1, folder.getSelection().length );
    assertSame( item, folder.getSelection()[ 0 ] );
  }
  
  public void testSelection() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    // indexOf:
    assertEquals( 0, folder.indexOf( item0 ) );
    assertEquals( 1, folder.indexOf( item1 ) );
    // end indexOf
    folder.setSelection( new TabItem[]{
      item0
    } );
    TabItem[] selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item0, selection[ 0 ] );
    int selectionIndex = folder.getSelectionIndex();
    assertEquals( 0, selectionIndex );
    folder.setSelection( new TabItem[]{
      item1, item0
    } );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( 1, selectionIndex );
    folder.setSelection( new TabItem[ 0 ] );
    selection = folder.getSelection();
    assertEquals( 0, selection.length );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( -1, selectionIndex );
    try {
      folder.setSelection( null );
      fail( "Parameter items must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    folder.setSelection( 1 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( 1, selectionIndex );
    folder.setSelection( 3 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( 1, selectionIndex );
    folder.setSelection( -2 );
    selection = folder.getSelection();
    assertEquals( 1, selection.length );
    assertSame( item1, selection[ 0 ] );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( 1, selectionIndex );
    folder.setSelection( -1 );
    selection = folder.getSelection();
    assertEquals( 0, selection.length );
    selectionIndex = folder.getSelectionIndex();
    assertEquals( -1, selectionIndex );
    
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
