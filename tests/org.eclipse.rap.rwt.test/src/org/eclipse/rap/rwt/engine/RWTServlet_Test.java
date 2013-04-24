/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
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

  private RWTServlet servlet;
  private TestRequest request;
  private TestResponse response;

  @Before
  public void setUp() {
    servlet = new RWTServlet();
    request = new TestRequest();
    response = new TestResponse();
  }

  @After
  public void tearDown() {
    ContextProvider.disposeContext();
  }

  @Test
  public void testInvalidRequestUrlWithPathInfo() throws Exception {
    request.setPathInfo( "foo" );

    RWTServlet.handleInvalidRequest( request, response );

    assertEquals( HttpServletResponse.SC_NOT_FOUND, response.getErrorStatus() );
  }

  @Test
  public void testCreateRedirectUrl() {
    request.setPathInfo( "/" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap", url );
  }

  @Test
  public void testCreateRedirectUrlWithParam() {
    request.setParameter( "param1", "value1" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap?param1=value1", url );
  }

  @Test
  public void testCreateRedirectUrlWithTwoParams() {
    request.setParameter( "param1", "value1" );
    request.setParameter( "param2", "value2" );

    String url = RWTServlet.createRedirectUrl( request );

    assertTrue(    "/fooapp/rap?param1=value1&param2=value2".equals( url )
                || "/fooapp/rap?param2=value2&param1=value1".equals( url ) );
  }

  @Test
  public void testServiceHandlerHasServiceStore() throws ServletException, IOException {
    final List<ServiceStore> log = new ArrayList<ServiceStore>();
    ApplicationContextImpl applicationContext = createApplicationContext();
    applyTestSession( request, applicationContext );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    applicationContext.getServiceManager().registerServiceHandler( "foo", new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        log.add( ContextProvider.getServiceStore() );
      }
    } );
    initServlet( servlet, applicationContext );

    servlet.doPost( request, new TestResponse() );

    assertNotNull( log.get( 0 ) );
  }

  @Test
  public void testSetApplicationContextInServiceContext() throws ServletException, IOException {
    final AtomicReference<ApplicationContext> captor = new AtomicReference<ApplicationContext>();
    ApplicationContextImpl applicationContext = createApplicationContext();
    applyTestSession( request, applicationContext );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    applicationContext.getServiceManager().registerServiceHandler( "foo", new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        captor.set( ContextProvider.getContext().getApplicationContext() );
      }
    } );
    initServlet( servlet, applicationContext );

    servlet.doPost( request, new TestResponse() );

    assertSame( applicationContext, captor.get() );
  }

  @Test
  public void testEnsureUISession() {
    request.setSession( new TestSession() );
    ServiceContext serviceContext
      = new ServiceContext( request, response, createApplicationContext() );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertNotNull( serviceContext.getUISession() );
  }

  @Test
  public void testEnsureUISession_returnsExistingUISession() {
    ApplicationContextImpl applicationContext = createApplicationContext();
    HttpSession httpSession = new TestSession();
    request.setSession( httpSession );
    UISessionImpl uiSession = new UISessionImpl( applicationContext, httpSession );
    uiSession.attachToHttpSession();
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertSame( uiSession, serviceContext.getUISession() );
  }

  @Test
  public void testEnsureUISession_returnsExistingUISession_withConnectionId() {
    ApplicationContextImpl applicationContext = createApplicationContext();
    HttpSession httpSession = new TestSession();
    request.setSession( httpSession );
    request.setParameter( "cid", "foo" );
    UISessionImpl uiSession = new UISessionImpl( applicationContext, httpSession, "foo" );
    uiSession.attachToHttpSession();
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );

    RWTServlet.ensureUISession( serviceContext );

    assertSame( uiSession, serviceContext.getUISession() );
  }

  private static void initServlet( HttpServlet servlet, ApplicationContextImpl applicationContext )
    throws ServletException
  {
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getAttribute( anyString() ) ).thenReturn( applicationContext );
    ServletConfig servletConfig = mock( ServletConfig.class );
    when( servletConfig.getServletContext() ).thenReturn( servletContext );
    servlet.init( servletConfig );
  }

  private static ApplicationContextImpl createApplicationContext() {
    ServletContext servletContext = new TestServletContext();
    ApplicationConfiguration configuration = mock( ApplicationConfiguration.class );
    ApplicationContextImpl applicationContext
      = new ApplicationContextImpl( configuration, servletContext );
    applicationContext.activate();
    applicationContext.attachToServletContext();
    return applicationContext;
  }

  private static void applyTestSession( TestRequest request,
                                        ApplicationContextImpl applicationContext )
  {
    TestSession session = new TestSession();
    session.setServletContext( applicationContext.getServletContext() );
    request.setSession( session );
  }

}
