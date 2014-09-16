/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.internal.widgets.buttonkit.ButtonOperationHandler;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CComboLCA_Test {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private Display display;
  private Shell shell;
  private CCombo ccombo;
  private CComboLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    ccombo = new CCombo( shell, SWT.NONE );
    lca = new CComboLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( ccombo );
    ControlLCATestUtil.testFocusListener( ccombo );
    ControlLCATestUtil.testMouseListener( ccombo );
    ControlLCATestUtil.testKeyListener( ccombo );
    ControlLCATestUtil.testTraverseListener( ccombo );
    ControlLCATestUtil.testMenuDetectListener( ccombo );
    ControlLCATestUtil.testHelpListener( ccombo );
  }

  @Test
  public void testPreserveValues() {
    CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );
    Fixture.markInitialized( display );
    // Test preserving a CCombo with no items and (naturally) no selection
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION_INDEX ) );
    Object visibleItemCount = adapter.getPreserved( CComboLCA.PROP_VISIBLE_ITEM_COUNT );
    assertEquals( new Integer( ccombo.getVisibleItemCount() ), visibleItemCount );
    assertNull( adapter.getPreserved( CComboLCA.PROP_TEXT_LIMIT ) );
    assertEquals( new Point( 0, 0 ), adapter.getPreserved( CComboLCA.PROP_SELECTION ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_LIST_VISIBLE ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    // Test preserving CCombo with items, where one is selected
    Fixture.clearPreserved();
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    ccombo.select( 1 );
    ccombo.setTextLimit( 10 );
    ccombo.setListVisible( true );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    ccombo.addSelectionListener( selectionListener );
    ccombo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 2, items.length );
    assertEquals( "item 1", items[ 0 ] );
    assertEquals( "item 2", items[ 1 ] );
    assertEquals( new Integer( 1 ), adapter.getPreserved( PROP_SELECTION_INDEX ) );
    visibleItemCount = adapter.getPreserved( CComboLCA.PROP_VISIBLE_ITEM_COUNT );
    assertEquals( new Integer( ccombo.getVisibleItemCount() ), visibleItemCount );
    assertEquals( "item 2", adapter.getPreserved( Props.TEXT ) );
    assertEquals( new Integer( 10 ), adapter.getPreserved( CComboLCA.PROP_TEXT_LIMIT ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CComboLCA.PROP_LIST_VISIBLE ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    ccombo.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    ccombo.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    ccombo.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( null, ccombo.getToolTipText() );
    Fixture.clearPreserved();
    ccombo.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( "some text", ccombo.getToolTipText() );
    Fixture.clearPreserved();
  }

  @Test
  public void testEditablePreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
  }

  @Test
  public void testTextIsNotRenderdBack() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( ccombo );
    getRemoteObject( getId( ccombo ) ).setHandler( new CComboOperationHandler( ccombo ) );

    Fixture.fakeSetProperty( getId( ccombo ), "text", "some text" );
    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text is sent back to the client
    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "text" ) );
    assertEquals( "some text", ccombo.getText() );
  }

  @Test
  public void testSelectionAfterRemoveAll() {
    ccombo = new CCombo( shell, SWT.READ_ONLY );
    Button button = new Button( shell, SWT.PUSH );
    getRemoteObject( button ).setHandler( new ButtonOperationHandler( button ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.markInitialized( button );
    ccombo.add( "item 1" );
    ccombo.select( 0 );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        ccombo.removeAll();
        ccombo.add( "replacement for item 1" );
        ccombo.select( 0 );
      }
    } );

    // Simulate button click that executes widgetSelected
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.findSetProperty( ccombo, PROP_SELECTION_INDEX ).asInt() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "ccombo" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( ccombo );
    lca.renderInitialization( ccombo );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof CComboOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    CComboOperationHandler handler = spy( new CComboOperationHandler( ccombo ) );
    getRemoteObject( getId( ccombo ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( ccombo ), "Help", new JsonObject() );
    lca.readData( ccombo );

    verify( handler ).handleNotifyHelp( ccombo, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertEquals( getId( ccombo.getParent() ), getParent( operation ) );
  }


  @Test
  public void testRenderFlatStyle() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.FLAT );

    lca.renderInitialization( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( getStyles( operation ).contains( "FLAT" ) );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setFont( new Font( display, "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialVisibleItemCount() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "visibleItemCount" ) );
  }

  @Test
  public void testRenderVisibleItemCount() throws IOException {
    ccombo.setVisibleItemCount( 10 );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( ccombo, "visibleItemCount" ).asInt() );
  }

  @Test
  public void testRenderVisibleItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "visibleItemCount" ) );
  }

  @Test
  public void testRenderInitialItems() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "items" ) );
  }

  @Test
  public void testRenderItems() throws IOException {
    ccombo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( "a" ).add( "b" ).add( "c" );
    assertEquals( expected, message.findSetProperty( ccombo, "items" ) );
  }

  @Test
  public void testRenderItemsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "items" ) );
  }

  @Test
  public void testRenderInitialListVisible() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "listVisible" ) );
  }

  @Test
  public void testRenderListVisible() throws IOException {
    ccombo.setListVisible( true );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( ccombo, "listVisible" ) );
  }

  @Test
  public void testRenderListVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "listVisible" ) );
  }

  @Test
  public void testRenderInitialSelectionIndex() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getProperties().names().indexOf( "selectionIndex" ) == -1 );
  }

  @Test
  public void testRenderSelectionIndex() throws IOException {
    ccombo.setItems( new String[] { "a", "b", "c" } );

    ccombo.select( 1 );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( ccombo, "selectionIndex" ).asInt() );
  }

  @Test
  public void testRenderSelectionIndex_onItemsChange() throws IOException {
    ccombo.setItems( new String[] { "a", "b", "c" } );
    ccombo.select( 1 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.setItems( new String[] { "a", "b" } );
    ccombo.select( 1 );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( ccombo, "selectionIndex" ).asInt() );
  }

  @Test
  public void testRenderSelectionIndexUnchanged() throws IOException {
    ccombo.setItems( new String[] { "a", "b", "c" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "selectionIndex" ) );
  }

  @Test
  public void testRenderInitialEditable() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "editable" ) );
  }

  @Test
  public void testRenderEditable() throws IOException {
    ccombo.setEditable( false );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( ccombo, "editable" ) );
  }

  @Test
  public void testRenderEditableUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setEditable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "editable" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( ccombo, "text" ).asString() );
  }

  @Test
  public void testRenderTextReadOnly() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );

    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( ccombo, "text" ).asString() );
  }

  @Test
  public void testRenderTextNotEditable() throws IOException {
    ccombo.setEditable( false );

    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( ccombo, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "text" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    ccombo.setText( "foo bar" );

    ccombo.setSelection( new Point( 1, 3 ) );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( ccombo, "selection" );
    assertTrue( JsonArray.readFrom( "[ 1, 3 ]" ).equals( actual ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    ccombo.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setSelection( new Point( 1, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "selection" ) );
  }

  @Test
  public void testRenderInitialTextLimit() throws IOException {
    lca.render( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertFalse( operation.getProperties().names().contains( "textLimit" ) );
  }

  @Test
  public void testRenderTextLimit() throws IOException {
    ccombo.setTextLimit( 10 );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( ccombo, "textLimit" ).asInt() );
  }

  @Test
  public void testRenderTextLimitNoLimit() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    ccombo.setTextLimit( 10 );
    Fixture.preserveWidgets();

    ccombo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( ccombo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    ccombo.setTextLimit( CCombo.LIMIT );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( ccombo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitResetWithNegative() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    ccombo.setTextLimit( -5 );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( ccombo, "textLimit" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( ccombo, "Selection" ) );
    assertNull( message.findListenOperation( ccombo, "DefaultSelection" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( ccombo, "DefaultSelection" ) );
    assertNull( message.findListenOperation( ccombo, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener selection = mock( Listener.class );
    ccombo.addListener( SWT.Selection, selection );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.removeListener( SWT.Selection, selection );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( ccombo, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener selection = mock( Listener.class );
    ccombo.addListener( SWT.DefaultSelection, selection );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.removeListener( SWT.DefaultSelection, selection );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( ccombo, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( ccombo, "selection" ) );
  }

  @Test
  public void testRenderAddModifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( ccombo, "Modify" ) );
  }

  @Test
  public void testRenderRemoveModifyListener() throws Exception {
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    };
    ccombo.addModifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.removeModifyListener( listener );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( ccombo, "Modify" ) );
  }

  @Test
  public void testRenderModifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( ccombo, "modify" ) );
  }

  @Test
  public void testRenderAddVerifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( ccombo, "Modify" ) );
  }

  @Test
  public void testRenderRemoveVerifyListener() throws Exception {
    VerifyListener listener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    };
    ccombo.addVerifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.removeVerifyListener( listener );
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( ccombo, "Modify" ) );
  }

  @Test
  public void testRenderVerifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.preserveWidgets();

    ccombo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( ccombo, "verify" ) );
  }

}
