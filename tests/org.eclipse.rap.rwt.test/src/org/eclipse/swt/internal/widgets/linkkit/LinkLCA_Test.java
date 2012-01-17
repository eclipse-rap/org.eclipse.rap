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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.*;
import org.json.*;

public class LinkLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private LinkLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new LinkLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Link link = new Link( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( link );
    ControlLCATestUtil.testFocusListener( link );
    ControlLCATestUtil.testMouseListener( link );
    ControlLCATestUtil.testKeyListener( link );
    ControlLCATestUtil.testTraverseListener( link );
    ControlLCATestUtil.testMenuDetectListener( link );
    ControlLCATestUtil.testHelpListener( link );
  }

  public void testPreserveValues() {
    Link link = new Link( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( link );
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
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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

  public void testSelectionEvent() {
    final StringBuilder log = new StringBuilder();
    final Link link = new Link( shell, SWT.NONE );
    link.setText( "Big <a>Bang</a>" );
    link.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( link, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    String linkId = WidgetUtil.getId( link );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, linkId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".index", "0" );
    Fixture.readDataAndProcessAction( link );
    assertEquals( "selectionEvent", log.toString() );
  }

  public void testIllegalSelectionEvent() {
    // Selection event should not fire if index out of bounds (see bug 252354)
    Link link = new Link( shell, SWT.NONE );
    link.setText( "No Link" );
    link.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        fail( "Should not be fired" );
      }
    } );
    String linkId = WidgetUtil.getId( link );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, linkId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".index", "0" );
    Fixture.readDataAndProcessAction( link );
  }

  public void testRenderCreate() throws IOException {
    Link link = new Link( shell, SWT.NONE );

    lca.renderInitialization( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( "rwt.widgets.Link", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Link link = new Link( shell, SWT.NONE );

    lca.renderInitialization( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertEquals( WidgetUtil.getId( link.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    Link link = new Link( shell, SWT.NONE );

    lca.render( link );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( link );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException, JSONException {
    Link link = new Link( shell, SWT.NONE );

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

  public void testRenderTextEmpty() throws IOException {
    Link link = new Link( shell, SWT.NONE );
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

  public void testRenderTextUnchanged() throws IOException {
    Link link = new Link( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );

    link.setText( "foo <a>123</a> bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( link, "text" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Link link = new Link( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( link, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Link link = new Link( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    link.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.removeSelectionListener( listener );
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( link, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Link link = new Link( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( link );
    Fixture.preserveWidgets();

    link.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( link );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( link, "selection" ) );
  }
}
