/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rap.rwt.osgi.internal.CutOffContextPathWrapper.RequestWrapper;
import org.junit.Before;
import org.junit.Test;


public class CutOffContextPathWrapper_Test {
  private static final String ALIAS = "alias";

  private CutOffContextPathWrapper cutOffContextPathWrapper;
  private HttpServlet servlet;

  @Before
  public void setUp() {
    servlet = mock( HttpServlet.class );
    ServletContext servletContext = mock( ServletContext.class );
    cutOffContextPathWrapper = new CutOffContextPathWrapper( servlet, servletContext, ALIAS );
  }

  @Test
  public void testDestroy() {
    cutOffContextPathWrapper.destroy();

    verify( servlet ).destroy();
  }

  @Test
  public void testGetInitParameter() {
    String key = "key";
    cutOffContextPathWrapper.getInitParameter( key );

    verify( servlet ).getInitParameter( key );
  }

  @Test
  public void testGetServletConfig() {
    cutOffContextPathWrapper.getServletConfig();

    verify( servlet ).getServletConfig();
  }

  @Test
  public void testGetInitParameterNames() {
    cutOffContextPathWrapper.getInitParameterNames();

    verify( servlet ).getInitParameterNames();
  }

  @Test
  public void testGetServletContext() {
    cutOffContextPathWrapper.getServletContext();

    verify( servlet ).getServletContext();
  }

  @Test
  public void testGetServletInfo() {
    cutOffContextPathWrapper.getServletInfo();

    verify( servlet ).getServletInfo();
  }

  @Test
  public void testInit() throws ServletException {
    cutOffContextPathWrapper.init();

    verify( servlet ).init();
  }

  @Test
  public void testInitWithServletConfig() throws ServletException {
    ServletConfig servletConfig = mock( ServletConfig.class );
    cutOffContextPathWrapper.init( servletConfig );

    verify( servlet ).init( servletConfig );
  }

  @Test
  public void testGetServletName() {
    cutOffContextPathWrapper.getServletName();

    verify( servlet ).getServletName();
  }

  @Test
  public void testService() throws Exception {
    HttpServletRequest request = mock( HttpServletRequest.class );
    HttpServletResponse response = mock( HttpServletResponse.class );

    cutOffContextPathWrapper.service( request, response );

    verify( servlet ).service( any( RequestWrapper.class ), eq( response ) );
  }

  @Test
  public void testRequestWrapper() {
    HttpServletRequest servletRequest = mock( HttpServletRequest.class );
    ServletContext servletContext = mock( ServletContext.class );
    RequestWrapper requestWrapper = new RequestWrapper( servletRequest, servletContext, ALIAS );

    String servletPath = requestWrapper.getServletPath();
    ServletContext foundContext = requestWrapper.getSession().getServletContext();

    assertEquals( ALIAS, servletPath );
    assertSame( servletContext, foundContext );
  }

  @Test
  public void testLogWithThrowable() {
    Throwable throwable = new Throwable();
    String message = "message";
    cutOffContextPathWrapper.log( message, throwable );

    verify( servlet ).log( message, throwable );
  }

  @Test
  public void testLog() {
    String message = "message";
    cutOffContextPathWrapper.log( message );

    verify( servlet ).log( message );
  }
}
