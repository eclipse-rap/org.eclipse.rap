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

import java.util.ArrayList;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;
import com.w4t.engine.lifecycle.PhaseId;


public class TableColumn_Test extends TestCase {
  
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
    TableColumn col1 = new TableColumn( table, SWT.NONE );
    assertEquals( 1, table.getColumnCount() );
    assertSame( col1, table.getColumn( 0 ) );
    // Insert an item before first item
    TableColumn col0 = new TableColumn( table, SWT.NONE, 0 );
    assertEquals( 2, table.getColumnCount() );
    assertSame( col0, table.getColumn( 0 ) );
    // Try to add an item whit an index which is out of bounds
    try {
      new TableColumn ( table, SWT.NONE, table.getColumnCount() + 8 );
      String msg 
        = "Index out of bounds expected when creating a column with " 
        + "index > columnCount";
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
    TableColumn column = new TableColumn( table, SWT.NONE );
    assertSame( table, column.getParent() );
    // Test creating column without parent
    try {
      new TableColumn( null, SWT.NONE );
      fail( "Must not allow to create TableColumn withh null-parent." );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testDisplay() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    assertSame( display, column.getDisplay() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );

    TableColumn column = new TableColumn( table, SWT.NONE );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TableColumn( table, SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    assertTrue( ( column.getStyle() & SWT.LEFT ) != 0 );
    column = new TableColumn( table, SWT.RIGHT );
    assertTrue( ( column.getStyle() & SWT.RIGHT ) != 0 );
  }
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    
    assertEquals( 0, column.getWidth() );
    assertEquals( "", column.getText() );
    assertNull( column.getImage() );
  }
  
  public void testWidth() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    
    // Setting 'normal' width
    column.setWidth( 70 );
    assertEquals( 70, column.getWidth() );
    column.setWidth( 0 );
    assertEquals( 0, column.getWidth() );
    
    // Setting a negative width apart from -2 leads to some minimal column width
    column.setWidth( -1 );
    assertEquals( 6, column.getWidth() );
    column.setWidth( -3 );
    assertEquals( 6, column.getWidth() );

    // Settting width to -2 calculates the width from the current text
    column.setWidth( -2 );
    assertEquals( 12, column.getWidth() );
    column.setText( "column 1" );
    column.setWidth( -2 );
    Point expected = FontSizeEstimation.stringExtent( column.getText(), 
                                                      table.getFont() );
    assertEquals( expected.x, column.getWidth() );
  }
  
  public void testResizeEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    final TableColumn column = new TableColumn( table, SWT.NONE );
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
    assertEquals( 1, log.size() );
    event = ( ControlEvent )log.get( 0 );
    assertEquals( column, event.getSource() );
  }
}
