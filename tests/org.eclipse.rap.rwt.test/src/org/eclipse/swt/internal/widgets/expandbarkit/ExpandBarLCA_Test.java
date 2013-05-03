/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expandbarkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExpandBarLCA_Test {

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    lca = new ExpandBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( expandBar );
    ControlLCATestUtil.testFocusListener( expandBar );
    ControlLCATestUtil.testMouseListener( expandBar );
    ControlLCATestUtil.testKeyListener( expandBar );
    ControlLCATestUtil.testTraverseListener( expandBar );
    ControlLCATestUtil.testMenuDetectListener( expandBar );
    ControlLCATestUtil.testHelpListener( expandBar );
  }

  @Test
  public void testPreserveValues() {
    expandBar.setSize( expandBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    Fixture.markInitialized( display );
    // control: enabled
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    expandBar.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( expandBar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    expandBar.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    expandBar.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    expandBar.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    expandBar.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
  }

  @Test
  public void testRenderCreateWithVScroll() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "V_SCROLL" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( WidgetUtil.getId( expandBar.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandBar );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandBar ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialBottomSpacingBounds() throws IOException {
    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findCreateProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBottomSpacingBounds() throws IOException {
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findSetProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBottomSpacingBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "boubottomSpacingBoundsnds" ) );
  }

  @Test
  public void testRenderInitialVScrollBarVisible() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderVScrollBarVisible() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    new ExpandItem( expandBar, SWT.NONE );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderVScrollBarVisibleUnchanged() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );
    Fixture.markInitialized( vScroll );

    new ExpandItem( expandBar, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderInitialVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertTrue( operation.getPropertyNames().indexOf( "vScrollBarMax" ) == -1 );
  }

  @Test
  public void testRenderVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    new ExpandItem( expandBar, SWT.NONE );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetProperty( expandBar, "vScrollBarMax" ) );
  }

  @Test
  public void testRenderVScrollBarMaxUnchanged() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    new ExpandItem( expandBar, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "vScrollBarMax" ) );
  }

  @Test
  public void testRenderAddExpandListener() throws Exception {
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( expandBar, "Expand" ) );
  }

  @Test
  public void testRenderAddCollapseListener() throws Exception {
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( expandBar, "Collapse" ) );
  }

  @Test
  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    Fixture.markInitialized( vScroll );
    Fixture.preserveWidgets();

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( vScroll, "Selection" ) );
  }

  @Test
  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    SelectionListener listener = new SelectionAdapter() { };
    vScroll.addSelectionListener( listener );
    Fixture.markInitialized( vScroll );
    Fixture.preserveWidgets();

    vScroll.removeSelectionListener( listener );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( vScroll, "Selection" ) );
  }

  @Test
  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ScrollBar vScroll = expandBar.getVerticalBar();
    Fixture.markInitialized( display );
    Fixture.markInitialized( vScroll );

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( vScroll, "Selection" ) );
  }

}
