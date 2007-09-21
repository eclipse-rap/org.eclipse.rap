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

import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.service.ISessionStore;


public class RWTRequestVersionControl {
  
  static final String VERSION = RWTRequestVersionControl.class + ".Version";
  private static final String CHANGED 
    = RWTRequestVersionControl.class + ".Changed";

  private RWTRequestVersionControl() {
    // prevent instance creation
  }

  public static void determine() {
    ISessionStore session = ContextProvider.getSession();
    Integer version = ( Integer )session.getAttribute( VERSION );
    if( version == null ) {
      version = new Integer( 0 );
      session.setAttribute( VERSION, version );
    }
    LifeCycleServiceHandler.initializeStateInfo();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( VERSION, version );
  }

  public static void increase() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Integer version = ( Integer )stateInfo.getAttribute( VERSION );
    Integer newVersion = new Integer( version.intValue() + 1 );
    stateInfo.setAttribute( VERSION, newVersion );
    ContextProvider.getSession().setAttribute( VERSION, newVersion );
    stateInfo.setAttribute( CHANGED, Boolean.TRUE );
  }

  public static boolean hasChanged() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Boolean result = ( Boolean )stateInfo.getAttribute( CHANGED );
    return result != null && result.booleanValue();
  }

  public static boolean check() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Integer version = ( Integer )stateInfo.getAttribute( VERSION );
    HttpServletRequest request = ContextProvider.getRequest();
    String versionParam = request.getParameter( RequestParams.REQUEST_COUNTER );
    return    versionParam == null 
           || version.equals( Integer.valueOf( versionParam ) );
  }

  public static void store() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Integer version = ( Integer )stateInfo.getAttribute( VERSION );
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( VERSION, version );
  }

  public static Integer getVersion() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Integer )stateInfo.getAttribute( VERSION );
  }

}
