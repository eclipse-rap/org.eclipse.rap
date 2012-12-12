/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.engine;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class RWTServlet_Test extends TestCase {

  public void testInvalidRequestUrlWithPathInfo() throws Exception {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    request.setPathInfo( "foo" );

    RWTServlet.handleInvalidRequest( request, response );

    assertEquals( HttpServletResponse.SC_NOT_FOUND, response.getErrorStatus() );
  }

  public void testCreateRedirectUrl() {
    TestRequest request = new TestRequest();
    request.setPathInfo( "/" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap", url );
  }

  public void testCreateRedirectUrlWithParam() {
    TestRequest request = new TestRequest();
    request.setParameter( "param1", "value1" );

    String url = RWTServlet.createRedirectUrl( request );

    assertEquals( "/fooapp/rap?param1=value1", url );
  }

  public void testCreateRedirectUrlWithTwoParams() {
    TestRequest request = new TestRequest();
    request.setParameter( "param1", "value1" );
    request.setParameter( "param2", "value2" );

    String url = RWTServlet.createRedirectUrl( request );

    assertTrue(    "/fooapp/rap?param1=value1&param2=value2".equals( url )
                || "/fooapp/rap?param2=value2&param1=value1".equals( url ) );
  }

  public void testServiceHandlerHasServiceStore() throws ServletException, IOException {
    final List<IServiceStore> log = new ArrayList<IServiceStore>();
    ApplicationContextImpl applicationContext = createApplicationContext();
    TestRequest request = createTestRequest( applicationContext );
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "foo" );
    applicationContext.getServiceManager().registerServiceHandler( "foo", new ServiceHandler() {
      public void service( HttpServletRequest request, HttpServletResponse response) {
        log.add( ContextProvider.getServiceStore() );
      }
    } );
    RWTServlet rwtDelegate = new RWTServlet();

    rwtDelegate.doPost( request, new TestResponse() );

    assertNotNull( log.get( 0 ) );
  }

  private static ApplicationContextImpl createApplicationContext() {
    ServletContext servletContext = new TestServletContext();
    ApplicationConfiguration configuration = mock( ApplicationConfiguration.class );
    ApplicationContextImpl result = new ApplicationContextImpl( configuration, servletContext );
    result.activate();
    ApplicationContextUtil.set( result.getServletContext(), result );
    return result;
  }

  private static TestRequest createTestRequest( ApplicationContextImpl applicationContext ) {
    TestSession session = new TestSession();
    session.setServletContext( applicationContext.getServletContext() );
    TestRequest result = new TestRequest();
    result.setSession( session );
    return result;
  }
}
