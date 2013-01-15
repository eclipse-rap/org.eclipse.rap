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

import java.util.Map;


/**
 * Classes that implement this interface are used to handle operations from a remote object.
 * Implementations must apply the operations to the corresponding target object.
 * <p>
 * It is recommended to extend {@link AbstractOperationHandler} rather than to implement this
 * interface.
 * </p>
 * <p>
 * <strong>Note:</strong> The classes and interfaces in the package
 * <em>org.eclipse.rap.rwt.remote</em> are still considered <strong>provisional</strong>. They are
 * expected to evolve over the next releases, which may lead to slight changes. We make the package
 * available to enable the development of custom components with the new API.
 * </p>
 *
 * @see AbstractOperationHandler
 * @see RemoteObject
 * @since 2.0
 */
public interface OperationHandler {

  /**
   * Handles a <em>set</em> operation from the remote object. With a set operation, the remote
   * object informs the receiver that one or more properties have changed their values. The
   * implementation of this method must apply the new property values to the target object in a
   * suitable order.
   *
   * @param properties a map with the properties
   */
  public abstract void handleSet( Map<String, Object> properties );

  /**
   * Handles a <em>call</em> operation from the remote object. With a call operation, the remote
   * object instructs the receiver to call a method on the target object. The method call may be
   * parameterized with the given properties.
   *
   * @param method the name of the method to call
   * @param parameters the parameters for the method call, may be empty, but never <code>null</code>
   */
  public abstract void handleCall( String method, Map<String, Object> parameters );

  /**
   * Handles a <em>notify</em> operation from the remote object. With a notify operation, the remote
   * object notifies the receiver that an event has occurred. An implementation of this method must
   * notify the corresponding listeners attached to the target object.
   *
   * @param event the name of the event that occurred
   * @param properties the event properties, maybe empty but never <code>null</code>
   */
  public abstract void handleNotify( String event, Map<String, Object> properties );

}
