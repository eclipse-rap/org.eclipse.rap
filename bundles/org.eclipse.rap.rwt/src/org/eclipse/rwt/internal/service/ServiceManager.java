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

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.service.IServiceHandler;


/** <p>provides the appropriate HttpServlet request service handler for the
 *  given runtime mode.</p>
 */
public final class ServiceManager {

  public static void registerServiceHandler( String id, IServiceHandler handler ) {
    getInstance().registerServiceHandler( id, handler );
  }

  public static void unregisterServiceHandler( String id ) {
    getInstance().unregisterServiceHandler( id );
  }

  /** <p>returns the appropriate service handler.</p> */
  public static IServiceHandler getHandler() {
    return getInstance().getHandler();
  }

  private static ServiceManagerInstance getInstance() {
    return ( ServiceManagerInstance )ApplicationContext.getSingleton( ServiceManagerInstance.class );
  }

  private ServiceManager() {
    // prevent instance creation
  }
}