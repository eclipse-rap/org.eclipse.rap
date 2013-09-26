/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolbarkit;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolBarLCA_Test {

  private Display display;
  private Shell shell;
  private ToolBarLCA lca;
  private ToolBar toolBar;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    toolBar = new ToolBar( shell, SWT.NONE );
    lca = new ToolBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( toolBar );
    ControlLCATestUtil.testFocusListener( toolBar );
    ControlLCATestUtil.testMouseListener( toolBar );
    ControlLCATestUtil.testKeyListener( toolBar );
    ControlLCATestUtil.testTraverseListener( toolBar );
    ControlLCATestUtil.testMenuDetectListener( toolBar );
    ControlLCATestUtil.testHelpListener( toolBar );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertEquals( "rwt.widgets.ToolBar", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
    assertFalse( Arrays.asList( styles ).contains( "H_SCROLL" ) );
  }

  @Test
  public void testRenderCreate_Vertical() throws IOException {
    toolBar = new ToolBar( shell, SWT.VERTICAL );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
    assertFalse( Arrays.asList( styles ).contains( "V_SCROLL" ) );
  }

  @Test
  public void testRenderCreate_Flat() throws IOException {
    toolBar = new ToolBar( shell, SWT.FLAT );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "FLAT" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( toolBar );
    lca.renderInitialization( toolBar );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ToolBarOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ToolBarOperationHandler handler = spy( new ToolBarOperationHandler( toolBar ) );
    getRemoteObject( getId( toolBar ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( toolBar ), "Help", new JsonObject() );
    lca.readData( toolBar );

    verify( handler ).handleNotifyHelp( toolBar, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertEquals( WidgetUtil.getId( toolBar.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    toolBar.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( toolBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( toolBar, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolBar );

    toolBar.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolBar, "customVariant" ) );
  }

}
