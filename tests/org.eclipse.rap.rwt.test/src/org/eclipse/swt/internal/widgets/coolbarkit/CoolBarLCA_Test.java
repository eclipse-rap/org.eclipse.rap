/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class CoolBarLCA_Test {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    lca = CoolBarLCA.INSTANCE;
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( bar );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( bar );
    lca.preserveValues( bar );
    RemoteAdapter adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
    Fixture.clearPreserved();
    bar.setLocked( true );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( bar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( "rwt.widgets.CoolBar", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( bar );
    lca.renderInitialization( bar );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof CoolBarOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    CoolBarOperationHandler handler = spy( new CoolBarOperationHandler( bar ) );
    getRemoteObject( getId( bar ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( bar ), "Help", new JsonObject() );
    lca.readData( bar );

    verify( handler ).handleNotifyHelp( bar, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( bar );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( getId( bar.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderLocked() throws IOException {
    lca.preserveValues( bar );
    bar.setLocked( true );
    lca.renderChanges( bar );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( bar, "locked" );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "locked" ) );
  }

}
