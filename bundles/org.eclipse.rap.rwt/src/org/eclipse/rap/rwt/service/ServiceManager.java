/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;


/**
 * A <code>ServiceManager</code> is responsible to manage existing
 * <code>ServiceHandler</code>s.
 *
 * @see ServiceHandler
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ServiceManager {

  /**
   * Registers a new service handler at the service manager. Service handlers
   * have an <code>id</code> to identify the request which are handled by the
   * registered service handler.
   *
   * @param id the identifier of this service handler used in the URL
   * @param serviceHandler the <code>ServiceHandler</code> implementation
   *
   * @see ServiceHandler
   * @see ServiceHandler#REQUEST_PARAM
   */
  void registerServiceHandler( String id, ServiceHandler serviceHandler );

  /**
   * Unregisters a service handler.
   *
   * @param id the identifier of this service handler
   *
   * @see ServiceHandler
   */
  void unregisterServiceHandler( String id );
}
