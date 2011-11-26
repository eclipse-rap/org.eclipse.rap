/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treecolumnkit;

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
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class TreeColumnLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeColumnLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    lca = new TreeColumnLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    TreeColumn column = new TreeColumn( tree, SWT.CENTER );
    Fixture.markInitialized( display );
    // text
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    column.setText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    // image
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
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, column.getToolTipText() );
    Fixture.clearPreserved();
    column.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", column.getToolTipText() );
    Fixture.clearPreserved();
    // alignment
    column.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    String alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "left", alignment );
    Fixture.clearPreserved();
    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "right", alignment );
    Fixture.clearPreserved();
    column.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "center", alignment );
    Fixture.clearPreserved();
    // left,resizable,moveable,selection_listeners,width
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Object left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TreeColumnLCA.getLeft( column ) ), left );
    Object resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.TRUE, resizable );
    Object moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
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
    left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TreeColumnLCA.getLeft( column ) ), left );
    resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.FALSE, resizable );
    moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.TRUE, moveable );
    Object width = adapter.getPreserved( TreeColumnLCA.PROP_WIDTH );
    assertEquals( new Integer( 30 ), width );
  }

  public void testResizeEvent() {
    final StringBuffer log = new StringBuffer();
    final TreeColumn column = new TreeColumn( tree, SWT.NONE );
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
    Fixture.executeLifeCycleFromServerThread();
    // Simulate request that changes column width
    int newWidth = column.getWidth() + 2;
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.controlResized", columnId );
    Fixture.fakeRequestParam( columnId + ".width", String.valueOf( newWidth ) );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "controlResized", log.toString() );
    assertEquals( newWidth, column.getWidth() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertTrue( adapter.isInitialized() );
    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( newWidth ), message.findSetProperty( column, "width" ) );
  }

  public void testGetLeft() {
    TreeColumn column0 = new TreeColumn( tree, SWT.NONE );
    column0.setWidth( 10 );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setWidth( 10 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setWidth( 10 );
    // Test with natural column order
    assertEquals( 0, TreeColumnLCA.getLeft( column0 ) );
    assertEquals( 10, TreeColumnLCA.getLeft( column1 ) );
    assertEquals( 20, TreeColumnLCA.getLeft( column2 ) );
    // Test with reverted column order
    tree.setColumnOrder( new int[]{
      2, 1, 0
    } );
    assertEquals( 0, TreeColumnLCA.getLeft( column2 ) );
    assertEquals( 10, TreeColumnLCA.getLeft( column1 ) );
    assertEquals( 20, TreeColumnLCA.getLeft( column0 ) );
  }

  public void testMoveColumn() {
    TreeColumn column0 = new TreeColumn( tree, SWT.NONE );
    column0.setText( "Col 0" );
    column0.setWidth( 10 );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "Col 1" );
    column1.setWidth( 20 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "Col 2" );
    column2.setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60 (as created)
    // Move Col 1 over Col 0 (left half), thereafter order should be:
    // Col 1, Col 0, Col 2
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column1, 3 );
    int[] columnOrder = tree.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0 (right half), thereafter order should be:
    // Col 0, Col 1, Col 2
    tree.setColumnOrder( new int[]{
      1, 0, 2
    } );
    TreeColumnLCA.moveColumn( column1, 27 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column2, 13 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column2, 3 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column2, -30 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 0
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column0, 100 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 1 onto itself (left half), order should stay unchanged:
    // Col 1, Col 2, Col 0
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column1, 13 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2
    tree.setColumnOrder( new int[]{
      0, 1, 2
    } );
    TreeColumnLCA.moveColumn( column0, 33 );
    columnOrder = tree.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  public void testRenderCreate() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.TableColumn", operation.getType() );
  }

  public void testRenderCreateWithAligment() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.RIGHT );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.TableColumn", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "RIGHT" ) );
  }

  public void testRenderParent() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.renderInitialization( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( WidgetUtil.getId( column.getParent() ), operation.getParent() );
  }

  public void testRenderInitialIndex() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "index" ) == -1 );
  }

  public void testRenderIndex() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    new TreeColumn( tree, SWT.NONE, 0 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( column, "index" ) );
  }

  public void testRenderIndexUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    new TreeColumn( tree, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "index" ) );
  }

  public void testRenderInitialToolTip() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "toolTip" ) == -1 );
  }

  public void testRenderToolTip() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setToolTipText( "foo" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "toolTip" ) );
  }

  public void testRenderToolTipUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "toolTip" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( column, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "customVariant" ) );
  }

  public void testRenderInitialText() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setText( "foo" );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
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
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
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
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
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
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "left" ) == -1 );
  }

  public void testRenderLeft() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    TreeColumn col2 = new TreeColumn( tree, SWT.NONE, 0 );
    col2.setWidth( 50 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 50 ), message.findSetProperty( column, "left" ) );
  }

  public void testRenderLeftUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    TreeColumn col2 = new TreeColumn( tree, SWT.NONE, 0 );
    col2.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "left" ) );
  }

  public void testRenderInitialWidth() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "width" ) == -1 );
  }

  public void testRenderWidth() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setWidth( 50 );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 50 ), message.findSetProperty( column, "width" ) );
  }

  public void testRenderWidthUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "width" ) );
  }

  public void testRenderInitialResizable() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "resizable" ) == -1 );
  }

  public void testRenderResizable() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setResizable( false );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( column, "resizable" ) );
  }

  public void testRenderResizableUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setResizable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "resizable" ) );
  }

  public void testRenderInitialMoveable() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "moveable" ) == -1 );
  }

  public void testRenderMoveable() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setMoveable( true );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( column, "moveable" ) );
  }

  public void testRenderMoveableUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setMoveable( true );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "moveable" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    lca.render( column );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getPropertyNames().indexOf( "alignment" ) == -1 );
  }

  public void testRenderAlignment() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setAlignment( SWT.RIGHT );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( column, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "alignment" ) );
  }


  public void testRenderAddSelectionListener() throws Exception {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( column, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
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
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( column, "selection" ) );
  }
}
