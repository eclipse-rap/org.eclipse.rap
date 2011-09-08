/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ccombokit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.Message;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.protocol.Message.CreateOperation;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

public class CComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private Display display;
  private Shell shell;
  private CComboLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new CComboLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    CCombo ccombo = new CCombo( shell, SWT.DEFAULT );
    Fixture.markInitialized( display );
    // Test preserving a CCombo with no items and (naturally) no selection
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION_INDEX ) );
    Object visibleItemCount = adapter.getPreserved( CComboLCA.PROP_VISIBLE_ITEM_COUNT );
    assertEquals( new Integer( ccombo.getVisibleItemCount() ), visibleItemCount );
    assertEquals( new Integer( Text.LIMIT ), adapter.getPreserved( CComboLCA.PROP_TEXT_LIMIT ) );
    assertEquals( new Point( 0, 0 ), adapter.getPreserved( CComboLCA.PROP_SELECTION ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_LIST_VISIBLE ) );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    assertEquals( Boolean.FALSE, hasListeners );
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
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    hasListeners = ( Boolean )adapter.getPreserved( CComboLCA.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    // control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ccombo.addControlListener( new ControlAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    ccombo.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    ccombo.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
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
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // activateListener
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( ccombo, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testEditablePreserveValues() {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    // activateListeners, focusListeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    Boolean focusListener = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, focusListener );
    Fixture.clearPreserved();
    ccombo.addFocusListener( new FocusAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadData() {
    final CCombo ccombo = new CCombo( shell, SWT.NONE );
    String ccomboId = WidgetUtil.getId( ccombo );
    // init CCombo items
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    // read list visibility
    Fixture.fakeRequestParam( ccomboId + ".listVisible", "true" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( true, ccombo.getListVisible() );
    // read changed selection
    Fixture.fakeRequestParam( ccomboId + ".selectedItem", "1" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( 1, ccombo.getSelectionIndex() );
    // read changed selection and ensure that SelectionListener gets called
    final StringBuffer log = new StringBuffer();
    ccombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertSame( ccombo, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    } );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( ccomboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, ccomboId );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( 0, ccombo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
    // read changed text
    Fixture.fakeRequestParam( ccomboId + ".text", "abc" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( "abc", ccombo.getText() );
    // read changed selection
    Fixture.fakeRequestParam( ccomboId + ".text", "abc" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "1" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  public void testReadText() {
    final CCombo ccombo = new CCombo( shell, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( ccombo );
    // test without verify listener
    Fixture.fakeNewRequest( display );
    String textId = WidgetUtil.getId( ccombo );
    Fixture.fakeRequestParam( textId + ".text", "some text" );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "some text", ccombo.getText() );
    // test with verify listener
    final StringBuffer log = new StringBuffer();
    ccombo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        assertEquals( ccombo, event.widget );
        assertEquals( "verify me", event.text );
        assertEquals( 0, event.start );
        assertEquals( 9, event.end );
        log.append( event.text );
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "verify me", ccombo.getText() );
    assertEquals( "verify me", log.toString() );
  }

  public void testTextSelectionWithVerifyEvent() {
    final java.util.List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    shell.open();
    String ccomboId = WidgetUtil.getId( ccombo );
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    VerifyListener emptyVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    };
    ccombo.addVerifyListener( emptyVerifyListener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( ccombo );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), ccombo.getSelection() );
    assertEquals( "verify me", ccombo.getText() );
    ccombo.removeVerifyListener( emptyVerifyListener );
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    ccombo.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    ccombo.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), ccombo.getSelection() );
    assertEquals( "verified", ccombo.getText() );
    ccombo.removeVerifyListener( alteringVerifyListener );
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    ccombo.setText( "" );
    alteringVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    };
    ccombo.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), ccombo.getSelection() );
    assertEquals( "", ccombo.getText() );
    ccombo.removeVerifyListener( alteringVerifyListener );
  }

  public void testTextLimit() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.BORDER );
    // run LCA one to dump the here uninteresting prolog
    Fixture.fakeResponseWriter();
    lca.renderChanges( ccombo );
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( ccombo );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setTextLimit" ) );
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( ccombo );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ccombo.setTextLimit( 12 );
    lca.renderChanges( ccombo );
    String expected = "setTextLimit( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // textLimit = CCombo.LIMIT is tread as 'no limit'
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( ccombo );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ccombo.setTextLimit( CCombo.LIMIT );
    lca.renderChanges( ccombo );
    expected = "setTextLimit( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testSelectionAfterRemoveAll() {
    final CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );
    ccombo.add( "item 1" );
    ccombo.select( 0 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        ccombo.removeAll();
        ccombo.add( "replacement for item 1" );
        ccombo.select( 0 );
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
    assertEquals( new Integer( 0 ), message.findSetProperty( ccombo, PROP_SELECTION_INDEX ) );
  }

  public void testRenderCreate() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.renderInitialization( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
    assertEquals( Boolean.TRUE, operation.getProperty( "ccombo" ) );
  }

  public void testRenderParent() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.renderInitialization( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertEquals( WidgetUtil.getId( ccombo.getParent() ), operation.getParent() );
  }


  public void testRenderFlatStyle() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.FLAT );

    lca.renderInitialization( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "FLAT" ) );
  }
  public void testRenderInitialItemHeight() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "itemHeight" ) != -1 );
  }

  public void testRenderItemHeight() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 22 ), message.findSetProperty( ccombo, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "itemHeight" ) );
  }

  public void testRenderInitialVisibleItemCount() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "visibleItemCount" ) == -1 );
  }

  public void testRenderVisibleItemCount() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setVisibleItemCount( 10 );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( ccombo, "visibleItemCount" ) );
  }

  public void testRenderVisibleItemCountUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "visibleItemCount" ) );
  }

  public void testRenderInitialItems() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  public void testRenderItems() throws IOException, JSONException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"a\", \"b\", \"c\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( ccombo, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderItemsUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "items" ) );
  }

  public void testRenderInitialListVisible() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "listVisible" ) == -1 );
  }

  public void testRenderListVisible() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setListVisible( true );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( ccombo, "listVisible" ) );
  }

  public void testRenderListVisibleUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "listVisible" ) );
  }

  public void testRenderInitialSelectionIndex() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndex" ) == -1 );
  }

  public void testRenderSelectionIndex() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    ccombo.setItems( new String[] { "a", "b", "c" } );

    ccombo.select( 1 );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( ccombo, "selectionIndex" ) );
  }

  public void testRenderSelectionIndexUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    ccombo.setItems( new String[] { "a", "b", "c" } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.select( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "selectionIndex" ) );
  }

  public void testRenderInitialEditable() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "editable" ) == -1 );
  }

  public void testRenderEditable() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setEditable( false );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( ccombo, "editable" ) );
  }

  public void testRenderEditableUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setEditable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "editable" ) );
  }

  public void testRenderInitialText() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    lca.render( ccombo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( ccombo );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );

    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( ccombo, "text" ) );
  }

  public void testRenderTextNotEditable() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );

    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "text" ) );
  }

  public void testRenderTextAfterMakeItEditable() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );

    ccombo.setEditable( true );
    ccombo.setText( "foo" );
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( ccombo, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );

    ccombo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( ccombo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( ccombo, "text" ) );
  }
}
