/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.*;

public class TextLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private TextLCA textLCA;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeResponseWriter();
    textLCA = new TextLCA();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
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
    //Selection_Listener
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    String propSelectionLsnr = TextLCAUtil.PROP_SELECTION_LISTENER;
    Boolean hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    text.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.TRUE, hasListeners );
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
    final StringBuffer log = new StringBuffer();
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
    final StringBuffer log = new StringBuffer();
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

  public void testSelectionWithVerifyEvent() {
    final java.util.List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    // register preserve-values phase-listener
    final Text text = new Text( shell, SWT.NONE );
    shell.open();
    String textId = WidgetUtil.getId( text );
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    VerifyListener emptyVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    };
    text.addVerifyListener( emptyVerifyListener );
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
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verify me", text.getText() );
    text.removeVerifyListener( emptyVerifyListener );
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    text.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    text.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verified", text.getText() );
    text.removeVerifyListener( alteringVerifyListener );
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    text.setText( "" );
    alteringVerifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    };
    text.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getText() );
    text.removeVerifyListener( alteringVerifyListener );
  }

  public void testPreserveText() {
    Text text = new Text( shell, SWT.SINGLE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( text );
    Fixture.fakeNewRequest();
    String textId = WidgetUtil.getId( text );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
  }

  public void testVerifyAndModifyEvent() {
    final java.util.List<TypedEvent> log = new ArrayList<TypedEvent>();
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
    //verify modify-Listeners
    Fixture.preserveWidgets();
    Boolean hasModifyListener = ( Boolean )getPreserved( text, TextLCAUtil.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasModifyListener );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    hasModifyListener = ( Boolean )getPreserved( text, TextLCAUtil.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasModifyListener );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    Boolean hasVerifyListener = ( Boolean )getPreserved( text, TextLCAUtil.PROP_VERIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasVerifyListener );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    hasVerifyListener = ( Boolean )getPreserved( text, TextLCAUtil.PROP_VERIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasVerifyListener );
    Fixture.clearPreserved();
    //Bounds
    Rectangle rectangle = new Rectangle( 10, 10, 200, 100 );
    text.setBounds( rectangle );
    Fixture.preserveWidgets();
    assertEquals( rectangle, getPreserved( text, Props.BOUNDS ) );
    Fixture.clearPreserved();
    //control_listeners
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  getPreserved( text, Props.CONTROL_LISTENERS ) );
    Fixture.clearPreserved();
    text.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.CONTROL_LISTENERS ) );
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

  public void testPreserveModifyListener() {
    Fixture.markInitialized( display );
    Text text = new Text( shell, SWT.SINGLE );
    text.addModifyListener( createModifyListener() );
    Fixture.preserveWidgets();
    Object preserved = getPreserved( text, TextLCAUtil.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, preserved );
  }

  public void testPreserveModifyListenerWhenReadOnly() {
    Fixture.markInitialized( display );
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );
    Fixture.preserveWidgets();
    Object preserved = getPreserved( text, TextLCAUtil.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, preserved );
  }

  public void testWriteModifyListenerWhenReadOnly() throws IOException {
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( text, "modify" ) );
  }

  public void testRenderCreate() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( "rwt.widgets.Text", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SINGLE" ) );
  }

  public void testRenderCreateMultiWithWrap() throws IOException {
    Text text = new Text( shell, SWT.MULTI | SWT.WRAP );

    textLCA.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( "rwt.widgets.Text", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

  public void testRenderAlingment() throws Exception {
    Text text = new Text( shell, SWT.SINGLE | SWT.CENTER );
    Fixture.fakeResponseWriter();

    textLCA.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CENTER" ) );
  }

  public void testRenderParent() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( WidgetUtil.getId( text.getParent() ), operation.getParent() );
  }

  public void testRenderInitialMessage() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  public void testRenderMessage() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setMessage( "test" );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "message" ) );
  }

  public void testRenderMessageUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setMessage( "test" );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  public void testRenderPasswordEchoChar() throws IOException {
    Text text = new Text( shell, SWT.PASSWORD );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "?", message.findSetProperty( text, "echoChar" ) );
  }

  public void testRenderMultiEchoChar() throws IOException {
    Text text = new Text( shell, SWT.MULTI );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderInitialEchoChar() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderEchoChar() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setEchoChar( '*' );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "*", message.findSetProperty( text, "echoChar" ) );
  }

  public void testRenderEchoCharUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEchoChar( '*' );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  public void testRenderInitialEditable() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  public void testRenderEditable() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setEditable( false );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( text, "editable" ) );
  }

  public void testRenderEditableUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEditable( false );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  public void testRenderSelection() throws IOException, JSONException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );

    text.setSelection( 1, 3 );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( text, "selection" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 3 ]", actual ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setSelection( 1, 3 );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  public void testRenderInitialTextLimit() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "textLimit" ) );
  }

  public void testRenderTextLimit() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setTextLimit( 10 );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( text, "textLimit" ) );
  }

  public void testRenderTextLimitUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

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
    textLCA.renderChanges( text );

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
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( text, "textLimit" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addSelectionListener( new SelectionAdapter() { } );
    textLCA.renderChanges( text );

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
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "selection" ) );
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
    textLCA.renderChanges( text );

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
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "modify" ) );
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
    textLCA.renderChanges( text );

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
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( text, "verify" ) );
  }

  public void testRenderInitialText() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
  }

  public void testRenderText() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );

    text.setText( "test" );
    textLCA.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setText( "foo" );
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );

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
