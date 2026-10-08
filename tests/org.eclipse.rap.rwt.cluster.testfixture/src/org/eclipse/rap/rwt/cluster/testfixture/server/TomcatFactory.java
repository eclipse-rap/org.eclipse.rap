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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat.TomcatCluster;
import org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat.TomcatEngine;


public class TomcatFactory implements IServletEngineFactory {

  public IServletEngine createServletEngine() {
    return new DelegatingServletEngine( new TomcatEngine() );
  }
  
  public IServletEngine createServletEngine( int port ) {
    return new DelegatingServletEngine( new TomcatEngine( port ) );
  }

  public IServletEngineCluster createServletEngineCluster() {
    return new TomcatCluster();
  }
}
