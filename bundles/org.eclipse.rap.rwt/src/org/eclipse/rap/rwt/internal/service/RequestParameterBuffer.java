/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.service.UISession;


/**
 * This class serves as a session-wide buffer to store and later on merge the
 * once stored request parameters with those of the current request.
 */
final class RequestParameterBuffer {

  private static final String BUFFER = RequestParameterBuffer.class.getName() + "#buffer:-)";

  /**
   * Buffers the given <code>parameters</code> within the session for later use
   * with <code>merge</code>. If the session has already parameters stored, the
   * method returns immediately.
   */
  static void store( Map<String, String[]> parameters ) {
    // [rst] The following if statement is a fix for bug 265008, but later lead to bug 369549,
    //       commented since according to bug 265008, the fix is not needed anymore.
    // [if] Store parameters only once.
    //      Workaround for bug 265008
    // if( getBufferedParameters() == null ) {
    HashMap<String, String[]> buffer = new HashMap<String, String[]>( parameters );
    UISession uiSession = ContextProvider.getUISession();
    uiSession.setAttribute( BUFFER, buffer );
    // }
  }

  /**
   * Merges previously <code>store</code>d request parameters with those of the
   * current request. Parameters of the current request take precedence over the
   * stored parameters.
   * <p>
   * If there are no stored parameters, this method does nothing.
   * </p>
   * <p>
   * After this method has completed, the buffered request parameters are
   * discarded.
   * </p>
   */
  static void merge() {
    Map<String, String[]> bufferedParams = getBufferedParameters();
    if( bufferedParams != null ) {
      HttpServletRequest request = ContextProvider.getRequest();
      WrappedRequest wrappedRequest = new WrappedRequest( request, bufferedParams );
      ServiceContext context = ContextProvider.getContext();
      context.setRequest( wrappedRequest );
    }
    ContextProvider.getUISession().removeAttribute( BUFFER );
  }

  @SuppressWarnings("unchecked")
  static Map<String, String[]> getBufferedParameters() {
    return ( Map<String, String[]> )ContextProvider.getUISession().getAttribute( BUFFER );
  }

  private RequestParameterBuffer() {
    // prevent instantiation
  }

}
