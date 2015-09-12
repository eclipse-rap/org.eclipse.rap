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
package org.eclipse.swt.internal.widgets.menukit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.internal.widgets.shellkit.ShellOperationHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MenuLCA_Test {

  private Display display;
  private Shell shell;
  private MenuLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = MenuLCA.INSTANCE;
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderBoundsForMenuBar() {
    getRemoteObject( shell ).setHandler( new ShellOperationHandler( shell ) );
    Menu menuBar = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( menuBar );
    shell.setMenuBar( menuBar );

    JsonArray bounds = new JsonArray()
      .add( 0 )
      .add( 0 )
      .add( 1234 )
      .add( 4321 );
    Fixture.fakeSetProperty( getId( shell ), "bounds", bounds );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray result = message.findSetProperty( menuBar, "bounds" ).asArray();
    assertEquals( 1234, result.get( 2 ).asInt() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( getStyles( operation ).contains( "BAR" ) );
  }

  @Test
  public void testRenderCreatePopUp() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( getStyles( operation ).contains( "POP_UP" ) );
  }

  @Test
  public void testRenderCreateDropDown() throws IOException {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( getStyles( operation ).contains( "DROP_DOWN" ) );
  }

  @Test
  public void testRenderCreatePopUp_NoRadioGroup() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP | SWT.NO_RADIO_GROUP );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( getStyles( operation ).contains( "POP_UP" ) );
    assertTrue( getStyles( operation ).contains( "NO_RADIO_GROUP" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );
    String id = getId( menu );
    lca.renderInitialization( menu );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof MenuOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuOperationHandler handler = spy( new MenuOperationHandler( menu ) );
    getRemoteObject( getId( menu ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( menu ), "Help", new JsonObject() );
    lca.readData( menu );

    verify( handler ).handleNotifyHelp( menu );
  }

  @Test
  public void testRenderDispose() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderDispose( menu );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( menu ), operation.getTarget() );
  }

  @Test
  public void testRenderParent_bar() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( shell ), message.findCreateProperty( menu, "parent" ).asString() );
  }

  @Test
  public void testRenderParent_popup() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( shell ), message.findCreateProperty( menu, "parent" ).asString() );
  }

  @Test
  public void testRenderShowListener() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( menu, "Show" ) );
  }

  @Test
  public void testRenderShowListener_bar() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.renderInitialization( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
  }

  @Test
  public void testRenderInitialBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertFalse( operation.getProperties().names().contains( "bounds" ) );
  }

  @Test
  public void testRenderBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    shell.setMenuBar( menu );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menu, "bounds" ) );
  }

  @Test
  public void testRenderBoundsUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    shell.setMenuBar( menu );
    // Note: Menu bounds are preserved in ShellLCA#readData
    WidgetUtil.getLCA( shell ).readData( shell );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "bounds" ) );
  }

  @Test
  public void testRenderBoundsAfterRemoveMenubar() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    shell.setMenuBar( menu );

    // Note: Menu bounds are preserved in ShellLCA#readData
    WidgetUtil.getLCA( shell ).readData( shell );
    shell.setMenuBar( null );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = message.findSetProperty( menu, "bounds" ).asArray();
    assertEquals( new JsonArray().add( 0 ).add( 0 ).add( 0 ).add( 0 ), bounds );
  }

  @Test
  public void testRenderBoundsAfterShellBoundsChange() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    shell.setMenuBar( menu );

    // Note: Menu bounds are preserved in ShellLCA#readData
    WidgetUtil.getLCA( shell ).readData( shell );
    shell.setBounds( 1, 2, 3, 4 );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menu, "bounds" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( menu, "customVariant" ).asString() );
  }

  @Test
  public void testRenderInitialEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertFalse( operation.getProperties().names().contains( "enabled" ) );
  }

  @Test
  public void testRenderEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setEnabled( false );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( menu, "enabled" ) );
  }

  @Test
  public void testRenderEnabledUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "enabled" ) );
  }

  @Test
  public void testRenderDirection_onBar() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR | SWT.RIGHT_TO_LEFT );

    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "rtl", message.findSetProperty( menu, "direction" ).asString() );
  }

  @Test
  public void testRenderInitialOrientation() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.render( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertFalse( operation.getProperties().names().contains( "direction" ) );
  }

  @Test
  public void testRenderOrientation() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    menu.setOrientation( SWT.RIGHT_TO_LEFT );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "rtl", message.findSetProperty( menu, "direction" ).asString() );
  }

  @Test
  public void testRenderOrientationUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setOrientation( SWT.RIGHT_TO_LEFT );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "direction" ) );
  }

  @Test
  public void testRenderListen_Menu() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( menu );
    Fixture.clearPreserved();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddMenuListener_MenuBar() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.clearPreserved();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertNull( message.findListenOperation( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddMenuListener_DropDown() throws Exception {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );
    Fixture.markInitialized( menu );
    Fixture.clearPreserved();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( menu, "Hide" ) );
  }

  @Test
  public void testRenderRemoveMenuListener() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuListener listener = mock( MenuListener.class );
    menu.addMenuListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.clearPreserved();

    menu.removeMenuListener( listener );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( menu, "Hide" ) );
  }

  @Test
  public void testRenderMenuListenerUnchanged() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( mock( MenuListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertNull( message.findListenOperation( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddHelpListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addHelpListener( mock ( HelpListener.class ) );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( menu, "Help" ) );
  }

  @Test
  public void testRenderRemoveHelpListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Listener listener = mock ( Listener.class );
    menu.addListener( SWT.Help, listener );
    Fixture.markInitialized( menu );
    Fixture.clearPreserved();

    menu.removeListener( SWT.Help, listener );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( menu, "Help" ) );
  }

  @Test
  public void testRenderShowMenu() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.setLocation( 1, 2 );
    menu.setVisible( true );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "showMenu" );
    assertEquals( 1, operation.getParameters().get( "x" ).asInt() );
    assertEquals( 2, operation.getParameters().get( "y" ).asInt() );
  }

  @Test
  public void testRenderUnhideItems() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    new MenuItem( menu, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    Fixture.fakeNotifyOperation( getId( menu ), ClientMessageConst.EVENT_SHOW, null );
    lca.renderChanges( menu );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "unhideItems" );
    assertEquals( JsonValue.TRUE, operation.getParameters().get( "reveal" ) );
  }

}
