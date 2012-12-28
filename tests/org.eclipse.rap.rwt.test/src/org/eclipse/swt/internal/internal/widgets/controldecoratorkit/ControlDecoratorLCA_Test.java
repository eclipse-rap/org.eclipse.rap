/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


@SuppressWarnings("deprecation")
public class ControlDecoratorLCA_Test {

  private Display display;
  private Shell shell;
  private Button control;
  private ControlDecorator decorator;
  private ControlDecoratorLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    decorator = new ControlDecorator( control, SWT.NONE, null );
    lca = new ControlDecoratorLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    decorator.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( decorator ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( decorator );

    ArgumentCaptor<SelectionEvent> capture = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( capture.capture() );
    SelectionEvent event = capture.getValue();
    assertEquals( decorator, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), getEventBounds( event ) );
    assertTrue( event.doit );
  }

  @Test
  public void testDefaultSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    decorator.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( decorator ), ClientMessageConst.EVENT_DEFAULT_SELECTION, null );
    Fixture.readDataAndProcessAction( decorator );

    ArgumentCaptor<SelectionEvent> capture = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetDefaultSelected( capture.capture() );
    SelectionEvent event = capture.getValue();
    assertEquals( decorator, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( SWT.NONE, event.detail );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), getEventBounds( event ) );
    assertTrue( event.doit );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertEquals( "rwt.widgets.ControlDecorator", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "LEFT" ) );
    assertTrue( styles.contains( "CENTER" ) );
  }

  @Test
  public void testRenderCreateWithRightAndBottom() throws IOException {
    decorator = new ControlDecorator( control, SWT.RIGHT | SWT.BOTTOM, null );

    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "RIGHT" ) );
    assertTrue( styles.contains( "BOTTOM" ) );
  }

  @Test
  public void testRenderCreateWithLeftAndTop() throws IOException {
    decorator = new ControlDecorator( control, SWT.LEFT | SWT.TOP, null );

    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertTrue( styles.contains( "LEFT" ) );
    assertTrue( styles.contains( "TOP" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertEquals( WidgetUtil.getId( decorator.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( decorator );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( decorator ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialBounds() throws IOException, JSONException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findCreateProperty( decorator, "bounds" );
    assertEquals( 0, bounds.getInt( 2 ) );
    assertEquals( 0, bounds.getInt( 3 ) );
  }

  @Test
  public void testRenderBounds() throws IOException, JSONException {
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( decorator, "bounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  @Test
  public void testRenderBoundsUnchanged() throws IOException {
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "bounds" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    decorator.setText( "foo" );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( decorator, "text" ) );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    decorator.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "image" ) );
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testRenderInitialVisible() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "visible" ) == -1 );
  }

  @Test
  public void testRenderVisible() throws IOException {
    shell.open();
    decorator.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );

    decorator.show();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    decorator.isVisible();
    assertEquals( Boolean.TRUE, message.findSetProperty( decorator, "visible" ) );
  }

  @Test
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

  @Test
  public void testRenderInitialShowHover() throws IOException {
    lca.render( decorator );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( decorator );
    assertTrue( operation.getPropertyNames().indexOf( "showHover" ) == -1 );
  }

  @Test
  public void testRenderShowHover() throws IOException {
    decorator.setShowHover( false );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( decorator, "showHover" ) );
  }

  @Test
  public void testRenderShowHoverUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );

    decorator.setShowHover( false );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( decorator, "showHover" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( decorator, "Selection" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( decorator, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    SelectionListener listener = new SelectionAdapter() { };
    decorator.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.removeSelectionListener( listener );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( decorator, "Selection" ) );
    assertEquals( Boolean.FALSE, message.findListenProperty( decorator, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( decorator, "Selection" ) );
    assertNull( message.findListenOperation( decorator, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( decorator, "Selection" ) );
    assertNull( message.findListenOperation( decorator, "DefaultSelection" ) );
  }

  @Test
  public void testRenderDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( decorator );
    Fixture.preserveWidgets();

    decorator.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( decorator );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( decorator, "DefaultSelection" ) );
    assertNull( message.findListenOperation( decorator, "Selection" ) );
  }

  private static Rectangle getEventBounds( SelectionEvent event ) {
    return new Rectangle( event.x, event.y, event.width, event.height );
  }
}
