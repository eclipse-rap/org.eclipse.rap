/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class RWTClusterSupport implements Filter {

  public void init( FilterConfig filterConfig ) {
  }

  public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
    throws IOException, ServletException
  {
    beforeService( request );
    chain.doFilter( request, response );
    afterService( request );
  }

  public void destroy() {
  }

  private static HttpSession getHttpSession( ServletRequest request ) {
    HttpServletRequest httpRequest = ( HttpServletRequest )request;
    return httpRequest.getSession( false );
  }

  private static void beforeService( ServletRequest request ) {
    HttpSession httpSession = getHttpSession( request );
    if( httpSession != null ) {
      beforeService( httpSession );
    }
  }

  private static void beforeService( HttpSession httpSession ) {
    SessionStoreImpl sessionStore = SessionStoreImpl.getInstanceFromSession( httpSession );
    if( sessionStore != null ) {
      sessionStore.attachHttpSession( httpSession );
    }
  }

  private static void afterService( ServletRequest request ) {
    HttpSession httpSession = getHttpSession( request );
    if( httpSession != null ) {
      afterService( httpSession );
    }
  }

  private static void afterService( HttpSession httpSession ) {
    markSessionChanged( httpSession );
  }

  private static void markSessionChanged( HttpSession httpSession ) {
    SessionStoreImpl sessionStore = SessionStoreImpl.getInstanceFromSession( httpSession );
    SessionStoreImpl.attachToSession( httpSession, sessionStore );
  }
}
