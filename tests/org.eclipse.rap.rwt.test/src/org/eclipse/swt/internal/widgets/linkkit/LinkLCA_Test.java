/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.linkkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class LinkLCA_Test {

  private Display display;
  private Shell shell;
  private LinkLCA lca;
  private Link link;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    link = new Link( shell, SWT.NONE );
    lca = new LinkLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( link );
    ControlLCATestUtil.testFocusListener( link );
    ControlLCATestUtil.testMouseListener( link );
    ControlLCATestUtil.testKeyListener( link );
    ControlLCATestUtil.testTraverseListener( link );
    ControlLCATestUtil.testMenuDetectListener( link );
    ControlLCATestUtil.testHelpListener( link );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( link );
    assertEquals( "", adapter.getPreserved( LinkLCA.PROP_TEXT ) );
    Fixture.clearPreserved();
    link.setText( "some text" );
    link.addSelectionListener( new SelectionAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", adapter.getPreserved( LinkLCA.PROP_TEXT ) );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    link.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    link.setEnabled( true );
    // visible
    link.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    link.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( link );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    link.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    link.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    link.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    link.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    link.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, link.getToolTipText() );
    Fixture.clearPreserved();
    link.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", link.getToolTipText() );
  }

  @Test
  public void testSelectionEvent() {
    link.setText( "Big <a>Bang</a>" );
    SelectionListener listener = mock( SelectionListener.class );
    link.addSelectionListener( listener );

    fakeWidgetSelectedEvent();
    Fixture.readDataAndProcessAction( link );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( null, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
  }

  @Test
  public void testIllegalSelectionEvent() {
    // Selection event should not fire if index out of bounds (see bug 252354)
    link.setText( "No Link" );
    link.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        fail( "Should not be fired" );
      }
    } );

    fakeWidgetSelectedEvent();
    Fixture.readDataAndProcessAction( link );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( "rwt.widgets.Link", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( WidgetUtil.getId( link.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException, JSONException {
    link.setText( "foo <a>123</a> bar" );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    JSONArray text = ( JSONArray )message.findSetProperty( link, "text" );
    assertEquals( 3, text.length() );
    JSONArray segment1 = ( JSONArray )text.get( 0 );
    JSONArray segment2 = ( JSONArray )text.get( 1 );
    JSONArray segment3 = ( JSONArray )text.get( 2 );
    assertEquals( "foo ", segment1.get( 0 ) );
    assertEquals( JSONObject.NULL, segment1.get( 1 ) );
    assertEquals( "123", segment2.get( 0 ) );
    assertEquals( new Integer( 0 ), segment2.get( 1 ) );
    assertEquals( " bar", segment3.get( 0 ) );
    assertEquals( JSONObject.NULL, segment3.get( 1 ) );
  }

  @Test
  public void testRenderTextEmpty() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    link.setText( "foo bar" );

    Fixture.preserveWidgets();
    link.setText( "" );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    JSONArray text = ( JSONArray )message.findSetProperty( link, "text" );
    assertEquals( 0, text.length() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );

    link.setText( "foo <a>123</a> bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( link, "text" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( link, "Selection" ) );
    assertNull( message.findListenOperation( link, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    link.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.removeListener( SWT.Selection, listener );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( link, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( link, "Selection" ) );
  }

  private void fakeWidgetSelectedEvent() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_INDEX, Integer.valueOf( 0 ) );
    Fixture.fakeNotifyOperation( getId( link ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
  }
}
