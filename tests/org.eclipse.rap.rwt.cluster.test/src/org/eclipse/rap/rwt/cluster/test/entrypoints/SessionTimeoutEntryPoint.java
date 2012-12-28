/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.widgets.Display;


public class SessionTimeoutEntryPoint implements EntryPoint {

  public static int SESSION_SWEEP_INTERVAL = 3000;

  private static volatile boolean sessionInvalidated;

  public static boolean isSessionInvalidated() {
    return sessionInvalidated;
  }

  @SuppressWarnings("unused")
  public int createUI() {
    sessionInvalidated = false;
    new ServerPushSession().start();
    new Display();
    HttpSession httpSession = RWT.getUISession().getHttpSession();
    httpSession.setAttribute( "listener", new SessionInvalidationListener() );
    httpSession.setMaxInactiveInterval( 1 );
    return 0;
  }

  private static class SessionInvalidationListener implements HttpSessionBindingListener {

    public void valueUnbound( HttpSessionBindingEvent event ) {
      sessionInvalidated = true;
    }

    public void valueBound( HttpSessionBindingEvent event ) {
    }
  }
}
