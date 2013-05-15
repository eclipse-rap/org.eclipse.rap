/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UrlParameters;
import org.eclipse.swt.internal.SerializableCompatibility;


public final class RequestCounter implements SerializableCompatibility {

  private static final String ATTR_INSTANCE = RequestCounter.class.getName() + "#instance:";

  private final AtomicInteger requestId;

  private RequestCounter() {
    requestId = new AtomicInteger();
  }

  public static RequestCounter getInstance() {
    HttpServletRequest request = ContextProvider.getRequest();
    String connectionId = request.getParameter( UrlParameters.PARAM_CONNECTION_ID );
    String attributeName = getRequestCounterAttributeName( connectionId );
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    RequestCounter result = ( RequestCounter )httpSession.getAttribute( attributeName );
    if( result == null ) {
      result = new RequestCounter();
      httpSession.setAttribute( attributeName, result );
    }
    return result;
  }

  public static void reattachToHttpSession( HttpSession httpSession, String connectionId ) {
    String attributeName = getRequestCounterAttributeName( connectionId );
    Object value = httpSession.getAttribute( attributeName );
    httpSession.setAttribute( attributeName, value );
  }

  public int nextRequestId() {
    return requestId.incrementAndGet();
  }

  public int currentRequestId() {
    return requestId.get();
  }

  private static String getRequestCounterAttributeName( String connectionId ) {
    return ATTR_INSTANCE + ( connectionId == null ? "" : connectionId );
  }

}
