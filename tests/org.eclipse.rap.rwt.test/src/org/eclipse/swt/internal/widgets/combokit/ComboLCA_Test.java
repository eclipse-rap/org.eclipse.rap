/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.combokit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ComboLCA_Test {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private Display display;
  private Shell shell;
  private Combo combo;
  private ComboLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    combo = new Combo( shell, SWT.NONE );
    lca = new ComboLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( combo );
    ControlLCATestUtil.testFocusListener( combo );
    ControlLCATestUtil.testMouseListener( combo );
    ControlLCATestUtil.testKeyListener( combo );
    ControlLCATestUtil.testTraverseListener( combo );
    ControlLCATestUtil.testMenuDetectListener( combo );
    ControlLCATestUtil.testHelpListener( combo );
  }

  @Test
  public void testPreserveValues() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    Fixture.markInitialized( display );
    // Test preserving a combo with no items and (naturally) no selection
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION_INDEX ) );
    assertNull( adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT ) );
    Object visibleItemCount = adapter.getPreserved( ComboLCA.PROP_VISIBLE_ITEM_COUNT );
    assertEquals( new Integer( combo.getVisibleItemCount() ), visibleItemCount );
    assertEquals( Boolean.FALSE, adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    assertEquals( new Point( 0, 0 ), adapter.getPreserved( ComboLCA.PROP_SELECTION ) );
    // Test preserving combo with items were one is selected
    Fixture.clearPreserved();
    combo.add( "item 1" );
    combo.add( "item 2" );
    combo.select( 1 );
    combo.setListVisible( true );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    combo.addSelectionListener( selectionListener );
    combo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 2, items.length );
    assertEquals( "item 1", items[ 0 ] );
    assertEquals( "item 2", items[ 1 ] );
    assertEquals( new Integer( 1 ), adapter.getPreserved( PROP_SELECTION_INDEX ) );
    visibleItemCount = adapter.getPreserved( ComboLCA.PROP_VISIBLE_ITEM_COUNT );
    assertEquals( new Integer( combo.getVisibleItemCount() ), visibleItemCount );
    assertEquals( "item 2", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    combo.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    combo.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    combo.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( null, combo.getToolTipText() );
    Fixture.clearPreserved();
    combo.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( "some text", combo.getToolTipText() );
    Fixture.clearPreserved();
  }

  @Test
  public void testEditablePreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    Fixture.clearPreserved();
    // textLimit
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    Integer textLimit = ( Integer )adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( 10 ), textLimit );
  }

  @Test
  public void testReadData_ListVisible() {
    combo.add( "item 1" );
    combo.add( "item 2" );

    Fixture.fakeSetParameter( getId( combo ), "listVisible", Boolean.TRUE );
    lca.readData( combo );

    assertTrue( combo.getListVisible() );
  }

  @Test
  public void testReadData_SelectedItem() {
    combo.add( "item 1" );
    combo.add( "item 2" );

    Fixture.fakeSetParameter( getId( combo ), "selectionIndex", Integer.valueOf( 1 ) );
    lca.readData( combo );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testFireSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    combo.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( combo ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( combo );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testFireDefaultSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    combo.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( combo ), ClientMessageConst.EVENT_DEFAULT_SELECTION, null );
    Fixture.readDataAndProcessAction( combo );

    verify( listener, times( 1 ) ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testReadData_Text() {
    Fixture.fakeSetParameter( getId( combo ), "text", "abc" );

    lca.readData( combo );

    assertEquals( "abc", combo.getText() );
  }

  @Test
  public void testReadData_TextAndSelection() {
    Fixture.fakeSetParameter( getId( combo ), "text", "abc" );
    Fixture.fakeSetParameter( getId( combo ), "selectionStart", Integer.valueOf( 1 ) );
    Fixture.fakeSetParameter( getId( combo ), "selectionLength", Integer.valueOf( 1 ) );

    lca.readData( combo );

    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testTextIsNotRenderdBack() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );

    Fixture.fakeSetParameter( getId( combo ), "text", "some text" );
    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text is sent back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertEquals( "some text", combo.getText() );
  }

  @Test
  public void testReadText_WithVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    combo.setText( "some text" );
    VerifyListener listener = mock( VerifyListener.class );
    combo.addVerifyListener( listener );

    Fixture.fakeSetParameter( getId( combo ), "text", "verify me" );
    Fixture.executeLifeCycleFromServerThread();

    assertEquals( "verify me", combo.getText() );
    ArgumentCaptor<VerifyEvent> captor = ArgumentCaptor.forClass( VerifyEvent.class );
    verify( listener, times( 1 ) ).verifyText( captor.capture() );
    VerifyEvent event = captor.getValue();
    assertEquals( "verify me", event.text );
    assertEquals( 0, event.start );
    assertEquals( 9, event.end );
  }

  @Test
  public void testTextSelectionWithVerifyEvent_EmptyListener() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    VerifyListener listener = mock( VerifyListener.class );
    combo.addVerifyListener( listener );

    fakeTextAndSelectionParameters( "verify me", 1, 0 );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 1 ) ).verifyText( any( VerifyEvent.class ) );
    assertEquals( "verify me", combo.getText() );
    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertNull( message.findSetOperation( combo, "selection" ) );
  }

  @Test
  public void testTextSelectionWithVerifyEvent_ListenerDoesNotChangeSelection() {
    combo.setText( "" );
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = "verified";
      }
    } );

    fakeTextAndSelectionParameters( "verify me", 1, 0 );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    assertEquals( "verified", combo.getText() );
  }

  @Test
  public void testTextSelectionWithVerifyEvent_ListenerAdjustsSelection() {
    combo.setText( "" );
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = "";
      }
    } );

    fakeTextAndSelectionParameters( "verify me", 1, 0 );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( new Point( 0, 0 ), combo.getSelection() );
    assertEquals( "", combo.getText() );
  }

  @Test
  public void testSelectionAfterRemoveAll() {
    combo = new Combo( shell, SWT.READ_ONLY );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    combo.add( "item 1" );
    combo.select( 0 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        combo.removeAll();
        combo.add( "replacement for item 1" );
        combo.select( 0 );
      }
    } );

    // Simulate button click that executes widgetSelected
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 0 ), message.findSetProperty( combo, PROP_SELECTION_INDEX ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( WidgetUtil.getId( combo.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialItemHeight() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().contains( "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeight() throws IOException {
    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 22 ), message.findSetProperty( combo, "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialVisibleItemCount() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "visibleItemCount" ) == -1 );
  }

  @Test
  public void testRenderVisibleItemCount() throws IOException {
    combo.setVisibleItemCount( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "visibleItemCount" ) );
  }

  @Test
  public void testRenderVisibleItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "visibleItemCount" ) );
  }

  @Test
  public void testRenderInitialItems() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  @Test
  public void testRenderItems() throws IOException, JSONException {
    combo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"a\", \"b\", \"c\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testRenderItemsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "items" ) );
  }

  @Test
  public void testRenderInitialListVisible() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "listVisible" ) == -1 );
  }

  @Test
  public void testRenderListVisible() throws IOException {
    combo.setListVisible( true );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( combo, "listVisible" ) );
  }

  @Test
  public void testRenderListVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "listVisible" ) );
  }

  @Test
  public void testRenderEditable() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "editable" ) == -1 );
  }

  @Test
  public void testRenderEditable_ReadOnly() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( Boolean.FALSE, operation.getProperty( "editable" ) );
  }

  @Test
  public void testRenderInitialSelectionIndex() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndex" ) == -1 );
  }

  @Test
  public void testRenderSelectionIndex() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );

    combo.select( 1 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( combo, "selectionIndex" ) );
  }

  @Test
  public void testRenderSelectionIndexUnchanged() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selectionIndex" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    combo.setText( "foo" );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( combo, "text" ) );
  }

  @Test
  public void testRenderTextNotEditable() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    combo.setText( "foo" );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException, JSONException {
    combo.setText( "foo bar" );

    combo.setSelection( new Point( 1, 3 ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 3 ]", actual ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    combo.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setSelection( new Point( 1, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selection" ) );
  }

  @Test
  public void testRenderInitialTextLimit() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "textLimit" ) == -1 );
  }

  @Test
  public void testRenderTextLimit() throws IOException {
    combo.setTextLimit( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitNoLimit() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();

    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitResetWithNegative() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    combo.setTextLimit( -5 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testListenSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "Selection" ) );
    assertNull( message.findListenOperation( combo, "DefaultSelection" ) );
  }

  @Test
  public void testListenDefaultSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "Selection" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "DefaultSelection" ) );
  }

  @Test
  public void testRemoveListenSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.Selection, listener );
    Fixture.preserveWidgets();

    combo.removeListener( SWT.Selection, listener );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "Selection" ) );
  }

  @Test
  public void testRemoveListenDefaultSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.DefaultSelection, listener );
    Fixture.preserveWidgets();

    combo.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "selection" ) );
  }

  @Test
  public void testRenderAddModifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderRemoveModifyListener() throws Exception {
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    };
    combo.addModifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.removeModifyListener( listener );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderModifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "modify" ) );
  }

  @Test
  public void testRenderAddVerifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderRemoveVerifyListener() throws Exception {
    VerifyListener listener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    };
    combo.addVerifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.removeVerifyListener( listener );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderVerifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "verify" ) );
  }

  private void fakeTextAndSelectionParameters( String text, int start, int length ) {
    Fixture.fakeSetParameter( getId( combo ), "text", text );
    Fixture.fakeSetParameter( getId( combo ), "selectionStart", Integer.valueOf( start ) );
    Fixture.fakeSetParameter( getId( combo ), "selectionLength", Integer.valueOf( length ) );
  }
}
