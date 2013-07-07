/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RemoteObjectLifeCycleAdapter_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRender_delegatesToRegisteredRemoteObjects() {
    DeferredRemoteObject remoteObject = mockAndRegisterDeferredRemoteObject( "id", null );

    RemoteObjectLifeCycleAdapter.render();

    verify( remoteObject ).render( same( ContextProvider.getProtocolWriter() ) );
  }

  @Test
  public void testRender_removesAllDestroyedRemoteObjectsFromRegistry() {
    setDestroyed( mockAndRegisterDeferredRemoteObject( "deferred", null ) );
    setDestroyed( mockAndRegisterLifeCycleRemoteObject( "lifecycle", null ) );

    RemoteObjectLifeCycleAdapter.render();

    assertNull( RemoteObjectRegistry.getInstance().get( "deferred" ) );
    assertNull( RemoteObjectRegistry.getInstance().get( "lifecycle" ) );
  }

  @Test
  public void testRender_doesNotRemoveAliveRemoteObjectsFromRegistry() {
    mockAndRegisterDeferredRemoteObject( "deferred", null );
    mockAndRegisterLifeCycleRemoteObject( "lifecycle", null );

    RemoteObjectLifeCycleAdapter.render();

    assertNotNull( RemoteObjectRegistry.getInstance().get( "deferred" ) );
    assertNotNull( RemoteObjectRegistry.getInstance().get( "lifecycle" ) );
  }

  @Test
  public void testReadData_delegatesSetOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    Fixture.fakeSetOperation( "id", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleSet( eq( properties ) );
  }

  @Test
  public void testReadData_delegatesCallOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    Fixture.fakeCallOperation( "id", "method", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleCall( eq( "method" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotDirectlyDelegateNotifyOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    Fixture.fakeNotifyOperation( "id", "event", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verifyZeroInteractions( handler );
  }

  @Test
  public void testReadData_schedulesNotifyOperationsForHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    Fixture.fakeNotifyOperation( "id", "event", properties );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleNotify( eq( "event" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotFailWhenNoHandlerRegistered() {
    mockAndRegisterDeferredRemoteObject( "id", null );

    RemoteObjectLifeCycleAdapter.readData();
  }

  @Test
  public void testReadData_failsWhenNoHandlerRegisteredForOperations() {
    mockAndRegisterDeferredRemoteObject( "id", null );
    Fixture.fakeCallOperation( "id", "method", new JsonObject().add( "foo", "bar" ) );

    try {
      RemoteObjectLifeCycleAdapter.readData();
      fail();
    } catch( UnsupportedOperationException exception ) {
      String expected = "No operation handler registered for remote object: id";
      assertEquals( expected, exception.getMessage() );
    }
  }

  @Test
  public void testReadData_delegatesOnlyToDeferredRemoteObjects() {
    OperationHandler deferredHandler = mock( OperationHandler.class );
    OperationHandler lifecycleHandler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "deferred", deferredHandler );
    mockAndRegisterLifeCycleRemoteObject( "lifecycle", lifecycleHandler );
    Fixture.fakeCallOperation( "deferred", "method", new JsonObject().add( "foo", "bar" ) );
    Fixture.fakeCallOperation( "lifecycle", "method", new JsonObject().add( "foo", "bar" ) );

    RemoteObjectLifeCycleAdapter.readData();

    verify( deferredHandler ).handleCall( eq( "method" ), any( JsonObject.class ) );
    verify( lifecycleHandler, never() ).handleCall( eq( "method" ), any( JsonObject.class ) );
  }

  private static DeferredRemoteObject mockAndRegisterDeferredRemoteObject( String id,
                                                                           OperationHandler handler )
  {
    DeferredRemoteObject remoteObject = mock( DeferredRemoteObject.class );
    when( remoteObject.getId() ).thenReturn( id );
    when( remoteObject.getHandler() ).thenReturn( handler );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  private static LifeCycleRemoteObject mockAndRegisterLifeCycleRemoteObject( String id,
                                                                             OperationHandler handler )
  {
    LifeCycleRemoteObject remoteObject = mock( LifeCycleRemoteObject.class );
    when( remoteObject.getId() ).thenReturn( id );
    when( remoteObject.getHandler() ).thenReturn( handler );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  private static void setDestroyed( RemoteObjectImpl remoteObject ) {
    when( Boolean.valueOf( remoteObject.isDestroyed() ) ).thenReturn( Boolean.TRUE );
  }

}
