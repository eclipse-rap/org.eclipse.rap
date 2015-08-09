/*******************************************************************************
 * Copyright (c) 2011, 2015 Rüdiger Herrmann and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
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
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
  }

  @Test
  public void testRenderCreateWithBalloon() throws IOException {
    toolTip = new ToolTip( shell, SWT.BALLOON );

    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    assertTrue( getStyles( operation ).contains( "BALLOON" ) );
  }

  @Test
  public void testRenderCreateWithIconError() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_ERROR );

    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    assertTrue( getStyles( operation ).contains( "ICON_ERROR" ) );
  }

  @Test
  public void testRenderCreateWithIconWarning() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_WARNING );

    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    assertTrue( getStyles( operation ).contains( "ICON_WARNING" ) );
  }

  @Test
  public void testRenderCreateWithIconInformation() throws IOException {
    toolTip = new ToolTip( shell, SWT.ICON_INFORMATION );

    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( "rwt.widgets.ToolTip", operation.getType() );
    assertTrue( getStyles( operation ).contains( "ICON_INFORMATION" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( toolTip );
    lca.renderInitialization( toolTip );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ToolTipOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ToolTipOperationHandler handler = spy( new ToolTipOperationHandler( toolTip ) );
    getRemoteObject( getId( toolTip ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( toolTip ), "Selection", new JsonObject() );
    lca.readData( toolTip );

    verify( handler ).handleNotifySelection( toolTip, new JsonObject() );
  }

  @Test
  public void testRenderMarkupEnabled() throws IOException {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findCreateProperty( toolTip, "markupEnabled" ) );
  }

  @Test
  public void testRenderMarkupEnabled_default() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertFalse( operation.getProperties().names().contains( "markupEnabled" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertEquals( getId( toolTip.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( toolTip ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertFalse( operation.getProperties().names().contains( "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    toolTip.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( toolTip, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "customVariant" ) );
  }

  @Test
  public void testRenderInitialRoundedBorder() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertFalse( operation.getProperties().names().contains( "roundedBorder" ) );
  }

  @Test
  public void testRenderRoundedBorder() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color color = new Color( display, 0, 255, 0 );

    graphicsAdapter.setRoundedBorder( 2, color, 5, 6, 7, 8 );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[2, [0, 255, 0, 255], 5, 6, 7, 8]" );
    assertEquals( expected, message.findSetProperty( toolTip, "roundedBorder" ) );
  }

  @Test
  public void testRenderRoundedBorderUnchanged() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color color = new Color( display, 0, 255, 0 );
    Fixture.markInitialized( toolTip );

    graphicsAdapter.setRoundedBorder( 2, color, 5, 6, 7, 8 );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "roundedBorder" ) );
  }

  @Test
  public void testRenderInitialBackgroundGradient() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolTip );
    assertFalse( operation.getProperties().names().contains( "backgroundGradient" ) );
  }

  @Test
  public void testRenderBackgroundGradient() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] gradientColors = new Color[] {
      new Color( display, 0, 255, 0 ),
      new Color( display, 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };

    graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected
      = JsonArray.readFrom( "[[[0, 255, 0, 255], [0, 0, 255, 255]], [0, 100], true]" );
    assertEquals( expected, message.findSetProperty( toolTip, "backgroundGradient" ) );
  }

  @Test
  public void testRenderBackgroundGradientUnchanged() throws IOException {
    IWidgetGraphicsAdapter graphicsAdapter = toolTip.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] gradientColors = new Color[] {
      new Color( display, 0, 255, 0 ),
      new Color( display, 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };
    Fixture.markInitialized( toolTip );

    graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "backgroundGradient" ) );
  }

  @Test
  public void testRenderInitialAutoHide() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderAutoHide() throws IOException {
    toolTip.setAutoHide( true );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderAutoHideUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setAutoHide( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "autoHide" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    toolTip.setText( "foo" );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolTip, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "text" ) );
  }

  @Test
  public void testRenderInitialMessage() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "message" ) );
  }

  @Test
  public void testRenderMessage() throws IOException {
    toolTip.setMessage( "foo" );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolTip, "message" ).asString() );
  }

  @Test
  public void testRenderMessageUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setMessage( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "message" ) );
  }

  @Test
  public void testRenderInitialLocation() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "location" ) );
  }

  @Test
  public void testRenderLocation() throws IOException {
    toolTip.setLocation( 10, 20 );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[10, 20]" );
    assertEquals( expected, message.findSetProperty( toolTip, "location" ) );
  }

  @Test
  public void testRenderLocationUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setLocation( 10, 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "location" ) );
  }

  @Test
  public void testRenderInitialVisible() throws IOException {
    lca.render( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "visible" ) );
  }

  @Test
  public void testRenderVisible() throws IOException {
    toolTip.setVisible( true );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( toolTip, "visible" ) );
  }

  @Test
  public void testRenderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( toolTip );

    toolTip.setVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolTip, "visible" ) );
  }

  @Test
  public void testRenderListen_Selection() throws Exception {
    Fixture.markInitialized( toolTip );
    Fixture.preserveWidgets();

    toolTip.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( toolTip, "Selection" ) );
  }

  @Test
  public void testRenderListen_NoDefaultSelection() throws Exception {
    Fixture.markInitialized( toolTip );
    Fixture.preserveWidgets();

    toolTip.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( toolTip );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( toolTip, "DefaultSelection" ) );
  }

}
