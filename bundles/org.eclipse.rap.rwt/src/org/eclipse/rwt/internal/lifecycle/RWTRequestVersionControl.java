/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.rwt.service.ISessionStore;


public final class RWTRequestVersionControl {
  
  private static final String VERSION
    = RWTRequestVersionControl.class + ".Version";

  public static boolean isValid() {
    Integer version = ( Integer )RWT.getServiceStore().getAttribute( VERSION );    
    HttpServletRequest request = ContextProvider.getRequest();
    String requestId = request.getParameter( RequestParams.REQUEST_COUNTER );
    boolean initialRequest = requestId == null;
    boolean inValidVersionState = version == null && requestId != null;
    return    !inValidVersionState
           && ( initialRequest || version.toString().equals( requestId ) );
  }

  public static Integer nextRequestId() {
    Integer result = ( Integer )RWT.getServiceStore().getAttribute( VERSION );
    if( result == null ) {
      result = new Integer( 0 );
    } else {
      result = new Integer( result.intValue() + 1 );
    }
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( VERSION, result );
    RWT.getServiceStore().setAttribute( VERSION, null );
    return result;
  }

  public static void beforeService() {
    ISessionStore session = ContextProvider.getSession();
    Integer version = ( Integer )session.getAttribute( VERSION );
    RWT.getServiceStore().setAttribute( VERSION, version );
  }

  public static void afterService() {
    try {
      IServiceStore serviceStore = RWT.getServiceStore();
      Integer version = ( Integer )serviceStore.getAttribute( VERSION );
      if( version != null ) {
        ISessionStore session = ContextProvider.getSession();
        session.setAttribute( VERSION, version );
      }
    } catch( final RuntimeException ignore ) {
      // TODO [fappel]: rude solution for problems with blocked threads. But
      //                as that blocking mechanism will be replaced on the road
      //                to 1.1 this hack will be also obsolete soon. 
    }
  }
  
  private RWTRequestVersionControl() {
    // prevent instantiation
  }
}
