/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.swt.widgets.*;

public class TableColumnLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValus() {
    Table table = new Table( shell, SWT.BORDER );
    TableColumn column = new TableColumn( table, SWT.CENTER );
    Fixture.markInitialized( display );
    //text
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    column.setText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    //image
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    column.setImage( image );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, column.getToolTipText() );
    Fixture.clearPreserved();
    column.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", column.getToolTipText() );
    Fixture.clearPreserved();
    //alignment
    column.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Integer alignment
     = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    Fixture.clearPreserved();
    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.RIGHT, alignment.intValue() );
    Fixture.clearPreserved();
    column.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TableLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.CENTER, alignment.intValue() );
    Fixture.clearPreserved();
    //zindex,left,sortimage,resizable,moveable,selection_listeners,width
    Fixture.preserveWidgets();
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
    Fixture.clearPreserved();
    column.setMoveable( true );
    column.setResizable( false );
    column.setWidth( 30 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    column.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
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
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testResizeEvent() {
    final StringBuffer log = new StringBuffer();
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
    String columnId = WidgetUtil.getId( column );
    //
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread( );
    // Simulate request that changes column width
    int newWidth = column.getWidth() + 2;
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.controlResized", columnId );
    Fixture.fakeRequestParam( columnId + ".width", String.valueOf( newWidth ) );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "controlResized", log.toString() );
    assertEquals( newWidth, column.getWidth() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertTrue( adapter.isInitialized() );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setWidth( " + newWidth + " )" ) != -1 );
  }

  public void testGetLeft() {
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
    TableColumn column3 = new TableColumn( table, SWT.NONE );
    column3.setText( "Col 3" );
    column3.setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // (as created)
    // Move Col 3 between position 0 and position 1
    // Then move the same column between position 2 and 3
    // thereafter order should be:
    // Col 0, Col 1, Col 3, Col 2
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column3, 5 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 3, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    assertEquals( 2, columnOrder[ 3 ] );
    TableColumnLCA.moveColumn( column3, 55 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 3, columnOrder[ 2 ] );
    assertEquals( 2, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90 (as created)
    // Move Col 1 over Col 0 (left half), thereafter order should be:
    // Col 1, Col 0, Col 2, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column1, 3 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 1 over Col 0 (right half), thereafter order should be:
    // Col 0, Col 1, Col 2, Col 3
    table.setColumnOrder( new int[] { 1, 0, 2, 3 } );
    TableColumnLCA.moveColumn( column1, 27 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column2, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column2, 3 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column2, -30 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 3, Col 0
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column0, 100 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 3, columnOrder[ 2 ] );
    assertEquals( 0, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 1 onto itself, order should stay unchanged:
    // Col 0, Col 1, Col 2, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column1, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..90
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2, Col 3
    table.setColumnOrder( new int[] { 0, 1, 2, 3 } );
    TableColumnLCA.moveColumn( column0, 33 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  // see bug 336340
  public void testMoveColumn_ZeroWidth() {
    Fixture.markInitialized( display );
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( table );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( column0 );
    column0.setText( "Col 0" );
    column0.setWidth( 10 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( column1 );
    column1.setText( "Col 1" );
    column1.setWidth( 20 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( column2 );
    column2.setText( "Col 2" );
    column2.setWidth( 0 );
    TableColumn column3 = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( column3 );
    column3.setText( "Col 3" );
    column3.setWidth( 30 );
    Fixture.preserveWidgets();
    String column1Id = WidgetUtil.getId( column1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( column1Id + ".left", String.valueOf( 35 ) );
    Fixture.executeLifeCycleFromServerThread( );
    String markup = Fixture.getAllMarkup();
    String expected = "var w = wm.findWidgetById( \"" + column1Id + "\" );w.setLeft( 10 );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }
}
