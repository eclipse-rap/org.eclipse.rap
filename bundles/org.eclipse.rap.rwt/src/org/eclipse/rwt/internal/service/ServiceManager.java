/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;


public class ServiceManager implements IServiceManager {
  private final ServiceHandlerRegistry customHandlers;
  private final IServiceHandler handlerDispatcher;
  private IServiceHandler lifeCycleRequestHandler;
  
  private final class HandlerDispatcher implements IServiceHandler {
    public void service() throws ServletException, IOException {
      if( isCustomHandler() ) {
        IServiceHandler customHandler = getCustomHandler();
        customHandler.service();
      } else {
        getLifeCycleRequestHandler().service();
      }
    }
  }
  
  public ServiceManager() {
    handlerDispatcher = new HandlerDispatcher();
    customHandlers = new ServiceHandlerRegistry();
  }
  
  public void registerServiceHandler( String id, IServiceHandler handler ) {
    customHandlers.put( id, handler );
  }

  public void unregisterServiceHandler( String id ) {
    customHandlers.remove( id );
  }
  
  public IServiceHandler getHandler() {
    return handlerDispatcher;
  }

  public void activate() {
    customHandlers.activate();
  }

  public void deactivate() {
    customHandlers.deactivate();
  }
  
  //////////////////
  // helping methods
  
  private static String getCustomHandlerId() {
    HttpServletRequest request = ContextProvider.getRequest();
    return request.getParameter( IServiceHandler.REQUEST_PARAM );
  }
  
  private IServiceHandler getLifeCycleRequestHandler() {
    if( lifeCycleRequestHandler == null ) {
      lifeCycleRequestHandler = new LifeCycleServiceHandler();
    }
    return lifeCycleRequestHandler;
  }

  private boolean isCustomHandler() {
    return customHandlers.isCustomHandler( getCustomHandlerId() );
  }
  
  private IServiceHandler getCustomHandler() {
    return customHandlers.get( getCustomHandlerId() );
  }
}