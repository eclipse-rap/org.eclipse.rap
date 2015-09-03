/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.groupkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GroupLCA_Test {

  private Display display;
  private Shell shell;
  private Group group;
  private GroupLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    group = new Group( shell, SWT.NONE );
    lca = new GroupLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( group );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( group );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( "rwt.widgets.Group", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( group );
    lca.renderInitialization( group );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof GroupOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    GroupOperationHandler handler = spy( new GroupOperationHandler( group ) );
    getRemoteObject( getId( group ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( group ), "Help", new JsonObject() );
    lca.readData( group );

    verify( handler ).handleNotifyHelp( group, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( group );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( getId( group.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDirection_default() throws IOException {
    lca.renderInitialization( group );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertNull( operation.getProperties().get( "direction" ) );
  }

  @Test
  public void testRenderDirection_RTL() throws IOException {
    group = new Group( shell, SWT.RIGHT_TO_LEFT );

    lca.renderInitialization( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "rtl", message.findCreateProperty( group, "direction" ).asString() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( group );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    group.setText( "foo" );
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( group, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithMnemonic() throws IOException {
    group.setText( "te&st" );
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( group, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( group );

    group.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "text" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    group.setText( "te&st" );
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( group, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( group );

    group.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( group );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "mnemonicIndex" ) );
  }

}
