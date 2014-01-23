/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.engine;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTServlet_Test {

  private ApplicationContextImpl applicationContext;
  private ServiceHandler serviceHandler;
  private RWTServlet servlet;
  private TestRequest request;
  private TestResponse response;

  @Before
  public void setUp() throws ServletException {
    applicationContext = mockApplicationContext();
    serviceHandler = mock( ServiceHandler.class );
    fakeServiceHandler( applicationContext, serviceHandler );
    servlet = new RWTServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockServletContext( applicationContext );
      }
    };
    servlet.init();
    request = new TestRequest();
    response = new TestResponse();
  }

  @After
  public void tearDown() {
    ContextProvider.disposeContext();
  }

  @Test
  public void testHandleRequest_whenApplicationContextNotReady() throws Exception {
    fakeAllowsRequest( applicationContext, false );
    HttpServletResponse response = mock( HttpServletResponse.class );

    servlet.doGet( request, response );

    verify( response ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
    verifyNoMoreInteractions( response );
  }

  @Test
  public void testHandleRequest_withValidUrl() throws Exception {
    request.setServletPath( "/foo" );
    request.setPathInfo( null );
    request.setSession( mock( HttpSession.class ) );

    servlet.doGet( request, response );

    verify( serviceHandler ).service( request, response );
  }

  @Test
  public void testHandleRequest_withRootServlet() throws Exception {
    request.setServletPath( "" );
    request.setPathInfo( "/" );
    request.setSession( mock( HttpSession.class ) );

    servlet.doGet( request, response );

    verify( serviceHandler ).service( request, response );
  }

  @Test
  public void testHandleRequest_withTrailingSlash() throws Exception {
    request.setRequestURI( "/foo/bar" );
    request.setPathInfo( "/" );
    request.setSession( mock( HttpSession.class ) );

    servlet.doGet( request, response );

    assertEquals( "/foo/bar", response.getRedirect() );
  }

  @Test
  public void testHandleRequest_withIllegalPathInfo() throws Exception {
    request.setPathInfo( "foo" );

    servlet.doGet( request, response );

    assertEquals( HttpServletResponse.SC_NOT_FOUND, response.getErrorStatus() );
  }

  @Test
  public void testCreateRedirectUrl() {
    request.setPathInfo( "/" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap", url );
  }

  @Test
  public void testCreateRedirectUrl_withParam() {
    request.setParameter( "param1", "value1" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap?param1=value1", url );
  }

  @Test
  public void testCreateRedirectUrl_withTwoParams() {
    request.setParameter( "param1", "value1" );
    request.setParameter( "param2", "value2" );

    String url = RWTServlet.createRedirectUrl( request );

    assertTrue(    "/fooapp/rap?param1=value1&param2=value2".equals( url )
                || "/fooapp/rap?param2=value2&param1=value1".equals( url ) );
  }

  @Test
  public void testServiceHandlerHasServiceStore() throws ServletException, IOException {
    final AtomicReference<ServiceStore> serviceStoreRef = new AtomicReference<ServiceStore>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        serviceStoreRef.set( ContextProvider.getServiceStore() );
      }
    } );
    request.setSession( new TestSession() );

    servlet.doPost( request, new TestResponse() );

    assertNotNull( serviceStoreRef.get() );
  }

  @Test
  public void testSetApplicationContextInServiceContext() throws ServletException, IOException {
    final AtomicReference<ApplicationContext> appContextRef = new AtomicReference<ApplicationContext>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        appContextRef.set( ContextProvider.getContext().getApplicationContext() );
      }
    } );
    request.setSession( new TestSession() );

    servlet.doPost( request, new TestResponse() );

    assertSame( applicationContext, appContextRef.get() );
  }

  @Test
  public void testEnsureUISession() {
    ApplicationContextImpl applicationContext = createRealApplicationContext();
    request.setSession( new TestSession() );
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertNotNull( serviceContext.getUISession() );
    ContextProvider.disposeContext();
  }

  @Test
  public void testEnsureUISession_returnsExistingUISession() {
    ApplicationContextImpl applicationContext = createRealApplicationContext();
    HttpSession httpSession = new TestSession();
    request.setSession( httpSession );
    UISessionImpl uiSession = new UISessionImpl( applicationContext, httpSession );
    uiSession.attachToHttpSession();
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertSame( uiSession, serviceContext.getUISession() );
    ContextProvider.disposeContext();
  }

  @Test
  public void testEnsureUISession_returnsExistingUISession_withConnectionId() {
    ApplicationContextImpl applicationContext = createRealApplicationContext();
    HttpSession httpSession = new TestSession();
    request.setSession( httpSession );
    request.setParameter( "cid", "foo" );
    UISessionImpl uiSession = new UISessionImpl( applicationContext, httpSession, "foo" );
    uiSession.attachToHttpSession();
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertSame( uiSession, serviceContext.getUISession() );
    ContextProvider.disposeContext();
  }

  private static ServletContext mockServletContext( ApplicationContext applicationContext ) {
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getAttribute( anyString() ) ).thenReturn( applicationContext );
    return servletContext;
  }

  private static ApplicationContextImpl mockApplicationContext() {
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    when( applicationContext.getEntryPointManager() ).thenReturn( mock( EntryPointManager.class ) );
    when( applicationContext.getClientSelector() ).thenReturn( mock( ClientSelector.class ) );
    when( Boolean.valueOf( applicationContext.isActive() ) ).thenReturn( Boolean.TRUE );
    when( Boolean.valueOf( applicationContext.allowsRequests() ) ).thenReturn( Boolean.TRUE );
    return applicationContext;
  }

  private static void fakeServiceHandler( ApplicationContext applicationContext,
                                          ServiceHandler serviceHandler )
  {
    ServiceManagerImpl serviceManager = mock( ServiceManagerImpl.class );
    when( serviceManager.getHandler() ).thenReturn( serviceHandler );
    when( applicationContext.getServiceManager() ).thenReturn( serviceManager );
  }

  private static void fakeAllowsRequest( ApplicationContextImpl applicationContext,
                                         boolean allowsRequests )
  {
    when( Boolean.valueOf( applicationContext.allowsRequests() ) )
      .thenReturn( Boolean.valueOf( allowsRequests ) );
  }

  private static ApplicationContextImpl createRealApplicationContext() {
    ServletContext servletContext = new TestServletContext();
    ApplicationConfiguration configuration = mock( ApplicationConfiguration.class );
    ApplicationContextImpl applicationContext = new ApplicationContextImpl( configuration,
                                                                            servletContext );
    applicationContext.activate();
    applicationContext.attachToServletContext();
    return applicationContext;
  }

}
