/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This servlet is registered for alias "/" and redirects all requests to the
 * <code>examples</code> servlet.
 */
public class RedirectServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final String REDIRECT_URL = "examples";

  @Override
  protected void doGet( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException
  {
    redirect( req, resp );
  }

  @Override
  protected void doPost( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException
  {
    redirect( req, resp );
  }

  private static void redirect( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( request.getPathInfo().equals( "/" ) ) {
      response.sendRedirect( response.encodeRedirectURL( REDIRECT_URL ) );
    } else {
      response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }
  }
}
