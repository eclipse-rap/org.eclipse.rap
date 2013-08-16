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
package org.eclipse.swt.internal.widgets.shellkit;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_ACTIVATE;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ShellLCA_Test {

  private Display display;
  private Shell shell;
  private ShellLCA lca;
  private Image image;

  @Before
  public void setUp() throws IOException {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ShellLCA();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    image = createImage( display, Fixture.IMAGE1 );
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
    assertEquals( new Point( 30, 0 ), adapter.getPreserved( ShellLCA.PROP_MINIMUM_SIZE ) );
    Fixture.clearPreserved();
    shell.setText( "some text" );
    shell.open();
    shell.setActive();
    IShellAdapter shellAdapter
     = shell.getAdapter( IShellAdapter.class );
    shellAdapter.setActiveControl( button );
    shell.addShellListener( new ShellAdapter() { } );
    shell.setMaximized( true );
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
    Color background =new Color( display, 122, 33, 203 );
    shell.setBackground( background );
    Color foreground =new Color( display, 211, 178, 211 );
    shell.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
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
  public void testShellActivate_doesNotRenderPropertyBack() {
    Shell shellToActivate = new Shell( display, SWT.NONE );
    getRemoteObject( shellToActivate ).setHandler( new ShellOperationHandler( shellToActivate ) );
    shellToActivate.open();
    Shell activeShell = new Shell( display, SWT.NONE );
    getRemoteObject( activeShell ).setHandler( new ShellOperationHandler( activeShell ) );
    activeShell.open();
    activeShell.setActive();
    Fixture.markInitialized( display );
    Fixture.markInitialized( activeShell );
    Fixture.markInitialized( shellToActivate );

    Fixture.fakeNotifyOperation( getId( shellToActivate ), EVENT_ACTIVATE, null );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shellToActivate, "active" ) );
  }

  @Test
  public void testAlpha() throws Exception {
    shell.open();

    shell.setAlpha( 23 );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 23, message.findSetProperty( shell, "alpha" ).asInt() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( shell );
    assertEquals( "rwt.widgets.Shell", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( shell );
    lca.renderInitialization( shell );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ShellOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ShellOperationHandler handler = spy( new ShellOperationHandler( shell ) );
    getRemoteObject( getId( shell ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( shell ), "Help", new JsonObject() );
    lca.readData( shell );

    verify( handler ).handleNotifyHelp( shell, new JsonObject() );
  }

  @Test
  public void testRenderMode() throws Exception {
    shell.open();

    shell.setMaximized( true );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "maximized", message.findSetProperty( shell, "mode" ).asString() );
  }

  @Test
  public void testRenderFullscreen() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    shell.open();
    shell.setFullScreen( true );
    Fixture.preserveWidgets();

    shell.setFullScreen( false );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( shell, "mode" ) );
  }

  @Test
  public void testResetFullscreen() throws Exception {
    shell.open();

    shell.setFullScreen( true );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "fullscreen", message.findSetProperty( shell, "mode" ).asString() );
  }

  @Test
  public void testRenderDefaultButtonIntiallyNull() throws IOException {
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "defaultButton" ) );
  }

  @Test
  public void testRenderDefaultButtonInitiallySet() throws Exception {
    Button button = new Button( shell, SWT.PUSH );

    shell.setDefaultButton( button );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( getId( button ), message.findSetProperty( shell, "defaultButton" ).asString() );
  }

  @Test
  public void testRenderDefaultButtonUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
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
    Button button = new Button( shell, SWT.PUSH );
    shell.setDefaultButton( button );
    Fixture.preserveWidgets();

    button.dispose();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( shell, "defaultButton" ) );
  }

  @Test
  public void testResetDefaultButton_AfterFocusControlChange() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Button button = new Button( shell, SWT.PUSH );
    Text text = new Text( shell, SWT.NONE );

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( button ) );
    Fixture.executeLifeCycleFromServerThread();

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( text ) );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( shell, "defaultButton" ) );
  }

  @Test
  public void testRenderActiveControlIntiallyNull() throws IOException {
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "activeControl" ) );
  }

  @Test
  public void testRenderActiveControlInitiallySet() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );

    adapter.setActiveControl( button );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( getId( button ), message.findSetProperty( shell, "activeControl" ).asString() );
  }

  @Test
  public void testRenderActiveControlUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Button button = new Button( shell, SWT.PUSH );
    IShellAdapter adapter = shell.getAdapter( IShellAdapter.class );
    adapter.setActiveControl( button );

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
    Fixture.preserveWidgets();

    adapter.setActiveControl( null );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( shell, "activeControl" ) );
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

    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "[100,200]", message.findSetProperty( shell, "minimumSize" ).toString() );
  }

  @Test
  public void testRenderStyleFlags() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();

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

    shell.setText( "foo" );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( shell, "text" ).asString() );
  }

  // NOTE: Resize and Move are currently always set to listen after creation. This is to keep
  //       the previous behavior where updates for bounds were always sent immediately.
  @Test
  public void testRenderControlListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();

    lca.renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( shell, "Resize" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( shell, "Move" ) );
  }

  @Test
  public void testRenderActivateListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();

    lca.render( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( shell, "Activate" ) );
  }

  @Test
  public void testRenderCloseListener() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();

    lca.render( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( shell, "Close" ) );
  }

  @Test
  public void testRenderActive() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();

    shell.setActive();
    assertTrue( shell.getDisplay().getActiveShell() == shell );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( shell, "active" ) );
  }

  @Test
  public void testRenderParentShellForDialogShell() throws Exception {
    Shell parentShell = new Shell( display );
    Shell dialogShell = new Shell( parentShell );
    Fixture.markInitialized( display );
    Fixture.markInitialized( parentShell );
    Fixture.preserveWidgets();

    lca.renderInitialization( dialogShell );

    Message message = Fixture.getProtocolMessage();
    String parentId = getId( parentShell );
    assertEquals( parentId, message.findCreateProperty( dialogShell, "parentShell" ).asString() );
  }

  @Test
  public void testTitleImageWithCaptionBar() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();

    shell.setImage( image );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImage() );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 58 ).add( 12 );
    assertEquals( expected, message.findSetProperty( shell, "image" ) );
  }

  @Test
  public void testTitleImageWithoutCaptionBar() throws Exception {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();

    shell.setImage( image );
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

    shell.setImage( image );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImage() );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 58 ).add( 12 );
    assertEquals( expected, message.findSetProperty( shell, "image" ) );
  }

  @Test
  public void testTitleImageWithMultipleImages() throws Exception {
    Shell shell = new Shell( display, SWT.TITLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();

    shell.setImages( new Image[] { image } );
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( shell.getImages()[ 0 ] );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 58 ).add( 12 );
    assertEquals( expected, message.findSetProperty( shell, "image" ) );
  }

  @Test
  public void testRenderVisibilityIntiallyFalse() throws IOException {
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "visibility" ) );
  }

  @Test
  public void testRenderVisibilityInitiallyTrue() throws IOException {
    shell.open();
    lca.renderChanges( shell );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( shell, "visibility" ) );
  }

}
