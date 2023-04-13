/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public class WidgetLCA_Test {

  private Shell shell;
  private Button widget;
  private String shellId;
  private String widgetId;
  private WidgetLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    widget = new Button( shell, SWT.CHECK );
    shellId = getId( shell );
    widgetId = getId( widget );
    lca = new TestWidgetLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testReadData_dispatchesSetOperation() {
    OperationHandler handler = mockAndRegisterOperationHandler( widgetId );
    JsonObject parameters = new JsonObject().add( "foo", 23 );
    Fixture.fakeSetOperation( widgetId, parameters );

    lca.readData( widget );

    verify( handler ).handleSet( eq( parameters ) );
  }

  @Test
  public void testReadData_dispatchesCallOperation() {
    OperationHandler handler = mockAndRegisterOperationHandler( widgetId );
    JsonObject parameters = new JsonObject().add( "a", 23 );
    Fixture.fakeCallOperation( widgetId, "foo", parameters );

    lca.readData( widget );

    verify( handler ).handleCall( eq( "foo" ), eq( parameters ) );
  }

  @Test
  public void testReadData_dispatchesNotifyOperation() {
    OperationHandler handler = mockAndRegisterOperationHandler( widgetId );
    JsonObject parameters = new JsonObject().add( "a", 23 );
    Fixture.fakeNotifyOperation( widgetId, "foo", parameters );

    lca.readData( widget );

    verify( handler ).handleNotify( eq( "foo" ), eq( parameters ) );
  }

  @Test
  public void testReadData_dispatchesOnlyOperationsForTargetWidget() {
    OperationHandler handler = mockAndRegisterOperationHandler( widgetId );
    JsonObject shellProperties = new JsonObject().add( "foo", 23 );
    Fixture.fakeSetOperation( shellId, shellProperties );
    JsonObject widgetProperties = new JsonObject().add( "bar", 42 );
    Fixture.fakeSetOperation( widgetId, widgetProperties );

    lca.readData( widget );

    verify( handler, never() ).handleSet( eq( shellProperties ) );
  }

  @Test
  public void testReadData_dispatchesOperationsInOrder() {
    OperationHandler handler = mockAndRegisterOperationHandler( widgetId );
    Fixture.fakeSetOperation( widgetId, new JsonObject().add( "n", 1 ) );
    Fixture.fakeNotifyOperation( widgetId, "foo", new JsonObject().add( "n", 2 ) );
    Fixture.fakeSetOperation( widgetId, new JsonObject().add( "n", 3 ) );
    Fixture.fakeCallOperation( widgetId, "bar", new JsonObject().add( "n", 4 ) );
    Fixture.fakeCallOperation( widgetId, "bar", new JsonObject().add( "n", 5 ) );

    lca.readData( widget );

    InOrder order = inOrder( handler );
    order.verify( handler ).handleSet( eq( new JsonObject().add( "n", 1 ) ) );
    order.verify( handler ).handleNotify( eq( "foo" ), eq( new JsonObject().add( "n", 2 ) ) );
    order.verify( handler ).handleSet( eq( new JsonObject().add( "n", 3 ) ) );
    order.verify( handler ).handleCall( eq( "bar" ), eq( new JsonObject().add( "n", 4 ) ) );
    order.verify( handler ).handleCall( eq( "bar" ), eq( new JsonObject().add( "n", 5 ) ) );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testReadData_doesNothingIfNoOperations() {
    lca.readData( widget );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadData_failsIfRemoteObjectIsMissing() {
    Fixture.fakeSetOperation( widgetId, new JsonObject().add( "n", 1 ) );

    lca.readData( widget );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadData_failsIfHandlerIsMissing() {
    Fixture.fakeSetOperation( widgetId, new JsonObject().add( "n", 1 ) );
    mockAndRegisterRemoteObject( widgetId, null );

    lca.readData( widget );
  }

  @Test
  public void testRenderDispose_destroysRemoteObjects() throws IOException {
    RemoteObjectImpl remoteObject = mockAndRegisterRemoteObject( widgetId, null );

    lca.renderDispose( widget );

    verify( remoteObject ).destroy();
  }

  @Test
  public void testRenderDispose_withDisposedParent_destroysRemoteObjects() throws IOException {
    RemoteObjectImpl remoteObject = mockAndRegisterRemoteObject( widgetId, null );
    shell.dispose();

    lca.renderDispose( widget );

    verify( remoteObject ).markDestroyed();
  }

  @Test
  public void testRenderDispose_skippedWhenParentIsDisposed() throws IOException {
    Fixture.markInitialized( widget );

    shell.dispose();
    lca.renderDispose( widget );

    assertNull( Fixture.getProtocolMessage().findDestroyOperation( widget ) );
  }

  private static OperationHandler mockAndRegisterOperationHandler( String id ) {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterRemoteObject( id, handler );
    return handler;
  }

  private static RemoteObjectImpl mockAndRegisterRemoteObject( String id,
                                                               OperationHandler handler )
  {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( id );
    when( remoteObject.getHandler() ).thenReturn( handler );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  private static class TestWidgetLCA extends WidgetLCA {

    @Override
    public void preserveValues( Widget widget ) {
    }

    @Override
    public void renderInitialization( Widget widget ) throws IOException {
    }

    @Override
    public void renderChanges( Widget widget ) throws IOException {
    }

  }

}
