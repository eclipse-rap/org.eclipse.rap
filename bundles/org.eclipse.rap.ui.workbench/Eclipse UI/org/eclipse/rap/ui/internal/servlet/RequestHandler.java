/*******************************************************************************
 * Copyright (c) 2006, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.IEngineConfig;
import org.eclipse.rwt.internal.engine.RWTDelegate;


public class RequestHandler extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  private static RWTDelegate servlet;
  private static EngineConfigWrapper engineConfigWrapper;

  public RequestHandler() {
    if( servlet == null ) {
      servlet = new RWTDelegate();
    }
  }
  
  public void init( final ServletConfig config ) throws ServletException {
    super.init( config );
    // TODO [bm] this is not thread-safe in case of two concurrent init calls
    if( engineConfigWrapper == null ) {
      engineConfigWrapper = new EngineConfigWrapper();
      prepareServletContext( config );
      servlet.init( config );
    } else {
      prepareServletContext( config );
    }
  }

  public void service( final HttpServletRequest request,
                       final HttpServletResponse response )
    throws ServletException, IOException
  {
    servlet.doPost( request, response );
  }
  
  public void destroy() {
	  // TODO [bm] save a list of RequestHandlers in HttpServer
	  // and destroy them on shutdown
	  // BUT: do we need to destroy?
	  // distroy() is not overridden and empty in GenericServlet
//    servlet.destroy();
  }

  private static void prepareServletContext( final ServletConfig config ) {
    ServletContext servletContext = config.getServletContext();
    servletContext.setAttribute( IEngineConfig.class.getName(),
                                 engineConfigWrapper );
  }
}
