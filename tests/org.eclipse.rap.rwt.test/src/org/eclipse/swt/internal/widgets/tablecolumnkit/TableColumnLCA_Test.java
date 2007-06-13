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
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.internal.lifecycle.PreserveWidgetsPhaseListener;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


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
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    assertEquals( 0, TableColumnLCA.getLeft( column0 ) );
    assertEquals( column1.getWidth(), TableColumnLCA.getLeft( column1 ) );
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
    // Move Col 1 over Col 0, thereafter order should be: Col 0, Col 1, Col 2
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    TableColumnLCA.moveColumn( column0, 27 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0, thereafter order should be: Col 0, Col 1, Col 2
    table.setColumnOrder( new int[] { 1, 0, 2 } );
    TableColumnLCA.moveColumn( column1, 27 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }
}
