/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;



public final class RWTRequestVersionControl {
  private static final Integer INITIAL_REQUEST_ID = new Integer( -1 );

  static final String REQUEST_COUNTER = "requestCounter";

  public static RWTRequestVersionControl getInstance() {
    Object instance = SessionSingletonBase.getInstance( RWTRequestVersionControl.class );
    return ( RWTRequestVersionControl )instance;
  }

  private Integer requestId;
  
  private RWTRequestVersionControl() {
    requestId = INITIAL_REQUEST_ID;
  }

  public boolean isValid() {
    String sentRequestId = ContextProvider.getRequest().getParameter( REQUEST_COUNTER );
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
  
  public void setCurrentRequestId( Integer version ) {
    this.requestId = version;
  }
}
