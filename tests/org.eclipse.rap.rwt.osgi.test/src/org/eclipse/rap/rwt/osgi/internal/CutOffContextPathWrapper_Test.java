/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.osgi.internal.CutOffContextPathWrapper.RequestWrapper;


public class CutOffContextPathWrapper_Test extends TestCase {
  private static final String ALIAS = "alias";
  
  private CutOffContextPathWrapper cutOffContextPathWrapper;
  private HttpServlet servlet;

  public void testDestroy() {
    cutOffContextPathWrapper.destroy();
    
    verify( servlet ).destroy();
  }

  public void testGetInitParameter() {
    String key = "key";
    cutOffContextPathWrapper.getInitParameter( key );
    
    verify( servlet ).getInitParameter( key );
  }

  public void testGetServletConfig() {
    cutOffContextPathWrapper.getServletConfig();
    
    verify( servlet ).getServletConfig();
  }

  public void testGetInitParameterNames() {
    cutOffContextPathWrapper.getInitParameterNames();
    
    verify( servlet ).getInitParameterNames();
  }

  public void testGetServletContext() {
    cutOffContextPathWrapper.getServletContext();
    
    verify( servlet ).getServletContext();
  }

  public void testGetServletInfo() {
    cutOffContextPathWrapper.getServletInfo();
    
    verify( servlet ).getServletInfo();
  }

  public void testInit() throws ServletException {
    cutOffContextPathWrapper.init();
    
    verify( servlet ).init();
  }

  public void testInitWithServletConfig() throws ServletException {
    ServletConfig servletConfig = mock( ServletConfig.class );
    cutOffContextPathWrapper.init( servletConfig );
    
    verify( servlet ).init( servletConfig );
  }

  public void testGetServletName() {
    cutOffContextPathWrapper.getServletName();
    
    verify( servlet ).getServletName();
  }

  public void testService() throws Exception {
    HttpServletRequest request = mock( HttpServletRequest.class );
    HttpServletResponse response = mock( HttpServletResponse.class );
    
    cutOffContextPathWrapper.service( request, response );
    
    verify( servlet ).service( any( RequestWrapper.class ), eq( response ) );
  }
  
  public void testRequestWrapper() {
    RequestWrapper requestWrapper = new RequestWrapper( mock( HttpServletRequest.class ), ALIAS );
    
    String servletPath = requestWrapper.getServletPath();
    
    assertEquals( "/" + ALIAS, servletPath );
  }

  public void testLogWithThrowable() {
    Throwable throwable = new Throwable();
    String message = "message";
    cutOffContextPathWrapper.log( message, throwable );

    verify( servlet ).log( message, throwable );
  }

  public void testLog() {
    String message = "message";
    cutOffContextPathWrapper.log( message );
    
    verify( servlet ).log( message );
  }

  protected void setUp() {
    servlet = mock( HttpServlet.class );
    cutOffContextPathWrapper = new CutOffContextPathWrapper( servlet, ALIAS );
  }
}
