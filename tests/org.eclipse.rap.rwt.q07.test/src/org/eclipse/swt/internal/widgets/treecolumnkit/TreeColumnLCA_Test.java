/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treecolumnkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.treeitemkit.TreeItemLCA;
import org.eclipse.swt.widgets.*;

public class TreeColumnLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
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
    Integer alignment = ( Integer )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    Fixture.clearPreserved();
    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( SWT.RIGHT, alignment.intValue() );
    Fixture.clearPreserved();
    column.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( Integer )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( SWT.CENTER, alignment.intValue() );
    Fixture.clearPreserved();
    // zindex,left,sortimage,resizable,moveable,selection_listeners,width
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Object zindex = adapter.getPreserved( TreeColumnLCA.PROP_Z_INDEX );
    assertEquals( new Integer( TreeColumnLCA.getZIndex( column ) ), zindex );
    Object left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TreeColumnLCA.getLeft( column ) ), left );
    Object sortDir = adapter.getPreserved( TreeColumnLCA.PROP_SORT_DIRECTION );
    assertEquals( TreeColumnLCA.getSortDirection( column ), sortDir );
    Object resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.TRUE, resizable );
    Object moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.FALSE, moveable );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
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
    zindex = adapter.getPreserved( TreeColumnLCA.PROP_Z_INDEX );
    assertEquals( new Integer( TreeColumnLCA.getZIndex( column ) ), zindex );
    left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( TreeColumnLCA.getLeft( column ) ), left );
    sortDir = adapter.getPreserved( TreeColumnLCA.PROP_SORT_DIRECTION );
    assertEquals( TreeColumnLCA.getSortDirection( column ), sortDir );
    resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.FALSE, resizable );
    moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.TRUE, moveable );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Object width = adapter.getPreserved( TreeColumnLCA.PROP_WIDTH );
    assertEquals( new Integer( 30 ), width );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testResizeEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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
    String displayId = DisplayUtil.getId( display );
    String columnId = WidgetUtil.getId( column );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    //
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    // Simulate request that changes column width
    int newWidth = column.getWidth() + 2;
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.controlResized", columnId );
    Fixture.fakeRequestParam( columnId + ".width", String.valueOf( newWidth ) );
    Fixture.executeLifeCycleFromServerThread();
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
    Tree tree = new Tree( shell, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display );
    Tree tree = new Tree( shell, SWT.NONE );
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

  public void testRenderAlignment() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( column );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeColumnLCA lca = new TreeColumnLCA();
    column.setAlignment(  SWT.RIGHT );
    lca.renderChanges( column );
    String expected = "w.setAlignment( 1, qx.constant.Layout.ALIGN_RIGHT )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

}
