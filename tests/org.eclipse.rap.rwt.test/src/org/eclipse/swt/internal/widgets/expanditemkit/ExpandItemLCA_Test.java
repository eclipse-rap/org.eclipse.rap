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
package org.eclipse.swt.internal.widgets.expanditemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.WidgetDataUtil;
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
  public void testExpandItem() {
    ExpandListener listener = mock( ExpandListener.class );
    expandBar.addExpandListener( listener );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", true );
    fakeExpandEvent( expandItem, "Expand" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<ExpandEvent> captor = ArgumentCaptor.forClass( ExpandEvent.class );
    verify( listener, times( 1 ) ).itemExpanded( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( expandBar, event.getSource() );
    assertEquals( expandItem, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
    assertTrue( expandItem.getExpanded() );
  }

  @Test
  public void testExpandPropertyInsideExpandEvent() {
    final AtomicBoolean log = new AtomicBoolean();
    ExpandListener listener = new ExpandListener() {
      public void itemExpanded( ExpandEvent event ) {
        log.set( ( ( ExpandItem )( event.item ) ).getExpanded() );
      }
      public void itemCollapsed( ExpandEvent event ) {
      }
    };
    expandBar.addExpandListener( listener );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", true );
    fakeExpandEvent( expandItem, "Expand" );
    Fixture.readDataAndProcessAction( display );

    assertTrue( log.get() );
  }

  @Test
  public void testCollapseItem() {
    ExpandListener listener = mock( ExpandListener.class );
    expandBar.addExpandListener( listener );
    expandItem.setExpanded( true );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", false );
    fakeExpandEvent( expandItem, "Collapse" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<ExpandEvent> captor = ArgumentCaptor.forClass( ExpandEvent.class );
    verify( listener, times( 1 ) ).itemCollapsed( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( expandBar, event.getSource() );
    assertEquals( expandItem, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
    assertFalse( expandItem.getExpanded() );
  }

  @Test
  public void testExpandPropertyInsideCollapseEvent() {
    final AtomicBoolean log = new AtomicBoolean( true );
    ExpandListener listener = new ExpandListener() {
      public void itemExpanded( ExpandEvent event ) {
      }
      public void itemCollapsed( ExpandEvent event ) {
        log.set( ( ( ExpandItem )( event.item ) ).getExpanded() );
      }
    };
    expandItem.setExpanded( true );
    expandBar.addExpandListener( listener );

    Fixture.fakeSetProperty( WidgetUtil.getId( expandItem ), "expanded", false );
    fakeExpandEvent( expandItem, "Collapse" );
    Fixture.readDataAndProcessAction( display );

    assertFalse( log.get() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( "rwt.widgets.ExpandItem", operation.getType() );
    assertTrue( operation.getPropertyNames().indexOf( "style" ) == -1 );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( WidgetUtil.getId( expandBar ), operation.getParent() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandItem );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandItem ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    expandItem.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( expandItem, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "customVariant" ) );
  }

  @Test
  public void testRenderInitialBounds() throws IOException {
    expandItem.setText( "foo" );

    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findCreateProperty( expandItem, "bounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBounds() throws IOException {
    expandItem.setText( "foo" );

    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "bounds" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    expandItem.setText( "foo" );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( expandItem, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    expandItem.setImage( image );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( expandItem, "image" ) );
  }

  @Test
  public void testRenderInitialHeaderHeight() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "headerHeight" ) == -1 );
  }

  @Test
  public void testRenderHeaderHeight() throws IOException {
    expandBar.setFont( new Font( display, "Arial", 22, SWT.BOLD )  );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 26, message.findSetProperty( expandItem, "headerHeight" ).asInt() );
  }

  @Test
  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandBar.setFont( new Font( display, "Arial", 22, SWT.BOLD )  );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "headerHeight" ) );
  }

  @Test
  public void testRenderData() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo", "bar" } );
    expandItem.setData( "foo", "string" );
    expandItem.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( expandItem, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo" } );
    expandItem.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  private static void fakeExpandEvent( ExpandItem item, String eventName ) {
    JsonObject properties = new JsonObject().add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( item.getParent() ), eventName, properties );
  }

}
