/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TreeLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    // Selection_Listener
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    tree.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // Tree_Listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( TreeLCA.PROP_TREE_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    TreeListener treeListener = new TreeListener() {

      public void treeCollapsed( final TreeEvent e ) {
      }

      public void treeExpanded( final TreeEvent e ) {
      }
    };
    tree.addTreeListener( treeListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( TreeLCA.PROP_TREE_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // HeaderHight,HeaderVisible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Object headerheight = adapter.getPreserved( TreeLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( 0 ), headerheight );
    Object headervisible = adapter.getPreserved( TreeLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.FALSE, headervisible );
    RWTFixture.clearPreserved();
    tree.setHeaderVisible( true );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    headerheight = adapter.getPreserved( TreeLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( tree.getHeaderHeight() ), headerheight );
    headervisible = adapter.getPreserved( TreeLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.TRUE, headervisible );
    RWTFixture.clearPreserved();
    // column_order
    TreeColumn child1 = new TreeColumn( tree, SWT.NONE, 0 );
    child1.setText( "child1" );
    TreeColumn child2 = new TreeColumn( tree, SWT.NONE, 1 );
    child2.setText( "child2" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    int[] columnOrder1 = tree.getColumnOrder();
    Integer[] columnOrder2 = ( Integer[] )adapter.getPreserved( TreeLCA.PROP_COLUMN_ORDER );
    assertEquals( new Integer( columnOrder1[ 0 ] ), columnOrder2[ 0 ] );
    assertEquals( new Integer( columnOrder1[ 1 ] ), columnOrder2[ 1 ] );
    RWTFixture.clearPreserved();
    // control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    tree.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    tree.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( tree );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    tree.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    tree.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    tree.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    tree.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    tree.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    tree.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    // tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, tree.getToolTipText() );
    RWTFixture.clearPreserved();
    tree.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( "some text", tree.getToolTipText() );
    RWTFixture.clearPreserved();
    // activate_listeners Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    tree.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( tree, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
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
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testDefaultSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
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
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testDefaultSelectionEventUntyped() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
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
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( "itemSelected", log.toString() );
  }

  public void testInvalidScrollValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final Tree tree = new Tree( shell, SWT.NONE );
    String treeId = WidgetUtil.getId( tree );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( treeId + ".scrollLeft", "undefined" );
    Fixture.fakeRequestParam( treeId + ".scrollTop", "80" );
    RWTFixture.executeLifeCycleFromServerThread();
    ITreeAdapter adapter = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    assertEquals( 80, adapter.getScrollTop() );
    assertEquals( 0, adapter.getScrollLeft() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
