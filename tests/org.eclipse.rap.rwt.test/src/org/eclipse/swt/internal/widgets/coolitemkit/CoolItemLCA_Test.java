/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import static java.util.Arrays.asList;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ICoolBarAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CoolItemLCA_Test {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolItem item;
  private CoolItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    item = new CoolItem( bar, SWT.NONE );
    lca = new CoolItemLCA();
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveValues() {
    Button button = new Button( bar, SWT.NONE );
    Fixture.markInitialized( item );
    item.setControl( button );
    item.setSize( 30, 20 );
    Rectangle rectangle = new Rectangle( 0, 0, item.getSize().x, item.getSize().y );
    lca.preserveValues( item );
    WidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( button, adapter.getPreserved( CoolItemLCA.PROP_CONTROL ) );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    item.setControl( null );
    lca.preserveValues( item );
    assertNull( adapter.getPreserved( CoolItemLCA.PROP_CONTROL ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.CoolItem", operation.getType() );
    assertEquals( asList( "NONE" ), getStyles( operation ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( item );
    lca.renderInitialization( item );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof CoolItemOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( getId( bar ), getParent( operation ) );
  }

  @Test
  public void testRenderVertical() throws IOException {
    item = new CoolItem( bar, SWT.VERTICAL );
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( asList( "VERTICAL" ), getStyles( operation ) );
  }

  @Test
  public void testRenderBounds() throws IOException {
    item.setSize( 10, 20 );
    lca.renderInitialization( item );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    JsonArray expected = JsonArray.readFrom( "[0, 0, 0, 20]" );
    assertEquals( expected, operation.getProperties().get( "bounds" ) );
  }

  @Test
  public void testRenderControl() throws Exception {
    Button button = new Button( bar, SWT.NONE );
    Fixture.markInitialized( item );

    item.setControl( button );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( item, "control" );
    assertEquals( getId( button ), operation.getProperties().get( "control" ).asString() );
  }

  @Test
  public void testRenderData() throws IOException {
    registerDataKeys( new String[]{ "foo", "bar" } );
    item.setData( "foo", "string" );
    item.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( item, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    registerDataKeys( new String[]{ "foo" } );
    item.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testSnapBackItemMoved() {
    bar.setSize( 400, 25 );
    item.setSize( 250, 25 );
    getRemoteObject( item ).setHandler( new CoolItemOperationHandler( item ) );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.setSize( 250, 25 );
    // Set up environment; get displayId first as it currently is in 'real life'
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( bar );
    Fixture.markInitialized( item );
    Fixture.markInitialized( item1 );
    // get adapter to set item order
    ICoolBarAdapter cba = bar.getAdapter( ICoolBarAdapter.class );

    // Simulate that fist item is dragged around but dropped at its original
    // position
    cba.setItemOrder( new int[] { 0, 1 } );

    fakeMove( item, 10 );
    Fixture.executeLifeCycleFromServerThread();

    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( item, "bounds" ) );
  }

  private void fakeMove( CoolItem coolItem, int x ) {
    Fixture.fakeNewRequest();
    Fixture.fakeCallOperation( getId( coolItem ), "move", new JsonObject().add( "left", x ) );
  }

}
