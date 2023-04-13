/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_IN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_OUT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_HELP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MENU_DETECT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_UP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SET_DATA;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_TRAVERSE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class TreeOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Tree tree;
  private Tree mockedTree;
  private TreeOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    tree = new Tree( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    tree.setBounds( 0, 0, 100, 100 );
    mockedTree = mock( Tree.class );
    handler = new TreeOperationHandler( mockedTree );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetSelection_single() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 3 );
    TreeItem item = tree.getItem( 1 );

    JsonArray selection = new JsonArray().add( getId( item ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new TreeItem[] { item }, tree.getSelection() );
  }

  @Test
  public void testHandleSetSelection_multi() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 3 );
    TreeItem item1 = tree.getItem( 0 );
    TreeItem item2 = tree.getItem( 2 );

    JsonArray selection = new JsonArray().add( getId( item1 ) ).add( getId( item2 ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new TreeItem[] { item1, item2 }, tree.getSelection() );
  }

  @Test
  public void testHandleSetScrollLeft() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 1 );
    TreeItem item = tree.getItem( 0 );
    item.setText( "very long text that makes horizontal bar visible" );

    handler.handleSet( new JsonObject().add( "scrollLeft", 1 ) );

    assertEquals( 1, getTreeAdapter( tree ).getScrollLeft() );
  }

  @Test
  public void testHandleSetScrollLeft_setsHorizontalScrollBarSelection() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 1 );
    TreeItem item = tree.getItem( 0 );
    item.setText( "very long text that makes horizontal bar visible" );

    handler.handleSet( new JsonObject().add( "scrollLeft", 1 ) );

    assertEquals( 1, tree.getHorizontalBar().getSelection() );
  }

  @Test
  public void testHandleSetTopItemIndex() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 10 );

    handler.handleSet( new JsonObject().add( "topItemIndex", 1 ) );

    assertEquals( tree.getItem( 1 ), tree.getTopItem() );
  }

  @Test
  public void testHandleSetTopItemIndex_setsVerticalScrollBarSelection() {
    handler = new TreeOperationHandler( tree );
    createTreeItems( tree, 10 );

    handler.handleSet( new JsonObject().add( "topItemIndex", 1 ) );

    assertEquals( tree.getItemHeight(), tree.getVerticalBar().getSelection() );
  }

  @Test
  public void testHandleNotifySelection() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifySelection_withDetail_hyperlink() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "hyperlink" )
      .add( "text", "foo" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( RWT.HYPERLINK, event.detail );
    assertEquals( "foo", event.text );
  }

  @Test
  public void testHandleNotifySelection_withDetail_check() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "check" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( SWT.CHECK, event.detail );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifyFocusIn() {
    handler.handleNotify( EVENT_FOCUS_IN, new JsonObject() );

    verify( mockedTree ).notifyListeners( eq( SWT.FocusIn ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusOut() {
    handler.handleNotify( EVENT_FOCUS_OUT, new JsonObject() );

    verify( mockedTree ).notifyListeners( eq( SWT.FocusOut ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDown() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.MouseDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyMouseDown_skippedOnHeader() {
    tree.setHeaderVisible( true );
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 10 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( spyTree, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDoubleClick() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOUBLE_CLICK, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.MouseDoubleClick ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 2, event.count );
  }

  @Test
  public void testHandleNotifyMouseUp() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_UP, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.MouseUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyTraverse() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );
    handler.handleNotify( EVENT_TRAVERSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedTree ).notifyListeners( eq( SWT.Traverse ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 9, event.keyCode );
    assertEquals( 9, event.character );
  }

  @Test
  public void testHandleNotifyTraverse_wrongModifier() {
    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );
    handler.handleNotify( EVENT_TRAVERSE, properties );

    verify( mockedTree, never() ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyKeyDown() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );
    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedTree ).notifyListeners( eq( SWT.KeyDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotifyKeyUp() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );
    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedTree ).notifyListeners( eq( SWT.KeyUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotifyMenuDetect() {
    JsonObject properties = new JsonObject().add( "x", 1 ).add( "y", 2 );
    handler.handleNotify( EVENT_MENU_DETECT, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedTree ).notifyListeners( eq( SWT.MenuDetect ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
  }

  @Test
  public void testHandleNotifyHelp() {
    handler.handleNotify( EVENT_HELP, new JsonObject() );

    verify( mockedTree ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyExpand() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_EXPAND, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.Expand ), captor.capture() );
    assertSame( item, captor.getValue().item );
  }

  /* 438023: [Tree] Expand/Collapse event has null item in some cases
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=438023
   */
  @Test
  public void testHandleNotifyExpand_withDisposedItem() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );
    item.dispose();

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_EXPAND, properties );

    verify( spyTree, never() ).notifyListeners( eq( SWT.Expand ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyCollapse() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_COLLAPSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyTree ).notifyListeners( eq( SWT.Collapse ), captor.capture() );
    assertSame( item, captor.getValue().item );
  }

  /* 438023: [Tree] Expand/Collapse event has null item in some cases
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=438023
   */
  @Test
  public void testHandleNotifyCollapse_withDisposedItem() {
    Tree spyTree = spy( tree );
    handler = new TreeOperationHandler( spyTree );
    TreeItem item = new TreeItem( spyTree, SWT.NONE );
    item.dispose();

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_COLLAPSE, properties );

    verify( spyTree, never() ).notifyListeners( eq( SWT.Collapse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifySetData() {
    handler.handleNotify( EVENT_SET_DATA, new JsonObject() );

    verify( mockedTree, never() ).notifyListeners( eq( SWT.SetData ), any( Event.class ) );
  }

  @Test
  public void testHandleCallRenderToolTipText() {
    handler = new TreeOperationHandler( tree );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
        buffer.append( getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );

    JsonObject properties = new JsonObject().add( "item", getId( item ) ).add( "column", 0 );
    handler.handleCall( "renderToolTipText", properties );

    assertEquals( getId( item ) + ",0", CellToolTipUtil.getAdapter( tree ).getCellToolTipText() );
  }

  private static void createTreeItems( Tree tree, int number ) {
    for( int i = 0; i < number; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "item " + i );
    }
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    return tree.getAdapter( ITreeAdapter.class );
  }

}
