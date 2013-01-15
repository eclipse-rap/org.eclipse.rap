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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

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
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    RemoteObjectRegistry.getInstance().register( remoteObject );

    RemoteObjectLifeCycleAdapter.render();

    verify( remoteObject ).render( same( ContextProvider.getProtocolWriter() ) );
  }

  @Test
  public void testReadData_delegatesSetOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterRemoteObject( "id", handler );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeSetOperation( "id", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleSet( eq( properties ) );
  }

  @Test
  public void testReadData_delegatesCallOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterRemoteObject( "id", handler );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeCallOperation( "id", "method", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleCall( eq( "method" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotDirectlyDelegateNotifyOperationsToHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterRemoteObject( "id", handler );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeNotifyOperation( "id", "event", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verifyZeroInteractions( handler );
  }

  @Test
  public void testReadData_schedulesNotifyOperationsForHandlers() {
    OperationHandler handler = mock( OperationHandler.class );
    mockAndRegisterRemoteObject( "id", handler );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeNotifyOperation( "id", "event", properties );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectLifeCycleAdapter.readData();

    verify( handler ).handleNotify( eq( "event" ), eq( properties ) );
  }

  @Test
  public void testReadData_doesNotFailWhenNoHandlerRegistered() {
    mockAndRegisterRemoteObject( "id", null );

    RemoteObjectLifeCycleAdapter.readData();
  }

  @Test
  public void testReadData_failsWhenNoHandlerRegisteredForOperations() {
    mockAndRegisterRemoteObject( "id", null );
    Fixture.fakeCallOperation( "id", "method", createTestProperties() );

    try {
      RemoteObjectLifeCycleAdapter.readData();
      fail();
    } catch( UnsupportedOperationException exception ) {
      String expected = "No operation handler registered for remote object: id";
      assertEquals( expected, exception.getMessage() );
    }
  }

  private static RemoteObjectImpl mockAndRegisterRemoteObject( String id, OperationHandler handler )
  {
    RemoteObjectImpl remoteObject = mockRemoteObjectImpl( id, handler );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  private static RemoteObjectImpl mockRemoteObjectImpl( String id, OperationHandler handler ) {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( id );
    when( remoteObject.getHandler() ).thenReturn( handler );
    return remoteObject;
  }

  private static Map<String, Object> createTestProperties() {
    HashMap<String, Object> result = new HashMap<String, Object>();
    result.put( "foo", "bar" );
    return result;
  }

}
