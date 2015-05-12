/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.scalekit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScaleLCA_Test {

  private Display display;
  private Shell shell;
  private Scale scale;
  private ScaleLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    scale = new Scale( shell, SWT.NONE );
    lca = new ScaleLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testScalePreserveValues() {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment and ageIncrement
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( scale );
    Integer minimum = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MINIMUM );
    assertEquals( 0, minimum.intValue() );
    Integer maximum = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MAXIMUM );
    assertEquals( 100, maximum.intValue() );
    Integer selection = ( Integer )adapter.getPreserved( ScaleLCA.PROP_SELECTION );
    assertEquals( 0, selection.intValue() );
    Integer increment = ( Integer )adapter.getPreserved( ScaleLCA.PROP_INCREMENT );
    assertEquals( 1, increment.intValue() );
    Integer pageIncrement = ( Integer )adapter.getPreserved( ScaleLCA.PROP_PAGE_INCREMENT );
    assertEquals( 10, pageIncrement.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( scale );
  }

  private void testPreserveControlProperties( Scale scale ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    scale.setBounds( rectangle );
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( scale );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    scale.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( scale );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    scale.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = new Color( display, 122, 33, 203 );
    scale.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    scale.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    scale.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( "rwt.widgets.Scale", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( scale );
    lca.renderInitialization( scale );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ScaleOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ScaleOperationHandler handler = spy( new ScaleOperationHandler( scale ) );
    getRemoteObject( getId( scale ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( scale ), "Help", new JsonObject() );
    lca.readData( scale );

    verify( handler ).handleNotifyHelp( scale, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( getId( scale.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderCreateWithHorizontal() throws IOException {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );

    lca.renderInitialization( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( getStyles( operation ).contains( "HORIZONTAL" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    lca.render( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertFalse( operation.getProperties().names().contains( "minimum" ) );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    scale.setMinimum( 10 );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( scale, "minimum" ).asInt() );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    lca.render( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertFalse( operation.getProperties().names().contains( "maximum" ) );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    scale.setMaximum( 10 );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( scale, "maximum" ).asInt() );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    scale.setSelection( 10 );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( scale, "selection" ).asInt() );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "selection" ) );
  }

  @Test
  public void testRenderInitialIncrement() throws IOException {
    lca.render( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertFalse( operation.getProperties().names().contains( "increment" ) );
  }

  @Test
  public void testRenderIncrement() throws IOException {
    scale.setIncrement( 2 );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( scale, "increment" ).asInt() );
  }

  @Test
  public void testRenderIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "increment" ) );
  }

  @Test
  public void testRenderInitialPageIncrement() throws IOException {
    lca.render( scale );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertFalse( operation.getProperties().names().contains( "pageIncrement" ) );
  }

  @Test
  public void testRenderPageIncrement() throws IOException {
    scale.setPageIncrement( 20 );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( scale, "pageIncrement" ).asInt() );
  }

  @Test
  public void testRenderPageIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "pageIncrement" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( scale, "Selection" ) );
    assertNull( message.findListenOperation( scale, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    scale.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.removeListener( SWT.Selection, listener );
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( scale, "Selection" ) );
    assertNull( message.findListenOperation( scale, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( scale, "selection" ) );
  }

  @Test
  public void testRenderChanges_rendersClientListener() throws IOException {
    scale.addListener( SWT.Selection, new ClientListener( "" ) );

    lca.renderChanges( scale );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( scale, "addListener" ) );
  }

}
