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

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.swt.internal.SerializableCompatibility;

// TODO [rh] find a handier name
public final class RequestId implements SerializableCompatibility {
  private static final String ATTR_INSTANCE = RequestId.class.getName() + "#instance";
  private static final Integer INITIAL_REQUEST_ID = new Integer( -1 );

  static final String REQUEST_COUNTER = "requestCounter";

  public static RequestId getInstance() {
    HttpSession session = ContextProvider.getUISession().getHttpSession();
    RequestId result = ( RequestId )session.getAttribute( ATTR_INSTANCE );
    if( result == null ) {
      result = new RequestId();
      session.setAttribute( ATTR_INSTANCE, result );
    }
    return result;
  }

  private Integer requestId;

  private RequestId() {
    requestId = INITIAL_REQUEST_ID;
  }

  public boolean isValid() {
    String sentRequestId = ProtocolUtil.readHeadPropertyValue( REQUEST_COUNTER );
    boolean initialRequest = sentRequestId == null;
    boolean invalidVersionState = INITIAL_REQUEST_ID.equals( requestId ) && sentRequestId != null;
    boolean requestIdEquals = requestId.toString().equals( sentRequestId );
    return !invalidVersionState && ( initialRequest || requestIdEquals );
  }

  public Integer nextRequestId() {
    requestId = new Integer( requestId.intValue() + 1 );
    return requestId;
  }

  public Integer getCurrentRequestId() {
    return requestId;
  }

}
