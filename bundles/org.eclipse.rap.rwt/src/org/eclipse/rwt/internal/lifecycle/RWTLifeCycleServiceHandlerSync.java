/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;

/**
 * TODO [fappel]: documentation
 */
public class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{
  private static final String PATTERN_RELOAD
    = "if( confirm( ''{0}'' ) ) '{ window.location.reload( false ) }'";

  public void service() throws ServletException, IOException {
    synchronized( ContextProvider.getSession() ) {
      serviceInternal();
    }
  }

  void serviceInternal() throws ServletException, IOException {
    LifeCycleServiceHandler.initializeStateInfo();
    RWTRequestVersionControl.beforeService();
    try {
      if(    RWTRequestVersionControl.isValid()
          || LifeCycleServiceHandler.isSessionRestart()
          || ContextProvider.getRequest().getSession().isNew() )
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
    String message = RWTMessages.getMessage( "RWT_MultipleInstancesError" );
    Object[] param = new Object[] { message };
    // Note: [rst] Do not use writeText as umlauts must not be encoded here
    out.write( MessageFormat.format( PATTERN_RELOAD, param ) );
    LifeCycleServiceHandler.writeOutput();
  }
}