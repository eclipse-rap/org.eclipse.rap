/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;


public class TreeColumn_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Tree tree;
  private List<Event> eventLog;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    eventLog = new ArrayList<Event>();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreation() {
    // Add one item
    TreeColumn col1 = new TreeColumn( tree, SWT.NONE );
    assertEquals( 1, tree.getColumnCount() );
    assertSame( col1, tree.getColumn( 0 ) );
    // Insert an item before first item
    TreeColumn col0 = new TreeColumn( tree, SWT.NONE, 0 );
    assertEquals( 2, tree.getColumnCount() );
    assertSame( col0, tree.getColumn( 0 ) );
    // Try to add an item whit an index which is out of bounds
    try {
      new TreeColumn( tree, SWT.NONE, tree.getColumnCount() + 8 );
      String msg = "Index out of bounds expected when creating a column with "
                   + "index > columnCount";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParent() {
    // Test creating column with valid parent
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertSame( tree, column.getParent() );
    // Test creating column without parent
    try {
      new TreeColumn( null, SWT.NONE );
      fail( "Must not allow to create TreeColumn withh null-parent." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testDisplay() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertSame( display, column.getDisplay() );
  }

  public void testStyle() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TreeColumn( tree, SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TreeColumn( tree, SWT.RIGHT );
    assertTrue( ( column.getStyle() & SWT.RIGHT ) != 0 );
  }

  public void testInitialValues() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertEquals( 0, column.getWidth() );
    assertEquals( "", column.getText() );
    assertEquals( null, column.getToolTipText() );
    assertNull( column.getImage() );
    assertEquals( true, column.getResizable() );
    assertEquals( false, column.getMoveable() );
    assertEquals( SWT.LEFT, column.getAlignment() );
  }

  public void testAlignment() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertEquals( SWT.LEFT, column.getAlignment() );
    column = new TreeColumn( tree, SWT.LEFT );
    assertEquals( SWT.LEFT, column.getAlignment() );
    column = new TreeColumn( tree, SWT.CENTER );
    assertEquals( SWT.CENTER, column.getAlignment() );
    column = new TreeColumn( tree, SWT.RIGHT );
    assertEquals( SWT.RIGHT, column.getAlignment() );
    column.setAlignment( SWT.LEFT );
    assertEquals( SWT.LEFT, column.getAlignment() );
    column.setAlignment( 4712 );
    assertEquals( SWT.LEFT, column.getAlignment() );
  }

  public void testWidth() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    // Initial value
    assertEquals( 0, column.getWidth() );
    // Setting 'normal' width
    column.setWidth( 70 );
    assertEquals( 70, column.getWidth() );
    column.setWidth( 0 );
    assertEquals( 0, column.getWidth() );
    // Setting a negative is ignored
    column.setWidth( 4711 );
    column.setWidth( -1 );
    assertEquals( 4711, column.getWidth() );
    column.setWidth( -2 );
    assertEquals( 4711, column.getWidth() );
    column.setWidth( -3 );
    assertEquals( 4711, column.getWidth() );
  }

  public void testPack() {
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.addListener( SWT.Resize, new LoggingListener() );
    // Ensure that controlResized is fired when pack changes the width
    column.setWidth( 12312 );
    eventLog.clear();
    column.pack();
    assertSame( eventLog.get( 0 ).widget, column );
    // Ensure that controlResized is *not* fired when pack doesn't change the
    // width
    eventLog.clear();
    column.pack();
    assertEquals( 0, eventLog.size() );
    // pack calculates a minimal width for an empty column
    column = new TreeColumn( tree, SWT.NONE );
    column.pack();
    assertTrue( column.getWidth() > 0 );
    // Test that an image on a column is taken into account
    column = new TreeColumn( tree, SWT.NONE );
    Image image = Graphics.getImage( "resources/images/test-50x100.png",
                              TreeColumn_Test.class.getClassLoader() );
    column.setImage( image );
    column.pack();
    assertTrue( column.getWidth() >= image.getBounds().width );
    // An item wider than the column itself strechtes the column
    column = new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setImage( image );
    column.pack();
    assertTrue( column.getWidth() >= item.getBounds().width );
    int widthNoSortIndicator = column.getWidth();
    tree.setSortColumn( column );
    tree.setSortDirection( SWT.UP );
    column.pack();
    assertTrue( column.getWidth() > widthNoSortIndicator);

  }

  public void testPackRespectSubItems() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "Item 0" );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    subitem.setText( "Subitem 0.0" );
    column.pack();
    int columnWidth = column.getWidth();
    item.setExpanded( true );

    column.pack();

    assertTrue( column.getWidth() > columnWidth );
  }

  public void testPackWithVirtual() {
    // Must not try to access items if there aren't any
    tree = new Tree( shell, SWT.VIRTUAL );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setWidth( 200 );
    column.addListener( SWT.Resize, new LoggingListener() );
    column.pack();
    assertEquals( 1, eventLog.size() ); // ensure that pack() did something
    // Ensure that pack does not resolve virtual items
    eventLog.clear();
    tree = new Tree( shell, SWT.VIRTUAL );
    column = new TreeColumn( tree, SWT.NONE );
    tree.setSize( 100, 50 );
    tree.setItemCount( 100 );
    tree.addListener( SWT.SetData, new LoggingListener() );
    column.pack();
    assertEquals( 0, eventLog.size() );
  }

  public void testResizeEvent() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.addListener( SWT.Resize, new LoggingListener() );
    // Changing column width leads to resize event
    column.setWidth( column.getWidth() + 1 );
    assertEquals( 1, eventLog.size() );
    Event event = eventLog.get( 0 );
    assertSame( column, event.widget );
    // Setting the column width to the same value it already has as well leads
    // to resize event
    eventLog.clear();
    column.setWidth( column.getWidth() );
    assertEquals( 0, eventLog.size() );
  }

  public void testDisposeLast() {
    TreeColumn column0 = new TreeColumn( tree, SWT.NONE );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "itemText for column 0" );
    item.setText( 1, "itemText for column 1" );
    column1.dispose();
    assertEquals( "", item.getText( 1 ) );
    assertEquals( "itemText for column 0", item.getText() );
    column0.dispose();
    assertEquals( 0, tree.getColumnCount() );
    assertEquals( 1, tree.getItemCount() );
    assertEquals( "itemText for column 0", item.getText() );
  }

  public void testSetResizable() {
    TreeColumn treeColumn = new TreeColumn( tree, SWT.NONE );
    assertTrue( ":a:", treeColumn.getResizable() == true );
    treeColumn.setResizable( false );
    assertTrue( ":b:", treeColumn.getResizable() == false );
    treeColumn.setResizable( false );
    assertTrue( ":c:", treeColumn.getResizable() == false );
    treeColumn.setResizable( true );
    assertTrue( ":d:", treeColumn.getResizable() == true );
  }

  public void testSetToolTip() {
    TreeColumn treeColumn = new TreeColumn( tree, SWT.NONE );
    String tooltip = "foobar";
    assertEquals( null, treeColumn.getToolTipText() );
    treeColumn.setToolTipText( tooltip );
    assertEquals( tooltip, treeColumn.getToolTipText() );
    treeColumn.setToolTipText( "" );
    assertEquals( "", treeColumn.getToolTipText() );
  }

  public void testFireMoveEventOnColumnResize() {
    TreeColumn column0 = new TreeColumn( tree, SWT.NONE );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.addListener( SWT.Move, new LoggingListener() );

    column0.setWidth( 100 );

    assertEquals( 1, eventLog.size() );
    Event event = eventLog.get( 0 );
    assertEquals( SWT.Move, event.type );
    assertSame( column1, event.widget );
  }

  public void testAddSelectionListener() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.addSelectionListener( mock( SelectionListener.class ) );
    
    assertTrue( column.isListening( SWT.Selection ) );
    assertTrue( column.isListening( SWT.DefaultSelection ) );
  }
  
  public void testRemoveSelectionListener() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    column.addSelectionListener( listener );

    column.removeSelectionListener( listener );
    
    assertFalse( column.isListening( SWT.Selection ) );
    assertFalse( column.isListening( SWT.DefaultSelection ) );
  }

  public void testAddSelectionListenerWithNullArgument() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    
    try {
      column.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemoveSelectionListenerWithNullArgument() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    
    try {
      column.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  //////////////////
  // Helping classes

  private class LoggingListener implements Listener {
    public void handleEvent( Event event ) {
      eventLog.add( event );
    }
  }

}
