/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.sashkit;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SashLCA_Test {

  private Display display;
  private Shell shell;
  private Sash sash;
  private SashLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    sash = new Sash( shell, SWT.NONE );
    lca = new SashLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( sash );
    ControlLCATestUtil.testFocusListener( sash );
    ControlLCATestUtil.testMouseListener( sash );
    ControlLCATestUtil.testKeyListener( sash );
    ControlLCATestUtil.testTraverseListener( sash );
    ControlLCATestUtil.testMenuDetectListener( sash );
    ControlLCATestUtil.testHelpListener( sash );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    //control: enabled
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( true );
    //visible
    sash.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    sash.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( sash );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sash.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    sash.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = new Color( display, 122, 33, 203 );
    sash.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    sash.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    sash.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, sash.getToolTipText() );
    Fixture.clearPreserved();
    sash.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( "some text", sash.getToolTipText() );
    Fixture.clearPreserved();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    assertEquals( "rwt.widgets.Sash", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( sash );
    lca.renderInitialization( sash );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof SashOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    SashOperationHandler handler = spy( new SashOperationHandler( sash ) );
    getRemoteObject( getId( sash ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( sash ), "Help", new JsonObject() );
    lca.readData( sash );

    verify( handler ).handleNotifyHelp( sash, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    assertEquals( WidgetUtil.getId( sash.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderCreateWithHorizontal() throws IOException {
    sash = new Sash( shell, SWT.HORIZONTAL );

    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sash );
    Fixture.preserveWidgets();

    sash.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( sash );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( sash, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    sash.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sash );
    Fixture.preserveWidgets();

    sash.removeListener( SWT.Selection, listener );
    lca.renderChanges( sash );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( sash, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sash );
    Fixture.preserveWidgets();

    sash.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( sash );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( sash, "Selection" ) );
  }

}
