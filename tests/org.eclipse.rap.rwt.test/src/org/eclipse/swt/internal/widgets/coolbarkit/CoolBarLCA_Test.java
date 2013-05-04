/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
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
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class CoolBarLCA_Test {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    lca = new CoolBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( bar );
    ControlLCATestUtil.testMouseListener( bar );
    ControlLCATestUtil.testKeyListener( bar );
    ControlLCATestUtil.testTraverseListener( bar );
    ControlLCATestUtil.testMenuDetectListener( bar );
    ControlLCATestUtil.testHelpListener( bar );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( bar );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( bar );
    lca.preserveValues( bar );
    WidgetAdapter adapter = WidgetUtil.getAdapter( bar );
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
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    bar.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    bar.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    bar.setFont( font );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
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

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( "rwt.widgets.CoolBar", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( WidgetUtil.getId( bar.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderLocked() throws IOException {
    lca.preserveValues( bar );
    bar.setLocked( true );
    lca.renderChanges( bar );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( bar, "locked" );
    assertEquals( JsonValue.TRUE, operation.getProperty( "locked" ) );
  }

  @Test
  public void testItemReordering1() {
    bar.setSize( 400, 25 );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setSize( 10, 10 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 10, 10 );
    CoolItem item2 = new CoolItem( bar, SWT.NONE );
    item2.setSize( 10, 10 );
    // get adapter to set item order
    ICoolBarAdapter cba = bar.getAdapter( ICoolBarAdapter.class );

    // ensure initial state
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );

    // Simulate that item2 is dragged left of item1
    fakeMove( item2, item1.getBounds().x - 4, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 2, bar.getItemOrder()[ 1 ] );
    assertEquals( 1, bar.getItemOrder()[ 2 ] );

    // Simulate that item0 is dragged after the last item
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    fakeMove( item0, item2.getBounds().x + item2.getBounds().width + 10, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 2, bar.getItemOrder()[ 1 ] );
    assertEquals( 0, bar.getItemOrder()[ 2 ] );

    // Simulate that item0 is dragged onto itself -> nothing should change
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    fakeMove( item0, item0.getBounds().x + 2, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );

    // Simulate that item1 is before the first item
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    fakeMove( item1, item0.getBounds().x - 5, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );
  }

  @Test
  public void testItemReordering2() {
    bar.setSize( 400, 25 );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setSize( 250, 25 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 250, 25 );
    // get adapter to set item order
    ICoolBarAdapter cba = bar.getAdapter( ICoolBarAdapter.class );

    // Drag item0 and drop it inside the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );

    fakeMove( item0, 483, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );

    // Drag item0 and drop it beyond the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );

    fakeMove( item0, 2000, 0 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );
  }

  @Test
  public void testSnapBackItemMoved() {
    bar.setSize( 400, 25 );
    CoolItem item0 = new CoolItem( bar, SWT.NONE );
    item0.setSize( 250, 25 );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 250, 25 );
    // Set up environment; get displayId first as it currently is in 'real life'
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( bar );
    Fixture.markInitialized( item0 );
    Fixture.markInitialized( item1 );
    // get adapter to set item order
    ICoolBarAdapter cba = bar.getAdapter( ICoolBarAdapter.class );

    // Simulate that fist item is dragged around but dropped at its original
    // position
    cba.setItemOrder( new int[] { 0, 1 } );

    fakeMove( item0, 10, 0 );
    Fixture.executeLifeCycleFromServerThread();

    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( item0, "bounds" ) );
  }

  private void fakeMove( CoolItem coolItem, int x, int y ) {
    Fixture.fakeNewRequest();
    Fixture.fakeCallOperation( getId( coolItem ), "move", new JsonObject().add( "left", x ) );
  }

}
