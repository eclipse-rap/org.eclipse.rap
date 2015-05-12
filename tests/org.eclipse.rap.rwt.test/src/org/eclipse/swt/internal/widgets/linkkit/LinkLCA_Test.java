/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.linkkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
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
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LinkLCA_Test {

  private Display display;
  private Shell shell;
  private LinkLCA lca;
  private Link link;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    link = new Link( shell, SWT.NONE );
    lca = new LinkLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( link );
    ControlLCATestUtil.testFocusListener( link );
    ControlLCATestUtil.testMouseListener( link );
    ControlLCATestUtil.testKeyListener( link );
    ControlLCATestUtil.testTraverseListener( link );
    ControlLCATestUtil.testMenuDetectListener( link );
    ControlLCATestUtil.testHelpListener( link );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( link );
    assertEquals( "", adapter.getPreserved( LinkLCA.PROP_TEXT ) );
    Fixture.clearPreserved();
    link.setText( "some text" );
    link.addSelectionListener( new SelectionAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", adapter.getPreserved( LinkLCA.PROP_TEXT ) );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    link.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    link.setEnabled( true );
    // visible
    link.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    link.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( link );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    link.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    link.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    link.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    link.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    link.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, link.getToolTipText() );
    Fixture.clearPreserved();
    link.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", link.getToolTipText() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( link );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( "rwt.widgets.Link", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( link );
    lca.renderInitialization( link );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof LinkOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    LinkOperationHandler handler = spy( new LinkOperationHandler( link ) );
    getRemoteObject( getId( link ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( link ), "Help", new JsonObject() );
    lca.readData( link );

    verify( handler ).handleNotifyHelp( link, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( link );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( getId( link.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( link );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    link.setText( "foo <a>123</a> bar" );
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray text = ( JsonArray )message.findSetProperty( link, "text" );
    assertEquals( 3, text.size() );
    JsonArray segment1 = text.get( 0 ).asArray();
    JsonArray segment2 = text.get( 1 ).asArray();
    JsonArray segment3 = text.get( 2 ).asArray();
    assertEquals( "foo ", segment1.get( 0 ).asString() );
    assertEquals( JsonObject.NULL, segment1.get( 1 ) );
    assertEquals( "123", segment2.get( 0 ).asString() );
    assertEquals( 0, segment2.get( 1 ).asInt() );
    assertEquals( " bar", segment3.get( 0 ).asString() );
    assertEquals( JsonObject.NULL, segment3.get( 1 ) );
  }

  @Test
  public void testRenderTextEmpty() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    link.setText( "foo bar" );

    Fixture.preserveWidgets();
    link.setText( "" );
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray text = ( JsonArray )message.findSetProperty( link, "text" );
    assertEquals( 0, text.size() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );

    link.setText( "foo <a>123</a> bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( link, "text" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( link, "Selection" ) );
    assertNull( message.findListenOperation( link, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    link.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.removeListener( SWT.Selection, listener );
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( link, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( link, "Selection" ) );
  }

}
