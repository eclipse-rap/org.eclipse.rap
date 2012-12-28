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
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_ACTIVATE;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class ShellLCA_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    Shell shell = new Shell( display, SWT.NONE );
    ControlLCATestUtil.testFocusListener( shell );
    ControlLCATestUtil.testMouseListener( shell );
    ControlLCATestUtil.testKeyListener( shell );
    ControlLCATestUtil.testTraverseListener( shell );
    ControlLCATestUtil.testMenuDetectListener( shell );
    ControlLCATestUtil.testHelpListener( shell );
  }

  @Test
  public void testPreserveValues() {
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_MODE ) );
    assertEquals( new Point( 80, 0 ), adapter.getPreserved( ShellLCA.PROP_MINIMUM_SIZE ) );
    Fixture.clearPreserved();
    shell.setText( "some text" );
    shell.open();
    shell.setActive();
    IShellAdapter shellAdapter
     = shell.getAdapter( IShellAdapter.class );
    shellAdapter.setActiveControl( button );
    shell.addShellListener( new ShellAdapter() { } );
    shell.setMaximized( true );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setImage( image );
    shell.setMinimumSize( 100, 100 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( image, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( button, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( shell, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( "maximized", adapter.getPreserved( ShellLCA.PROP_MODE ) );
    assertEquals( new Point( 100, 100 ), adapter.getPreserved( ShellLCA.PROP_MINIMUM_SIZE ) );
    Fixture.clearPreserved();
    //control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    shell.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    shell.setEnabled( true );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    shell.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( shell );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    shell.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 100, 150 );
    shell.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    shell.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    shell.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    shell.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, shell.getToolTipText() );
    Fixture.clearPreserved();
    shell.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( "some text", shell.getToolTipText() );
  }

  @Test
  public void testReadDataForClosed() {
    shell.open();
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_CLOSE, null );
    Fixture.readDataAndProcessAction( shell );

    verify( listener ).shellClosed( any( ShellEvent.class ) );
  }

  @Test
  public void testReadDataForActiveControl() {
    Label label = new Label( shell, SWT.NONE );
    Label otherLabel = new Label( shell, SWT.NONE );
    setActiveControl( shell, otherLabel );

    Fixture.fakeSetParameter( getId( shell ), "activeControl", getId( label ) );
    Fixture.readDataAndProcessAction( display );

    assertSame( label, getActiveControl( shell ) );
  }

  @Test
  public void testReadDataForMode_Maximixed() {
    shell.open();

    Fixture.fakeSetParameter( getId( shell ), "mode", "maximized" );
    Fixture.readDataAndProcessAction( shell );

    assertTrue( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
  }

  @Test
  public void testReadDataForMode_Minimixed() {
    shell.open();

    Fixture.fakeSetParameter( getId( shell ), "mode", "minimized" );
    Fixture.readDataAndProcessAction( shell );

    assertFalse( shell.getMaximized() );
    assertTrue( shell.getMinimized() );
  }

  @Test
  public void testReadDataForMode_Restore() {
    shell.open();
    shell.setMaximized( true );

    Fixture.fakeSetParameter( getId( shell ), "mode", "null" );
    Fixture.readDataAndProcessAction( shell );

    assertFalse( shell.getMaximized() );
  }

  @Test
  public void testReadModeBoundsOrder_Maximize() {
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.open();

    fakeModeAndBounds( "maximized", 0, 0, 800, 600 );
    Fixture.readDataAndProcessAction( shell );

    assertEquals( displayBounds, shell.getBounds() );
  }

  @Test
  public void testReadModeBoundsOrder_Restore() {
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.setMaximized( true );
    shell.open();

    fakeModeAndBounds( "null", 10, 10, 100, 100 );
    Fixture.readDataAndProcessAction( shell );

    assertEquals( shellBounds, shell.getBounds() );
  }

  @Test
  public void testUntypedActivateEvent() {
    shell.open();
    Shell otherShell = new Shell( display );
    otherShell.open();
    Listener listener = mock( Listener.class );
    shell.addListener( SWT.Activate, listener );

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_ACTIVATE, null );
    Fixture.readDataAndProcessAction( display );

    verify( listener ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testTypedActivateEvent() {
    shell.open();
    Shell otherShell = new Shell( display );
    otherShell.open();
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );

    Fixture.fakeNewRequest();
    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_ACTIVATE, null );
    Fixture.readDataAndProcessAction( display );

    verify( listener, times( 1 ) ).shellActivated( any( ShellEvent.class ) );
  }

  @Test
  public void testLatestOpenedShellIsActive() {
    shell.open();
    Shell secondShell = new Shell( display );
    secondShell.open();

    assertSame( secondShell, display.getActiveShell() );
  }

  @Test
  public void testShellActivateWithoutEventListeners() {
    Shell shellToActivate = new Shell( display );
    shellToActivate.setData( "shellToActivate" );
    shellToActivate.open();
    Shell activeShell = new Shell( display );
    activeShell.setData( "activeShell" );
    activeShell.open();
    activeShell.setActive();

    Fixture.fakeNotifyOperation( getId( shellToActivate ), ClientMessageConst.EVENT_ACTIVATE, null );
    Fixture.executeLifeCycleFromServerThread();

    assertSame( shellToActivate, display.getActiveShell() );
  }

  @Test
  public void testShellActivate() {
    final StringBuilder activateEventLog = new StringBuilder();
    Listener activateListener = new Listener() {
      public void handleEvent( Event event ) {
        if( event.type == SWT.Activate ) {
          activateEventLog.append( "activated:" + ( ( Shell )event.widget ).getText() + "|" );
        } else {
          activateEventLog.append( "deactivated:" + ( ( Shell )event.widget ).getText() + "|" );
        }
      }
    };
    final StringBuilder shellEventLog = new StringBuilder();
    ShellListener shellListener = new ShellAdapter() {
      @Override
      public void shellActivated( ShellEvent event ) {
        shellEventLog.append( "activated:" + ( ( Shell )event.widget ).getText() + "|" );
      }
      @Override
      public void shellDeactivated( ShellEvent event ) {
        shellEventLog.append( "deactivated:" + ( ( Shell )event.widget ).getText() + "|" );
      }
    };
    Shell shellToActivate = new Shell( display, SWT.NONE );
    shellToActivate.setText( "shellToActivate" );
    shellToActivate.open();
    Shell activeShell = new Shell( display, SWT.NONE );
    activeShell.setText( "activeShell" );
    activeShell.open();
    activeShell.setActive();
    shellToActivate.addListener( SWT.Activate, activateListener );
    shellToActivate.addListener( SWT.Deactivate, activateListener );
    activeShell.addListener( SWT.Activate, activateListener );
    activeShell.addListener( SWT.Deactivate, activateListener );
    shellToActivate.addShellListener( shellListener );
    activeShell.addShellListener( shellListener );
    activateEventLog.setLength( 0 );
    shellEventLog.setLength( 0 );

    Fixture.markInitialized( display );
    Fixture.markInitialized( activeShell );
    Fixture.markInitialized( shellToActivate );

    Fixture.fakeNotifyOperation( getId( shellToActivate ), EVENT_ACTIVATE, null );
    Fixture.executeLifeCycleFromServerThread();

    assertSame( shellToActivate, display.getActiveShell() );
    String expected = "deactivated:activeShell|activated:shellToActivate|";
    assertEquals( expected, activateEventLog.toString() );
    assertEquals( expected, shellEventLog.toString() );
    // Ensure that no setActive javaScript code is rendered for client-side activated Shell
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shellToActivate, "active" ) );
  }

  @Test
  public void testNoDeactivateNullActiveShell() {
    // no deactivation event must be created for a null active shell (NPE)
    shell.setVisible( true );
    Shell shell2 = new Shell( display );
    shell2.setVisible( true );

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_ACTIVATE, null );
    Fixture.readDataAndProcessAction( display );

    assertSame( shell, display.getActiveShell() );
  }

  @Test
  public void testDisposeSingleShell() {
    shell.open();

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_CLOSE, null );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 0, display.getShells().length );
    assertEquals( null, display.getActiveShell() );
    assertTrue( shell.isDisposed() );
  }

  @Test
  public void testAlpha() throws Exception {
    shell.open();
    ShellLCA lca = new ShellLCA();

    shell.setAlpha( 23 );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 23 ), message.findSetProperty( shell, "alpha" ) );
  }

  @Test
  public void testRenderMode() throws Exception {
    shell.open();
    ShellLCA lca = new ShellLCA();

    shell.setMaximized( true );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "maximized", message.findSetProperty( shell, "mode" ) );
  }

  @Test
  public void testRenderFullscreen() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    shell.open();
    ShellLCA lca = new ShellLCA();
    shell.setFullScreen( true );
    Fixture.preserveWidgets();

    shell.setFullScreen( false );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( shell, "mode" ) );
  }

  @Test
  public void testResetFullscreen() throws Exception {
    shell.open();
    ShellLCA lca = new ShellLCA();

    shell.setFullScreen( true );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "fullscreen", message.findSetProperty( shell, "mode" ) );
  }

  @Test
  public void testRenderDefaultButtonIntiallyNull() throws IOException {
    ShellLCA lca = new ShellLCA();

    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "defaultButton" ) );
  }

  @Test
  public void testRenderDefaultButtonInitiallySet() throws Exception {
    ShellLCA lca = new ShellLCA();
    Button button = new Button( shell, SWT.PUSH );

    shell.setDefaultButton( button );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( button ), message.findSetProperty( shell, "defaultButton" ) );
  }

  @Test
  public void testRenderDefaultButtonUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    ShellLCA lca = new ShellLCA();
    shell.setDefaultButton( new Button( shell, SWT.PUSH ) );

    Fixture.preserveWidgets();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "defaultButton" ) );
  }

  @Test
  public void testResetDefaultButton() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    ShellLCA lca = new ShellLCA();
    Button button = new Button( shell, SWT.PUSH );
    shell.setDefaultButton( button );
    Fixture.preserveWidgets();

    button.dispose();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( shell, "defaultButton" ) );
  }

  @Test
  public void testResetDefaultButton_AfterFocusControlChange() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Button button = new Button( shell, SWT.PUSH );
    Text text = new Text( shell, SWT.NONE );

    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( button ) );
    Fixture.executeLifeCycleFromServerThread();

    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( text ) );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( shell, "defaultButton" ) );
  }

  @Test
  public void testRenderActiveControlIntiallyNull() throws IOException {
    ShellLCA lca = new ShellLCA();

    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "activeControl" ) );
  }

  @Test
  public void testRenderActiveControlInitiallySet() throws Exception {
    ShellLCA lca = new ShellLCA();
    Button button = new Button( shell, SWT.PUSH );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );

    adapter.setActiveControl( button );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( button ), message.findSetProperty( shell, "activeControl" ) );
  }

  @Test
  public void testRenderActiveControlUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Button button = new Button( shell, SWT.PUSH );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    adapter.setActiveControl( button );
    ShellLCA lca = new ShellLCA();

    Fixture.preserveWidgets();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "activeControl" ) );
  }

  @Test
  public void testResetActiveControl() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Button button = new Button( shell, SWT.PUSH );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    adapter.setActiveControl( button );
    ShellLCA lca = new ShellLCA();
    Fixture.preserveWidgets();

    adapter.setActiveControl( null );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( shell, "activeControl" ) );
  }

  @Test
  public void testRenderInitialBounds() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ControlLCAUtil.renderBounds( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "bounds" ) );
  }

  @Test
  public void testRenderMinimumSize() throws Exception {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    shell.setMinimumSize( 100, 200 );
    ShellLCA lca = new ShellLCA();

    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "[100,200]", message.findSetProperty( shell, "minimumSize" ).toString() );
  }

  @Test
  public void testRenderStyleFlags() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    lca.renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( shell );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "NO_TRIM" ) );
  }

  @Test
  public void testRenderText() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setText( "foo" );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( shell, "text" ) );
  }

  // NOTE: Resize and Move are currently always set to listen after creation. This is to keep
  //       the previous behavior where updates for bounds were always sent immediately.
  @Test
  public void testRenderControlListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    lca.renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( shell, "Resize" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( shell, "Move" ) );
  }

  @Test
  public void testRenderActivateListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    lca.render( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( shell, "Activate" ) );
  }

  @Test
  public void testRenderCloseListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    lca.render( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( shell, "Close" ) );
  }

  @Test
  public void testRenderActive() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setActive();
    assertTrue( shell.getDisplay().getActiveShell() == shell );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( shell, "active" ) );
  }

  @Test
  public void testRenderParentShellForDialogShell() throws Exception {
    Shell parentShell = new Shell( display );
    Shell dialogShell = new Shell( parentShell );
    Fixture.markInitialized( display );
    Fixture.markInitialized( parentShell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    lca.renderInitialization( dialogShell );

    Message message = Fixture.getProtocolMessage();
    String parentId = WidgetUtil.getId( parentShell );
    assertEquals( parentId, message.findCreateProperty( dialogShell, "parentShell" ) );
  }

  @Test
  public void testTitleImageWithCaptionBar() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImage() );
    String expected = "[\"" + imageLocation + "\", 58, 12 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( shell, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testTitleImageWithoutCaptionBar() throws Exception {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testTitleImageWithCaptionBarWihoutMinMaxClose() throws Exception {
    Shell shell = new Shell( display, SWT.TITLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImage() );
    String expected = "[\"" + imageLocation + "\", 58, 12 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( shell, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testTitleImageWithMultipleImages() throws Exception {
    Shell shell = new Shell( display, SWT.TITLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    ShellLCA lca = new ShellLCA();

    shell.setImages( new Image[] { Graphics.getImage( Fixture.IMAGE1 ) } );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImages()[ 0 ] );
    String expected = "[\"" + imageLocation + "\", 58, 12 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( shell, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testRenderVisibilityIntiallyFalse() throws IOException {
    ShellLCA lca = new ShellLCA();

    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "visibility" ) );
  }

  @Test
  public void testRenderVisibilityInitiallyTrue() throws IOException {
    ShellLCA lca = new ShellLCA();

    shell.open();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( shell, "visibility" ) );
  }

  private static Control getActiveControl( Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    return shellAdapter.getActiveControl();
  }

  private static void setActiveControl( Shell shell, Control control ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( control );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

  private void fakeModeAndBounds( String mode, int x, int y, int width, int heigth ) {
    Fixture.fakeSetParameter( getId( shell ), "mode", mode );
    Fixture.fakeSetParameter( getId( shell ), "bounds.x", Integer.valueOf( x ) );
    Fixture.fakeSetParameter( getId( shell ), "bounds.y", Integer.valueOf( y ) );
    Fixture.fakeSetParameter( getId( shell ), "bounds.width", Integer.valueOf( width ) );
    Fixture.fakeSetParameter( getId( shell ), "bounds.heigth", Integer.valueOf( heigth ) );
  }

}
