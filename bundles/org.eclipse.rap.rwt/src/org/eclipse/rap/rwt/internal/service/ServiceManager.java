/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.IServiceManager;


public class ServiceManager implements IServiceManager {
  private final ServiceHandler lifeCycleRequestHandler;
  private final ServiceHandlerRegistry customHandlers;

  public ServiceManager( ServiceHandler lifeCycleRequestHandler ) {
    this.lifeCycleRequestHandler = lifeCycleRequestHandler;
    customHandlers = new ServiceHandlerRegistry();
  }

  public ServiceHandler getServiceHandler( String customId ) {
    return customHandlers.get( customId );
  }

  public void registerServiceHandler( String id, ServiceHandler handler ) {
    customHandlers.put( id, handler );
  }

  public void unregisterServiceHandler( String id ) {
    customHandlers.remove( id );
  }

  public void clear() {
    customHandlers.clear();
  }

  public ServiceHandler getHandler() {
    ServiceHandler result;
    String customId = getCustomHandlerId();
    if( customId != null && customId.length() > 0 ) {
      result = getCustomHandlerChecked( customId );
    } else {
      result = lifeCycleRequestHandler;
    }
    return result;
  }

  private ServiceHandler getCustomHandlerChecked( String customId ) {
    ServiceHandler customHandler = customHandlers.get( customId );
    if( customHandler == null ) {
      throw new IllegalArgumentException( "No service handler registered with id " + customId );
    }
    return customHandler;
  }

  private static String getCustomHandlerId() {
    return ContextProvider.getRequest().getParameter( ServiceHandler.REQUEST_PARAM );
  }

}
