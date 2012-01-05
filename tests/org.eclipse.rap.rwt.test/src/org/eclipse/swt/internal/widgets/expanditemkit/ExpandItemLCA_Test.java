/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class ExpandItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandItem expandItem;
  private ExpandItemLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandItem = new ExpandItem( expandBar, SWT.NONE );
    lca = new ExpandItemLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testExpandEvent() {
    final StringBuilder log = new StringBuilder();
    ExpandListener listener = new ExpandListener() {
      public void itemCollapsed( ExpandEvent event ) {
        assertEquals( expandBar, event.getSource() );
        assertEquals( expandItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "collapsed" );
      }

      public void itemExpanded( ExpandEvent event ) {
        assertEquals( expandBar, event.getSource() );
        assertEquals( expandItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "expanded" );
      }
    };
    expandBar.addExpandListener( listener );
    String expandItemId = WidgetUtil.getId( expandItem );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, expandItemId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "expanded", log.toString() );
    log.setLength( 0 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, null );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_COLLAPSED, expandItemId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "collapsed", log.toString() );
  }

  public void testExpandCollapse() {
    String expandItemId = WidgetUtil.getId( expandItem );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, expandItemId );
    Fixture.readDataAndProcessAction( expandItem );
    assertEquals( true, expandItem.getExpanded() );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_COLLAPSED, expandItemId );
    Fixture.readDataAndProcessAction( expandItem );
    assertEquals( false, expandItem.getExpanded() );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( "rwt.widgets.ExpandItem", operation.getType() );
    assertTrue( operation.getPropertyNames().indexOf( "style" ) == -1 );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertEquals( WidgetUtil.getId( expandBar ), operation.getParent() );
  }

  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandItem );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandItem ), operation.getTarget() );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    expandItem.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( expandItem, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "customVariant" ) );
  }

  public void testRenderInitialBounds() throws IOException, JSONException {
    expandItem.setText( "foo" );

    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findCreateProperty( expandItem, "bounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  public void testRenderBounds() throws IOException, JSONException {
    expandItem.setText( "foo" );

    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( expandItem, "bounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  public void testRenderBoundsUnchanged() throws IOException {
    expandItem.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "bounds" ) );
  }

  public void testRenderInitialText() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    expandItem.setText( "foo" );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( expandItem, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandItem.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    expandItem.setImage( image );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( expandItem, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    expandItem.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    expandItem.setImage( image );

    Fixture.preserveWidgets();
    expandItem.setImage( null );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( expandItem, "image" ) );
  }

  public void testRenderInitialHeaderHeight() throws IOException {
    lca.render( expandItem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandItem );
    assertTrue( operation.getPropertyNames().indexOf( "headerHeight" ) == -1 );
  }

  public void testRenderHeaderHeight() throws IOException {
    expandBar.setFont( Graphics.getFont( "Arial", 22, SWT.BOLD )  );
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 26 ), message.findSetProperty( expandItem, "headerHeight" ) );
  }

  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandItem );

    expandBar.setFont( Graphics.getFont( "Arial", 22, SWT.BOLD )  );
    Fixture.preserveWidgets();
    lca.renderChanges( expandItem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandItem, "headerHeight" ) );
  }
}
