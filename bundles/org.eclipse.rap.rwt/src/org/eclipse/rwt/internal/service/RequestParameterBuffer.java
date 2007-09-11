/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
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
public final class RequestParameterBuffer {

  private static final String BUFFER
    = RequestParameterBuffer.class.getName() + "#buffer:-)";

  /**
   * Buffers the given <code>parameters</code> within the session for later
   * use with <code>merge</code>.
   */
  public static void store( final Map parameters ) {
    Map buffer = new HashMap( parameters );
    HttpSession session = ContextProvider.getRequest().getSession();
    session.setAttribute( BUFFER, buffer );
  }

  /**
   * Merges perviously <code>store</code>d request parameters with those of 
   * the current request. Parameters of the current request take precedence 
   * over the stored parameters.
   * <p>If there are no stored no parameters, this method does nothing.</p>
   * <p>After this method has completed, the buffered request parameters are
   * discarded.</p>
   */
  public static void merge() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpSession session = request.getSession();
    Map bufferedParams = ( Map )session.getAttribute( BUFFER );
    if( bufferedParams != null ) {
      WrappedRequest wrappedRequest = new WrappedRequest( request, 
                                                          bufferedParams );
      ServiceContext context = ContextProvider.getContext();
      context.setRequest( wrappedRequest );
    }
    session.removeAttribute( BUFFER );
  }

  private RequestParameterBuffer() {
    // prevent instantiation
  }
}
