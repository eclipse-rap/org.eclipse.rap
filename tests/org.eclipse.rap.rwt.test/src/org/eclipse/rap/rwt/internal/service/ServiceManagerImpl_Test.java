/*******************************************************************************
 * Copyright (c) 2002, 2021 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.RWTProperties.SERVICE_HANDLER_BASE_URL;
import static org.eclipse.rap.rwt.internal.RWTProperties.SERVICE_HANDLER_USE_RELATIVE_URL;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServiceManagerImpl_Test {

  private ServiceHandler defaultServiceHandler;
  private ServiceManagerImpl serviceManager;

  @Before
  public void setUp() {
    Fixture.setUp();
    defaultServiceHandler = mock( ServiceHandler.class );
    serviceManager = new ServiceManagerImpl( defaultServiceHandler );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRegisterServiceHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );

    serviceManager.registerServiceHandler( "id", serviceHandler );

    assertSame( serviceHandler, serviceManager.getServiceHandler( "id" ) );
  }

  @Test
  public void testRegisterServiceHandler_failsWhithSameId() {
    serviceManager.registerServiceHandler( "foo", mock( ServiceHandler.class ) );

    try {
      serviceManager.registerServiceHandler( "foo", mock( ServiceHandler.class ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage().toLowerCase( Locale.ENGLISH );
      assertTrue( message.contains( "already registered" ) && message.contains( "foo" ) );
    }
  }

  @Test
  public void testRegisterServiceHandler_failsWithNullId() {
    try {
      serviceManager.registerServiceHandler( null, mock( ServiceHandler.class ) );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  @Test
  public void testRegisterServiceHandler_failsWithEmptyId() {
    try {
      serviceManager.registerServiceHandler( "", mock( ServiceHandler.class ) );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  @Test
  public void testRegisterServiceHandler_failsWithNullHandler() {
    try {
      serviceManager.registerServiceHandler( "id", null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  @Test
  public void testUnregisterServiceHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.unregisterServiceHandler( "id" );

    assertNull( serviceManager.getServiceHandler( "id" ) );
  }

  @Test
  public void testUnregisterServiceHandler_doesNotFailWithUnknownId() {
    serviceManager.unregisterServiceHandler( "id" );
  }

  @Test
  public void testUnregisterServiceHandler_failsWithNull() {
    try {
      serviceManager.unregisterServiceHandler( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  @Test
  public void testUnregisterServiceHandler_failsWithEmpty() {
    try {
      serviceManager.unregisterServiceHandler( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  @Test
  public void testClear_removesCustomHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.clear();

    assertNull( serviceManager.getServiceHandler( "id" ) );
  }

  @Test
  public void testClear_retainsDefaultHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.clear();

    assertSame( defaultServiceHandler, serviceManager.getHandler() );
  }

  @Test
  public void testGetHandler_returnsDefaultHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );
    serviceManager.registerServiceHandler( "id", serviceHandler );

    ServiceHandler handler = serviceManager.getHandler();

    assertSame( defaultServiceHandler, handler );
  }

  @Test
  public void testGetHandler_returnsCustomHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );
    serviceManager.registerServiceHandler( "id", serviceHandler );
    TestRequest request = Fixture.fakeNewGetRequest();
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "id" );

    ServiceHandler handler = serviceManager.getHandler();

    assertSame( serviceHandler, handler );
  }

  @Test
  public void testGetHandler_failsWithUnknownId() {
    TestRequest request = Fixture.fakeNewGetRequest();
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "id" );

    try {
      serviceManager.getHandler();
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "id" ) );
    }
  }

  @Test
  public void testGetServiceHandlerUrl_returnsAbsoluteUrl() {
    String url = serviceManager.getServiceHandlerUrl( "foo" );

    assertTrue( url.startsWith( "/fooapp/rap?servicehandler=foo" ) );
  }

  public void testGetServiceHandlerUrl_includesConnectionId() {
    getContext().setUISession( mockUISessionWithConnectionId( "bar" ) );

    String url = serviceManager.getServiceHandlerUrl( "foo" );

    assertEquals( "/fooapp/rap?servicehandler=foo&cid=bar", url );
  }

  @Test
  public void testGetServiceHandlerUrl_returnsUrlWithCharactersEscaped() {
    getContext().setUISession( mockUISessionWithConnectionId( "bar" ) );

    String url = serviceManager.getServiceHandlerUrl( "Smørre brød" );

    assertEquals( "/fooapp/rap?servicehandler=Sm%C3%B8rre%20br%C3%B8d&cid=bar", url );
  }

  @Test
  public void testGetServiceHandlerUrl_failsWithNull() {
    try {
      serviceManager.getServiceHandlerUrl( null );
      fail();
    } catch( NullPointerException exception ) {
      assertThat( exception.getMessage(), containsString( "parameter" ) );
    }
  }

  @Test
  public void testGetServiceHandlerUrl_withoutUISession() {
    getContext().setUISession( null );

    String url = serviceManager.getServiceHandlerUrl( "foo" );

    assertEquals( "/fooapp/rap?servicehandler=foo", url );
  }

  @Test
  public void testGetServiceHandlerUrl_withBaseUrl() {
    getContext().setUISession( null );
    System.setProperty( SERVICE_HANDLER_BASE_URL, "http://foo/bar" );

    String url = serviceManager.getServiceHandlerUrl( "foo" );

    assertEquals( "http://foo/bar/fooapp/rap?servicehandler=foo", url );
    System.getProperties().remove( SERVICE_HANDLER_BASE_URL );
  }
  
  @Test
  public void testGetServiceHandlerUrl_withRelativeURL() {
    getContext().setUISession( null );
    System.setProperty( SERVICE_HANDLER_USE_RELATIVE_URL, "true" );

    String url = serviceManager.getServiceHandlerUrl( "foo" );

    assertEquals( "?servicehandler=foo", url );
    System.getProperties().remove( SERVICE_HANDLER_USE_RELATIVE_URL );
  }

  private static UISessionImpl mockUISessionWithConnectionId( String connectionId ) {
    UISessionImpl uiSession = mock( UISessionImpl.class );
    when( Boolean.valueOf( uiSession.isBound() ) ).thenReturn( Boolean.TRUE );
    when( uiSession.getConnectionId() ).thenReturn( connectionId );
    return uiSession;
  }

}
