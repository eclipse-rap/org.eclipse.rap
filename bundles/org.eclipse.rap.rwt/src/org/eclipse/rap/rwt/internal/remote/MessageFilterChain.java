/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;


/**
 * The filter chain that must process incoming messages.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface MessageFilterChain {

  /**
   * Handles an incoming message and returns the response.
   *
   * @param request the incoming message to process
   * @return the message to be sent to the client in response
   */
  ResponseMessage handleMessage( RequestMessage request );

}
