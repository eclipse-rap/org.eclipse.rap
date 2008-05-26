/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.swt.widgets.*;

public class TableColumnLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testPreserveValus() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Table table = new Table( shell, SWT.BORDER );
    TableColumn column = new TableColumn( table, SWT.CENTER );
    RWTFixture.markInitialized( display );
    //text
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    column.setText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    //image
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    RWTFixture.clearPreserved();
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    column.setImage( image );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, column.getToolTipText() );
    RWTFixture.clearPreserved();
    column.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", column.getToolTipText() );
    RWTFixture.clearPreserved();
    //alignment
    column.setAlignment( SWT.LEFT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Integer alignment
     = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    RWTFixture.clearPreserved();
    column.setAlignment( SWT.RIGHT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.RIGHT, alignment.intValue() );
    RWTFixture.clearPreserved();
    column.setAlignment( SWT.CENTER );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.CENTER, alignment.intValue() );
    RWTFixture.clearPreserved();
    //zindex,left,sortimage,resizable,moveable,selection_listeners,width
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Object zIndex = adapter.getPreserved( TableColumnLCA.PROP_Z_INDEX );
    assertEquals( new Integer( TableColumnLCA.getZIndex( column ) ), zIndex );
    Object left = adapter.getPreserved( TableColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TableColumnLCA.getLeft( column ) ), left );
    Object sortDir = adapter.getPreserved( TableColumnLCA.PROP_SORT_DIRECTION );
    assertEquals( TableColumnLCA.getSortDirection( column ), sortDir );
    Object resizable = adapter.getPreserved( TableColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.TRUE, resizable );
    Object moveable = adapter.getPreserved( TableColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.FALSE, moveable );
    Boolean hasListeners
     = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    column.setMoveable( true );
    column.setResizable( false );
    column.setWidth( 30 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    column.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    zIndex = adapter.getPreserved( TableColumnLCA.PROP_Z_INDEX );
    assertEquals( new Integer( TableColumnLCA.getZIndex( column ) ), zIndex );
    left = adapter.getPreserved( TableColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TableColumnLCA.getLeft( column ) ), left );
    sortDir = adapter.getPreserved( TableColumnLCA.PROP_SORT_DIRECTION );
    assertEquals( TableColumnLCA.getSortDirection( column ), sortDir );
    resizable = adapter.getPreserved( TableColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.FALSE, resizable );
    moveable = adapter.getPreserved( TableColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.TRUE, moveable );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Object width = adapter.getPreserved( TableColumnLCA.PROP_WIDTH );
    assertEquals( new Integer( 30 ), width );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testResizeEvent() {
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
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    //
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread( );
    // Simulate request that changes column width
    int newWidth = column.getWidth() + 2;
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.controlResized", columnId );
    Fixture.fakeRequestParam( columnId + ".width", String.valueOf( newWidth ) );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "controlResized", log.toString() );
    assertEquals( newWidth, column.getWidth() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertTrue( adapter.isInitialized() );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setWidth( " + newWidth + " )" ) != -1 );
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
    table.setColumnOrder( new int[]{
      2, 1, 0
    } );
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
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column1, 3 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0 (right half), thereafter order should be:
    // Col 0, Col 1, Col 2
    table.setColumnOrder( new int[]{
      1, 0, 2
    } );
    TableColumnLCA.moveColumn( column1, 27 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column2, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column2, 3 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column2, -30 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column0, 100 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 1 onto itself (left half), order should stay unchanged:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column1, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TableColumnLCA.moveColumn( column0, 33 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }
}
