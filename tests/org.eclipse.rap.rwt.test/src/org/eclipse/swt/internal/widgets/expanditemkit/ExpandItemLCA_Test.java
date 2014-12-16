/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expanditemkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.expandbarkit.ExpandBarOperationHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ExpandItemLCA_Test {

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandItem expandItem;
  private ExpandItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandItem = new ExpandItem( expandBar, SWT.NONE );
    lca = new ExpandItemLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExpandPropertyInsideExpandEvent() {
    getRemoteObject( expandBar ).setHandler( new ExpandBarOperationHandler( expandBar ) );
    getRemoteObject( expandItem ).setHandler( new ExpandItemOperationHandler( expandItem ) );
    ExpandListener listener = mock( ExpandListener.class );
    expandBar.addExpandListener( listener );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", true );
    fakeExpandEvent( expandItem, "Expand" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<ExpandEvent> captor = ArgumentCaptor.forClass( ExpandEvent.class );
    verify( listener ).itemExpanded( captor.capture() );
    assertTrue( ( ( ExpandItem )captor.getValue().item ).getExpanded() );
  }

  @Test
  public void testExpandPropertyInsideCollapseEvent() {
    getRemoteObject( expandBar ).setHandler( new ExpandBarOperationHandler( expandBar ) );
    getRemoteObject( expandItem ).setHandler( new ExpandItemOperationHandler( expandItem ) );
    ExpandListener listener = mock( ExpandListener.class );
    expandItem.setExpanded( true );
    expandBar.addExpandListener( listener );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", false );
    fakeExpandEvent( expandItem, "Collapse" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<ExpandEvent> captor = ArgumentCaptor.forClass( ExpandEvent.class );
    verify( listener ).itemCollapsed( captor.capture() );
    assertFalse( ( ( ExpandItem )captor.getValue().item ).getExpanded() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( "rwt.widgets.ExpandItem", operation.getType() );
    assertFalse( operation.getProperties().names().contains( "style" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( expandItem );
    lca.renderInitialization( expandItem );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ExpandItemOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( getId( expandBar ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandItem ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertFalse( operation.getProperties().names().contains( "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    expandItem.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( expandItem, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "customVariant" ) );
  }

  @Test
  public void testRenderInitialBounds() throws IOException {
    expandItem.setText( "foo" );

    lca.render( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findCreateProperty( expandItem, "bounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBounds() throws IOException {
    expandItem.setText( "foo" );

    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findSetProperty( expandItem, "bounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBoundsUnchanged() throws IOException {
    expandItem.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "bounds" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    expandItem.setText( "foo" );
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( expandItem, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    expandItem.setImage( image );
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( expandItem, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    expandItem.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    expandItem.setImage( image );

    Fixture.preserveWidgets();
    expandItem.setImage( null );
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( expandItem, "image" ) );
  }

  @Test
  public void testRenderInitialHeaderHeight() throws IOException {
    lca.render( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertFalse( operation.getProperties().names().contains( "headerHeight" ) );
  }

  @Test
  public void testRenderHeaderHeight() throws IOException {
    expandBar.setFont( new Font( display, "Arial", 22, SWT.BOLD )  );
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 26, message.findSetProperty( expandItem, "headerHeight" ).asInt() );
  }

  @Test
  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandBar.setFont( new Font( display, "Arial", 22, SWT.BOLD )  );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "headerHeight" ) );
  }

  @Test
  public void testRenderData() throws IOException {
    registerDataKeys( new String[]{ "foo", "bar" } );
    expandItem.setData( "foo", "string" );
    expandItem.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( expandItem, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    registerDataKeys( new String[]{ "foo" } );
    expandItem.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  private static void fakeExpandEvent( ExpandItem item, String eventName ) {
    JsonObject properties = new JsonObject().add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( item.getParent() ), eventName, properties );
  }

}
