/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class TableColumnLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Table table;
  private TableColumnLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    table = new Table( shell, SWT.NONE );
    lca = new TableColumnLCA();
    Fixture.fakeNewRequest( display );
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
    // left,sortimage,resizable,moveable,selection_listeners,width
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Object left = adapter.getPreserved( TableColumnLCA.PROP_LEFT );
    assertEquals( new Integer( getColumnLeft( column ) ), left );
    Object resizable = adapter.getPreserved( TableColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.TRUE, resizable );
    Object moveable = adapter.getPreserved( TableColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.FALSE, moveable );
    Fixture.clearPreserved();
    column.setMoveable( true );
    column.setResizable( false );
    column.setWidth( 30 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    column.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    left = adapter.getPreserved( TableColumnLCA.PROP_LEFT );
    assertEquals( new Integer( getColumnLeft( column ) ), left );
    resizable = adapter.getPreserved( TableColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.FALSE, resizable );
    moveable = adapter.getPreserved( TableColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.TRUE, moveable );
    Object width = adapter.getPreserved( TableColumnLCA.PROP_WIDTH );
    assertEquals( new Integer( 30 ), width );
  }

  public void testResizeEvent() {
    final StringBuilder log = new StringBuilder();
    Table table = new Table( shell, SWT.NONE );
    final TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 20 );
    column.addControlListener( new ControlListener() {
      public void controlMoved( ControlEvent e ) {
        fail( "unexpected event: controlMoved" );
      }

      public void controlResized( ControlEvent e ) {
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
    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( newWidth ), message.findSetProperty( column, "width" ) );
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
    assertEquals( 0, getColumnLeft( column0 ) );
    assertEquals( 10, getColumnLeft( column1 ) );
    assertEquals( 20, getColumnLeft( column2 ) );
    // Test with reverted column order
    table.setColumnOrder( new int[]{
      2, 1, 0
    } );
    assertEquals( 0, getColumnLeft( column2 ) );
    assertEquals( 10, getColumnLeft( column1 ) );
    assertEquals( 20, getColumnLeft( column0 ) );
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
    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( column1, "left" ) );
  }

  public void testMoveColumnFixedColumnTarget() {
    Table table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 80 );
    TableColumn column3 = table.getColumn( 3 );
    TableColumnLCA.moveColumn( column3, 105 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  public void testMoveColumnFixedColumnSource() {
    Table table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    TableColumn column0 = table.getColumn( 0 );
    TableColumnLCA.moveColumn( column0, 105 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  public void testMoveColumnFixedColumnRightHalfTarget() {
    Table table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 100 );
    TableColumn column3 = table.getColumn( 3 );
    TableColumnLCA.moveColumn( column3, 145 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  public void testRenderCreate() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.TableColumn", operation.getType() );
  }

  public void testRenderCreateWithAligment() throws IOException {
    TableColumn column = new TableColumn( table, SWT.RIGHT );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.TableColumn", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "RIGHT" ) );
  }

  public void testRenderParent() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( WidgetUtil.getId( column.getParent() ), operation.getParent() );
  }

  public void testRenderInitialIndex() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "index" ) == -1 );
  }

  public void testRenderIndex() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    new TableColumn( table, SWT.NONE, 0 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( column, "index" ) );
  }

  public void testRenderIndexUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    new TableColumn( table, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "index" ) );
  }

  public void testRenderInitialToolTip() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "toolTip" ) == -1 );
  }

  public void testRenderToolTip() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setToolTipText( "foo" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "toolTip" ) );
  }

  public void testRenderToolTipUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "toolTip" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( column, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "customVariant" ) );
  }

  public void testRenderInitialText() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setText( "foo" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    column.setImage( image );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( column, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    column.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    column.setImage( image );

    Fixture.preserveWidgets();
    column.setImage( null );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( column, "image" ) );
  }

  public void testRenderInitialLeft() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "left" ) == -1 );
  }

  public void testRenderLeft() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    TableColumn col2 = new TableColumn( table, SWT.NONE, 0 );
    col2.setWidth( 50 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 50 ), message.findSetProperty( column, "left" ) );
  }

  public void testRenderLeftUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    TableColumn col2 = new TableColumn( table, SWT.NONE, 0 );
    col2.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "left" ) );
  }

  public void testRenderInitialWidth() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "width" ) == -1 );
  }

  public void testRenderWidth() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setWidth( 50 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 50 ), message.findSetProperty( column, "width" ) );
  }

  public void testRenderWidthUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "width" ) );
  }

  public void testRenderInitialResizable() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "resizable" ) == -1 );
  }

  public void testRenderResizable() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setResizable( false );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( column, "resizable" ) );
  }

  public void testRenderResizableUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setResizable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "resizable" ) );
  }

  public void testRenderInitialMoveable() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "moveable" ) == -1 );
  }

  public void testRenderMoveable() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setMoveable( true );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( column, "moveable" ) );
  }

  public void testRenderMoveableUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setMoveable( true );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "moveable" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "alignment" ) == -1 );
  }

  public void testRenderAlignment() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setAlignment( SWT.RIGHT );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( column, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "alignment" ) );
  }

  public void testRenderInitialFixed() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "fixed" ) == -1 );
  }

  public void testRenderFixed() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setData( "fixedColumns", Integer.valueOf( 1 ) );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( column, "fixed" ) );
  }

  public void testRenderFixedUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    table.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "fixed" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( column, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    TableColumn column = new TableColumn( table, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    column.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.removeSelectionListener( listener );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( column, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( column, "selection" ) );
  }

  private static int getColumnLeft( TableColumn column ) {
    return column.getParent().getAdapter( ITableAdapter.class ).getColumnLeft( column );
  }

  private Table createFixedColumnsTable( Shell shell ) {
    Table table = new Table( shell, SWT.NONE );
    table.setData( "fixedColumns", new Integer( 1 ) );
    for( int i = 0; i < 10; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( 50 );
      column.setText( "Column " + i );
    }
    return table;
  }

}