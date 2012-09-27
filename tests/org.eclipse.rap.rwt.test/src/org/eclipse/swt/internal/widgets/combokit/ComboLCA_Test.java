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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
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
import org.mockito.ArgumentCaptor;

public class ComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private Display display;
  private Shell shell;
  private Combo combo;
  private ComboLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    combo = new Combo( shell, SWT.NONE );
    lca = new ComboLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
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
  }

  public void testEditablePreserveValues() {
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

  public void testReadData_ListVisible() {
    combo.add( "item 1" );
    combo.add( "item 2" );

    Fixture.fakeSetParameter( getId( combo ), "listVisible", Boolean.TRUE );
    lca.readData( combo );

    assertEquals( true, combo.getListVisible() );
  }

  public void testReadData_SelectedItem() {
    combo.add( "item 1" );
    combo.add( "item 2" );

    Fixture.fakeSetParameter( getId( combo ), "selectionIndex", Integer.valueOf( 1 ) );
    lca.readData( combo );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  public void testFireSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    combo.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( combo ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.readDataAndProcessAction( combo );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testReadData_Text() {
    Fixture.fakeSetParameter( getId( combo ), "text", "abc" );

    lca.readData( combo );

    assertEquals( "abc", combo.getText() );
  }

  public void testReadData_TextAndSelection() {
    Fixture.fakeSetParameter( getId( combo ), "text", "abc" );
    Fixture.fakeSetParameter( getId( combo ), "selectionStart", Integer.valueOf( 1 ) );
    Fixture.fakeSetParameter( getId( combo ), "selectionLength", Integer.valueOf( 1 ) );

    lca.readData( combo );

    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

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

  public void testReadText_WithVerifyListener() {
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
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 0 ), message.findSetProperty( combo, PROP_SELECTION_INDEX ) );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( "rwt.widgets.Combo", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertEquals( WidgetUtil.getId( combo.getParent() ), operation.getParent() );
  }

  public void testRenderInitialItemHeight() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().contains( "itemHeight" ) );
  }

  public void testRenderItemHeight() throws IOException {
    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 22 ), message.findSetProperty( combo, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setFont( Graphics.getFont( "Arial", 16, SWT.NONE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "itemHeight" ) );
  }

  public void testRenderInitialVisibleItemCount() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "visibleItemCount" ) == -1 );
  }

  public void testRenderVisibleItemCount() throws IOException {
    combo.setVisibleItemCount( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "visibleItemCount" ) );
  }

  public void testRenderVisibleItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setVisibleItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "visibleItemCount" ) );
  }

  public void testRenderInitialItems() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "items" ) == -1 );
  }

  public void testRenderItems() throws IOException, JSONException {
    combo.setItems( new String[] { "a", "b", "c" } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    String expected = "[ \"a\", \"b\", \"c\" ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "items" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderItemsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setItems( new String[] { "a", "b", "c" } );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "items" ) );
  }

  public void testRenderInitialListVisible() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "listVisible" ) == -1 );
  }

  public void testRenderListVisible() throws IOException {
    combo.setListVisible( true );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( combo, "listVisible" ) );
  }

  public void testRenderListVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setListVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "listVisible" ) );
  }

  public void testRenderEditable() throws IOException {
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
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selectionIndex" ) == -1 );
  }

  public void testRenderSelectionIndex() throws IOException {
    combo.setItems( new String[] { "a", "b", "c" } );

    combo.select( 1 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( combo, "selectionIndex" ) );
  }

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

  public void testRenderInitialText() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
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
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "text" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException, JSONException {
    combo.setText( "foo bar" );

    combo.setSelection( new Point( 1, 3 ) );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( combo, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 3 ]", actual ) );
  }

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

  public void testRenderInitialTextLimit() throws IOException {
    lca.render( combo );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( combo );
    assertTrue( operation.getPropertyNames().indexOf( "textLimit" ) == -1 );
  }

  public void testRenderTextLimit() throws IOException {
    combo.setTextLimit( 10 );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( combo, "textLimit" ) );
  }

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

  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );

    combo.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( combo, "textLimit" ) );
  }

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

  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( combo );
    Fixture.preserveWidgets();

    combo.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( combo );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( combo, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
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