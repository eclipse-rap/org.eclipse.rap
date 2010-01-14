/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.service.*;

public class RWTDelegate extends HttpServlet {

  private static final long serialVersionUID = 1L;

  ////////////////////
  // Servlet overrides
  
  public void init( final ServletConfig config ) throws ServletException {  
    super.init( config );
    IEngineConfig engineConfig = getEngineConfig();
    try {
      ConfigurationReader.setEngineConfig( engineConfig );
      createResourceManagerInstance();
    } catch( Exception e ) {
      throw new ServletException( e );
    }
  }

  public void doGet( final HttpServletRequest request,
                     final HttpServletResponse response )
    throws ServletException, IOException
  {
    doPost( request, response );
  }

  public void doPost( final HttpServletRequest request,
                      final HttpServletResponse response )
    throws ServletException, IOException 
  {
    request.setCharacterEncoding( "UTF-8" );
    if( request.getPathInfo() == null ) {
      HttpServletRequest wrappedRequest = getWrappedRequest( request );
      try {
        ServiceContext context = new ServiceContext( wrappedRequest, response );
        ContextProvider.setContext( context );
        createSessionStore();
        ServiceManager.getHandler().service();
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
  
  /////////////////////////////////////////
  // Methods to be overridden by subclasses
  
  protected HttpServletRequest getWrappedRequest( final HttpServletRequest req )
    throws ServletException
  {
    return req;
  }

  //////////////////
  // Helping methods
  
  private IEngineConfig getEngineConfig() {
    String name = IEngineConfig.class.getName();
    ServletContext servleContext = getServletContext();
    IEngineConfig result = ( IEngineConfig )servleContext.getAttribute( name );
    if( result == null ) {
      result = new EngineConfig( servleContext.getRealPath( "/" ) );
      servleContext.setAttribute( name, result );
    }
    return result;
  }
  
  private void createResourceManagerInstance() {
    IConfiguration configuration = ConfigurationReader.getConfiguration();
    String resources = configuration.getInitialization().getResources();
    ResourceManagerImpl.createInstance( getWebAppBase().toString(), resources );
  }

  private File getWebAppBase() {
    IEngineConfig engineConfig = ConfigurationReader.getEngineConfig();
    return engineConfig.getServerContextDir();
  }

  private void createSessionStore() {
    // Ensure that there is exactly one ISessionStore per session created
    synchronized( this ) {
      ContextProvider.getSession();
    }
  }

  static void handleInvalidRequest( final HttpServletRequest request,
                                    final HttpServletResponse response )  
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

  static String createRedirectUrl( final HttpServletRequest request ) {
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