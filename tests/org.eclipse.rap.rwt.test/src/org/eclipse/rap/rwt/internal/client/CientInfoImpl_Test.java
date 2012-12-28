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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.rap.rwt.internal.remote.RemoteObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectFactory;
import org.eclipse.rap.rwt.internal.remote.RemoteOperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class CientInfoImpl_Test {

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
    RemoteObjectFactory factory = fakeRemoteObjectFactory( mock( RemoteObject.class ) );

    new ClientInfoImpl();

    verify( factory ).createServiceObject( eq( "rwt.client.ClientInfo" ) );
  }

  @Test
  public void testGetTimezoneOffset_failsWhenTimezoneOffsetNotSet() {
    fakeRemoteObjectFactory( mock( RemoteObject.class ) );

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
    fakeRemoteObjectFactory( remoteObject );
    ClientInfoImpl clientInfo = new ClientInfoImpl();
    RemoteOperationHandler handler = getHandler( remoteObject );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "timezoneOffset", new Integer( -90 ) );
    handler.handleSet( parameters );

    assertEquals( -90, clientInfo.getTimezoneOffset() );
  }

  @Test
  public void testGetLocale_returnsNullWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    fakeRemoteObjectFactory();

    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertNull( clientInfo.getLocale() );
  }

  @Test
  public void testGetLocales_returnsEmptyArrayWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    fakeRemoteObjectFactory();

    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertEquals( 0, clientInfo.getLocales().length );
  }

  @Test
  public void testGetLocale_readsLocaleFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    fakeRemoteObjectFactory();

    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();

    assertEquals( new Locale( "en-US" ), clientInfo.getLocale() );
  }

  @Test
  public void testGetLocales_readsLocalesFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    fakeRemoteObjectFactory();

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
    fakeRemoteObjectFactory();

    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientInfoImpl clientInfo = new ClientInfoImpl();
    clientInfo.getLocales()[ 0 ] = new Locale( "de-DE" );

    assertEquals( new Locale( "en-US" ), clientInfo.getLocales()[ 0 ] );
  }

  private static RemoteOperationHandler getHandler( RemoteObject remoteObject ) {
    ArgumentCaptor<RemoteOperationHandler> captor
      = ArgumentCaptor.forClass( RemoteOperationHandler.class );
    verify( remoteObject ).setHandler( captor.capture() );
    return captor.getValue();
  }

  private void fakeRemoteObjectFactory() {
    fakeRemoteObjectFactory( mock( RemoteObject.class ) );
  }

  private RemoteObjectFactory fakeRemoteObjectFactory( RemoteObject remoteObject ) {
    RemoteObjectFactory factory = mock( RemoteObjectFactory.class );
    when( factory.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeRemoteObjectFactory( factory );
    return factory;
  }

}
