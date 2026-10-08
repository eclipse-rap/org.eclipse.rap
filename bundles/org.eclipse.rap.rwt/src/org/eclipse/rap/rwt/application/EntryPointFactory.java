/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.application;


/**
 * Implementations of this interface can be used to register entrypoints with the framework. It is
 * also possible to register the class that implements {@link EntryPoint} directly, but using a
 * factory provides more flexibility as it leaves the creation of the class to the application.
 *
 * @since 2.0
 */
public interface EntryPointFactory {

  /**
   * Creates a new entrypoint instance. This method will be called by the framework for every UI
   * session. Implementations must provide a new instance every time this method is called.
   */
  EntryPoint create();

}
