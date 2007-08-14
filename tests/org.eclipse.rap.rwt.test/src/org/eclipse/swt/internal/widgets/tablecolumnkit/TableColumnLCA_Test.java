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

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.*;


public class TableColumnLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testResizeEvent() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    final TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 20 );
    column.addControlListener( new ControlListener() {
      public void controlMoved( final ControlEvent e ) {
        fail( "unexpected event: controlMoved" );
      }
      public void controlResized( final ControlEvent e ) {
        assertSame( column, e.getSource() );
        log.append( "controlResized" );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String columnId = WidgetUtil.getId( column );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    //
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    // Simulate request that changes column width
    RWTFixture.fakeUIThread();
    int newWidth = column.getWidth() + 2;
    RWTFixture.removeUIThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.controlResized", 
                              columnId );
    Fixture.fakeRequestParam( columnId + ".width", String.valueOf( newWidth ) );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( "controlResized", log.toString() );
    assertEquals( newWidth, column.getWidth() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertTrue( adapter.isInitialized() );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setWidth( " + newWidth + " )" ) != -1 );
    RWTFixture.removeUIThread();
  }
  
  public void testGetLeft() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setWidth( 10 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 10 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setWidth( 10 );
    
    // Test with natural column order
    assertEquals( 0, TableColumnLCA.getLeft( column0 ) );
    assertEquals( 10, TableColumnLCA.getLeft( column1 ) );
    assertEquals( 20, TableColumnLCA.getLeft( column2 ) );
    
    // Test with reverted column order
    table.setColumnOrder( new int[] { 2, 1, 0 } );
    assertEquals( 0, TableColumnLCA.getLeft( column2 ) );
    assertEquals( 10, TableColumnLCA.getLeft( column1 ) );
    assertEquals( 20, TableColumnLCA.getLeft( column0 ) );
  }
  
  public void testMoveColumn() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setText( "Col 0" );
    column0.setWidth( 10 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setText( "Col 1" );
    column1.setWidth( 20 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "Col 2" );
    column2.setWidth( 30 );
    
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60 (as created)
    // Move Col 1 over Col 0 (left half), thereafter order should be: 
    // Col 1, Col 0, Col 2
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column1, 3 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0 (right half), thereafter order should be: 
    // Col 0, Col 1, Col 2
    table.setColumnOrder( new int[] { 1, 0, 2 } );
    TableColumnLCA.moveColumn( column1, 27 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column2, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );

    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column2, 3 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );

    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column2, -30 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );

    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column0, 100 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );

    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 1 onto itself (left half), order should stay unchanged:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column1, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );

    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column0, 33 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }
}
