/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class TreeLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testMinimalInitialization() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeLCA lca = new TreeLCA();
    lca.renderInitialization( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "new org.eclipse.rwt.widgets.Tree()" ) != -1 );
    assertTrue( markup.indexOf( "w.setSelectionPadding( 3, 5 )" ) != -1 );
    assertTrue( markup.indexOf( "w.setIndentionWidth" ) != -1 );
    assertTrue( markup.indexOf( "w.setHasCheckBoxes(" ) == -1 );
    assertTrue( markup.indexOf( "w.setHasNoScroll(" ) == -1 );
    assertTrue( markup.indexOf( "w.setHasMultiSelection(" ) == -1 );
    assertTrue( markup.indexOf( "w.setHasFullSelection(" ) == -1 );
    assertTrue( markup.indexOf( "w.setCheckBoxMetrics( " ) == -1 );
    assertTrue( markup.indexOf( "w.setIsVirtual( " ) == -1 );
  }

  public void testInitialization() throws Exception {
    int style = SWT.MULTI | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL;
    Tree tree = new Tree( shell, style );
    TreeLCA lca = new TreeLCA();
    lca.renderInitialization( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "new org.eclipse.rwt.widgets.Tree()" ) != -1 );
    assertTrue( markup.indexOf( "w.setHasCheckBoxes( true )" ) != -1 );
    assertTrue( markup.indexOf( "w.setHasMultiSelection( true )" ) != -1 );
    assertTrue( markup.indexOf( "w.setHasFullSelection( true )" ) != -1 );
    assertTrue( markup.indexOf( "w.setIsVirtual( true )" ) != -1 );
    assertTrue( markup.indexOf( "w.setCheckBoxMetrics( " ) != -1 );
    assertTrue( markup.indexOf( "w.setSelectionPadding" ) == -1 );
  }

  public void testInitializationWithNoScroll() throws Exception {
    Tree tree = new Tree( shell, SWT.NO_SCROLL );
    TreeLCA lca = new TreeLCA();
    lca.renderInitialization( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setHasNoScroll( true )" ) != -1 );
  }

  public void testRenderTopItemIndex() throws Exception {
    TreeLCA lca = new TreeLCA();
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    lca.renderChanges( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setTopItemIndex( " ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ITreeAdapter treeAdapter
      = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    treeAdapter.setTopItemIndex( 4 );
    lca.renderChanges( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setTopItemIndex( 4 )" )  != -1 );
  }

  public void testRenderColumnCount() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeLCA lca = new TreeLCA();
    lca.render( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setColumnCount( 0" ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setColumnCount( 3" ) != -1 );
  }

  public void testRenderTreeColumn() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeLCA lca = new TreeLCA();
    lca.render( tree );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setTreeColumn( " ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    tree.setColumnOrder( new int[]{ 1, 0 } );
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setTreeColumn( 1" ) != -1 );
  }

  public void testRenderLinesVisible() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setBounds( 0, 0, 100, 100 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeLCA lca = new TreeLCA();
    lca.render( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setLinesVisible( " ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    tree.setLinesVisible( true );
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setLinesVisible( true )" ) != -1 );
    lca.preserveValues( tree );
    Fixture.fakeResponseWriter();
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setLinesVisible( true )" ) == -1 );
  }

  public void testRenderHorizontalScrollBar() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setBounds( 0, 0, 100, 100 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeLCA lca = new TreeLCA();
    lca.render( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setScrollBarsVisible( " ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setWidth( 200 );
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setScrollBarsVisible( true, false )" ) != -1 );
  }

  public void testRenderVerticalScrollBar() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setBounds( 0, 0, 100, 100 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeLCA lca = new TreeLCA();
    lca.render( tree );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "w.setScrollBarsVisible( " ) == -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    for( int i = 0; i < 100; i++ ) {
      new TreeItem( tree, SWT.None );
    }
    lca.render( tree );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setScrollBarsVisible( false, true )" ) != -1 );
  }

  public void testGetItemMetricsImageWidth() {
    Image image1 = Graphics.getImage( Fixture.IMAGE_100x50 );
    Image image2 = Graphics.getImage( Fixture.IMAGE_50x100 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item1" );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setText( "item2" );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    item3.setText( "item3" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageWidth );
    item2.setImage( image2 );
    item1.setImage( image1 );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 50, metrics[ 0 ].imageWidth );
    item2.setImage( (Image) null );
    item1.setImage( (Image) null );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageWidth );
  }

  public void testGetItemMetricsImageLeft() {
    Image image1 = Graphics.getImage( Fixture.IMAGE_100x50 );
    Image image2 = Graphics.getImage( Fixture.IMAGE_50x100 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 200 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item1" );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setText( "item2" );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    item3.setText( "item3" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageLeft );
    assertEquals( 203, metrics[ 1 ].imageLeft );
    item2.setImage( image2 );
    item1.setImage( 1, image1 );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageLeft );
    assertEquals( 203, metrics[ 1 ].imageLeft );
  }

  public void testGetItemMetricsCellLeft() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 210 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "column2" );
    column3.setWidth( 200 );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( 210, metrics[ 1 ].left );
    assertEquals( 410, metrics[ 2 ].left );
  }

  public void testGetItemMetricsCellWidth() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 210 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "column2" );
    column3.setWidth( 220 );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 210, metrics[ 0 ].width );
    assertEquals( 200, metrics[ 1 ].width );
    assertEquals( 220, metrics[ 2 ].width );
  }

  public void testGetItemMetricsTextLeftWithImage() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( 1, "item12" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 203, metrics[ 1 ].textLeft );
    item1.setImage( 1, image );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 306, metrics[ 1 ].textLeft );
  }

  public void testGetItemMetricsTextLeftWithCheckbox() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.CHECK );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item" );
    item1.setImage( image );
    int expected = 123;
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( expected, metrics[ 0 ].textLeft );
  }

  public void testGetItemMetricsTextWidthWithCheckbox() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.CHECK );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item" );
    item1.setImage( image );
    int expected = 72;
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( expected, metrics[ 0 ].textWidth );
  }

  public void testPreserveValues() {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    // Selection_Listener
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {};
    tree.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // HeaderHight,HeaderVisible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Object headerheight = adapter.getPreserved( TreeLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( 0 ), headerheight );
    Object headervisible = adapter.getPreserved( TreeLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.FALSE, headervisible );
    Fixture.clearPreserved();
    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    headerheight = adapter.getPreserved( TreeLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( tree.getHeaderHeight() ), headerheight );
    headervisible = adapter.getPreserved( TreeLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.TRUE, headervisible );
    Fixture.clearPreserved();
    // column_count
    TreeColumn child1 = new TreeColumn( tree, SWT.NONE, 0 );
    child1.setText( "child1" );
    TreeColumn child2 = new TreeColumn( tree, SWT.NONE, 1 );
    child2.setText( "child2" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Integer columnCount
      = ( Integer )adapter.getPreserved( TreeLCA.PROP_COLUMN_COUNT );
    assertEquals( new Integer( 2 ), columnCount );
    Fixture.clearPreserved();
    // item metrics
    child1.setWidth( 150 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    ItemMetrics[] metrics
      = ( ItemMetrics[] )adapter.getPreserved( TreeLCA.PROP_ITEM_METRICS );
    assertEquals( 150, metrics[ 1 ].left );
    Fixture.clearPreserved();
    // item height
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Integer itemHeight
      = ( Integer )adapter.getPreserved( TreeLCA.PROP_ITEM_HEIGHT );
    assertEquals( new Integer( 56 ) , itemHeight );
    Fixture.clearPreserved();
    // scroll left
    ITreeAdapter treeAdapter
      = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    treeAdapter.setScrollLeft( 50 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( new Integer( 50 ),
                  adapter.getPreserved( TreeLCA.PROP_SCROLL_LEFT ) );
    Fixture.clearPreserved();
    // scroll bars
    Fixture.preserveWidgets();
    Object preserved = adapter.getPreserved( TreeLCA.PROP_HAS_H_SCROLL_BAR );
    assertTrue( preserved != null );
    preserved = adapter.getPreserved( TreeLCA.PROP_HAS_V_SCROLL_BAR );
    assertTrue( preserved != null );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    tree.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    tree.setEnabled( true );
    // visible
    tree.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    tree.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( tree );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "1 Item" );
    tree.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    tree.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    tree.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    tree.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    tree.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, tree.getToolTipText() );
    Fixture.clearPreserved();
    tree.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( "some text", tree.getToolTipText() );
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    tree.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( tree, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Tree tree = new Tree( shell, SWT.NONE );
    final TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        log.append( "itemSelected" );
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( true, event.doit );
        // ensure same behaviour as SWT: bounds are undefined in tree selection
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".item",
                              treeItemId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testDefaultSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Tree tree = new Tree( shell, SWT.NONE );
    final TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new SelectionAdapter() {

      public void widgetDefaultSelected( final SelectionEvent event ) {
        log.append( "itemSelected" );
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( true, event.doit );
        // ensure same behaviour as SWT: bounds are undefined in tree selection
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item",
                              treeItemId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testDefaultSelectionEventUntyped() {
    final StringBuffer log = new StringBuffer();
    final Tree tree = new Tree( shell, SWT.NONE );
    final TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addListener( SWT.DefaultSelection, new Listener() {

      public void handleEvent( final Event event ) {
        log.append( "itemSelected" );
        assertEquals( treeItem, event.item );
        assertEquals( true, event.doit );
        // ensure same behaviour as SWT: bounds are undefined in tree selection
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item",
                              treeItemId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testInvalidScrollValues() {
    final Tree tree = new Tree( shell, SWT.NONE );
    String treeId = WidgetUtil.getId( tree );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( treeId + ".scrollLeft", "undefined" );
    Fixture.fakeRequestParam( treeId + ".scrollTop", "80" );
    Fixture.executeLifeCycleFromServerThread();
    ITreeAdapter adapter = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    //assertEquals( 80, adapter.getScrollTop() );
    assertEquals( 0, adapter.getScrollLeft() );
  }

  public void testScrollbarsSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ArrayList log = new ArrayList();
    Tree tree = new Tree( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( "scrollbarSelected" );
      }
    };
    tree.getHorizontalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    String tableId = WidgetUtil.getId( tree );
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.readDataAndProcessAction( tree );
    assertEquals( 1, log.size() );
    assertEquals( 10, tree.getHorizontalBar().getSelection() );
    log.clear();
    tree.getVerticalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "10" );
    Fixture.readDataAndProcessAction( tree );
    assertEquals( 2, log.size() );
    assertEquals( 10 * tree.getItemHeight(),
                  tree.getVerticalBar().getSelection());
  }

  public void testWriteScrollbarsSelectionListener() throws IOException {
    Fixture.fakeNewRequest();
    Tree tree = new Tree( shell, SWT.NONE );
    SelectionAdapter listener = new SelectionAdapter() {
    };
    tree.getHorizontalBar().addSelectionListener( listener );
    TreeLCA lca = new TreeLCA();
    lca.renderChanges( tree );
    String markup = Fixture.getAllMarkup();
    String expected = "w.setHasScrollBarsSelectionListener( true );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  // TODO [tb] : Test for fake redraw calls checkAllData

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }
}
