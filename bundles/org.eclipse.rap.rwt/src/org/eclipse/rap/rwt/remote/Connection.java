/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.remote;

import org.eclipse.rap.rwt.service.UISession;


/**
 * An instance of this interface represents a connection used to communicate with the client. Every
 * UI session has exactly one connection which can be obtained by calling
 * {@link UISession#getConnection()}.
 * <p>
 * <strong>Note:</strong> The classes and interfaces in the package
 * <em>org.eclipse.rap.rwt.remote</em> are still considered <strong>provisional</strong>. They are
 * expected to evolve over the next releases, which may lead to slight changes. We make the package
 * available to enable the development of custom components with the new API.
 * </p>
 *
 * @see UISession#getConnection()
 * @since 2.0
 */
public interface Connection {

  /**
   * Creates a new remote object on the client with the given remote type. The type must be known by
   * the client, and the client must be able to create an object of this type. The returned
   * <code>RemoteObject</code> can be used to communicate with the remote object.
   *
   * @param remoteType the type of the remote object to be created, must not be <code>null</code>
   * @return a representation of the remote object that has been created
   */
  RemoteObject createRemoteObject( String remoteType );

}
