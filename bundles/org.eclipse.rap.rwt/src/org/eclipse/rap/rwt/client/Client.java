/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.client;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientService;


/**
 * Represents a remote client. The client that is connected to the current session can be obtained
 * by calling <code>RWT.getClient()</code>.
 * <p>
 * RWT clients can provide services, e.g. to allow access to device-specific capabilities.
 * </p>
 *
 * @see WebClient
 * @see RWT#getClient()
 * @since 2.0
 */
public interface Client {

  /**
   * Returns this client's implementation of a given service, if available.
   *
   * @return the requested service if provided by this client, otherwise <code>null</code>
   */
  <T extends ClientService> T getService( Class<T> type );

}
