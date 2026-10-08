/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
package org.eclipse.rap.rwt.cluster.testfixture.test;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;

public class TestServletEngine implements IServletEngine {
  private final int port;

  public TestServletEngine() {
    this( -1 );
  }
  
  public TestServletEngine( int port ) {
    this.port = port;
  }

  public void start( Class entryPointClass ) throws Exception {
  }

  public void stop() throws Exception {
  }

  public void stop( int timeout ) throws Exception {
  }
  
  public int getPort() {
    return port;
  }
  
  public HttpSession[] getSessions() {
    return null;
  }
}