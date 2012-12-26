/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.engine;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;


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
    UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( httpSession );
    if( uiSession != null ) {
      uiSession.attachHttpSession( httpSession );
      attachApplicationContext( uiSession );
      PostDeserialization.runProcessors( uiSession );
    }
  }

  private static void attachApplicationContext( UISession uiSession ) {
    ServletContext servletContext = uiSession.getHttpSession().getServletContext();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    ApplicationContextUtil.set( uiSession, applicationContext );
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
    UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );
    RequestCounter.reattachToHttpSession( httpSession );
  }

}
