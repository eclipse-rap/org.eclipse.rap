/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;


/**
 * An <code>IServiceManager</code> is responsible to manager existing
 * <code>IServiceHandler</code>s.
 * 
 * @see IServiceHandler
 * 
 * @since 1.0
 */
public interface IServiceManager {
  
  /**
   * Registers a new service handler at the service manager. Service handlers
   * have an <code>id</code> to identify the request which are handled by the
   * registered service handler.
   * 
   * @param id the identifier of this service handler used in the URL
   * @param serviceHandler the <code>IServiceHandler</code> implementation
   * 
   * @see IServiceHandler
   * @see IServiceHandler#REQUEST_PARAM
   */
  void registerServiceHandler( String id, IServiceHandler serviceHandler );
  
  /**
   * Unregisters a service handler.
   * 
   * @param id the identifier of this service handler
   * 
   * @see IServiceHandler
   */
  void unregisterServiceHandler( String id );
}
