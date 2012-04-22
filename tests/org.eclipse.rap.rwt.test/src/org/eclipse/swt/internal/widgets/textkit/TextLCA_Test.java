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
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TextLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private TextLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeResponseWriter();
    lca = new TextLCA();
    display = new Display();
    shell = new Shell( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Text text = new Text( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( text );
    ControlLCATestUtil.testFocusListener( text );
    ControlLCATestUtil.testMouseListener( text );
    ControlLCATestUtil.testKeyListener( text );
    ControlLCATestUtil.testTraverseListener( text );
    ControlLCATestUtil.testMenuDetectListener( text );
    ControlLCATestUtil.testHelpListener( text );
  }

  public void testMultiPreserveValues() {
    Text text = new Text( shell, SWT.MULTI );
    testPreserveValues( text );
  }

  public void testPasswordPreserveValues() {
    Text text = new Text( shell, SWT.PASSWORD );
    testPreserveValues( text );
  }

  public void testSinglePreserveValues() {
    Text text = new Text( shell, SWT.SINGLE );
    testPreserveValues( text );
  }

  public void testReadData() {
    Text text = new Text( shell, SWT.NONE );
    String textId = WidgetUtil.getId( text );
    // read changed text
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( "abc", text.getText() );
    // read changed selection
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "1" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  public void testModifyEvent() {
    final StringBuilder log = new StringBuilder();
    final Text text = new Text( shell, SWT.NONE );
    text.addModifyListener( new ModifyListener() {

      public void modifyText( ModifyEvent event ) {
        assertEquals( text, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    shell.open();
    String textId = WidgetUtil.getId( text );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "new text" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "modifyText", log.toString() );
  }

  public void testVerifyEvent() {
    final StringBuilder log = new StringBuilder();
    final Text text = new Text( shell, SWT.NONE );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        assertEquals( text, event.getSource() );
        assertEquals( text, event.widget );
        assertTrue( event.doit );
        log.append( "verifyText" );
      }
    } );
    shell.open();
    String textId = WidgetUtil.getId( text );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "verifyText", log.toString() );
  }

  public void testSelectionWithVerifyEvent_emptyListener() {
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    String textId = WidgetUtil.getId( text );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    } );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( text );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
    assertNull( message.findSetOperation( text, "selection" ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verify me", text.getText() );
  }

  public void testSelectionWithVerifyEvent_listenerDoesNotChangeSelection() {
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    String textId = WidgetUtil.getId( text );
    text.setText( "" );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verified", text.getText() );
  }

  public void testSelectionWithVerifyEvent_listenerAdjustsSelection() {
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    final List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    String textId = WidgetUtil.getId( text );
    text.setText( "" );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getText() );
  }

  public void testPreserveText() {
    Text text = new Text( shell, SWT.SINGLE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( text );
    Fixture.fakeNewRequest( display );
    String textId = WidgetUtil.getId( text );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text and selection values are sent back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  public void testVerifyAndModifyEvent() {
    final List<TypedEvent> log = new ArrayList<TypedEvent>();
    // set up widgets to be tested
    final Text text = new Text( shell, SWT.NONE );
    shell.open();
    String textId = WidgetUtil.getId( text );
    // ensure that modify *and* verify event is fired
    text.setText( "" );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    } );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        log.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    assertTrue( log.get( 0 ) instanceof VerifyEvent );
    assertTrue( log.get( 1 ) instanceof ModifyEvent );
  }

  private static void testPreserveValues( Text text ) {
    //text
    text.setText( "some text" );
    Fixture.markInitialized( text.getDisplay() );
    Fixture.preserveWidgets();
    assertEquals( text.getText(), getPreserved( text, Props.TEXT ) );
    Fixture.clearPreserved();
    //text-limit
    Fixture.preserveWidgets();
    Integer textLimit = ( Integer )( getPreserved( text, TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertNull( textLimit );
    text.setTextLimit( 30 );
    Fixture.preserveWidgets();
    textLimit = ( Integer )( getPreserved( text, TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertEquals( 30, textLimit.intValue() );
    Fixture.clearPreserved();
    //selection
    Fixture.preserveWidgets();
    Point point = new Point( 0, 0 );
    assertEquals( point, getPreserved( text, TextLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    point = new Point( 3, 6 );
    text.setSelection( point );
    text.getSelection();
    Fixture.preserveWidgets();
    assertEquals( point, getPreserved( text, TextLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    //readonly
    Fixture.preserveWidgets();
    Boolean readonly = ( Boolean )getPreserved( text, TextLCAUtil.PROP_EDITABLE );
    assertEquals( Boolean.TRUE, readonly );
    Fixture.clearPreserved();
    text.setEditable( false );
    Fixture.preserveWidgets();
    readonly = ( Boolean )getPreserved( text, TextLCAUtil.PROP_EDITABLE );
    assertEquals( Boolean.FALSE, readonly );
    Fixture.clearPreserved();
    //Bounds
    Rectangle rectangle = new Rectangle( 10, 10, 200, 100 );
    text.setBounds( rectangle );
    Fixture.preserveWidgets();
    assertEquals( rectangle, getPreserved( text, Props.BOUNDS ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.ENABLED ) );
    Fixture.clearPreserved();
    text.setEnabled( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, getPreserved( text, Props.ENABLED ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    assertEquals( null, getPreserved( text, Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( text );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    text.setMenu( menu );
    Fixture.preserveWidgets();
    assertEquals( menu, getPreserved( text, Props.MENU ) );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.VISIBLE ) );
    Fixture.clearPreserved();
    text.setVisible( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, getPreserved( text, Props.VISIBLE ) );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    assertTrue( getPreserved( text, Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
  }

  public void testWriteModifyListenerWhenReadOnly() throws IOException {
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( text, "modify" ) );
  }

  public void testRenderCreate() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( "rwt.widgets.Text", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SINGLE" ) );
  }

  public void testRenderCreateMultiWithWrap() throws IOException {
    Text text = new Text( shell, SWT.MULTI | SWT.WRAP );

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( "rwt.widgets.Text", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "MULTI" ) );
    assertTrue( styles.contains( "WRAP" ) );
  }

  public void testRenderAlingment() throws Exception {
    Text text = new Text( shell, SWT.SINGLE | SWT.CENTER );
    Fixture.fakeResponseWriter();

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CENTER" ) );
  }

  public void testRenderCreateMultiWithScroll() throws IOException {
    Text text = new Text( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.fakeResponseWriter();

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "MULTI" ) );
    assertTrue( styles.contains( "H_SCROLL" ) );
    assertTrue( styles.contains( "V_SCROLL" ) );
    assertFalse( styles.contains( "ICON_CANCEL" ) );
    assertFalse( styles.contains( "ICON_SEARCH" ) );
  }

  public void testRenderCreateSearchWithIcons() throws IOException {
    Text text = new Text( shell, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH );
    Fixture.fakeResponseWriter();

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "SEARCH" ) );
    assertTrue( styles.contains( "ICON_CANCEL" ) );
    assertTrue( styles.contains( "ICON_SEARCH" ) );
    assertFalse( styles.contains( "H_SCROLL" ) );
    assertFalse( styles.contains( "V_SCROLL" ) );
  }

  public void testRenderParent() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( WidgetUtil.getId( text.getParent() ), operation.getParent() );
  }

  public void testRenderInitialMessage() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  public void testRenderMessage() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setMessage( "test" );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "message" ) );
  }

  public void testRenderMessageUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setMessage( "test" );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  public void testRenderPasswordEchoChar() throws IOException {
    Text text = new Text( shell, SWT.PASSWORD );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "?", message.findSetProperty( text, "echoChar" ) );
  }

  public void testRenderMultiEchoChar() throws IOException {
    Text text = new Text( shell, SWT.MULTI );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderInitialEchoChar() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderEchoChar() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setEchoChar( '*' );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "*", message.findSetProperty( text, "echoChar" ) );
  }

  public void testRenderEchoCharUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEchoChar( '*' );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderInitialEditable() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  public void testRenderEditable() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setEditable( false );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( text, "editable" ) );
  }

  public void testRenderEditableUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEditable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  public void testRenderSelection() throws IOException, JSONException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );

    text.setSelection( 1, 3 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( text, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 3 ]", actual ) );
  }

  public void testRenderSelectionAfterTextChange() throws IOException, JSONException {
    // See bug 376957
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );
    text.selectAll();
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.setText( "bar foo" );
    text.selectAll();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( text, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 0, 7 ]", actual ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setSelection( 1, 3 );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  public void testRenderInitialTextLimit() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "textLimit" ) );
  }

  public void testRenderTextLimit() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setTextLimit( 10 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( text, "textLimit" ) );
  }

  public void testRenderTextLimitUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "textLimit" ) );
  }

  public void testRenderTextLimitReset() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    text.setTextLimit( Text.LIMIT );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( text, "textLimit" ) );
  }

  public void testRenderTextLimitResetWithNegative() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    text.setTextLimit( -5 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( text, "textLimit" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( text, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    SelectionListener listener = new SelectionAdapter() { };
    text.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeSelectionListener( listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "selection" ) );
  }

  public void testRenderAddModifyListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( text, "modify" ) );
  }

  public void testRenderRemoveModifyListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    };
    text.addModifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeModifyListener( listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "modify" ) );
  }

  public void testRenderModifyListenerUnchanged() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "modify" ) );
  }

  public void testRenderAddVerifyListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( text, "verify" ) );
  }

  public void testRenderRemoveVerifyListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    VerifyListener listener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    };
    text.addVerifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeVerifyListener( listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "verify" ) );
  }

  public void testRenderVerifyListenerUnchanged() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "verify" ) );
  }

  public void testRenderInitialText() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
  }

  public void testRenderText() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setText( "test" );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
  }

  private static Object getPreserved( Text text, String property ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    return adapter.getPreserved( property );
  }

  private static ModifyListener createModifyListener() {
    return new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    };
  }
}
