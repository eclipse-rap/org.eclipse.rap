/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.listkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

public class ListLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ListLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ListLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    List list = new List( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( list );
    ControlLCATestUtil.testFocusListener( list );
    ControlLCATestUtil.testMouseListener( list );
    ControlLCATestUtil.testKeyListener( list );
    ControlLCATestUtil.testTraverseListener( list );
    ControlLCATestUtil.testMenuDetectListener( list );
    ControlLCATestUtil.testHelpListener( list );
  }

  public void testPreserveValues() {
    List list = new List( shell, SWT.SINGLE | SWT.BORDER );
    Fixture.markInitialized( display );
    // control: enabled
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    list.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    list.setEnabled( true );
    // visible
    list.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    list.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( list );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    list.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    list.getFocusIndex();
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    list.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    list.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    list.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    list.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( null, list.getToolTipText() );
    Fixture.clearPreserved();
    list.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( "some text", list.getToolTipText() );
  }

  public void testReadDataForSingle() {
    List list = new List( shell, SWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    // Fake request that selected a single item
    Fixture.fakeRequestParam( listId + ".selection", "0" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
    // Fake request that contains empty selection parameter -> must deselect all
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", "" );
    lca.readData( list );
    assertEquals( -1, list.getSelectionIndex() );
  }

  public void testReadDataForMulti() {
    List list = new List( shell, SWT.MULTI );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    // Fake request that selected 'item1' and 'item2'
    Fixture.fakeRequestParam( listId + ".selection", "0,1" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    int[] expected = new int[]{
      0, 1
    };
    assertTrue( Arrays.equals( expected, list.getSelectionIndices() ) );
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
  }

  public void testSelectionEvent() {
    final StringBuilder log = new StringBuilder();
    final List list = new List( shell, SWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( -1 );
    list.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( list, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    String listId = WidgetUtil.getId( list );
    Fixture.fakeRequestParam( listId + ".selection", "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, listId );
    Fixture.readDataAndProcessAction( list );
    assertEquals( "selectionEvent", log.toString() );
    assertEquals( 1, list.getSelectionIndex() );
  }

  public void testFocusedItem() {
    List list = new List( shell, SWT.NONE );
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    String listId = WidgetUtil.getId( list );
    // Test with focusIndex -1
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "-1" );
    Fixture.readDataAndProcessAction( list );
    assertEquals( -1, list.getFocusIndex() );
    // Test with value focusIndex
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "1" );
    Fixture.readDataAndProcessAction( list );
    assertEquals( 1, list.getFocusIndex() );
    // Test with focusIndex out of range
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "22" );
    Fixture.readDataAndProcessAction( list );
    assertEquals( 0, list.getFocusIndex() );
  }

  public void testReadTopIndex() {
    List list = new List( shell, SWT.MULTI );
    list.setSize( 100, 100 );
    for( int i = 0; i < 6; i++ ) {
      list.add( "Item " + i );
    }
    String listId = WidgetUtil.getId( list );
    Fixture.fakeRequestParam( listId + ".topIndex", "5" );
    ListLCA listLCA = new ListLCA();
    listLCA.readData( list );
    assertEquals( 5, list.getTopIndex() );
  }

  public void testRenderCreate() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertEquals( "rwt.widgets.List", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SINGLE" ) );
  }

  public void testRenderCreateWithMulti() throws IOException {
    List list = new List( shell, SWT.MULTI );

    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
  }

  public void testRenderParent() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertEquals( WidgetUtil.getId( list.getParent() ), operation.getParent() );
  }

  public void testRenderInitialItems() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  public void testRenderItems() throws IOException, JSONException {
    List list = new List( shell, SWT.NONE );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"Item 1\", \"Item 2\", \"Item 3\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderItemsUnchanged() throws IOException {
    List list = new List( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "items" ) );
  }

  public void testRenderInitialSelectionIndices() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndices" ) == -1 );
  }

  public void testRenderSelectionIndices() throws IOException, JSONException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.select( 1 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "selectionIndices" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[1]", actual ) );
  }

  public void testRenderSelectionIndicesWithMulti() throws IOException, JSONException {
    List list = new List( shell, SWT.MULTI );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.setSelection( new int[] { 1, 2 } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "selectionIndices" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[1,2]", actual ) );
  }

  public void testRenderSelectionIndicesUnchanged() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "selectionIndices" ) );
  }

  public void testRenderInitialTopIndex() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "topIndex" ) == -1 );
  }

  public void testRenderTopIndex() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.setTopIndex( 2 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( list, "topIndex" ) );
  }

  public void testRenderTopIndexUnchanged() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setTopIndex( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "topIndex" ) );
  }

  public void testRenderInitialFocusIndex() throws IOException {
    List list = new List( shell, SWT.NONE );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "focusIndex" ) == -1 );
  }

  public void testRenderFocusIndex() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    setFocusIndex( list, 2 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( list, "focusIndex" ) );
  }

  public void testRenderFocusIndexUnchanged() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    setFocusIndex( list, 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "focusIndex" ) );
  }

  public void testRenderInitialScrollBarsVisible() throws IOException, JSONException {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findCreateProperty( list, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Horizontal() throws IOException, JSONException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 20, 100 );

    list.add( "Item 1" );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ true, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Vertical() throws IOException, JSONException {
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 100, 20 );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, true ]", actual ) );
  }

  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    List list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    list.setSize( 20, 20 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "scrollBarsVisible" ) );
  }

  public void testRenderInitialItemDimensions() throws IOException {
    List list = new List( shell, SWT.NONE );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().contains( "itemDimensions" ) );
  }

  public void testRenderItemDimensions() throws IOException, JSONException {
    List list = new List( shell, SWT.NONE );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "itemDimensions" );
    assertEquals( list.getItemHeight(), actual.getInt( 1 ) );
  }

  public void testRenderItemDimensionsUnchanged() throws IOException {
    List list = new List( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "itemDimensions" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    List list = new List( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( list, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    List list = new List( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    list.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.removeSelectionListener( listener );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( list, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    List list = new List( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( list, "selection" ) );
  }

  private static void setFocusIndex( List list, int focusIndex ) {
    list.getAdapter( IListAdapter.class ).setFocusIndex( focusIndex );
  }
}
