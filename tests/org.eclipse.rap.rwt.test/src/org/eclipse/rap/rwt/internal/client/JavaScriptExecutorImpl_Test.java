/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.client.JavaScriptExecutorImpl;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JavaScriptExecutorImpl_Test {

  private static final String REMOTE_ID = "rwt.client.JavaScriptExecutor";

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExecute_createsRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new JavaScriptExecutorImpl();

    verify( connection ).createServiceObject( eq( REMOTE_ID ) );
  }

  @Test
  public void testExecute_createsCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    JavaScriptExecutorImpl executor = new JavaScriptExecutorImpl();

    executor.execute( "code 1" );

    verify( remoteObject ).call( eq( "execute" ), eq( createProperties( "content", "code 1" ) ) );
  }

  @Test
  public void testExecute_createsSeparateOperationForEveryCall() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    JavaScriptExecutorImpl executor = new JavaScriptExecutorImpl();

    executor.execute( "code 1" );
    executor.execute( "code 2" );

    verify( remoteObject ).call( eq( "execute" ), eq( createProperties( "content", "code 1" ) ) );
    verify( remoteObject ).call( eq( "execute" ), eq( createProperties( "content", "code 2" ) ) );
    verifyNoMoreInteractions( remoteObject );
  }

  private static Map<String, Object> createProperties( String parameter, Object value ) {
    HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put( parameter, value );
    return properties;
  }

  private static ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class);
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
