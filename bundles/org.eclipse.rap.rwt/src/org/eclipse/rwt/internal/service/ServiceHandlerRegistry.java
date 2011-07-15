/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.service.IServiceHandler;

class ServiceHandlerRegistry {
  private final Map<String, IServiceHandler> handlers;
  
  ServiceHandlerRegistry() {
    handlers = new HashMap<String, IServiceHandler>();
  }

  boolean isCustomHandler( String serviceHandlerId ) {
    synchronized( handlers ) {
      return handlers.containsKey( serviceHandlerId );
    }
  }
  
  void put( String serviceHandlerId, IServiceHandler serviceHandler ) {
    synchronized( handlers ) {
      handlers.put( serviceHandlerId, serviceHandler );
    }
  }

  void remove( String id ) {
    synchronized( handlers ) {
      handlers.remove( id );
    }
  }

  IServiceHandler get( String serviceHandlerId ) {
    synchronized( handlers ) {
      return handlers.get( serviceHandlerId );
    }
  }

  void clear() {
    synchronized( handlers ) {
      handlers.clear();
    }
  }
}