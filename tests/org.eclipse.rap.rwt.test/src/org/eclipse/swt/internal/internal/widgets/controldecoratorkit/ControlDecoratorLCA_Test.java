/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class ControlDecoratorLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Button control;
  private ControlDecorator decorator;
  private ControlDecoratorLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    decorator = new ControlDecorator( control, SWT.NONE, null );
    lca = new ControlDecoratorLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testSelectionEvent() {
    final StringBuilder log = new StringBuilder();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( decorator, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        assertEquals( decorator, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetDefaultSelected" );
      }
    };
    decorator.addSelectionListener( selectionListener );
    String decorId = WidgetUtil.getId( decorator );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, decorId );
    Fixture.readDataAndProcessAction( decorator );
    assertEquals( "widgetSelected", log.toString() );
    Fixture.fakeNewRequest();
    log.setLength( 0 );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, decorId );
    Fixture.readDataAndProcessAction( decorator );
    assertEquals( "widgetDefaultSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertEquals( "rwt.widgets.ControlDecorator", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "LEFT" ) );
    assertTrue( styles.contains( "CENTER" ) );
  }

  public void testRenderCreateWithRightAndBottom() throws IOException {
    decorator = new ControlDecorator( control, SWT.RIGHT | SWT.BOTTOM, null );

    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "RIGHT" ) );
    assertTrue( styles.contains( "BOTTOM" ) );
  }

  public void testRenderCreateWithLeftAndTop() throws IOException {
    decorator = new ControlDecorator( control, SWT.LEFT | SWT.TOP, null );

    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "LEFT" ) );
    assertTrue( styles.contains( "TOP" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertEquals( WidgetUtil.getId( decorator.getParent() ), operation.getParent() );
  }

  public void testRenderDispose() throws IOException {
    lca.renderDispose( decorator );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( decorator ), operation.getTarget() );
  }

  public void testRenderInitialBounds() throws IOException, JSONException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findCreateProperty( decorator, "bounds" );
    assertEquals( 0, bounds.getInt( 2 ) );
    assertEquals( 0, bounds.getInt( 3 ) );
  }

  public void testRenderBounds() throws IOException, JSONException {
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( decorator, "bounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  public void testRenderBoundsUnchanged() throws IOException {
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "bounds" ) );
  }

  public void testRenderInitialText() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    decorator.setText( "foo" );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( decorator, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    decorator.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    decorator.setImage( image );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( decorator, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    decorator.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    decorator.setImage( image );

    Fixture.preserveWidgets();
    decorator.setImage( null );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( decorator, "image" ) );
  }

  public void testRenderInitialVisible() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "visible" ) == -1 );
  }

  public void testRenderVisible() throws IOException {
    shell.open();
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );

    decorator.show();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    decorator.isVisible();
    assertEquals( Boolean.TRUE, message.findSetProperty( decorator, "visible" ) );
  }

  public void testRenderVisibleUnchanged() throws IOException {
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );

    decorator.show();
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "visible" ) );
  }

  public void testRenderInitialShowHover() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "showHover" ) == -1 );
  }

  public void testRenderShowHover() throws IOException {
    decorator.setShowHover( false );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( decorator, "showHover" ) );
  }

  public void testRenderShowHoverUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    decorator.setShowHover( false );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "showHover" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( decorator, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    SelectionListener listener = new SelectionAdapter() { };
    decorator.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.removeSelectionListener( listener );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( decorator, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( decorator, "selection" ) );
  }
}
