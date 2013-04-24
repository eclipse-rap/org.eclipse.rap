/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.engine;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.service.UISessionBuilder;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.internal.service.UrlParameters;
import org.eclipse.rap.rwt.service.ServiceHandler;


/**
 * The RWT servlet. This servlet receives all requests to a RAP application.
 * <p>
 * Usually, you only have to register this servlet manually in a traditional web
 * application, i.e. without OSGi.
 * </p>
 * <p>
 * In a traditional web application (without OSGi), this servlet must be
 * registered in the application's deployment descriptor like shown below. Note
 * that the RWT servlet has to be registered for every entrypoint of the
 * application.
 * </p>
 *
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;org.eclipse.rap.applicationConfiguration&lt;/param-name&gt;
 *   &lt;param-value&gt;com.example.HelloWorldConfiguration&lt;/param-value&gt;
 * &lt;/context-param&gt;
 *
 * &lt;listener&gt;
 *   &lt;listener-class&gt;org.eclipse.rap.rwt.engine.RWTServletContextListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 *
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;rwtServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;org.eclipse.rap.rwt.engine.RWTServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;rwtServlet&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/example&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 *
 * <pre>
 *
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class RWTServlet extends HttpServlet {

  private ApplicationContextImpl applicationContext;

  @Override
  public String getServletInfo() {
    return "RWT Servlet";
  }

  @Override
  public void init() throws ServletException {
    ServletContext servletContext = getServletContext();
    applicationContext = ApplicationContextImpl.getFrom( servletContext );
  }

  @Override
  public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    handleRequest( request, response );
  }

  @Override
  public void doPost( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    handleRequest( request, response );
  }

  private void handleRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException
  {
    if( request.getPathInfo() == null ) {
      handleValidRequest( request, response );
    } else {
      handleInvalidRequest( request, response );
    }
  }

  private void handleValidRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException
  {
    ServiceContext serviceContext = createServiceContext( request, response );
    ContextProvider.setContext( serviceContext );
    try {
      ensureUISession( serviceContext );
      getServiceHandler().service( request, response );
    } finally {
      ContextProvider.disposeContext();
    }
  }

  private ServiceContext createServiceContext( HttpServletRequest request,
                                               HttpServletResponse response )
  {
    ServiceContext context = new ServiceContext( request, response, applicationContext );
    context.setServiceStore( new ServiceStore() );
    return context;
  }

  static void ensureUISession( ServiceContext serviceContext ) {
    // Ensure that there is exactly one UISession per connection created
    synchronized( RWTServlet.class ) {
      HttpServletRequest request = serviceContext.getRequest();
      HttpSession httpSession = request.getSession( true );
      String connectionId = request.getParameter( UrlParameters.PARAM_CONNECTION_ID );
      UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( httpSession, connectionId );
      if( uiSession == null ) {
        uiSession = new UISessionBuilder( serviceContext ).buildUISession();
      }
      serviceContext.setUISession( uiSession );
    }
  }

  private ServiceHandler getServiceHandler() {
    return applicationContext.getServiceManager().getHandler();
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
