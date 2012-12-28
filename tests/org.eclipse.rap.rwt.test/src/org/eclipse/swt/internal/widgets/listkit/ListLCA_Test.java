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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ListLCA_Test {

  private Display display;
  private Shell shell;
  private ListLCA lca;
  private List list;
  private ScrollBar hScroll;
  private ScrollBar vScroll;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    list = new List( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    hScroll = list.getHorizontalBar();
    vScroll = list.getVerticalBar();
    lca = new ListLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( list );
    ControlLCATestUtil.testFocusListener( list );
    ControlLCATestUtil.testMouseListener( list );
    ControlLCATestUtil.testKeyListener( list );
    ControlLCATestUtil.testTraverseListener( list );
    ControlLCATestUtil.testMenuDetectListener( list );
    ControlLCATestUtil.testHelpListener( list );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    // control: enabled
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( list );
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

  @Test
  public void testReadSelectionForSingle() {
    createListItems( 3 );

    fakeSelection( new int[]{ 0 } );
    lca.readData( list );

    assertEquals( 0, list.getSelectionIndex() );
  }

  @Test
  public void testReadSelectionForSingle_DeselectAll() {
    createListItems( 3 );
    list.setSelection( 1 );

    fakeSelection( new int[]{} );
    lca.readData( list );

    assertEquals( -1, list.getSelectionIndex() );
  }

  @Test
  public void testReadSelectionForMulti() {
    list = new List( shell, SWT.MULTI );
    createListItems( 3 );

    fakeSelection( new int[]{ 0, 1 } );
    lca.readData( list );

    int[] expected = new int[]{ 0, 1 };
    assertTrue( Arrays.equals( expected, list.getSelectionIndices() ) );
  }

  @Test
  public void testSelectionEvent() {
    createListItems( 2 );
    SelectionListener listener = mock( SelectionListener.class );
    list.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( list ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( list );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( list, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
  }

  @Test
  public void testReadFocusedIndex() {
    createListItems( 3 );
    setFocusIndex( list, 0 );

    fakeFocusIndex( 1 );
    Fixture.readDataAndProcessAction( list );


    assertEquals( 1, list.getFocusIndex() );
  }

  @Test
  public void testReadFocusedIndex_Reset() {
    createListItems( 3 );
    setFocusIndex( list, 0 );

    fakeFocusIndex( -1 );
    Fixture.readDataAndProcessAction( list );

    assertEquals( -1, list.getFocusIndex() );
  }

  @Test
  public void testReadFocusedIndex_OutOfRange() {
    createListItems( 3 );
    setFocusIndex( list, 0 );

    fakeFocusIndex(22 );
    Fixture.readDataAndProcessAction( list );

    assertEquals( 0, list.getFocusIndex() );
  }

  @Test
  public void testReadTopIndex() {
    list.setSize( 100, 100 );
    createListItems( 10 );

    fakeTopIndex( 5 );
    Fixture.readDataAndProcessAction( list );

    assertEquals( 5, list.getTopIndex() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertEquals( "rwt.widgets.List", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SINGLE" ) );
  }

  @Test
  public void testRenderCreateWithMulti() throws IOException {
    List list = new List( shell, SWT.MULTI );

    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertEquals( WidgetUtil.getId( list.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialItems() throws IOException {
    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  @Test
  public void testRenderItems() throws IOException, JSONException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"Item 1\", \"Item 2\", \"Item 3\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testRenderItemsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "items" ) );
  }

  @Test
  public void testRenderInitialSelectionIndices() throws IOException {
    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndices" ) == -1 );
  }

  @Test
  public void testRenderSelectionIndices() throws IOException, JSONException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.select( 1 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "selectionIndices" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[1]", actual ) );
  }

  @Test
  public void testRenderSelectionIndicesWithMulti() throws IOException, JSONException {
    List list = new List( shell, SWT.MULTI );
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.setSelection( new int[] { 1, 2 } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "selectionIndices" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[1,2]", actual ) );
  }

  @Test
  public void testRenderSelectionIndicesUnchanged() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "selectionIndices" ) );
  }

  @Test
  public void testRenderInitialTopIndex() throws IOException {
    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "topIndex" ) == -1 );
  }

  @Test
  public void testRenderTopIndex() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    list.setTopIndex( 2 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( list, "topIndex" ) );
  }

  @Test
  public void testRenderTopIndexUnchanged() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.markInitialized( hScroll );
    Fixture.markInitialized( vScroll );

    list.setTopIndex( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "topIndex" ) );
  }

  @Test
  public void testRenderInitialFocusIndex() throws IOException {
    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( list );
    assertTrue( operation.getPropertyNames().indexOf( "focusIndex" ) == -1 );
  }

  @Test
  public void testRenderFocusIndex() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    setFocusIndex( list, 2 );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( list, "focusIndex" ) );
  }

  @Test
  public void testRenderFocusIndexUnchanged() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    setFocusIndex( list, 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "focusIndex" ) );
  }

  @Test
  public void testRenderInitialScrollBarsVisible() throws IOException {
    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisible_Horizontal() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    list.setSize( 20, 100 );

    list.add( "Item 1" );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisible_Vertical() throws IOException {
    list.setSize( 100, 20 );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertEquals( Boolean.TRUE, message.findSetProperty( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    list.setSize( 20, 20 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.markInitialized( hScroll );
    Fixture.markInitialized( vScroll );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderInitialItemDimensions() throws IOException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( list, "itemDimensions" ) );
  }

  @Test
  public void testRenderItemDimensions() throws IOException, JSONException {
    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( list, "itemDimensions" );
    assertEquals( list.getItemHeight(), actual.getInt( 1 ) );
  }

  @Test
  public void testRenderItemDimensionsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );

    list.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( list, "itemDimensions" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( list, "Selection" ) );
    assertNull( message.findListenOperation( list, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    list.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.removeListener( SWT.Selection, listener );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( list, "Selection" ) );
    assertNull( message.findListenOperation( list, "DefaultSelection" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( list, "DefaultSelection" ) );
    assertNull( message.findListenOperation( list, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    list.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( list, "DefaultSelection" ) );
    assertNull( message.findListenOperation( list, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( list );
    Fixture.preserveWidgets();

    list.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( list );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( list, "Selection" ) );
    assertNull( message.findListenOperation( list, "DefaultSelection" ) );
  }

  @Test
  public void testRenderMarkupEnabled() throws IOException {
    list.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( list );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findCreateProperty( list, "markupEnabled" ) );
  }

  private static void setFocusIndex( List list, int focusIndex ) {
    list.getAdapter( IListAdapter.class ).setFocusIndex( focusIndex );
  }

  private void createListItems( int number ) {
    for( int i = 0; i < number; i++ ) {
      list.add( "Item " + i );
    }
  }

  private void fakeSelection( int[] values ) {
    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( list ), "selection", values );
  }

  private void fakeFocusIndex( int value ) {
    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( list ), "focusIndex", Integer.valueOf( value ) );
  }

  private void fakeTopIndex( int value ) {
    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( list ), "topIndex", Integer.valueOf( value ) );
  }
}
