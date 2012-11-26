/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectLifeCycleAdapter_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRenderDelegatesToRegisteredRemoteObjects() {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    RemoteObjectRegistry.getInstance().register( remoteObject );

    RemoteObjectLifeCycleAdapter.render();

    verify( remoteObject ).render( same( ContextProvider.getProtocolWriter() ) );
  }

  public void testReadDataDelegatesSetOperationsToRegisteredRemoteObjects() {
    RemoteObjectImpl remoteObject = mockAndRegisterRemoteObject( "id" );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeSetOperation( "id", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( remoteObject ).handleSet( eq( properties ) );
  }

  public void testReadDataDelegatesCallOperationsToRegisteredRemoteObjects() {
    RemoteObjectImpl remoteObject = mockAndRegisterRemoteObject( "id" );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeCallOperation( "id", "method", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( remoteObject ).handleCall( eq( "method" ), eq( properties ) );
  }

  public void testReadDataDelegatesNotifyOperationsToRegisteredRemoteObjects() {
    RemoteObjectImpl remoteObject = mockAndRegisterRemoteObject( "id" );
    Map<String, Object> properties = createTestProperties();
    Fixture.fakeNotifyOperation( "id", "event", properties );

    RemoteObjectLifeCycleAdapter.readData();

    verify( remoteObject ).handleNotify( eq( "event" ), eq( properties ) );
  }

  private static RemoteObjectImpl mockAndRegisterRemoteObject( String id ) {
    RemoteObjectImpl remoteObject = mockRemoteObjectImpl( id );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  private static RemoteObjectImpl mockRemoteObjectImpl( String id ) {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( id );
    return remoteObject;
  }

  private static Map<String, Object> createTestProperties() {
    HashMap<String, Object> result = new HashMap<String, Object>();
    result.put( "foo", "bar" );
    return result;
  }

}
