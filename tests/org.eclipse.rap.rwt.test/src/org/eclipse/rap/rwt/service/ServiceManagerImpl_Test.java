/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.service;

import static org.mockito.Mockito.mock;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ServiceManagerImpl_Test extends TestCase {

  private ServiceHandler defaultServiceHandler;
  private ServiceManagerImpl serviceManager;

  @Override
  protected void setUp() {
    Fixture.setUp();
    defaultServiceHandler = mock( ServiceHandler.class );
    serviceManager = new ServiceManagerImpl( defaultServiceHandler );
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testRegisterServiceHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );

    serviceManager.registerServiceHandler( "id", serviceHandler );

    assertSame( serviceHandler, serviceManager.getServiceHandler( "id" ) );
  }

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

  public void testRegisterServiceHandler_failsWithNullId() {
    try {
      serviceManager.registerServiceHandler( null, mock( ServiceHandler.class ) );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testRegisterServiceHandler_failsWithEmptyId() {
    try {
      serviceManager.registerServiceHandler( "", mock( ServiceHandler.class ) );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  public void testRegisterServiceHandler_failsWithNullHandler() {
    try {
      serviceManager.registerServiceHandler( "id", null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testUnregisterServiceHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.unregisterServiceHandler( "id" );

    assertNull( serviceManager.getServiceHandler( "id" ) );
  }

  public void testUnregisterServiceHandler_doesNotFailWithUnknownId() {
    serviceManager.unregisterServiceHandler( "id" );
  }

  public void testUnregisterServiceHandler_failsWithNull() {
    try {
      serviceManager.unregisterServiceHandler( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testUnregisterServiceHandler_failsWithEmpty() {
    try {
      serviceManager.unregisterServiceHandler( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  public void testClear_removesCustomHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.clear();

    assertNull( serviceManager.getServiceHandler( "id" ) );
  }

  public void testClear_retainsDefaultHandler() {
    serviceManager.registerServiceHandler( "id", mock( ServiceHandler.class ) );

    serviceManager.clear();

    assertSame( defaultServiceHandler, serviceManager.getHandler() );
  }

  public void testGetHandler_returnsDefaultHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );
    serviceManager.registerServiceHandler( "id", serviceHandler );

    ServiceHandler handler = serviceManager.getHandler();

    assertSame( defaultServiceHandler, handler );
  }

  public void testGetHandler_returnsCustomHandler() {
    ServiceHandler serviceHandler = mock( ServiceHandler.class );
    serviceManager.registerServiceHandler( "id", serviceHandler );
    Fixture.fakeRequestParam( ServiceManagerImpl.REQUEST_PARAM, "id" );

    ServiceHandler handler = serviceManager.getHandler();

    assertSame( serviceHandler, handler );
  }

  public void testGetHandler_failsWithUnknownId() {
    Fixture.fakeRequestParam( ServiceManagerImpl.REQUEST_PARAM, "id" );

    try {
      serviceManager.getHandler();
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "id" ) );
    }
  }

  public void testGetServiceHandlerUrl_returnsUrl() {
    String url = RWT.getServiceManager().getServiceHandlerUrl( "foo" );

    assertEquals( "/fooapp/rap?servicehandler=foo", url );
  }

  public void testGetServiceHandlerUrl_returnsUrlWithCharactersEscaped() {
    String url = RWT.getServiceManager().getServiceHandlerUrl( "Smørre brød" );

    assertEquals( "/fooapp/rap?servicehandler=Sm%C3%B8rre%20br%C3%B8d", url );
  }

  public void testGetServiceHandlerUrl_failsWithNull() {
    try {
      RWT.getServiceManager().getServiceHandlerUrl( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

}
