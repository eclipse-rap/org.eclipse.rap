/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.WidgetDataUtil;
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

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.CoolItem", operation.getType() );
    assertArrayEquals( new String[] { "NONE" }, operation.getStyles() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( bar ), operation.getParent() );
  }

  @Test
  public void testRenderVertical() throws IOException {
    item = new CoolItem( bar, SWT.VERTICAL );
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertArrayEquals( new String[] { "VERTICAL" }, operation.getStyles() );
  }

  @Test
  public void testRenderBounds() throws IOException {
    item.setSize( 10, 20 );
    lca.renderInitialization( item );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    JsonArray expected = JsonArray.readFrom( "[0, 0, 0, 20]" );
    assertEquals( expected, operation.getProperty( "bounds" ) );
  }

  @Test
  public void testRenderControl() throws Exception {
    Button button = new Button( bar, SWT.NONE );
    Fixture.markInitialized( item );

    item.setControl( button );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( item, "control" );
    assertEquals( getId( button ), operation.getProperty( "control" ).asString() );
  }

  @Test
  public void testReadMove() {
    bar.setSize( 100, 10 );
    item.setSize( 10, 10 );
    CoolItem item2 = new CoolItem( bar, SWT.NONE );
    item2.setSize( 20, 10 );
    int oldX = item.getBounds().x;

    JsonObject parameters = new JsonObject().add( "left", 25 );
    Fixture.fakeCallOperation( getId( item ), "move", parameters  );
    Fixture.readDataAndProcessAction( display );

    assertTrue( oldX != item.getBounds().x );
  }

  @Test
  public void testRenderData() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo", "bar" } );
    item.setData( "foo", "string" );
    item.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( item, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo" } );
    item.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

}
