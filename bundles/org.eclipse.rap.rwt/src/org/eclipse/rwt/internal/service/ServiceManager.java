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

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.service.IServiceHandler;


/** <p>provides the appropriate HttpServlet request service handler for the
 *  given runtime mode.</p> 
 */
public final class ServiceManager {

  public static void registerServiceHandler( final String id, 
                                             final IServiceHandler handler ) 
  {
    getInstance().registerServiceHandler( id, handler );
  }

  public static void unregisterServiceHandler( final String id ) {
    getInstance().unregisterServiceHandler( id );
  }
  
  
  public static void setHandler( final IServiceHandler serviceHandler ) {
    getInstance().setHandler( serviceHandler );
  }
  
  /** <p>returns the appropriate service handler.</p> */
  public static IServiceHandler getHandler() {
    return getInstance().getHandler();
  }
  
  public static boolean isCustomHandler() {
    return getInstance().isCustomHandler();
  }
  
  public static IServiceHandler getCustomHandler() {
    return getInstance().getCustomHandler();
  }
  
  public static IServiceHandler getCustomHandler( final String id ) {
    return getInstance().getCustomHandler( id );
  }
  
  private static ServiceManagerInstance getInstance() {
    Class singletonType = ServiceManagerInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( ServiceManagerInstance )singleton;
  }
  
  private ServiceManager() {
    // prevent instance creation
  }
}