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
package org.eclipse.swt.internal.widgets.expandbarkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExpandBarLCA_Test {

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    lca = new ExpandBarLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( expandBar );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
  }

  @Test
  public void testRenderCreateWithVScroll() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );

    lca.render( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
    assertFalse( getStyles( operation ).contains( "V_SCROLL" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( expandBar );
    lca.renderInitialization( expandBar );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ExpandBarOperationHandler );
  }

  @Test
  public void testRenderInitialization_rendersExpandListener() throws Exception {
    lca.renderInitialization( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( expandBar, "Expand" ) );
  }

  @Test
  public void testRenderInitialization_rendersCollapseListener() throws Exception {
    lca.renderInitialization( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( expandBar, "Collapse" ) );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ExpandBarOperationHandler handler = spy( new ExpandBarOperationHandler( expandBar ) );
    getRemoteObject( getId( expandBar ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( expandBar ), "Help", new JsonObject() );
    lca.readData( expandBar );

    verify( handler ).handleNotifyHelp( expandBar, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( WidgetUtil.getId( expandBar.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandBar ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialBottomSpacingBounds() throws IOException {
    lca.render( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findSetProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBottomSpacingBounds() throws IOException {
    lca.renderChanges( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findSetProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBottomSpacingBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "boubottomSpacingBoundsnds" ) );
  }

  @Test
  public void testRenderInitialVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    lca.render( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertFalse( operation.getProperties().names().contains( "vScrollBarMax" ) );
  }

  @Test
  public void testRenderVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    new ExpandItem( expandBar, SWT.NONE );
    lca.renderChanges( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetProperty( expandBar, "vScrollBarMax" ) );
  }

  @Test
  public void testRenderVScrollBarMaxUnchanged() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    new ExpandItem( expandBar, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "vScrollBarMax" ) );
  }

}
