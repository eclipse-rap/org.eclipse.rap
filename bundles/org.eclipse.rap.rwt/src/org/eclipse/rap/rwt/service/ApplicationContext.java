/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.ApplicationRunner;


/**
 * An application context represents an instance of a RAP application. This context is shared by all
 * users who access this application. The current application context can be acquired by
 * {@link RWT#getApplicationContext()}. It can be used to store data that are shared between all UI
 * sessions of an application.
 * <p>
 * The application context is bound to the servlet context of the hosting web application. It is
 * destroyed when either the web application ends (i.e. the servlet context is destroyed) or when
 * {@link ApplicationRunner#stop()} is called.
 * </p>
 * <p>
 * The application context is <em>thread safe</em>, it can safely be accessed concurrently from
 * different threads.
 * </p>
 *
 * @see org.eclipse.rap.rwt.RWT
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings( "deprecation" )
public interface ApplicationContext extends IApplicationStore {

  /**
   * Stores the given value in this application context, associated with the given name. If another
   * value has already been stored with the given name, the old value is overwritten.
   *
   * @param name the name to associate the value with
   * @param value the object to be stored
   */
  void setAttribute( String name, Object value );

  /**
   * Returns the value which is stored under the given name in this application context.
   *
   * @param name the name whose associated value is requested
   * @return the object that is stored, or <code>null</code> if no object has been stored by that
   *         name
   */
  Object getAttribute( String name );

  /**
   * Removes the object which is stored under the given name in this application context. If no
   * value object was stored under the given name, this method does nothing.
   */
  void removeAttribute( String name );

}
