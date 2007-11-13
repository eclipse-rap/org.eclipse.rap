/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.service.ISessionStore;


public class RWTRequestVersionControl {
  private static final String VERSION
    = RWTRequestVersionControl.class + ".Version";

  public static boolean isValid() {
    ISessionStore session = ContextProvider.getSession();
    Integer version = ( Integer )session.getAttribute( VERSION );
    HttpServletRequest request = ContextProvider.getRequest();
    String requestId = request.getParameter( RequestParams.REQUEST_COUNTER );
    boolean initialRequest = version == null && requestId == null;
    boolean inValidVersionState =    version == null && requestId != null
                                  || version != null && requestId == null;
    return    !inValidVersionState
           && ( initialRequest || version.toString().equals( requestId ) );
  }

  public static Integer nextRequestId() {
    ISessionStore session = ContextProvider.getSession();
    Integer result = new Integer( new Object().hashCode() );
    session.setAttribute( VERSION, result );
    return result;
  }
}
