/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TextLCA_Test {

  private Display display;
  private Shell shell;
  private TextLCA lca;
  private Text text;

  @Before
  public void setUp() {
    Fixture.setUp();
    lca = new TextLCA();
    display = new Display();
    shell = new Shell( display );
    text = new Text( shell, SWT.NONE );
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( text );
    ControlLCATestUtil.testFocusListener( text );
    ControlLCATestUtil.testMouseListener( text );
    ControlLCATestUtil.testKeyListener( text );
    ControlLCATestUtil.testTraverseListener( text );
    ControlLCATestUtil.testMenuDetectListener( text );
    ControlLCATestUtil.testHelpListener( text );
  }

  @Test
  public void testMultiPreserveValues() {
    Text text = new Text( shell, SWT.MULTI );
    testPreserveValues( text );
  }

  @Test
  public void testPasswordPreserveValues() {
    Text text = new Text( shell, SWT.PASSWORD );
    testPreserveValues( text );
  }

  @Test
  public void testSinglePreserveValues() {
    Text text = new Text( shell, SWT.SINGLE );
    testPreserveValues( text );
  }

  @Test
  public void testPreserveText() {
    getRemoteObject( getId( text ) ).setHandler( new TextOperationHandler( text ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.fakeSetProperty( getId( text ), "text", "verify me" );
    Fixture.fakeSetProperty( getId( text ), "selectionStart", 1 );
    Fixture.fakeSetProperty( getId( text ), "selectionLength", 0 );

    Fixture.executeLifeCycleFromServerThread();

    // ensure that no text and selection values are sent back to the client
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  private static void testPreserveValues( Text text ) {
    //text
    text.setText( "some text" );
    Fixture.markInitialized( text.getDisplay() );
    Fixture.preserveWidgets();
    assertEquals( text.getText(), getPreserved( text, Props.TEXT ) );
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
  }

  @Test
  public void testWriteModifyListenerWhenReadOnly() throws IOException {
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addListener( SWT.Modify, mock( Listener.class ) );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( text, "Modify" ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( "rwt.widgets.Text", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SINGLE" ) );
  }

  @Test
  public void testRenderCreate_setsOperationHandler() throws IOException {
    String id = getId( text );
    lca.renderInitialization( text );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TextOperationHandler );
  }

  @Test
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

  @Test
  public void testRenderAlingment() throws Exception {
    Text text = new Text( shell, SWT.SINGLE | SWT.CENTER );
    Fixture.fakeResponseWriter();

    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CENTER" ) );
  }

  @Test
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

  @Test
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

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( text );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( text );
    assertEquals( WidgetUtil.getId( text.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialMessage() throws IOException {
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  @Test
  public void testRenderMessage() throws IOException {
    text.setMessage( "test" );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "message" ).asString() );
  }

  @Test
  public void testRenderMessageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setMessage( "test" );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "message" ) );
  }

  @Test
  public void testRenderPasswordEchoChar() throws IOException {
    Text text = new Text( shell, SWT.PASSWORD );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "?", message.findSetProperty( text, "echoChar" ).asString() );
  }

  @Test
  public void testRenderMultiEchoChar() throws IOException {
    Text text = new Text( shell, SWT.MULTI );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  @Test
  public void testRenderInitialEchoChar() throws IOException {
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  @Test
  public void testRenderEchoChar() throws IOException {
    text.setEchoChar( '*' );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "*", message.findSetProperty( text, "echoChar" ).asString() );
  }

  @Test
  public void testRenderEchoCharUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEchoChar( '*' );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "echoChar" ) );
  }

  @Test
  public void testRenderInitialEditable() throws IOException {
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  @Test
  public void testRenderEditable() throws IOException {
    text.setEditable( false );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( text, "editable" ) );
  }

  @Test
  public void testRenderEditableUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setEditable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "editable" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    text.setText( "foo bar" );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    text.setText( "foo bar" );

    text.setSelection( 1, 3 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 1, 3 ]" );
    assertEquals( expected, message.findSetProperty( text, "selection" ) );
  }

  @Test
  public void testRenderSelectionAfterTextChange() throws IOException {
    // See bug 376957
    text.setText( "foo bar" );
    text.selectAll();
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.setText( "bar foo" );
    text.selectAll();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 0, 7 ]" );
    assertEquals( expected, message.findSetProperty( text, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    text.setText( "foo bar" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setSelection( 1, 3 );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "selection" ) );
  }

  @Test
  public void testRenderInitialTextLimit() throws IOException {
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimit() throws IOException {
    text.setTextLimit( 10 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( text, "textLimit" ).asInt() );
  }

  @Test
  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    text.setTextLimit( Text.LIMIT );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( text, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitResetWithNegative() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setTextLimit( 10 );
    Fixture.preserveWidgets();
    text.setTextLimit( -5 );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( text, "textLimit" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( text, "DefaultSelection" ) );
    assertNull( message.findListenOperation( text, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    text.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( text, "DefaultSelection" ) );
    assertNull( message.findListenOperation( text, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "Selection" ) );
  }

  @Test
  public void testRenderAddModifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addListener( SWT.Modify, mock( Listener.class ) );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( text, "Modify" ) );
  }

  @Test
  public void testRenderRemoveModifyListener() throws Exception {
    Listener listener = mock( Listener.class );
    text.addListener( SWT.Modify, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeListener( SWT.Modify, listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( text, "Modify" ) );
  }

  @Test
  public void testRenderModifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addListener( SWT.Modify, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "Modify" ) );
  }

  @Test
  public void testRenderAddVerifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addListener( SWT.Verify, mock( Listener.class ) );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( text, "Modify" ) );
  }

  @Test
  public void testRenderVerifyModifyListener() throws Exception {
    Listener listener = mock( Listener.class );
    text.addListener( SWT.Verify, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.removeListener( SWT.Verify, listener );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( text, "Modify" ) );
  }

  @Test
  public void testRenderVerifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();

    text.addListener( SWT.Verify, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( text, "Modify" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    text.setText( "test" );
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( text, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );

    text.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( text, "text" ) );
  }

  @Test
  public void testRenderChanges_rendersClientListener() throws IOException {
    text.addListener( SWT.Paint, new ClientListener( "" ) );

    lca.renderChanges( text );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( text, "addListener" ) );
  }

  private static Object getPreserved( Text text, String property ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( text );
    return adapter.getPreserved( property );
  }

}
