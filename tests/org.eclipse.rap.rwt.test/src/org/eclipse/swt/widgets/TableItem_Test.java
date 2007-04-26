/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.Rectangle;


public class TableItem_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    // Add one item
    TableItem item1 = new TableItem( table, SWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertSame( item1, table.getItem( 0 ) );
    // Insert an item before first item
    TableItem item0 = new TableItem( table, SWT.NONE, 0 );
    assertEquals( 2, table.getItemCount() );
    assertSame( item0, table.getItem( 0 ) );
    // Try to add an item whit an index which is out of bounds
    try {
      new TableItem( table, SWT.NONE, table.getItemCount() + 8 );
      String msg 
        = "Index out of bounds expected when creating an item with " 
        + "index > itemCount";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParent() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    // Test creating column with valid parent
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    assertSame( table, item.getParent() );
    // Test creating column without parent
    try {
      new TableItem( null, SWT.NONE );
      fail( "Must not allow to create TableColumn withh null-parent." );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testBounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 11 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setWidth( 22 );
    
    // simple case: bounds for first and only item
    TableItem item = new TableItem( table, SWT.NONE );
    Rectangle bounds = item.getBounds();
    assertEquals( 0, bounds.x );
    assertEquals( 0, bounds.y );
    assertTrue( bounds.height > 0 );
    assertEquals( column1.getWidth(), bounds.width );
    
    // bounds for item in second column
    item.setText( 1, "abc" );
    bounds = item.getBounds( 1 );
    assertTrue( bounds.x >= column1.getWidth() );
    assertEquals( 0, bounds.y );
    assertTrue( bounds.height > 0 );
    assertEquals( column2.getWidth(), bounds.width );
  }
  
  public void testInvalidBounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "col1" );
    item.setText( 1, "col2" );
    
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getBounds( 1 ) );
  }
}
