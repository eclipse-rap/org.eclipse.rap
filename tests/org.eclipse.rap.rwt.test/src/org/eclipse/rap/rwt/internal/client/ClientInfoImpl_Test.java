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
package org.eclipse.rap.rwt.internal.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ClientInfoImpl_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new ClientInfoImpl();

    verify( connection ).createServiceObject( eq( "rwt.client.ClientInfo" ) );
  }

  @Test
  public void testGetTimezoneOffset_failsWhenTimezoneOffsetNotSet() {
    fakeConnection( mock( RemoteObject.class ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();

    try {
      clientInfo.getTimezoneOffset();
      fail();
    } catch( IllegalStateException exception ) {
      // expected
    }
  }

  @Test
  public void testGetTimezoneOffset_readsTimezoneOffsetFromHandler() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    ClientInfoImpl clientInfo = new ClientInfoImpl();
    OperationHandler handler = getHandler( remoteObject );

    handler.handleSet( new JsonObject().add( "timezoneOffset", -90 ) );

    assertEquals( -90, clientInfo.getTimezoneOffset() );
  }

  @Test
  public void testGetLocale_returnsNullWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    fakeConnection();

    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertNull( clientInfo.getLocale() );
  }

  @Test
  public void testGetLocales_returnsEmptyArrayWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    fakeConnection();

    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertEquals( 0, clientInfo.getLocales().length );
  }

  @Test
  public void testGetLocale_readsLocaleFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    fakeConnection();

    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertEquals( new Locale( "en-US" ), clientInfo.getLocale() );
  }

  @Test
  public void testGetLocales_readsLocalesFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    fakeConnection();

    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ), new Locale( "de-DE" ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertEquals( 2, clientInfo.getLocales().length );
    assertEquals( new Locale( "en-US" ), clientInfo.getLocales()[ 0 ] );
    assertEquals( new Locale( "de-DE" ), clientInfo.getLocales()[ 1 ] );
  }

  @Test
  public void testReturnsSaveLocalesCopy() {
    TestRequest request = Fixture.fakeNewGetRequest();
    fakeConnection();

    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();
    clientInfo.getLocales()[ 0 ] = new Locale( "de-DE" );

    assertEquals( new Locale( "en-US" ), clientInfo.getLocales()[ 0 ] );
  }

  private static OperationHandler getHandler( RemoteObject remoteObject ) {
    ArgumentCaptor<OperationHandler> captor = ArgumentCaptor.forClass( OperationHandler.class );
    verify( remoteObject ).setHandler( captor.capture() );
    return captor.getValue();
  }

  private void fakeConnection() {
    fakeConnection( mock( RemoteObject.class ) );
  }

  private ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
