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

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.application.EntryPoint;


public interface IServletEngine {
  String SERVLET_NAME= "rwt";
  String SERVLET_PATH = "/" + SERVLET_NAME;

  void start( Class<? extends EntryPoint> entryPointClass ) throws Exception;
  void stop( int timeout ) throws Exception;
  void stop() throws Exception;
  int getPort();

  HttpSession[] getSessions();
}
