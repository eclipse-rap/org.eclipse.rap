/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tooltipkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.RWT;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolTipLCA_Test {

  private Display display;
  private Shell shell;
  private ToolTip toolTip;
  private ToolTipLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    toolTip = new ToolTip( shell, SWT.NONE );
    lca = new ToolTipLCA();
    Fixture.markInitialized( display );
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testReadVisibleWithRequestParamFalse() {
    toolTip.setVisible( true );

    Fixture.fakeSetParameter( getId( toolTip ), "visible", Boolean.FALSE );
    Fixture.readDataAndProcessAction( display );

    assertFalse( toolTip.isVisible() );
  }

  @Test
  public void testReadVisibleWithNoRequestParam() {
    toolTip.setVisible( true );
    Fixture.readDataAndProcessAction( display );
    assertTrue( toolTip.isVisible() );
  }

  @Test
  public void testSelectionEvent() {
    final SelectionEvent[] eventLog = { null };
    toolTip.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        eventLog[ 0 ] = event;
      }
    } );

    Fixture.fakeNotifyOperation( getId( toolTip ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( display );

    SelectionEvent event = eventLog[ 0 ];
    assertNotNull( event );
    assertSame( toolTip, event.widget );
    assertTrue( event.doit );
    assertNull( event.data );
    assertNull( event.text );
    assertEquals( 0, event.detail );
    assertEquals( 0, event.y );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertSame( display, event.display );
    assertNull( event.item );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
  }

  @Test
  public void testRenderCreateWithBalloon() throws IOException {
    toolTip = new ToolTip( shell, SWT.BALLOON );

    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "BALLOON" ) );
  }

  @Test
  public void testRenderCreateWithIconError() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_ERROR );

    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "ICON_ERROR" ) );
  }

  @Test
  public void testRenderCreateWithIconWarning() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_WARNING );

    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "ICON_WARNING" ) );
  }

  @Test
  public void testRenderCreateWithIconInformation() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_INFORMATION );

    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "ICON_INFORMATION" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( WidgetUtil.getId( toolTip.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( toolTip );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( toolTip ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    toolTip.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( toolTip, "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "customVariant" ) );
  }

  @Test
  public void testRenderInitialRoundedBorder() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertTrue( operation.getPropertyNames().indexOf( "roundedBorder" ) == -1 );
  }

  @Test
  public void testRenderRoundedBorder() throws IOException, JSONException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color color = Graphics.getColor( 0, 255, 0 );

    graphicsAdapter.setRoundedBorder( 2, color, 5, 6, 7, 8 );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    JSONArray border = ( JSONArray )message.findSetProperty( toolTip, "roundedBorder" );
    assertEquals( 6, border.length() );
    assertEquals( 2, border.getInt( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", border.getJSONArray( 1 ) ) );
    assertEquals( 5, border.getInt( 2 ) );
    assertEquals( 6, border.getInt( 3 ) );
    assertEquals( 7, border.getInt( 4 ) );
    assertEquals( 8, border.getInt( 5 ) );
  }

  @Test
  public void testRenderRoundedBorderUnchanged() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color color = Graphics.getColor( 0, 255, 0 );
    Fixture.markInitialized( toolTip );

    graphicsAdapter.setRoundedBorder( 2, color, 5, 6, 7, 8 );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "roundedBorder" ) );
  }

  @Test
  public void testRenderInitialBackgroundGradient() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertTrue( operation.getPropertyNames().indexOf( "backgroundGradient" ) == -1 );
  }

  @Test
  public void testRenderBackgroundGradient() throws IOException, JSONException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };

    graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    JSONArray gradient = ( JSONArray )message.findSetProperty( toolTip, "backgroundGradient" );
    JSONArray colors = ( JSONArray )gradient.get( 0 );
    JSONArray stops = ( JSONArray )gradient.get( 1 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", colors.getJSONArray( 0 ) ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,255,255]", colors.getJSONArray( 1 ) ) );
    assertEquals( new Integer( 0 ), stops.get( 0 ) );
    assertEquals( new Integer( 100 ), stops.get( 1 ) );
    assertEquals( Boolean.TRUE, gradient.get( 2 ) );
  }

  @Test
  public void testRenderBackgroundGradientUnchanged() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };
    Fixture.markInitialized( toolTip );

    graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "backgroundGradient" ) );
  }

  @Test
  public void testRenderInitialAutoHide() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderAutoHide() throws IOException {
    toolTip.setAutoHide( true );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderAutoHideUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setAutoHide( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    toolTip.setText( "foo" );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolTip, "text" ) );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "text" ) );
  }

  @Test
  public void testRenderInitialMessage() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "message" ) );
  }

  @Test
  public void testRenderMessage() throws IOException {
    toolTip.setMessage( "foo" );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolTip, "message" ) );
  }

  @Test
  public void testRenderMessageUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setMessage( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "message" ) );
  }

  @Test
  public void testRenderInitialLocation() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "location" ) );
  }

  @Test
  public void testRenderLocation() throws IOException, JSONException {
    toolTip.setLocation( 10, 20 );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( toolTip, "location" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[10,20]", actual ) );
  }

  @Test
  public void testRenderLocationUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setLocation( 10, 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "location" ) );
  }

  @Test
  public void testRenderInitialVisible() throws IOException {
    lca.render( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "visible" ) );
  }

  @Test
  public void testRenderVisible() throws IOException {
    toolTip.setVisible( true );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( toolTip, "visible" ) );
  }

  @Test
  public void testRenderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "visible" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( toolTip );
    Fixture.preserveWidgets();

    toolTip.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( toolTip, "Selection" ) );
    assertNull( message.findListenOperation( toolTip, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    SelectionListener listener = new SelectionAdapter() { };
    toolTip.addSelectionListener( listener );
    Fixture.markInitialized( toolTip );
    Fixture.preserveWidgets();

    toolTip.removeSelectionListener( listener );
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( toolTip, "Selection" ) );
    assertNull( message.findListenOperation( toolTip, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( toolTip );
    Fixture.preserveWidgets();

    toolTip.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( toolTip, "selection" ) );
  }
}
