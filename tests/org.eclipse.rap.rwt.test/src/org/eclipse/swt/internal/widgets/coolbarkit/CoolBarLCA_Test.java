/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ICoolBarAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;


public final class CoolBarLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolBarLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    lca = new CoolBarLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( bar );
    ControlLCATestUtil.testMouseListener( bar );
    ControlLCATestUtil.testKeyListener( bar );
    ControlLCATestUtil.testTraverseListener( bar );
    ControlLCATestUtil.testMenuDetectListener( bar );
    ControlLCATestUtil.testHelpListener( bar );
  }

  public void testPreserveValues() {
    Fixture.markInitialized( bar );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( bar );
    lca.preserveValues( bar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
    Fixture.clearPreserved();
    bar.setLocked( true );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
    Fixture.clearPreserved();
    // control: enabled
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    bar.setEnabled( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    bar.setEnabled( true );
    // visible
    bar.setSize( 10, 10 );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    bar.setVisible( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( bar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    bar.setMenu( menu );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    bar.setBounds( rectangle );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // z-index
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    bar.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    bar.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    bar.setFont( font );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltiptext
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, bar.getToolTipText() );
    Fixture.clearPreserved();
    bar.setToolTipText( "some text" );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( "some text", bar.getToolTipText() );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( "rwt.widgets.CoolBar", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( WidgetUtil.getId( bar.getParent() ), operation.getParent() );
  }

  public void testRenderLocked() throws IOException {
    lca.preserveValues( bar );
    bar.setLocked( true );
    lca.renderChanges( bar );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( bar, "locked" );
    assertEquals( Boolean.TRUE, operation.getProperty( "locked" ) );
  }

  public void testItemReordering1() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    bar.setSize( 100, 10 );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setSize( 10, 10 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 10, 10 );
    CoolItem item2 = new CoolItem( bar, SWT.NONE );
    item2.setSize( 10, 10 );
    String item0Id = WidgetUtil.getId( item0 );
    String item2Id = WidgetUtil.getId( item2 );
    String item1Id = WidgetUtil.getId( item1 );
    AbstractWidgetLCA coolItemLCA = WidgetUtil.getLCA( item2 );
    // get adapter to set item order
    Object adapter = bar.getAdapter( ICoolBarAdapter.class );
    ICoolBarAdapter cba = (ICoolBarAdapter) adapter;

    // ensure initial state
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );

    // Simulate that item2 is dragged left of item1
    int newX = item1.getBounds().x - 4;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item2Id );
    Fixture.fakeRequestParam( item2Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item2 );
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 2, bar.getItemOrder()[ 1 ] );
    assertEquals( 1, bar.getItemOrder()[ 2 ] );

    // Simulate that item0 is dragged after the last item
    cba.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item2.getBounds().x + item2.getBounds().width + 10;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item0 );
    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 2, bar.getItemOrder()[ 1 ] );
    assertEquals( 0, bar.getItemOrder()[ 2 ] );

    // Simulate that item0 is dragged onto itself -> nothing should change
    cba.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item0.getBounds().x + 2;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item0 );
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );

    // Simulate that item1 is before the first item
    cba.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item0.getBounds().x - 5;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item1Id );
    Fixture.fakeRequestParam( item1Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item1 );
    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );
  }

  public void testItemReordering2() {
    shell.setLayout( new RowLayout() );
    bar.setSize(400, 25 );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setControl( new ToolBar( bar, SWT.NONE ) );
    item0.setSize( 250, 25 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 250, 25 );
    item1.setControl( new ToolBar( bar, SWT.NONE ) );
    shell.layout();
    shell.open();
    // Set up environment; get displayId first as it currently is in 'real life'
    String item0Id = WidgetUtil.getId( item0 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( bar );
    Fixture.markInitialized( item0 );
    Fixture.markInitialized( item0.getControl() );
    Fixture.markInitialized( item1 );
    Fixture.markInitialized( item1.getControl() );

    // get adapter to set item order
    Object adapter = bar.getAdapter( ICoolBarAdapter.class );
    ICoolBarAdapter cba = (ICoolBarAdapter) adapter;

    // Drag item0 and drop it inside the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", "483" );
    Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );

    // Drag item0 and drop it beyond the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", "2000" );
    Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );
  }

  public void testSnapBackItemMoved() {
    shell.setLayout( new RowLayout() );
    CoolBar bar = new CoolBar( shell, SWT.FLAT );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setControl( new ToolBar( bar, SWT.NONE ) );
    item0.setSize( 250, 25 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 250, 25 );
    item1.setControl( new ToolBar( bar, SWT.NONE ) );
    shell.layout();
    shell.open();
    // Set up environment; get displayId first as it currently is in 'real life'
    String item0Id = WidgetUtil.getId( item0 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( bar );
    Fixture.markInitialized( item0 );
    Fixture.markInitialized( item0.getControl() );
    Fixture.markInitialized( item1 );
    Fixture.markInitialized( item1.getControl() );

    // get adapter to set item order
    Object adapter = bar.getAdapter( ICoolBarAdapter.class );
    ICoolBarAdapter cba = (ICoolBarAdapter) adapter;

    // Simulate that fist item is dragged around but dropped at its original
    // position
    cba.setItemOrder( new int[] { 0, 1 } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", "10" );
    Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( item0, "bounds" ) );
  }

}
