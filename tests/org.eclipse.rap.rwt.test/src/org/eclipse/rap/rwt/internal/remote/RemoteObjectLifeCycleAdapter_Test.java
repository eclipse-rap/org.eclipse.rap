/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
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
    ClientMessage message = createMessage( createSetOperation( "id", properties ) );

    RemoteObjectLifeCycleAdapter.readData( message );

    verify( handler ).handleSet( eq( properties ) );
  }

  @Test
  public void testReadData_delegatesCallOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    ClientMessage message = createMessage( createCallOperation( "id", "method", properties ) );

    RemoteObjectLifeCycleAdapter.readData( message );

    verify( handler ).handleCall( eq( "method" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotDirectlyDelegateNotifyOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    ClientMessage message = createMessage( createNotifyOperation( "id", "event", properties ) );

    RemoteObjectLifeCycleAdapter.readData( message );

    verifyNoInteractions( handler );
  }

  @Test
  public void testReadData_schedulesNotifyOperationsForHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterDeferredRemoteObject( "id", handler );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    ClientMessage message = createMessage( createNotifyOperation( "id", "event", properties ) );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectLifeCycleAdapter.readData( message );

    verify( handler ).handleNotify( eq( "event" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotFailWhenNoHandlerRegistered() {
    mockAndRegisterDeferredRemoteObject( "id", null );
    ClientMessage message = createMessage();

    RemoteObjectLifeCycleAdapter.readData( message );
  }

  @Test
  public void testReadData_failsWhenNoHandlerRegisteredForOperations() {
    mockAndRegisterDeferredRemoteObject( "id", null );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    ClientMessage message = createMessage( createCallOperation( "id", "method", properties ) );

    try {
      RemoteObjectLifeCycleAdapter.readData( message );
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
    ClientMessage message = createMessage(
      createCallOperation( "deferred", "method", new JsonObject().add( "foo", "bar" ) ),
      createCallOperation( "lifecycle", "method", new JsonObject().add( "foo", "bar" ) ) );

    RemoteObjectLifeCycleAdapter.readData( message );

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

  // TODO [rst] Extract these methods to a utility, merge with Message, Operation?
  private static ClientMessage createMessage( JsonArray... operations ) {
    JsonArray operationsArray = new JsonArray();
    for( JsonArray operation : operations ) {
      operationsArray.add( operation );
    }
    return new ClientMessage( new JsonObject()
      .add( "head", new JsonObject() )
      .add( "operations", operationsArray ) );
  }

  private static JsonArray createSetOperation( String target, JsonObject properties ) {
    return new JsonArray().add( "set" ).add( target ).add( properties );
  }

  private static JsonArray createCallOperation( String target, String method, JsonObject properties )
  {
    return new JsonArray().add( "call" ).add( target ).add( method ).add( properties );
  }

  private static JsonArray createNotifyOperation( String target, String event, JsonObject properties )
  {
    return new JsonArray().add( "notify" ).add( target ).add( event ).add( properties );
  }

}
