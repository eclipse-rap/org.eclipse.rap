/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public final class CoolBarLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.FLAT );
    Boolean hasListeners;
    RWTFixture.markInitialized( bar );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( bar );
    lca.preserveValues( bar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.LOCKED ) );
    RWTFixture.clearPreserved();
    bar.setLocked( true );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.LOCKED ) );
    RWTFixture.clearPreserved();
    // control: enabled
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    bar.setEnabled( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // visible
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    bar.setVisible( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // menu
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( bar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    bar.setMenu( menu );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    bar.setBounds( rectangle );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // control_listeners is always true
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // z-index
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
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
    RWTFixture.clearPreserved();
    // tab_index
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // tooltiptext
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, bar.getToolTipText() );
    RWTFixture.clearPreserved();
    bar.setToolTipText( "some text" );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( "some text", bar.getToolTipText() );
    RWTFixture.clearPreserved();
    // activate_listeners
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( bar, new ActivateAdapter() {
    } );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testItemPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.FLAT );
    CoolItem item = new CoolItem( bar, SWT.NONE );
    Button button = new Button( bar, SWT.NONE );
    RWTFixture.markInitialized( item );
    item.setControl( button );
    item.setSize( 30, 20 );
    Rectangle rectangle = new Rectangle( 0,
                                         0,
                                         item.getSize().x,
                                         item.getSize().y );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( item );
    lca.preserveValues( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( button, adapter.getPreserved( Props.CONTROL ) );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }


  // public void testItemReordering1() {
  // RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  // Display display = new Display();
  // Shell shell = new Shell( display, SWT.NONE );
  // CoolBar bar = new CoolBar( shell, SWT.FLAT );
  // CoolItem item0 = new CoolItem( bar, SWT.NONE );
  // item0.setSize( 10, 10 );
  // CoolItem item1 = new CoolItem( bar, SWT.NONE );
  // item1.setSize( 10, 10 );
  // CoolItem item2 = new CoolItem( bar, SWT.NONE );
  // item2.setSize( 10, 10 );
  // String item0Id = WidgetUtil.getId( item0 );
  // String item2Id = WidgetUtil.getId( item2 );
  // String item1Id = WidgetUtil.getId( item1 );
  // AbstractWidgetLCA coolItemLCA = WidgetUtil.getLCA( item2 );
  // // get adapter to set item order
  // Object adapter = bar.getAdapter( ICoolBarAdapter.class );
  // ICoolBarAdapter cba = (ICoolBarAdapter) adapter;
  //
  // // ensure initial state
  // assertEquals( 0, bar.getItemOrder()[ 0 ] );
  // assertEquals( 1, bar.getItemOrder()[ 1 ] );
  // assertEquals( 2, bar.getItemOrder()[ 2 ] );
  //
  // // Simulate that item2 is dragged left of item1
  // int newX = item1.getBounds().x - 4;
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item2Id );
  // Fixture.fakeRequestParam( item2Id + ".bounds.x", String.valueOf( newX ) );
  // coolItemLCA.readData( item2 );
  // assertEquals( 0, bar.getItemOrder()[ 0 ] );
  // assertEquals( 2, bar.getItemOrder()[ 1 ] );
  // assertEquals( 1, bar.getItemOrder()[ 2 ] );
  //
  // // Simulate that item0 is dragged after the last item
  // cba.setItemOrder( new int[] { 0, 1, 2, } );
  // newX = item2.getBounds().x + item2.getBounds().width + 10;
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
  // Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
  // coolItemLCA.readData( item0 );
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item2 ) ] );
  // assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item0 ) ] );
  //
  // // Simulate that item0 is dragged onto itself -> nothing should change
  // cba.setItemOrder( new int[] { 0, 1, 2, } );
  // newX = item0.getBounds().x + 2;
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
  // Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
  // coolItemLCA.readData( item0 );
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item0 ) ] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
  // assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item2 ) ] );
  //
  // // Simulate that item1 is before the first item
  // cba.setItemOrder( new int[] { 0, 1, 2, } );
  // newX = item0.getBounds().x - 5;
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item1Id );
  // Fixture.fakeRequestParam( item1Id + ".bounds.x", String.valueOf( newX ) );
  // coolItemLCA.readData( item1 );
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item0 ) ] );
  // assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item2 ) ] );
  // }
  // public void testItemReordering2() throws IOException {
  // Display display = new Display();
  // Shell shell = new Shell( display, SWT.NONE );
  // shell.setLayout( new RowLayout() );
  // CoolBar bar = new CoolBar( shell, SWT.FLAT );
  // CoolItem item0 = new CoolItem( bar, SWT.NONE );
  // item0.setControl( new ToolBar( bar, SWT.NONE ) );
  // item0.setSize( 250, 25 );
  // CoolItem item1 = new CoolItem( bar, SWT.NONE );
  // item1.setSize( 250, 25 );
  // item1.setControl( new ToolBar( bar, SWT.NONE ) );
  // shell.layout();
  // shell.open();
  // // Set up environment; get displayId first as it currently is in 'real
  // life'
  // String displayId = DisplayUtil.getId( display );
  // String item0Id = WidgetUtil.getId( item0 );
  // RWTFixture.markInitialized( display );
  // RWTFixture.markInitialized( shell );
  // RWTFixture.markInitialized( bar );
  // RWTFixture.markInitialized( item0 );
  // RWTFixture.markInitialized( item0.getControl() );
  // RWTFixture.markInitialized( item1 );
  // RWTFixture.markInitialized( item1.getControl() );
  // RWTLifeCycle lifeCycle = createLifeCycle();
  //
  // // get adapter to set item order
  // Object adapter = bar.getAdapter( ICoolBarAdapter.class );
  // ICoolBarAdapter cba = (ICoolBarAdapter) adapter;
  //
  // // Drag item0 and drop it inside the bounds of item1
  // cba.setItemOrder( new int[] { 0, 1 } );
  // RWTFixture.fakeNewRequest();
  // Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
  // Fixture.fakeRequestParam( item0Id + ".bounds.x", "483" );
  // Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
  // lifeCycle.execute();
  // RWTFixture.fakeUIThread();
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 )] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item0 )] );
  //
  // // Drag item0 and drop it beyound the bounds of item1
  // cba.setItemOrder( new int[] { 0, 1 } );
  // RWTFixture.removeUIThread();
  // RWTFixture.fakeNewRequest();
  // Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
  // Fixture.fakeRequestParam( item0Id + ".bounds.x", "2000" );
  // Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
  // lifeCycle.execute();
  // RWTFixture.fakeUIThread();
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 )] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item0 )] );
  // RWTFixture.removeUIThread();
  // }
  // public void testSnapBackItemMoved() throws IOException {
  // Display display = new Display();
  // Shell shell = new Shell( display, SWT.NONE );
  // shell.setLayout( new RowLayout() );
  // CoolBar bar = new CoolBar( shell, SWT.FLAT );
  // CoolItem item0 = new CoolItem( bar, SWT.NONE );
  // item0.setControl( new ToolBar( bar, SWT.NONE ) );
  // item0.setSize( 250, 25 );
  // CoolItem item1 = new CoolItem( bar, SWT.NONE );
  // item1.setSize( 250, 25 );
  // item1.setControl( new ToolBar( bar, SWT.NONE ) );
  // shell.layout();
  // shell.open();
  // // Set up environment; get displayId first as it currently is in 'real
  // life'
  // String displayId = DisplayUtil.getId( display );
  // String item0Id = WidgetUtil.getId( item0 );
  // RWTFixture.markInitialized( display );
  // RWTFixture.markInitialized( shell );
  // RWTFixture.markInitialized( bar );
  // RWTFixture.markInitialized( item0 );
  // RWTFixture.markInitialized( item0.getControl() );
  // RWTFixture.markInitialized( item1 );
  // RWTFixture.markInitialized( item1.getControl() );
  // RWTLifeCycle lifeCycle = createLifeCycle();
  //
  // // get adapter to set item order
  // Object adapter = bar.getAdapter( ICoolBarAdapter.class );
  // ICoolBarAdapter cba = (ICoolBarAdapter) adapter;
  //
  // // Simulate that fist item is dragged around but dropped at its original
  // // position
  // cba.setItemOrder( new int[] { 0, 1 } );
  // RWTFixture.fakeNewRequest();
  // Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
  // Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
  // Fixture.fakeRequestParam( item0Id + ".bounds.x", "250" );
  // Fixture.fakeRequestParam( item0Id + ".bounds.y", "0" );
  // lifeCycle.execute();
  // RWTFixture.fakeUIThread();
  // assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item0 )] );
  // assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item1 )] );
  // String expected
  // = "var w = wm.findWidgetById( \"" + item0Id + "\" );"
  // + "w.setSpace(";
  // assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  //    RWTFixture.removeUIThread();
  //  }
  //////////////////
  // Helping methods
  private static RWTLifeCycle createLifeCycle() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    return lifeCycle;
  }
}
