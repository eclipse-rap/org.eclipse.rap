/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tabfolderkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TabFolderLCA_Test {

  private Display display;
  private Shell shell;
  private TabFolder folder;
  private TabFolderLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    folder = new TabFolder( shell, SWT.NONE );
    lca = new TabFolderLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( folder );
    ControlLCATestUtil.testFocusListener( folder );
    ControlLCATestUtil.testMouseListener( folder );
    ControlLCATestUtil.testKeyListener( folder );
    ControlLCATestUtil.testTraverseListener( folder );
    ControlLCATestUtil.testMenuDetectListener( folder );
    ControlLCATestUtil.testHelpListener( folder );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    //control: enabled
    Fixture.preserveWidgets();
    WidgetAdapter adapter = getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( true );
    //visible
    folder.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    folder.setVisible( false );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( folder );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    folder.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    folder.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = new Color( display, 122, 33, 203 );
    folder.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    folder.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    folder.setFont( font );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( null, folder.getToolTipText() );
    Fixture.clearPreserved();
    folder.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( "some text", folder.getToolTipText() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.TabFolder", operation.getType() );
    assertTrue( getStyles( operation ).contains( "TOP" ) );
  }

  @Test
  public void testRenderCreateOnBottom() throws IOException {
    folder = new TabFolder( shell, SWT.BOTTOM );

    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.TabFolder", operation.getType() );
    assertTrue( getStyles( operation ).contains( "BOTTOM" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( folder );
    lca.renderInitialization( folder );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TabFolderOperationHandler );
  }

  @Test
  public void testRenderInitialization_rendersSelectionListener() throws Exception {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( folder, "Selection" ) );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    TabFolderOperationHandler handler = spy( new TabFolderOperationHandler( folder ) );
    getRemoteObject( getId( folder ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( folder ), "Help", new JsonObject() );
    lca.readData( folder );

    verify( handler ).handleNotifyHelp( folder, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( getId( folder.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderInitialSelectionWithoutItems() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selection" ) );
  }

  @Test
  public void testRenderInitialSelectionWithItems() throws IOException {
    TabItem item = new TabItem( folder, SWT.NONE );
    new TabItem( folder, SWT.NONE );
    new TabItem( folder, SWT.NONE );

    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item ), message.findSetProperty( folder, "selection" ).asString() );
  }

  @Test
  public void testRenderSelection() throws IOException {
    new TabItem( folder, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    new TabItem( folder, SWT.NONE );

    folder.setSelection( 1 );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item ), message.findSetProperty( folder, "selection" ).asString() );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    new TabItem( folder, SWT.NONE );
    new TabItem( folder, SWT.NONE );
    new TabItem( folder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelection( 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selection" ) );
  }

  @Test
  public void testResetSelectionInSelectionEvent() {
    getRemoteObject( folder ).setHandler( new TabFolderOperationHandler( folder ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    final TabItem item1 = new TabItem( folder, SWT.NONE );
    TabItem item2 = new TabItem( folder, SWT.NONE );
    folder.setSelection( item1 );
    folder.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        folder.setSelection( item1 );
      }
    } );

    fakeWidgetSelected( folder, item2 );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item1 ), message.findSetProperty( folder, "selection" ).asString() );
  }

  private void fakeWidgetSelected( TabFolder folder, TabItem item ) {
    Fixture.fakeSetProperty( getId( folder ), "selection", getId( item ) );
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( folder ), ClientMessageConst.EVENT_SELECTION, parameters );
  }

}
