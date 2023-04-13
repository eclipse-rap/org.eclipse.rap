/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.scripting;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientFunction_Test {

  private static final String CLIENT_LISTENER_TYPE = "rwt.scripting.Function";

  @Before
  public void setup() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreation_failsWithStringNull() {
    try {
      new ClientFunction( ( String )null );
      fail();
    } catch( NullPointerException expected ) {
      assertTrue( expected.getMessage().contains( "scriptCode" ) );
    }
  }

  @Test
  public void testCreation_createsRemoteObject() {
    Connection connection = fakeConnection( mock( RemoteObject.class ) );

    new ClientFunction( "script code" );

    verify( connection ).createRemoteObject( CLIENT_LISTENER_TYPE );
  }

  @Test
  public void testCreation_initializesRemoteObject() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );

    new ClientFunction( "script code" );

    verify( remoteObject ).set( eq( "scriptCode" ), eq( "script code" ) );
  }

  @Test
  public void testGetRemoteId() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    when( remoteObject.getId() ).thenReturn( "foo" );
    fakeConnection( remoteObject );

    ClientFunction clientFunction = new ClientFunction( "script code" );

    assertEquals( "foo", clientFunction.getRemoteId() );
  }

  private Connection fakeConnection( RemoteObject listenerRemoteObject ) {
    Connection connection = mock( Connection.class );
    when( connection.createRemoteObject( eq( CLIENT_LISTENER_TYPE ) ) ).thenReturn( listenerRemoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
