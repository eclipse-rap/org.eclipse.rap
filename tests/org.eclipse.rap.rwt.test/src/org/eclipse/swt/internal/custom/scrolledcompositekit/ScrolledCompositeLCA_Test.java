/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;


public class ScrolledCompositeLCA_Test extends TestCase {

 private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";

  private Display display;
  private Shell shell;
  private ScrolledCompositeLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ScrolledCompositeLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    int scStyle = SWT.H_SCROLL | SWT.V_SCROLL;
    ScrolledComposite sc = new ScrolledComposite( shell, scStyle );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( PROP_SHOW_FOCUSED_CONTROL ) );
    sc.getHorizontalBar().setSelection( 23 );
    sc.getVerticalBar().setSelection( 42 );
    sc.setShowFocusedControl( true );
    assertEquals( 23, sc.getHorizontalBar().getSelection() );
    assertEquals( 42, sc.getVerticalBar().getSelection() );
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
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    // control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    sc.addControlListener( new ControlAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    sc.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    sc.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    sc.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    sc.addFocusListener( new FocusAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( sc, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadData() {
    final ArrayList<String> log = new ArrayList<String>();
    int scStyle = SWT.H_SCROLL | SWT.V_SCROLL;
    ScrolledComposite sc = new ScrolledComposite( shell, scStyle );
    sc.setContent( new Composite( sc, SWT.NONE ) );
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( "widgetSelected" );
      }
    };
    sc.getHorizontalBar().addSelectionListener( selectionListener );
    sc.getVerticalBar().addSelectionListener( selectionListener );
    String scId = WidgetUtil.getId( sc );
    Fixture.fakeRequestParam( scId + ".horizontalBar.selection", "10" );
    Fixture.fakeRequestParam( scId + ".verticalBar.selection", "10" );
    Fixture.readDataAndProcessAction( sc );
    assertEquals( 2, log.size() );
    assertEquals( new Point( 10, 10 ), sc.getOrigin() );
    assertEquals( 10, sc.getHorizontalBar().getSelection() );
    assertEquals( 10, sc.getVerticalBar().getSelection() );
    log.clear();
    Fixture.fakeRequestParam( scId + ".horizontalBar.selection", null );
    Fixture.fakeRequestParam( scId + ".verticalBar.selection", "20" );
    Fixture.readDataAndProcessAction( sc );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 10, 20 ), sc.getOrigin() );
    assertEquals( 10, sc.getHorizontalBar().getSelection() );
    assertEquals( 20, sc.getVerticalBar().getSelection() );
    log.clear();
    Fixture.fakeRequestParam( scId + ".horizontalBar.selection", "20" );
    Fixture.fakeRequestParam( scId + ".verticalBar.selection", null );
    Fixture.readDataAndProcessAction( sc );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 20, 20 ), sc.getOrigin() );
    assertEquals( 20, sc.getHorizontalBar().getSelection() );
    assertEquals( 20, sc.getVerticalBar().getSelection() );
    log.clear();
    Fixture.fakeRequestParam( scId + ".horizontalBar.selection", null );
    Fixture.fakeRequestParam( scId + ".verticalBar.selection", null );
    assertEquals( 0, log.size() );
  }

  public void testRenderCreate() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.renderInitialization( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertEquals( "rwt.widgets.ScrolledComposite", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "H_SCROLL" ) );
    assertTrue( Arrays.asList( styles ).contains( "V_SCROLL" ) );
  }

  public void testRenderParent() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.renderInitialization( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertEquals( WidgetUtil.getId( sc.getParent() ), operation.getParent() );
  }

  public void testRenderInitialContent() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertTrue( operation.getPropertyNames().indexOf( "content" ) == -1 );
  }

  public void testRenderContent() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    String contentId = WidgetUtil.getId( content );

    sc.setContent( content );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( contentId, message.findSetProperty( sc, "content" ) );
  }

  public void testRenderContentUnchanged() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setContent( content );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "content" ) );
  }

  public void testRenderInitialOrigin() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertTrue( operation.getPropertyNames().indexOf( "origin" ) == -1 );
  }

  public void testRenderOrigin() throws IOException, JSONException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    sc.setOrigin( 1, 2 );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( sc, "origin" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 1, 2 ]", actual ) );
  }

  public void testRenderOriginUnchanged() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setOrigin( 1, 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "origin" ) );
  }

  public void testRenderInitialShowFocusedControl() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sc );
    assertTrue( operation.getPropertyNames().indexOf( "showFocusedControl" ) == -1 );
  }

  public void testRenderShowFocusedControl() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    sc.setShowFocusedControl( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( sc, "showFocusedControl" ) );
  }

  public void testRenderShowFocusedControlUnchanged() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.setShowFocusedControl( true );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "showFocusedControl" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Horizontal() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( sc, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Horizontal() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    sc.getHorizontalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getHorizontalBar().removeSelectionListener( listener );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( sc, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Horizontal() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( sc, "scrollBarsSelection" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( sc, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    sc.getVerticalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getVerticalBar().removeSelectionListener( listener );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( sc, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );
    Fixture.preserveWidgets();

    sc.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( sc, "scrollBarsSelection" ) );
  }

  public void testRenderInitialScrollBarsVisible() throws IOException, JSONException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( sc );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findCreateProperty( sc, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Horizontal() throws IOException, JSONException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    sc.getHorizontalBar().setVisible( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( sc, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ true, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Vertical() throws IOException, JSONException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    sc.getVerticalBar().setVisible( true );
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( sc, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, true ]", actual ) );
  }

  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( sc );

    sc.getHorizontalBar().setVisible( false );
    sc.getVerticalBar().setVisible( false );
    Fixture.preserveWidgets();
    lca.renderChanges( sc );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( sc, "scrollBarsVisible" ) );
  }
}
