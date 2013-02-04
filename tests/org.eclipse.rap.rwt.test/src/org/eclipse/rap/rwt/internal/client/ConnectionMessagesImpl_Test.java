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
package org.eclipse.rap.rwt.internal.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ConnectionMessagesImpl_Test {

  private static final String REMOTE_ID = "rwt.client.ConnectionMessages";

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
  public void testWaitHintTimeoutDefaultValue() {
    ConnectionMessagesImpl messages = new ConnectionMessagesImpl();

    assertEquals( 1000, messages.getWaitHintTimeout() );
  }

  @Test
  public void testChangeWaitHintTimeout() {
    ConnectionMessagesImpl messages = new ConnectionMessagesImpl();
    messages.setWaitHintTimeout( 2000 );

    assertEquals( 2000, messages.getWaitHintTimeout() );
  }

  @Test
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new ConnectionMessagesImpl();

    verify( connection ).createServiceObject( eq( REMOTE_ID ) );
  }

  @Test
  public void testSetWaitHintTimeout_createsSetOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    ConnectionMessagesImpl messages = new ConnectionMessagesImpl();

    messages.setWaitHintTimeout( 1999 );

    verify( remoteObject ).set( eq( "waitHintTimeout" ), eq( 1999 ) );
  }

  private static ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
