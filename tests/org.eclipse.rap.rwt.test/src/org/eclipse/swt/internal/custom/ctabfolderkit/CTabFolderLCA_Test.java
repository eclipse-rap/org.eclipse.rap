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
package org.eclipse.swt.internal.custom.ctabfolderkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.mockito.ArgumentCaptor;


@SuppressWarnings("deprecation")
public class CTabFolderLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CTabFolderLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CTabFolderLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( folder );
    ControlLCATestUtil.testFocusListener( folder );
    ControlLCATestUtil.testMouseListener( folder );
    ControlLCATestUtil.testKeyListener( folder );
    ControlLCATestUtil.testTraverseListener( folder );
    ControlLCATestUtil.testMenuDetectListener( folder );
    ControlLCATestUtil.testHelpListener( folder );
  }

  public void testLCA() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );

    assertSame( CTabFolderLCA.class,
                folder.getAdapter( ILifeCycleAdapter.class ).getClass() );
    assertSame( CTabItemLCA.class,
                item.getAdapter( ILifeCycleAdapter.class ).getClass() );
  }

  public void testPreserveValues() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Label label = new Label( folder, SWT.NONE );
    folder.setTopRight( label, SWT.FILL );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    folder.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( folder );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( folder );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    folder.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    folder.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( true );
    // foreground background font
    Color controlBackground = Graphics.getColor( 122, 33, 203 );
    folder.setBackground( controlBackground );
    Color controlForeground = Graphics.getColor( 211, 178, 211 );
    folder.setForeground( controlForeground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    folder.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( controlBackground, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( controlForeground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( null, folder.getToolTipText() );
    Fixture.clearPreserved();
    folder.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( "some text", folder.getToolTipText() );
  }

  public void testChangeSelection() {
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
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
    Fixture.fakeSetParameter( getId( folder ), "selection", getId( item2 ) );
    Fixture.fakeNotifyOperation( getId( folder ), ClientMessageConst.EVENT_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread();

    assertSame( item2, folder.getSelection() );
    assertEquals( "visible=false", item1Control.markup.toString() );
    assertEquals( "visible=true", item2Control.markup.toString() );
  }

  public void testSelectionEvent() {
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    SelectionListener listener = mock( SelectionListener.class );
    folder.addSelectionListener( listener );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    folder.setSelection( item1 );

    Map<String, Object> parameters = new HashMap<String, Object>();
//    parameters.put( "item", WidgetUtil.getId( item2 ) );
    Fixture.fakeNotifyOperation( getId( folder ), ClientMessageConst.EVENT_SELECTED, parameters );
    Fixture.fakeSetParameter( getId( folder ),
                              CTabFolderLCA.PARAM_SELECTION,
                              getId( item2 ) );

    Fixture.readDataAndProcessAction( folder );

    assertSame( item2, folder.getSelection() );
    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testShowListEvent_WithVeto() {
    final CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 30, 130 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSelection( item1 );
    final List<CTabFolderEvent> log = new ArrayList<CTabFolderEvent>();
    folder.addCTabFolder2Listener( new CTabFolder2Adapter() {
      @Override
      public void showList( CTabFolderEvent event ) {
        event.doit = false;
        log.add( event );
      }
    } );

    Fixture.fakeNotifyOperation( getId( folder ), ClientMessageConst.EVENT_SHOW_LIST, null );
    Fixture.readDataAndProcessAction( folder );

    CTabFolderEvent event = log.get( 0 );
    Rectangle chevronRect = folder.getAdapter( ICTabFolderAdapter.class ).getChevronRect();
    Rectangle eventRet = new Rectangle( event.x, event.y, event.width, event.height);
    assertEquals( eventRet, chevronRect );
    assertFalse( event.doit );
    assertEquals( null, getShowListMenu( folder ) );
  }

  public void testShowListEvent_WithoutVeto() {
    final CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 30, 130 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );
    folder.setSelection( item1 );

    Fixture.fakeNotifyOperation( getId( folder ), ClientMessageConst.EVENT_SHOW_LIST, null );
    Fixture.readDataAndProcessAction( folder );

    ArgumentCaptor<CTabFolderEvent> captor = ArgumentCaptor.forClass( CTabFolderEvent.class );
    verify( listener, times( 1 ) ).showList( captor.capture() );
    assertTrue( captor.getValue().doit );
    assertEquals( 1, getShowListMenu( folder ).getItemCount() );
  }

  public void testRenderCreate() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "TOP" ) );
    assertTrue( styles.contains( "MULTI" ) );
  }

  public void testRenderCreateOnBottom() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.BOTTOM );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "BOTTOM" ) );
    assertTrue( styles.contains( "MULTI" ) );
    assertEquals( "bottom", message.findCreateProperty( folder, "tabPosition" ) );
  }

  public void testRenderSingleFlatAndClose() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE | SWT.FLAT | SWT.CLOSE );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "SINGLE" ) );
    assertTrue( styles.contains( "FLAT" ) );
    assertTrue( styles.contains( "CLOSE" ) );
  }

  public void testRenderParent() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( WidgetUtil.getId( folder.getParent() ), operation.getParent() );
  }

  public void testRenderToolTipTexts() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray texts = ( JSONArray )message.findCreateProperty( folder, "toolTipTexts" );
    assertEquals( 5, texts.length() );
  }

  public void testRenderDispose() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.renderDispose( folder );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( folder ), operation.getTarget() );
  }

  public void testRenderInitialTabPosition() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "tabPosition" ) == -1 );
  }

  public void testRenderTabPosition() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setTabPosition( SWT.BOTTOM );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "bottom", message.findSetProperty( folder, "tabPosition" ) );
  }

  public void testRenderTabPositionUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setTabPosition( SWT.BOTTOM );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "tabPosition" ) );
  }

  public void testRenderInitialTabHeight() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().contains( "tabHeight" ) );
  }

  public void testRenderTabHeight() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setTabHeight( 20 );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 20 ), message.findSetProperty( folder, "tabHeight" ) );
  }

  public void testRenderTabHeightUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setTabHeight( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "tabHeight" ) );
  }

  public void testRenderInitialMinMaxState() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "minMaxState" ) == -1 );
  }

  public void testRenderMinMaxState_Max() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setMaximized( true );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "max", message.findSetProperty( folder, "minMaxState" ) );
  }

  public void testRenderMinMaxState_Min() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setMinimized( true );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "min", message.findSetProperty( folder, "minMaxState" ) );
  }

  public void testRenderMinMaxStateUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMaximized( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "minMaxState" ) );
  }

  public void testRenderInitialMinimizeBoundsAndVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "minimizeBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "minimizeVisible" ) == -1 );
  }

  public void testRenderMinimizeBoundsAndVisible() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );

    folder.setMinimizeVisible( true );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "minimizeBounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[129,4,18,18]", actual )  );
    assertEquals( Boolean.TRUE, message.findSetProperty( folder, "minimizeVisible" ) );
  }

  public void testRenderMinimizeBoundsAndVisibleUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMinimizeVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "minimizeBounds" ) );
    assertNull( message.findSetOperation( folder, "minimizeVisible" ) );
  }

  public void testRenderInitialMaximizeBoundsAndVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "maximizeBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "maximizeVisible" ) == -1 );
  }

  public void testRenderMaximizeBoundsAndVisible() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );

    folder.setMaximizeVisible( true );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "maximizeBounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[129,4,18,18]", actual )  );
    assertEquals( Boolean.TRUE, message.findSetProperty( folder, "maximizeVisible" ) );
  }

  public void testRenderMaximizeBoundsAndVisibleUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setMaximizeVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "maximizeBounds" ) );
    assertNull( message.findSetOperation( folder, "maximizeVisible" ) );
  }

  public void testRenderInitialChevronBoundsAndVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "chevronBounds" ) == -1 );
    assertTrue( operation.getPropertyNames().indexOf( "chevronVisible" ) == -1 );
  }

  public void testRenderChevronBoundsAndVisible() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSize( 150, 150 );

    item.setText( "foo bar foo bar" );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "chevronBounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[120,6,27,18]", actual )  );
    assertEquals( Boolean.TRUE, message.findSetProperty( folder, "chevronVisible" ) );
  }

  public void testRenderChevronBoundsAndVisibleUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSize( 150, 150 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    item.setText( "foo bar foo bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "chevronBounds" ) );
    assertNull( message.findSetOperation( folder, "chevronVisible" ) );
  }

  public void testRenderInitialUnselectedCloseVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "unselectedCloseVisible" ) == -1 );
  }

  public void testRenderUnselectedCloseVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setUnselectedCloseVisible( false );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( folder, "unselectedCloseVisible" ) );
  }

  public void testRenderUnselectedCloseVisibleUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setUnselectedCloseVisible( false );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "unselectedCloseVisible" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );

    folder.setSelection( item );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( item ), message.findSetProperty( folder, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelection( item );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selection" ) );
  }

  public void testRenderInitialSelectionBackground() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackground" ) == -1 );
  }

  public void testRenderSelectionBackground() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setSelectionBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "selectionBackground" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,255,255]", actual ) );
  }

  public void testRenderSelectionBackgroundUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelectionBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackground" ) );
  }

  public void testRenderInitialSelectionForeground() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionForeground" ) == -1 );
  }

  public void testRenderSelectionForeground() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setSelectionForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "selectionForeground" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,255,255]", actual ) );
  }

  public void testRenderSelectionForegroundUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setSelectionForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionForeground" ) );
  }

  public void testRenderInitialSelectionBackgroundImage() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackgroundImage" ) == -1 );
  }

  public void testRenderSelectionBackgroundImage() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    folder.setSelectionBackground( image );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( folder, "selectionBackgroundImage" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderSelectionBackgroundImageUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    folder.setSelectionBackground( image );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackgroundImage" ) );
  }

  public void testRenderInitialSelectionBackgroundGradient() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "selectionBackgroundGradient" ) == -1 );
  }

  public void testRenderSelectionBackgroundGradient() throws IOException, JSONException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    Color[] gradientColors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    folder.setSelectionBackground( gradientColors , percents );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    JSONArray gradient
      = ( JSONArray )message.findSetProperty( folder, "selectionBackgroundGradient" );
    JSONArray colors = ( JSONArray )gradient.get( 0 );
    JSONArray stops = ( JSONArray )gradient.get( 1 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[255,0,0,255]", colors.getJSONArray( 0 ) ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", colors.getJSONArray( 1 ) ) );
    assertEquals( Integer.valueOf( 0 ), stops.get( 0 ) );
    assertEquals( Integer.valueOf( 50 ), stops.get( 1 ) );
    assertEquals( Boolean.FALSE, gradient.get( 2 ) );
  }

  public void testRenderSelectionBackgroundGradientUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
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

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "selectionBackgroundGradient" ) );
  }

  public void testRenderInitialBorderVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.render( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertTrue( operation.getPropertyNames().indexOf( "borderVisible" ) == -1 );
  }

  public void testRenderBorderVisible() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    folder.setBorderVisible( true );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( folder, "borderVisible" ) );
  }

  public void testRenderBorderVisibleUnchanged() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );

    folder.setBorderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( folder, "borderVisible" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( folder, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    folder.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.removeSelectionListener( listener );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( folder, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( folder, "selection" ) );
  }

  public void testRenderAddFolderListener() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addCTabFolder2Listener( new CTabFolder2Adapter() { } );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( folder, "folder" ) );
  }

  public void testRenderRemoveFolderListener() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabFolder2Adapter listener = new CTabFolder2Adapter() { };
    folder.addCTabFolder2Listener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.removeCTabFolder2Listener( listener );
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( folder, "folder" ) );
  }

  public void testRenderFolderListenerUnchanged() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( folder );
    Fixture.preserveWidgets();

    folder.addCTabFolder2Listener( new CTabFolder2Adapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( folder );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( folder, "folder" ) );
  }

  private static Menu getShowListMenu( CTabFolder folder ) {
    Menu result = null;
    try {
      Field field = CTabFolder.class.getDeclaredField( "showMenu" );
      field.setAccessible( true );
      result = ( Menu )field.get( folder );
    } catch( Exception e ) {
      e.printStackTrace();
    }
    return result;
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
      if( adapter == ILifeCycleAdapter.class ) {
        result = new AbstractWidgetLCA() {
          @Override
          public void preserveValues( Widget widget ) {
            Control control = ( Control )widget;
            IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
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
