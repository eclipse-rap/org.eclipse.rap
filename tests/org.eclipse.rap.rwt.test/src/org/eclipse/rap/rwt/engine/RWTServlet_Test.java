/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.engine;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.CONNECTION_ID;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTServlet_Test {

  private ApplicationContextImpl applicationContext;
  private StartupPage startupPage;
  private RWTServlet servlet;
  private TestRequest request;
  private TestResponse response;

  @Before
  public void setUp() throws ServletException {
    startupPage = mock( StartupPage.class );
    applicationContext = mockApplicationContext( startupPage );
    servlet = new RWTServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockServletContext( applicationContext );
      }
    };
    servlet.init();
    request = new TestRequest();
    request.setSession( new TestHttpSession() );
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

    verify( response ).sendError( SC_SERVICE_UNAVAILABLE );
    verifyNoMoreInteractions( response );
  }

  @Test
  public void testHandleRequest_withValidUrl() throws Exception {
    ServiceHandler lifeCycleServiceHandler = mock( ServiceHandler.class );
    fakeServiceHandler( applicationContext, lifeCycleServiceHandler );
    request.setMethod( HTTP.METHOD_POST );
    request.setContentType( HTTP.CONTENT_TYPE_JSON );
    request.setServletPath( "/foo" );
    request.setPathInfo( null );
    request.setSession( mock( HttpSession.class ) );

    servlet.doGet( request, response );

    verify( lifeCycleServiceHandler ).service( request, response );
  }

  @Test
  public void testHandleRequest_withRootServlet() throws Exception {
    ServiceHandler lifeCycleServiceHandler = mock( ServiceHandler.class );
    fakeServiceHandler( applicationContext, lifeCycleServiceHandler );
    request.setMethod( HTTP.METHOD_POST );
    request.setContentType( HTTP.CONTENT_TYPE_JSON );
    request.setServletPath( "" );
    request.setPathInfo( "/" );
    request.setSession( mock( HttpSession.class ) );

    servlet.doGet( request, response );

    verify( lifeCycleServiceHandler ).service( request, response );
  }

  @Test
  public void testHandleRequest_withTrailingSlash() throws Exception {
    request.setServletPath( "/foo" );
    request.setPathInfo( "/" );

    servlet.doGet( request, response );

    assertEquals( SC_NOT_FOUND, response.getErrorStatus() );
  }

  @Test
  public void testHandleRequest_withIllegalPathInfo() throws Exception {
    request.setServletPath( "/foo" );
    request.setPathInfo( "bar" );

    servlet.doGet( request, response );

    assertEquals( SC_NOT_FOUND, response.getErrorStatus() );
  }

  @Test
  public void testHandleRequest_toCustomServiceHandler_hasUISession() throws Exception {
    UISessionImpl uiSession = new UISessionImpl( applicationContext, request.getSession(), "cid" );
    uiSession.attachToHttpSession();
    final AtomicReference<UISession> uiSessionRef = new AtomicReference<>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        uiSessionRef.set( ContextProvider.getUISession() );
      }
    } );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    request.setParameter( CONNECTION_ID, "cid" );

    servlet.doGet( request, response );

    assertSame( uiSession, uiSessionRef.get() );
  }

  @Test
  public void testHandleRequest_toCustomServiceHandler_doesNotCreateNewUISession() throws Exception {
    final AtomicReference<UISession> uiSessionRef = new AtomicReference<>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        uiSessionRef.set( ContextProvider.getUISession() );
      }
    } );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );

    servlet.doGet( request, response );

    assertNull( uiSessionRef.get() );
  }

  @Test
  public void testServiceHandlerHasServiceStore() throws ServletException, IOException {
    final AtomicReference<ServiceStore> serviceStoreRef = new AtomicReference<>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        serviceStoreRef.set( ContextProvider.getServiceStore() );
      }
    } );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    request.setSession( new TestHttpSession() );

    servlet.doPost( request, new TestResponse() );

    assertNotNull( serviceStoreRef.get() );
  }

  @Test
  public void testSetApplicationContextInServiceContext() throws ServletException, IOException {
    final AtomicReference<ApplicationContext> appContextRef = new AtomicReference<>();
    fakeServiceHandler( applicationContext, new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        appContextRef.set( ContextProvider.getContext().getApplicationContext() );
      }
    } );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    request.setSession( new TestHttpSession() );

    servlet.doPost( request, new TestResponse() );

    assertSame( applicationContext, appContextRef.get() );
  }

  @Test
  public void testStartupContent_withHtmlAcceptHeader() throws Exception {
    request.setMethod( HTTP.METHOD_GET );
    request.setHeader( HTTP.HEADER_ACCEPT, HTTP.CONTENT_TYPE_HTML );

    servlet.service( request, response );

    verify( startupPage ).send( response );
  }

  @Test
  public void testStartupContent_withJsonAcceptHeader() throws Exception {
    request.setMethod( HTTP.METHOD_GET );
    request.setHeader( HTTP.HEADER_ACCEPT, HTTP.CONTENT_TYPE_JSON );

    servlet.service( request, response );

    verifyNoInteractions( startupPage );
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testStartupContent_withoutAcceptHeader() throws Exception {
    request.setMethod( HTTP.METHOD_GET );

    servlet.service( request, response );

    verify( startupPage ).send( response );
  }

  @Test
  public void testStartupPage_forHeadRequest() throws Exception {
    request.setMethod( "HEAD" );

    servlet.service( request, response );

    verify( startupPage ).send( any( HttpServletResponse.class ) );
  }

  @Test
  public void testHandlesInvalidRequestContentType() throws Exception {
    // SECURITY: Checking the content-type prevents CSRF attacks, see bug 413668
    // Also allows application to be started with POST request, see bug 416445
    request.setParameter( CONNECTION_ID, "cid" );
    request.setMethod( HTTP.METHOD_POST );
    request.setContentType( "text/plain" );

    servlet.service( request, response );

    verify( startupPage ).send( response );
  }

  private static ServletContext mockServletContext( ApplicationContext applicationContext ) {
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getAttribute( anyString() ) ).thenReturn( applicationContext );
    return servletContext;
  }

  private static ApplicationContextImpl mockApplicationContext( StartupPage startupPage ) {
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    when( applicationContext.getEntryPointManager() ).thenReturn( mock( EntryPointManager.class ) );
    ThemeManager themeManager = createThemeManager();
    when( applicationContext.getThemeManager() ).thenReturn( themeManager );
    when( applicationContext.getStartupPage() ).thenReturn( startupPage );
    ClientSelector clientSelector = createClientSelector();
    when( applicationContext.getClientSelector() ).thenReturn( clientSelector );
    when( applicationContext.getProbeStore() ).thenReturn( createProbeStore() );
    when( Boolean.valueOf( applicationContext.isActive() ) ).thenReturn( Boolean.TRUE );
    when( Boolean.valueOf( applicationContext.allowsRequests() ) ).thenReturn( Boolean.TRUE );
    return applicationContext;
  }

  private static ProbeStore createProbeStore() {
    return new ProbeStore( new TextSizeStorage() );
  }

  private static ClientSelector createClientSelector() {
    Client client = mock( Client.class );
    ClientSelector clientSelector = mock( ClientSelector.class );
    when( clientSelector.getSelectedClient( any( UISession.class ) ) ).thenReturn( client );
    return clientSelector;
  }

  private static ThemeManager createThemeManager() {
    ThemeManager themeManager = mock( ThemeManager.class );
    when( themeManager.getTheme( any( String.class) ) ).thenReturn( mock( Theme.class ) );
    return themeManager;
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

}
