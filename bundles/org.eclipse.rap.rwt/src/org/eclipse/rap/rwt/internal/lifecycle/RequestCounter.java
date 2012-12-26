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
package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.swt.internal.SerializableCompatibility;


public final class RequestCounter implements SerializableCompatibility {

  private static final String ATTR_INSTANCE = RequestCounter.class.getName() + "#instance";
  private static final String PROP_REQUEST_COUNTER = "requestCounter";

  private final AtomicInteger requestId;

  private RequestCounter() {
    requestId = new AtomicInteger();
  }

  public static RequestCounter getInstance() {
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    RequestCounter result = ( RequestCounter )httpSession.getAttribute( ATTR_INSTANCE );
    if( result == null ) {
      result = new RequestCounter();
      httpSession.setAttribute( ATTR_INSTANCE, result );
    }
    return result;
  }

  public static void reattachToHttpSession( HttpSession httpSession ) {
    Object value = httpSession.getAttribute( ATTR_INSTANCE );
    httpSession.setAttribute( ATTR_INSTANCE, value );
  }

  public boolean isValid() {
    String sentRequestId = ProtocolUtil.readHeadPropertyValue( PROP_REQUEST_COUNTER );
    if( sentRequestId == null ) {
      return requestId.get() == 0;
    }
    return requestId.toString().equals( sentRequestId );
  }

  public int nextRequestId() {
    return requestId.incrementAndGet();
  }

  public int currentRequestId() {
    return requestId.get();
  }

}
