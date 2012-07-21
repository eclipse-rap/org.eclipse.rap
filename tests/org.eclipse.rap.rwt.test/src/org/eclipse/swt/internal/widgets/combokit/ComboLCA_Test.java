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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
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
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private Display display;
  private Shell shell;
  private ComboLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ComboLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( combo );
    ControlLCATestUtil.testFocusListener( combo );
    ControlLCATestUtil.testMouseListener( combo );
    ControlLCATestUtil.testKeyListener( combo );
    ControlLCATestUtil.testTraverseListener( combo );
    ControlLCATestUtil.testMenuDetectListener( combo );
    ControlLCATestUtil.testHelpListener( combo );
  }

  public void testPreserveValues() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    Fixture.markInitialized( display );
    // Test preserving a combo with no items and (naturally) no selection
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
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
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
  }

  public void testEditablePreserveValues() {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    Fixture.clearPreserved();
    // textLimit
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    Integer textLimit = ( Integer )adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( 10 ), textLimit );
  }

  public void testReadData() {
    final Combo combo = new Combo( shell, SWT.NONE );
    String comboId = WidgetUtil.getId( combo );
    // init combo items
    combo.add( "item 1" );
    combo.add( "item 2" );
    // read list visibility
    Fixture.fakeRequestParam( comboId + ".listVisible", "true" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( true, combo.getListVisible() );
    // read changed selection
    Fixture.fakeRequestParam( comboId + ".selectedItem", "1" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 1, combo.getSelectionIndex() );
    // read changed selection and ensure that SelectionListener gets called
    final StringBuilder log = new StringBuilder();
    combo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertSame( combo, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    } );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( comboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, comboId );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 0, combo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
    // read changed selection
    Fixture.fakeRequestParam( comboId + ".text", "abc" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "1" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  public void testReadText() {
    final Combo combo = new Combo( shell, SWT.BORDER );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    Fixture.fakeNewRequest( display );
    String comboId = WidgetUtil.getId( combo );
    Fixture.fakeRequestParam( comboId + ".text", "some text" );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text is sent back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertEquals( "some text", combo.getText() );
  }

  public void testReadText_withVerifyListener() {
    final Combo combo = new Combo( shell, SWT.BORDER );
    combo.setText( "some text" );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    final StringBuilder log = new StringBuilder();
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        assertEquals( combo, event.widget );
        assertEquals( "verify me", event.text );
        assertEquals( 0, event.start );
        assertEquals( 9, event.end );
        log.append( event.text );
      }
    } );
    Fixture.fakeNewRequest( display );
    String comboId = WidgetUtil.getId( combo );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text is sent back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertEquals( "verify me", combo.getText() );
    assertEquals( "verify me", log.toString() );
  }

  public void testTextSelectionWithVerifyEvent_emptyListener() {
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    final Combo combo = new Combo( shell, SWT.NONE );
    shell.open();
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( combo );
    log.clear();
    Fixture.fakeNewRequest( display );
    String comboId = WidgetUtil.getId( combo );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
    assertNull( message.findSetOperation( combo, "selection" ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    assertEquals( "verify me", combo.getText() );
  }

  public void testTextSelectionWithVerifyEvent_listenerDoesNotChangeSelection() {
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    final Combo combo = new Combo( shell, SWT.NONE );
    shell.open();
    combo.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    combo.addVerifyListener( alteringVerifyListener );
    Fixture.fakeNewRequest( display );
    String comboId = WidgetUtil.getId( combo );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    assertEquals( "verified", combo.getText() );
  }

  public void testTextSelectionWithVerifyEvent_listenerAdjustsSelection() {
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    final Combo combo = new Combo( shell, SWT.NONE );
    shell.open();
    String comboId = WidgetUtil.getId( combo );
    combo.setText( "" );
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    } );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), combo.getSelection() );
    assertEquals( "", combo.getText() );
  }

  public void testSelectionAfterRemoveAll() {
    final Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "item 1" );
    combo.select( 0 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        combo.removeAll();
        combo.add( "replacement for item 1" );
        combo.select( 0 );
      }
    } );

    String buttonId = WidgetUtil.getId( button );

    // Execute life cycle once to simulate startup request
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();

    // Simulate button click that executes widgetSelected
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread();
    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 0 ), message.findSetProperty( combo, PROP_SELECTION_INDEX ) );
  }

  public void testRenderCreate() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( WidgetUtil.getId( combo.getParent() ), operation.getParent() );
  }

  public void testRenderInitialItemHeight() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().contains( "itemHeight" ) );
  }

  public void testRenderItemHeight() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 22 ), message.findSetProperty( combo, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "itemHeight" ) );
  }

  public void testRenderInitialVisibleItemCount() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "visibleItemCount" ) == -1 );
  }

  public void testRenderVisibleItemCount() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setVisibleItemCount( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "visibleItemCount" ) );
  }

  public void testRenderVisibleItemCountUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "visibleItemCount" ) );
  }

  public void testRenderInitialItems() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  public void testRenderItems() throws IOException, JSONException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"a\", \"b\", \"c\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderItemsUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "items" ) );
  }

  public void testRenderInitialListVisible() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "listVisible" ) == -1 );
  }

  public void testRenderListVisible() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setListVisible( true );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( combo, "listVisible" ) );
  }

  public void testRenderListVisibleUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "listVisible" ) );
  }

  public void testRenderEditable() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "editable" ) == -1 );
  }

  public void testRenderEditable_ReadOnly() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( Boolean.FALSE, operation.getProperty( "editable" ) );
  }

  public void testRenderInitialSelectionIndex() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndex" ) == -1 );
  }

  public void testRenderSelectionIndex() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.setItems( new String[] { "a", "b", "c" } );

    combo.select( 1 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( combo, "selectionIndex" ) );
  }

  public void testRenderSelectionIndexUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selectionIndex" ) );
  }

  public void testRenderInitialText() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setText( "foo" );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( combo, "text" ) );
  }

  public void testRenderTextNotEditable() throws IOException {
    Combo combo = new Combo( shell, SWT.READ_ONLY );

    combo.setText( "foo" );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException, JSONException {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.setText( "foo bar" );

    combo.setSelection( new Point( 1, 3 ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 3 ]", actual ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setSelection( new Point( 1, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "selection" ) );
  }

  public void testRenderInitialTextLimit() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "textLimit" ) == -1 );
  }

  public void testRenderTextLimit() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.setTextLimit( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "textLimit" ) );
  }

  public void testRenderTextLimitNoLimit() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();

    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  public void testRenderTextLimitUnchanged() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "textLimit" ) );
  }

  public void testRenderTextLimitReset() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  public void testRenderTextLimitResetWithNegative() throws IOException {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    combo.setTextLimit( -5 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( combo, "textLimit" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    combo.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.removeSelectionListener( listener );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( combo, "selection" ) );
  }

  public void testRenderAddModifyListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "modify" ) );
  }

  public void testRenderRemoveModifyListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
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
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "modify" ) );
  }

  public void testRenderModifyListenerUnchanged() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
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

  public void testRenderAddVerifyListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "verify" ) );
  }

  public void testRenderRemoveVerifyListener() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
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
    assertEquals( Boolean.FALSE, message.findListenProperty( combo, "verify" ) );
  }

  public void testRenderVerifyListenerUnchanged() throws Exception {
    Combo combo = new Combo( shell, SWT.NONE );
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
}