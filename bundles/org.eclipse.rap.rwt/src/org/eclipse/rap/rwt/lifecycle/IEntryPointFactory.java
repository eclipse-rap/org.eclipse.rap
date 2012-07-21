/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;


/**
 * Implementations of this interface can be used to register entrypoints with
 * the framework. It is also possible to register the class that implements
 * {@link IEntryPoint} directly, but using a factory provides greater
 * flexibility as it leaves the creation of the class to the application.
 *
 * @since 2.0
 */
public interface IEntryPointFactory {

  /**
   * Creates a new entrypoint instance.
   */
  IEntryPoint create();
}
