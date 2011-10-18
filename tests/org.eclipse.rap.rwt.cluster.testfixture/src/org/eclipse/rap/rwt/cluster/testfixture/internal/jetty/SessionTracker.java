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
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

class SessionTracker implements Filter {
  private final Map<String,HttpSession> sessions;

  SessionTracker() {
    sessions = new HashMap<String,HttpSession>();
  }

  public void init( FilterConfig filterConfig ) throws ServletException {
    sessions.clear();
  }

  public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
    throws IOException, ServletException
  {
    chain.doFilter( request, response );
    trackSession( request );
  }

  public void destroy() {
  }
  
  public HttpSession[] getSessions() {
    Collection<HttpSession> values = sessions.values();
    return values.toArray( new HttpSession[ values.size() ] );
  }

  private void trackSession( ServletRequest request ) {
    HttpSession session = getSession( request );
    if( session != null ) {
      sessions.put( session.getId(), session );
    }
  }

  private HttpSession getSession( ServletRequest request ) {
    HttpServletRequest httpRequest = ( HttpServletRequest )request;
    return httpRequest.getSession( false );
  }
}