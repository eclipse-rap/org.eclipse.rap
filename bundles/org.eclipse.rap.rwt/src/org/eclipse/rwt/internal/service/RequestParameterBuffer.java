/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class serves as a session-wide buffer to store an later on merge the 
 * once stored request parameters with those of the current request. 
 */
final class RequestParameterBuffer {

  private static final String BUFFER
    = RequestParameterBuffer.class.getName() + "#buffer:-)";

  /**
   * Buffers the given <code>parameters</code> within the session for later
   * use with <code>merge</code>. If the session has already parameters stored,
   * the method returns immediately.
   */
  static void store( final Map parameters ) {
    // [if] Store parameters only once.
    // Workaround for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=265008
    if( getBufferedParameters() == null ) {
      Map buffer = new HashMap( parameters );
      HttpSession session = ContextProvider.getRequest().getSession();
      session.setAttribute( BUFFER, buffer );
    }      
  }

  /**
   * Merges previously <code>store</code>d request parameters with those of 
   * the current request. Parameters of the current request take precedence 
   * over the stored parameters.
   * <p>If there are no stored parameters, this method does nothing.</p>
   * <p>After this method has completed, the buffered request parameters are
   * discarded.</p>
   */
  static void merge() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpSession session = request.getSession();
    Map bufferedParams = getBufferedParameters();
    if( bufferedParams != null ) {
      WrappedRequest wrappedRequest = new WrappedRequest( request, 
                                                          bufferedParams );
      ServiceContext context = ContextProvider.getContext();
      context.setRequest( wrappedRequest );
    }
    session.removeAttribute( BUFFER );
  }
  
  static Map getBufferedParameters() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpSession session = request.getSession();
    return ( Map )session.getAttribute( BUFFER );
  }

  private RequestParameterBuffer() {
    // prevent instantiation
  }
}
