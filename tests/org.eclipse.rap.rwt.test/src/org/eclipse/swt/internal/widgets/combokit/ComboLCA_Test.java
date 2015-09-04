/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( combo );
  }

  @Test
  public void testPreserveValues() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    Fixture.markInitialized( display );
    // Test preserving a combo with no items and (naturally) no selection
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( combo );
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
      @Override
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
  }

  @Test
  public void testEditablePreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( combo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    Fixture.clearPreserved();
    // textLimit
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    Integer textLimit = ( Integer )adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( 10 ), textLimit );
  }

  @Test
  public void testTextIsNotRenderdBack() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    getRemoteObject( getId( combo ) ).setHandler( new ComboOperationHandler( combo ) );

    Fixture.fakeSetProperty( getId( combo ), "text", "some text" );
    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text is sent back to the client
    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertEquals( "some text", combo.getText() );
  }

  @Test
  public void testSelectionAfterRemoveAll() {
    combo = new Combo( shell, SWT.READ_ONLY );
    Button button = new Button( shell, SWT.PUSH );
    getRemoteObject( getId( button ) ).setHandler( new ButtonOperationHandler( button ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.markInitialized( button );
    combo.add( "item 1" );
    combo.select( 0 );
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.findSetProperty( combo, PROP_SELECTION_INDEX ).asInt() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( combo );
    lca.renderInitialization( combo );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ComboOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( getId( combo.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDirection_default() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertNull( operation.getProperties().get( "direction" ) );
  }

  @Test
  public void testRenderDirection_RTL() throws IOException {
    combo = new Combo( shell, SWT.RIGHT_TO_LEFT );

    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "rtl", message.findCreateProperty( combo, "direction" ).asString() );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setFont( new Font( display, "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialVisibleItemCount() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "visibleItemCount" ) );
  }

  @Test
  public void testRenderVisibleItemCount() throws IOException {
    combo.setVisibleItemCount( 10 );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( combo, "visibleItemCount" ).asInt() );
  }

  @Test
  public void testRenderVisibleItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "visibleItemCount" ) );
  }

  @Test
  public void testRenderInitialItems() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "items" ) );
  }

  @Test
  public void testRenderItems() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ \"a\", \"b\", \"c\" ]" );
    JsonArray actual = ( JsonArray )message.findSetProperty( combo, "items" );
    assertEquals( expected, actual );
  }

  @Test
  public void testRenderItemsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "items" ) );
  }

  @Test
  public void testRenderInitialListVisible() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "listVisible" ) );
  }

  @Test
  public void testRenderListVisible() throws IOException {
    combo.setListVisible( true );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( combo, "listVisible" ) );
  }

  @Test
  public void testRenderListVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "listVisible" ) );
  }

  @Test
  public void testRenderEditable() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "editable" ) );
  }

  @Test
  public void testRenderEditable_ReadOnly() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( JsonValue.FALSE, operation.getProperties().get( "editable" ) );
  }

  @Test
  public void testRenderInitialSelectionIndex() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "selectionIndex" ) );
  }

  @Test
  public void testRenderSelectionIndex() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );

    combo.select( 1 );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( combo, "selectionIndex" ).asInt() );
  }

  @Test
  public void testRenderSelectionIndex_onItemsChange() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );
    combo.select( 1 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.setItems( new String[] { "a", "b" } );
    combo.select( 1 );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( combo, "selectionIndex" ).asInt() );
  }

  @Test
  public void testRenderSelectionIndexUnchanged() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selectionIndex" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    combo.setText( "foo" );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( combo, "text" ).asString() );
  }

  @Test
  public void testRenderTextNotEditable() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    combo.setText( "foo" );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    combo.setText( "foo bar" );

    combo.setSelection( new Point( 1, 3 ) );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( combo, "selection" );
    assertEquals( JsonArray.readFrom( "[ 1, 3 ]" ), actual );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    combo.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setSelection( new Point( 1, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selection" ) );
  }

  @Test
  public void testRenderInitialTextLimit() throws IOException {
    lca.render( combo );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertFalse( operation.getProperties().names().contains( "textLimit" ) );
  }

  @Test
  public void testRenderTextLimit() throws IOException {
    combo.setTextLimit( 10 );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( combo, "textLimit" ).asInt() );
  }

  @Test
  public void testRenderTextLimitNoLimit() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();

    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitResetWithNegative() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    combo.setTextLimit( -5 );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  @Test
  public void testListenSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( combo, "Selection" ) );
    assertNull( message.findListenOperation( combo, "DefaultSelection" ) );
  }

  @Test
  public void testListenDefaultSelection() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "Selection" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( combo, "DefaultSelection" ) );
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( combo, "Selection" ) );
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( combo, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "selection" ) );
  }

  @Test
  public void testRenderListen_Modify() throws Exception {
    Fixture.markInitialized( combo );
    Fixture.clearPreserved();

    combo.addListener( SWT.Modify, mock( Listener.class ) );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderListen_Verify() throws Exception {
    Fixture.markInitialized( combo );
    Fixture.clearPreserved();

    combo.addListener( SWT.Verify, mock( Listener.class ) );
    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( combo, "Modify" ) );
  }

  @Test
  public void testRenderChanges_rendersClientListener() throws IOException {
    combo.addListener( SWT.Verify, new ClientListener( "" ) );

    lca.renderChanges( combo );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( combo, "addListener" ) );
  }


}
