/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.scrolledcompositekit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScrolledCompositeLCA_Test {

 private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";

  private Display display;
  private Shell shell;
  private ScrolledComposite sc;
  private ScrollBar hScroll;
  private ScrollBar vScroll;
  private ScrolledCompositeLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    hScroll = sc.getHorizontalBar();
    vScroll = sc.getVerticalBar();
    lca = new ScrolledCompositeLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( sc );
    ControlLCATestUtil.testFocusListener( sc );
    ControlLCATestUtil.testMouseListener( sc );
    ControlLCATestUtil.testKeyListener( sc );
    ControlLCATestUtil.testTraverseListener( sc );
    ControlLCATestUtil.testMenuDetectListener( sc );
    ControlLCATestUtil.testHelpListener( sc );
  }

  @Test
  public void testPreserveValues() {
    WidgetAdapter adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( PROP_SHOW_FOCUSED_CONTROL ) );
    hScroll.setSelection( 23 );
    vScroll.setSelection( 42 );
    sc.setShowFocusedControl( true );
    assertEquals( 23, hScroll.getSelection() );
    assertEquals( 42, vScroll.getSelection() );
    Rectangle rectangle = new Rectangle( 12, 30, 20, 40 );
    sc.setBounds( rectangle );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, adapter.getPreserved( PROP_SHOW_FOCUSED_CONTROL ) );
    // bound
    sc.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( sc );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sc.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    sc.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sc.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sc.setEnabled( true );
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    sc.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    sc.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    sc.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, sc.getToolTipText() );
    Fixture.clearPreserved();
    sc.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( "some text", sc.getToolTipText() );
  }

  @Test
  public void testReadData_ScrollBarsSelection() {
    sc.setContent( new Composite( sc, SWT.NONE ) );

    Fixture.fakeSetProperty( getId( sc ), "horizontalBar.selection", 1 );
    Fixture.fakeSetProperty( getId( sc ), "verticalBar.selection", 2 );
    Fixture.readDataAndProcessAction( sc );

    assertEquals( new Point( 1, 2 ), sc.getOrigin() );
  }

  @Test
  public void testReadData_ScrollBarsSelectionEvent() {
    sc.setContent( new Composite( sc, SWT.NONE ) );
    SelectionListener selectionListener = mock( SelectionListener.class );
    hScroll.addSelectionListener( selectionListener );
    vScroll.addSelectionListener( selectionListener );

    Fixture.fakeNotifyOperation( getId( hScroll ), "Selection", null );
    Fixture.fakeNotifyOperation( getId( vScroll ), "Selection", null );
    Fixture.readDataAndProcessAction( sc );

    verify( selectionListener, times( 2 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertEquals( "rwt.widgets.ScrolledComposite", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "H_SCROLL" ) );
    assertTrue( Arrays.asList( styles ).contains( "V_SCROLL" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertEquals( WidgetUtil.getId( sc.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialContent() throws IOException {
    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertTrue( operation.getPropertyNames().indexOf( "content" ) == -1 );
  }

  @Test
  public void testRenderContent() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    String contentId = WidgetUtil.getId( content );

    sc.setContent( content );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( contentId, message.findSetProperty( sc, "content" ).asString() );
  }

  @Test
  public void testRenderContentUnchanged() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setContent( content );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "content" ) );
  }

  @Test
  public void testRenderInitialOrigin() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "selection" ) );
    assertNull( message.findSetOperation( vScroll, "selection" ) );
  }

  @Test
  public void testRenderOrigin() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    sc.setOrigin( 1, 2 );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 1, 2 ]" );
    assertEquals( expected, message.findSetProperty( sc, "origin" ) );
  }

  @Test
  public void testRenderOrigin_SetByScrollbar() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    hScroll.setSelection( 1 );
    vScroll.setSelection( 2 );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 1, 2 ]" );
    assertEquals( expected, message.findSetProperty( sc, "origin" ) );
  }

  @Test
  public void testRenderOriginUnchanged() throws IOException {
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setOrigin( 1, 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "selection" ) );
    assertNull( message.findSetOperation( vScroll, "selection" ) );
  }

  @Test
  public void testRenderInitialShowFocusedControl() throws IOException {
    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertTrue( operation.getPropertyNames().indexOf( "showFocusedControl" ) == -1 );
  }

  @Test
  public void testRenderShowFocusedControl() throws IOException {
    sc.setShowFocusedControl( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( sc, "showFocusedControl" ) );
  }

  @Test
  public void testRenderShowFocusedControlUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setShowFocusedControl( true );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "showFocusedControl" ) );
  }

  @Test
  public void testRenderAddScrollBarsSelectionListener_Horizontal() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( hScroll );
    Fixture.preserveWidgets();

    hScroll.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( hScroll, "Selection" ) );
  }

  @Test
  public void testRenderRemoveScrollBarsSelectionListener_Horizontal() throws Exception {
    SelectionListener listener = new SelectionAdapter() { };
    hScroll.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( hScroll );
    Fixture.preserveWidgets();

    hScroll.removeSelectionListener( listener );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( hScroll, "Selection" ) );
  }

  @Test
  public void testRenderScrollBarsSelectionListenerUnchanged_Horizontal() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( hScroll );
    Fixture.preserveWidgets();

    hScroll.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( hScroll, "Selection" ) );
  }

  @Test
  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( vScroll );
    Fixture.preserveWidgets();

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( vScroll, "Selection" ) );
  }

  @Test
  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    SelectionListener listener = new SelectionAdapter() { };
    vScroll.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( vScroll );
    Fixture.preserveWidgets();

    vScroll.removeSelectionListener( listener );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( vScroll, "Selection" ) );
  }

  @Test
  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( vScroll );
    Fixture.preserveWidgets();

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( vScroll, "Selection" ) );
  }

  @Test
  public void testRenderInitialScrollBarsVisible() throws IOException {
    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisible_Horizontal() throws IOException {
    hScroll.setVisible( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisible_Vertical() throws IOException {
    vScroll.setVisible( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertEquals( JsonValue.TRUE, message.findSetProperty( vScroll, "visibility" ) );
  }

  @Test
  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.markInitialized( hScroll );
    Fixture.markInitialized( vScroll );

    hScroll.setVisible( false );
    vScroll.setVisible( false );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hScroll, "visibility" ) );
    assertNull( message.findSetOperation( vScroll, "visibility" ) );
  }

}
