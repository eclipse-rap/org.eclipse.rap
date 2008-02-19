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

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;

import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;

/**
 * TODO [fappel]: documentation
 */
public final class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{
  private static final String MSG
    =   "Multiple browser-instances or browser-tabs per session are not\\n"
      + "supported. You may click OK for restarting the session.";
  private static final String PATTERN_RELOAD
    = "if( confirm( ''{0}'' ) ) '{ window.location.reload( false ) }'";

  public void service() throws ServletException, IOException {
    serviceInternal();
  }

  private void serviceInternal() throws ServletException, IOException {
    LifeCycleServiceHandler.initializeStateInfo();
    RWTRequestVersionControl.beforeService();
    try {
      if(    RWTRequestVersionControl.isValid()
          || LifeCycleServiceHandler.isSessionRestart()
          || ContextProvider.getRequest().getSession().isNew())
      {
        doService();
      } else {
        handleInvalidRequestCounter();
      }
    } finally {
      RWTRequestVersionControl.afterService();
    }
  }

  private void handleInvalidRequestCounter()
    throws IOException
  {
    LifeCycleServiceHandler.initializeStateInfo();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    Object[] param = new Object[] { MSG };
    out.writeText( MessageFormat.format( PATTERN_RELOAD, param ), null );
    LifeCycleServiceHandler.writeOutput();
  }
}