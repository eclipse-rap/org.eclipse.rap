/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rwt.internal.engine;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.service.*;


public class RWTDelegate extends HttpServlet {

  private static final long serialVersionUID = 1L;

  ////////////////////
  // Servlet overrides

  public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    doPost( request, response );
  }

  public void doPost( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    if( request.getPathInfo() == null ) {
      ServiceContext context = new ServiceContext( request, response );
      ContextProvider.setContext( context );
      try {
        createSessionStore();
        RWTFactory.getServiceManager().getHandler().service();
      } finally {
        ContextProvider.disposeContext();
      }
    } else {
      handleInvalidRequest( request, response );
    }
  }

  public String getServletInfo() {
    return "RAP Servlet Delegate";
  }

  //////////////////
  // Helping methods

  private void createSessionStore() {
    // Ensure that there is exactly one ISessionStore per session created
    synchronized( this ) {
      ContextProvider.getSession();
    }
  }

  static void handleInvalidRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( "/".equals( request.getPathInfo() ) ) {
      // In case of "http://example.com/webapp/servlet/" redirect to
      // "http://example.com/webapp/servlet" (same URL without trailing slash)
      String redirectUrl = createRedirectUrl( request );
      response.sendRedirect( response.encodeRedirectURL( redirectUrl ) );
    } else {
      // Otherwise send 404 - not found
      response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }
  }

  static String createRedirectUrl( HttpServletRequest request ) {
    String result = request.getContextPath() + request.getServletPath();
    Enumeration parameterNames = request.getParameterNames();
    if( parameterNames.hasMoreElements() ) {
      result += "?";
      boolean first = true;
      while( parameterNames.hasMoreElements() ) {
        String parameterName = ( String )parameterNames.nextElement();
        if( !first ) {
          result += "&";
        }
        result += parameterName;
        result += "=";
        result += request.getParameter( parameterName );
        first = false;
      }
    }
    return result;
  }
}