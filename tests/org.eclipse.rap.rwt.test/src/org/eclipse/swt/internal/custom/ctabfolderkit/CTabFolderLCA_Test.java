/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;


@SuppressWarnings("deprecation")
public class CTabFolderLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CTabFolderLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CTabFolderLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
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
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    // control_listeners
    folder.addControlListener( new ControlAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
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
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    folder.addFocusListener( new FocusAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( folder, new ActivateAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
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

    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );

    // Let pass one startup request to init the 'system'
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread( );

    // The actual test request: item1 is selected, the request selects item2
    folder.setSelection( item1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( folderId + ".selectedItemId", item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.executeLifeCycleFromServerThread( );
    assertSame( item2, folder.getSelection() );
    assertEquals( "visible=false", item1Control.markup.toString() );
    assertEquals( "visible=true", item2Control.markup.toString() );
  }

  public void testSelectionEvent() {
    final StringBuilder log = new StringBuilder();
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    };
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.addSelectionListener( listener );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );

    // Select item1 and fake request that selects item2
    folder.setSelection( item1 );
    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    String name = folderId + "." + CTabFolderLCA.PARAM_SELECTED_ITEM_ID;
    Fixture.fakeRequestParam( name, item2Id );
    Fixture.readDataAndProcessAction( folder );
    assertSame( item2, folder.getSelection() );
    assertEquals( "widgetSelected|", log.toString() );
  }

  public void testShowListEvent() {
    // Widgets for test
    final CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 30, 130 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    //
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    final ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    final StringBuilder log = new StringBuilder();
    CTabFolder2Listener listener = new CTabFolder2Adapter() {
      public void showList( CTabFolderEvent event ) {
        assertEquals( true, event.doit );
        log.append( "showList|" );
      }
    };
    CTabFolder2Listener vetoListener = new CTabFolder2Adapter() {
      public void showList( CTabFolderEvent event ) {
        Rectangle chevronRect = folderAdapter.getChevronRect();
        Rectangle eventRet
          = new Rectangle( event.x, event.y, event.width, event.height);
        assertEquals( eventRet, chevronRect );
        assertEquals( true, event.doit );
        assertEquals( folder, event.getSource() );
        log.append( "vetoShowList|" );
        event.doit = false;
      }
    };

    // Test showList event with listeners that prevents menu form showing
    // Note: this test must run first since it relies on the fact that the
    //       showList menu wasn't populated by previous showList requests
    folder.setSelection( item1 );
    folder.addCTabFolder2Listener( vetoListener );
    String folderId = WidgetUtil.getId( folder );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    Fixture.readDataAndProcessAction( folder );
    assertEquals( "vetoShowList|", log.toString() );
    Menu menu = getShowListMenu( folder );
    assertEquals( null, menu );
    // clean up above test
    folder.removeCTabFolder2Listener( vetoListener );

    // Test showList event with listeners that does not veto showing
    log.setLength( 0 );
    folder.addCTabFolder2Listener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    Fixture.readDataAndProcessAction( folder );
    assertEquals( "showList|", log.toString() );
    menu = getShowListMenu( folder );
    assertEquals( 1, menu.getItemCount() );
  }

  public void testRenderCreate() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "TOP" ) );
    assertTrue( styles.contains( "MULTI" ) );
  }

  public void testRenderCreateOnBottom() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.BOTTOM );

    lca.renderInitialization( folder );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( folder );
    assertEquals( "rwt.widgets.CTabFolder", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "BOTTOM" ) );
    assertTrue( styles.contains( "MULTI" ) );
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
    assertEquals( "#ff0000", colors.get( 0 ) );
    assertEquals( "#00ff00", colors.get( 1 ) );
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

    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
        result = new AbstractWidgetLCA() {
          public void preserveValues( Widget widget ) {
            Control control = ( Control )widget;
            IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
            Boolean visible = Boolean.valueOf( control.isVisible() );
            adapter.preserve( "visible", visible );
          }
          public void renderChanges( Widget widget ) throws IOException {
            markup.setLength( 0 );
            Control control = ( Control )widget;
            Boolean visible = Boolean.valueOf( control.isVisible() );
            if( WidgetLCAUtil.hasChanged( widget, "visible", visible ) ) {
              markup.append( "visible=" + visible );
            }
          }
          public void renderDispose( Widget widget ) throws IOException {
          }
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
