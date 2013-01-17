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
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menukit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    lca = new MenuLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUnassignedMenuBar() throws IOException {
    String shellId = WidgetUtil.getId( shell );
    Menu menuBar = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menuBar );
    // Ensure that a menuBar that is not assigned to any shell (via setMenuBar)
    // is rendered but without settings its parent
    lca.renderChanges( menuBar );
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menuBar, "parent" ) );
    // The contrary: an assigned menuBar has to be rendered with setParent
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();
    shell.setMenuBar( menuBar );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertEquals( shellId, message.findSetProperty( menuBar, "parent" ) );
    // Un-assigning a menuBar must result in setParent( null ) being rendered
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();
    shell.setMenuBar( null );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( menuBar, "parent" ) );
  }

  @Test
  public void testRenderBoundsForMenuBar() throws JSONException {
    Menu menuBar = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( menuBar );
    shell.setMenuBar( menuBar );

    Integer[] param = new Integer[] {
      Integer.valueOf( 0 ),
      Integer.valueOf( 0 ),
      Integer.valueOf( 1234 ),
      Integer.valueOf( 4321 )
    };
    Fixture.fakeSetParameter( getId( shell ), "bounds", param );
    Fixture.executeLifeCycleFromServerThread( );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( menuBar, "bounds" );
    assertEquals( 1234, bounds.getInt( 2 ) );
    assertNotNull( message.findSetOperation( menuBar, "bounds" ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "BAR" ) );
  }

  @Test
  public void testRenderCreatePopUp() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "POP_UP" ) );
  }

  @Test
  public void testRenderCreateDropDown() throws IOException {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "DROP_DOWN" ) );
  }

  @Test
  public void testRenderCreatePopUp_NoRadioGroup() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP | SWT.NO_RADIO_GROUP );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "POP_UP" ) );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "NO_RADIO_GROUP" ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderDispose( menu );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( menu ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialParent() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "parent" ) == -1 );
  }

  @Test
  public void testRenderParent() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    shell.setMenuBar( menu );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( shell ), message.findSetProperty( menu, "parent" ) );
  }

  @Test
  public void testRenderParentUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    shell.setMenuBar( menu );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "parent" ) );
  }

  @Test
  public void testRenderInitialBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "bounds" ) == -1 );
  }

  @Test
  public void testRenderBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    shell.setMenuBar( menu );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "bounds" ) );
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

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menu, "bounds" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( menu, "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "customVariant" ) );
  }

  @Test
  public void testRenderInitialEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "enabled" ) == -1 );
  }

  @Test
  public void testRenderEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setEnabled( false );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( menu, "enabled" ) );
  }

  @Test
  public void testRenderEnabledUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "enabled" ) );
  }

  @Test
  public void testRenderAddMenuListener() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Show" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddMenuListener_MenuBar() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "Show" ) );
    assertNull( message.findListenOperation( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddMenuListener_DropDown() throws Exception {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( mock( MenuListener.class ) );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Show" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Hide" ) );
  }

  @Test
  public void testRenderAddMenuListener_ArmListener() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    item.addArmListener( mock( ArmListener.class ) );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Show" ) );
  }

  @Test
  public void testRenderRemoveMenuListener() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuListener listener = mock( MenuListener.class );
    menu.addMenuListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.removeMenuListener( listener );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( menu, "Show" ) );
    assertEquals( Boolean.FALSE, message.findListenProperty( menu, "Hide" ) );
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

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "Help" ) );
  }

  @Test
  public void testRenderRemoveHelpListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    HelpListener listener = mock ( HelpListener.class );
    menu.addHelpListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.removeHelpListener( listener );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( menu, "Help" ) );
  }

  @Test
  public void testRenderHelpListenerUnchanged() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addHelpListener( mock ( HelpListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "help" ) );
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

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "showMenu" );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "x" ) );
    assertEquals( Integer.valueOf( 2 ), operation.getProperty( "y" ) );
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

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "unhideItems" );
    assertEquals( Boolean.TRUE, operation.getProperty( "reveal" ) );
  }

  @Test
  public void testFireShowEvent() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Listener listener = mock( Listener.class );
    menu.addListener( SWT.Show, listener );

    Fixture.fakeNotifyOperation( getId( menu ), ClientMessageConst.EVENT_SHOW, null );
    Fixture.readDataAndProcessAction( menu );

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testFireHideEvent() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Listener listener = mock( Listener.class );
    menu.addListener( SWT.Hide, listener );

    Fixture.fakeNotifyOperation( getId( menu ), ClientMessageConst.EVENT_HIDE, null );
    Fixture.readDataAndProcessAction( menu );

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testFireArmEvent() {
    Menu menu = new Menu( shell, SWT.POP_UP );
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Listener listener = mock( Listener.class );
    item.addListener( SWT.Arm, listener );

    Fixture.fakeNotifyOperation( getId( menu ), ClientMessageConst.EVENT_SHOW, null );
    Fixture.readDataAndProcessAction( item );

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }
}
