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
package org.eclipse.swt.internal.custom.ctabfolderkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.rap.rwt.testfixture.TestMessage.CreateOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class CTabFolderLCA_Test {

  private Display display;
  private Shell shell;
  private CTabFolder folder;
  private CTabFolderLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    folder = new CTabFolder( shell, SWT.NONE );
    lca = new CTabFolderLCA();
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
  public void testLCA() {
    CTabItem item = new CTabItem( folder, SWT.NONE );

    assertSame( CTabFolderLCA.class,
                folder.getAdapter( WidgetLifeCycleAdapter.class ).getClass() );
    assertSame( CTabItemLCA.class,
                item.getAdapter( WidgetLifeCycleAdapter.class ).getClass() );
  }

  @Test
  public void testPreserveValues() {
    Label label = new Label( folder, SWT.NONE );
    folder.setTopRight( label, SWT.FILL );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    folder.setBounds( rectangle );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = getAdapter( folder );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // menu
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
    // visible
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    folder.setVisible( false );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( true );
    // foreground background font
    Color controlBackground = new Color( display, 122, 33, 203 );
    folder.setBackground( controlBackground );
    Color controlForeground = new Color( display, 211, 178, 211 );
    folder.setForeground( controlForeground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    folder.setFont( font );
    Fixture.preserveWidgets();
    adapter = getAdapter( folder );
    assertEquals( controlBackground, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( controlForeground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
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
  public void testChangeSelection() {
    folder = new CTabFolder( shell, SWT.MULTI );
    Fixture.markInitialized( folder );
    getRemoteObject( folder ).setHandler( new CTabFolderOperationHandler( folder ) );
    folder.setSize( 100, 100 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItemControl item1Control = new CTabItemControl( folder, SWT.NONE );
    item1.setControl( item1Control );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    CTabItemControl item2Control = new CTabItemControl( folder, SWT.NONE );
    item2.setControl( item2Control );
    shell.open();

    // The actual test request: item1 is selected, the request selects item2
    folder.setSelection( item1 );
    Fixture.fakeSetProperty( getId( folder ), "selection", getId( item2 ) );
    Fixture.executeLifeCycleFromServerThread();

    assertSame( item2, folder.getSelection() );
    assertEquals( "visible=false", item1Control.markup.toString() );
    assertEquals( "visible=true", item2Control.markup.toString() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "TOP" ) );
    assertTrue( styles.contains( "MULTI" ) );
  }

  @Test
  public void testRenderCreateOnBottom() throws IOException {
    folder = new CTabFolder( shell, SWT.BOTTOM );

    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "BOTTOM" ) );
    assertTrue( styles.contains( "MULTI" ) );
    assertEquals( "bottom", message.findSetProperty( folder, "tabPosition" ).asString() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( folder );
    lca.renderInitialization( folder );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof CTabFolderOperationHandler );
  }

  @Test
  public void testRenderInitialization_rendersSelectionListener() throws Exception {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( folder, "Selection" ) );
  }

  @Test
  public void testRenderInitialization_rendersFolderListener() throws Exception {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( folder, "Folder" ) );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    CTabFolderOperationHandler handler = spy( new CTabFolderOperationHandler( folder ) );
    getRemoteObject( getId( folder ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( folder ), "Help", new JsonObject() );
    lca.readData( folder );

    verify( handler ).handleNotifyHelp( folder, new JsonObject() );
  }

  @Test
  public void testRenderSingleFlatAndClose() throws IOException {
    folder = new CTabFolder( shell, SWT.SINGLE | SWT.FLAT | SWT.CLOSE );

    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "SINGLE" ) );
    assertTrue( styles.contains( "FLAT" ) );
    assertTrue( styles.contains( "CLOSE" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( getId( folder.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderToolTipTexts() throws IOException {
    lca.renderInitialization( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray texts = ( JsonArray )message.findCreateProperty( folder, "toolTipTexts" );
    assertEquals( 5, texts.size() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( folder );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( getId( folder ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialTabPosition() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "tabPosition" ) == -1 );
  }

  @Test
  public void testRenderTabPosition() throws IOException {
    folder.setTabPosition( SWT.BOTTOM );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "bottom", message.findSetProperty( folder, "tabPosition" ).asString() );
  }

  @Test
  public void testRenderTabPositionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setTabPosition( SWT.BOTTOM );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "tabPosition" ) );
  }

  @Test
  public void testRenderInitialTabHeight() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( folder, "tabHeight" ) );
  }

  @Test
  public void testRenderTabHeight() throws IOException {
    folder.setTabHeight( 20 );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( folder, "tabHeight" ).asInt() );
  }

  @Test
  public void testRenderTabHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setTabHeight( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "tabHeight" ) );
  }

  @Test
  public void testRenderInitialMinMaxState() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "minMaxState" ) == -1 );
  }

  @Test
  public void testRenderMinMaxState_Max() throws IOException {
    folder.setMaximized( true );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "max", message.findSetProperty( folder, "minMaxState" ).asString() );
  }

  @Test
  public void testRenderMinMaxState_Min() throws IOException {
    folder.setMinimized( true );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "min", message.findSetProperty( folder, "minMaxState" ).asString() );
  }

  @Test
  public void testRenderMinMaxStateUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMaximized( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "minMaxState" ) );
  }

  @Test
  public void testRenderInitialMinimizeBoundsAndVisible() throws IOException {
    folder.setSize( 150, 150 );

    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "minimizeBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "minimizeVisible" ) == -1 );
  }

  @Test
  public void testRenderMinimizeBoundsAndVisible() throws IOException {
    folder.setSize( 150, 150 );

    folder.setMinimizeVisible( true );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[129, 4, 18, 18]" );
    assertEquals( expected, message.findSetProperty( folder, "minimizeBounds" ) );
    assertEquals( JsonValue.TRUE, message.findSetProperty( folder, "minimizeVisible" ) );
  }

  @Test
  public void testRenderMinimizeBoundsAndVisibleUnchanged() throws IOException {
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMinimizeVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "minimizeBounds" ) );
    assertNull( message.findSetOperation( folder, "minimizeVisible" ) );
  }

  @Test
  public void testRenderInitialMaximizeBoundsAndVisible() throws IOException {
    folder.setSize( 150, 150 );

    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "maximizeBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "maximizeVisible" ) == -1 );
  }

  @Test
  public void testRenderMaximizeBoundsAndVisible() throws IOException {
    folder.setSize( 150, 150 );

    folder.setMaximizeVisible( true );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[129, 4, 18, 18]" );
    assertEquals( expected, message.findSetProperty( folder, "maximizeBounds" ) );
    assertEquals( JsonValue.TRUE, message.findSetProperty( folder, "maximizeVisible" ) );
  }

  @Test
  public void testRenderMaximizeBoundsAndVisibleUnchanged() throws IOException {
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMaximizeVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "maximizeBounds" ) );
    assertNull( message.findSetOperation( folder, "maximizeVisible" ) );
  }

  @Test
  public void testRenderInitialChevronBoundsAndVisible() throws IOException {
    folder.setSize( 150, 150 );

    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "chevronBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "chevronVisible" ) == -1 );
  }

  @Test
  public void testRenderChevronBoundsAndVisible() throws IOException {
    CTabItem item = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSize( 150, 150 );

    item.setText( "foo bar foo bar" );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[120, 6, 27, 18]" );
    assertEquals( expected, message.findSetProperty( folder, "chevronBounds" ) );
    assertEquals( JsonValue.TRUE, message.findSetProperty( folder, "chevronVisible" ) );
  }

  @Test
  public void testRenderChevronBoundsAndVisibleUnchanged() throws IOException {
    CTabItem item = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    item.setText( "foo bar foo bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "chevronBounds" ) );
    assertNull( message.findSetOperation( folder, "chevronVisible" ) );
  }

  @Test
  public void testRenderInitialUnselectedCloseVisible() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "unselectedCloseVisible" ) == -1 );
  }

  @Test
  public void testRenderUnselectedCloseVisible() throws IOException {
    folder.setUnselectedCloseVisible( false );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( folder, "unselectedCloseVisible" ) );
  }

  @Test
  public void testRenderUnselectedCloseVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setUnselectedCloseVisible( false );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "unselectedCloseVisible" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    CTabItem item = new CTabItem( folder, SWT.NONE );

    folder.setSelection( item );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item ), message.findSetProperty( folder, "selection" ).asString() );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    CTabItem item = new CTabItem( folder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelection( item );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selection" ) );
  }

  @Test
  public void testRenderInitialSelectionBackground() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackground" ) == -1 );
  }

  @Test
  public void testRenderSelectionBackground() throws IOException {
    folder.setSelectionBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[0, 0, 255, 255]" );
    assertEquals( expected, message.findSetProperty( folder, "selectionBackground" ) );
  }

  @Test
  public void testRenderSelectionBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelectionBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackground" ) );
  }

  @Test
  public void testRenderInitialSelectionForeground() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionForeground" ) == -1 );
  }

  @Test
  public void testRenderSelectionForeground() throws IOException {
    folder.setSelectionForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[0, 0, 255, 255]" );
    assertEquals( expected, message.findSetProperty( folder, "selectionForeground" ) );
  }

  @Test
  public void testRenderSelectionForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelectionForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionForeground" ) );
  }

  @Test
  public void testRenderInitialSelectionBackgroundImage() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackgroundImage" ) == -1 );
  }

  @Test
  public void testRenderSelectionBackgroundImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    folder.setSelectionBackground( image );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( folder, "selectionBackgroundImage" ) );
  }

  @Test
  public void testRenderSelectionBackgroundImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    folder.setSelectionBackground( image );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackgroundImage" ) );
  }

  @Test
  public void testRenderInitialSelectionBackgroundGradient() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackgroundGradient" ) == -1 );
  }

  @Test
  public void testRenderSelectionBackgroundGradient() throws IOException {
    Color[] gradientColors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    folder.setSelectionBackground( gradientColors , percents );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected
      = JsonArray.readFrom( "[[[255, 0, 0, 255], [0, 255, 0, 255]], [0, 50], false]" );
    assertEquals( expected, message.findSetProperty( folder, "selectionBackgroundGradient" ) );
  }

  @Test
  public void testRenderSelectionBackgroundGradientUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    folder.setSelectionBackground( colors , percents );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackgroundGradient" ) );
  }

  @Test
  public void testRenderInitialBorderVisible() throws IOException {
    lca.render( folder );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "borderVisible" ) == -1 );
  }

  @Test
  public void testRenderBorderVisible() throws IOException {
    folder.setBorderVisible( true );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( folder, "borderVisible" ) );
  }

  @Test
  public void testRenderBorderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setBorderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "borderVisible" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( folder, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    folder.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( folder, "DefaultSelection" ) );
  }

  @Test
  public void testRenderDefaultSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( folder, "DefaultSelection" ) );
  }

  private static final class CTabItemControl extends Composite {
    private static final long serialVersionUID = 1L;

    public final StringBuilder markup = new StringBuilder();

    public CTabItemControl( Composite parent, int style ) {
      super( parent, style );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = new AbstractWidgetLCA() {
          @Override
          public void preserveValues( Widget widget ) {
            Control control = ( Control )widget;
            WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
            Boolean visible = Boolean.valueOf( control.isVisible() );
            adapter.preserve( "visible", visible );
          }
          @Override
          public void renderChanges( Widget widget ) throws IOException {
            markup.setLength( 0 );
            Control control = ( Control )widget;
            Boolean visible = Boolean.valueOf( control.isVisible() );
            if( WidgetLCAUtil.hasChanged( widget, "visible", visible ) ) {
              markup.append( "visible=" + visible );
            }
          }
          @Override
          public void renderDispose( Widget widget ) throws IOException {
          }
          @Override
          public void renderInitialization( Widget widget )
            throws IOException
          {
          }
          @Override
          public void readData( Widget widget ) {
          }
        };
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

}
