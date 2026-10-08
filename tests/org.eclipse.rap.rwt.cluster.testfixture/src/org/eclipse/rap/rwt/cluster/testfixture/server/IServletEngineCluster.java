/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import org.eclipse.rap.rwt.application.EntryPoint;


public interface IServletEngineCluster {
  IServletEngine addServletEngine();
  IServletEngine addServletEngine( int port );
  void removeServletEngine( IServletEngine servletEngine );
  void start( Class<? extends EntryPoint> entryPointClass ) throws Exception;
  void stop() throws Exception;
}
