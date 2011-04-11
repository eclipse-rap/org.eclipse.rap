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
package org.eclipse.rwt.service;

/**
 * The <code>IApplicationStore</code> can be used as store for data that spans
 * the lifecycle of a web application instance.
 * 
 * 
 * <p><strong>Note:</strong> the <code>IApplicationStore</code> implementation is used
 * in the so called application scope. That means that all information stored here will be 
 * lost once the application web context is destroyed. Application scope also implies
 * concurrent access. Therefore the implementation of <code>IApplicationStore</code>
 * has to provide a proper synchronization of its storage datastructure.</p>
 * 
 * @see org.eclipse.rwt.RWT
 * @since 1.4
 */
public interface IApplicationStore {

  /**
   * Stores the given value object with the given name as key in this
   * <code>IApplicationStore</code> instance.
   */
  void setAttribute( String name, Object value );

  /**
   * Returns the value object which is stored under the given name in this
   * <code>IApplicationStore</code> instance or null if no value object has been stored.
   */
  Object getAttribute( String name );

  /**
   * Removes the value object which is stored under the given name in this
   * <code>IApplicationStore</code> instance. Does nothing if no value object was stored
   * under the given name.
   */
  void removeAttribute( String name );
}
