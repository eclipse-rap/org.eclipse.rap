/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.RWTMessages;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WebClientMessages_Test {

  private static final String REMOTE_ID = "rwt.client.ClientMessages";

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
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new WebClientMessages();

    verify( connection ).createServiceObject( eq( REMOTE_ID ) );
  }

  @Test
  public void testUpdate_createsSetOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    WebClientMessages messages = new WebClientMessages();

    messages.update( Locale.CANADA );

    verify( remoteObject ).set( eq( "messages" ), any( JsonObject.class ) );
  }

  @Test
  public void testUpdate_doesNotCreateSetOperationForUnchangedMessages() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    WebClientMessages messages = new WebClientMessages();
    messages.update( Locale.CANADA );
    reset( remoteObject );

    messages.update( Locale.FRANCE );

    verify( remoteObject, times( 0 ) ).set( eq( "messages" ), any( JsonObject.class ) );
  }

  @Test
  public void testUpdate_renderModifiedMessages() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    WebClientMessages messages = spy( new WebClientMessages() );
    messages.update( Locale.CANADA );
    reset( remoteObject );

    when( messages.getMessage( RWTMessages.SERVER_ERROR ) ).thenReturn( "foo" );
    messages.update( Locale.FRANCE );

    JsonObject expected = new JsonObject().add( RWTMessages.SERVER_ERROR, "foo" );
    verify( remoteObject ).set( eq( "messages" ), eq( expected ) );
  }

  private static ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
