/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
package org.eclipse.rap.rwt.internal.client;

import jakarta.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.client.Client;


/**
 * @since 2.0
 */
public interface ClientProvider {

  boolean accept( HttpServletRequest request );

  Client getClient();

}
