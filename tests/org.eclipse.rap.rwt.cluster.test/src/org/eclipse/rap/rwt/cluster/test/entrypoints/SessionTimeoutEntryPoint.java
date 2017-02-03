/*******************************************************************************
 * Copyright (c) 2011, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.widgets.Display;


public class SessionTimeoutEntryPoint implements EntryPoint {

  public static int SESSION_SWEEP_INTERVAL = 3000;

  private static volatile boolean sessionInvalidated;

  public static boolean isSessionInvalidated() {
    return sessionInvalidated;
  }

  @Override
  @SuppressWarnings("unused")
  public int createUI() {
    sessionInvalidated = false;
    new ServerPushSession().start();
    new Display();
    UISession uiSession = RWT.getUISession();
    uiSession.addUISessionListener( new SessionInvalidationListener() );
    uiSession.getHttpSession().setMaxInactiveInterval( 1 );
    return 0;
  }

  private static class SessionInvalidationListener implements UISessionListener {

    @Override
    public void beforeDestroy( UISessionEvent event ) {
      sessionInvalidated = true;
    }

  }

}
