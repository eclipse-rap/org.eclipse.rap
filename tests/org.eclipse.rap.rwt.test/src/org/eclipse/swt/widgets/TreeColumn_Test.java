/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;


public class TreeColumn_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    // Test creating column with valid parent
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertSame( tree, column.getParent() );
    // Test creating column without parent
    try {
      new TreeColumn( null, SWT.NONE );
      fail( "Must not allow to create TreeColumn withh null-parent." );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testDisplay() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertSame( display, column.getDisplay() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TreeColumn( tree, SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TreeColumn( tree, SWT.RIGHT );
    assertTrue( ( column.getStyle() & SWT.RIGHT ) != 0 );
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn column;
    column = new TreeColumn( tree, SWT.NONE );
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
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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

//  public void testPack() {
//    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
//    final java.util.List log = new ArrayList();
//    ControlAdapter resizeListener = new ControlAdapter() {
//
//      public void controlResized( ControlEvent e ) {
//        log.add( e.widget );
//      }
//    };
//    Display display = new Display();
//    Shell shell = new Shell( display );
//    Tree tree = new Tree( shell, SWT.NONE );
//    TreeColumn column = new TreeColumn( tree, SWT.NONE );
//    column.addControlListener( resizeListener );
//    // Ensure that controlResized is fired when pack changes the width
//    column.setWidth( 12312 );
//    log.clear();
//    column.pack();
//    assertSame( log.get( 0 ), column );
//    // Ensure that controlResized is *not* fired when pack doesn't change the
//    // width
//    log.clear();
//    column.pack();
//    assertEquals( 0, log.size() );
//    column.removeControlListener( resizeListener );
//    // pack calculates a minimal width for an empty column
//    column = new TreeColumn( tree, SWT.NONE );
//    column.pack();
//    assertTrue( column.getWidth() > 0 );
//    // Test that an image on a column is taken into account
//    column = new TreeColumn( tree, SWT.NONE );
//    Image image = Image.find( "resources/images/test-50x100.png",
//                              TreeColumn_Test.class.getClassLoader() );
//    column.setImage( image );
//    column.pack();
//    assertTrue( column.getWidth() >= image.getBounds().width );
//    // An item wider than the column itself strechtes the column
//    column = new TreeColumn( tree, SWT.NONE );
//    TreeItem item = new TreeItem( tree, SWT.NONE );
//    item.setImage( image );
//    column.pack();
//    assertTrue( column.getWidth() >= item.getBounds().width );
//  }

// public void testPackWithVirtual() {
// RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
// final java.util.List log = new ArrayList();
// Listener setDataListener = new Listener() {
//
// public void handleEvent( final Event event ) {
// log.add( event.item );
// }
// };
// ControlListener resizeListener = new ControlAdapter() {
//
// public void controlResized( final ControlEvent event ) {
// log.add( event.widget );
// }
// };
// Display display = new Display();
// Shell shell = new Shell( display );
// Tree tree;
// TreeColumn column;
// // Must not try to access items if there aren't any
// log.clear();
// Tree = new Tree( shell, SWT.VIRTUAL );
// column = new TreeColumn( tree, SWT.NONE );
// column.setWidth( 200 );
// column.addControlListener( resizeListener );
// column.pack();
// assertEquals( 1, log.size() ); // ensure that pack() did something
// // Ensure that pack does not resolve virtual items
// log.clear();
// Tree = new Tree( shell, SWT.VIRTUAL );
// column = new TreeColumn( tree, SWT.NONE );
// Tree.setSize( 100, 50 );
// Tree.setItemCount( 100 );
// Tree.addListener( SWT.SetData, setDataListener );
// column.pack();
// assertEquals( 0, log.size() );
// }
  public void testResizeEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    final TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent event ) {
        fail( "unexpected event: controlMoved" );
      }

      public void controlResized( final ControlEvent event ) {
        log.add( event );
      }
    } );
    ControlEvent event;
    // Changing column width leads to resize event
    log.clear();
    column.setWidth( column.getWidth() + 1 );
    assertEquals( 1, log.size() );
    event = ( ControlEvent )log.get( 0 );
    assertSame( column, event.getSource() );
    // Setting the column width to the same value it already has as well leads
    // to resize event
    log.clear();
    column.setWidth( column.getWidth() );
    assertEquals( 0, log.size() );
  }

  public void testDisposeLast() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn treeColumn = new TreeColumn( tree, SWT.NONE );
    String tooltip = "foobar";
    assertEquals( null, treeColumn.getToolTipText() );
    treeColumn.setToolTipText( tooltip );
    assertEquals( tooltip, treeColumn.getToolTipText() );
    treeColumn.setToolTipText( "" );
    assertEquals( "", treeColumn.getToolTipText() );
  }
}
