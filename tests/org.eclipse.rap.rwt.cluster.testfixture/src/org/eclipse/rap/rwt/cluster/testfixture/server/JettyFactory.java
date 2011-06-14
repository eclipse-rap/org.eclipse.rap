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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import org.eclipse.rap.rwt.cluster.testfixture.internal.jetty.JettyCluster;
import org.eclipse.rap.rwt.cluster.testfixture.internal.jetty.JettyEngine;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;


public class JettyFactory implements IServletEngineFactory {

  public IServletEngine createServletEngine() {
    return new DelegatingServletEngine( new JettyEngine() );
  }
  
  public IServletEngine createServletEngine( int port ) {
    return new DelegatingServletEngine( new JettyEngine( port ) );
  }

  public IServletEngineCluster createServletEngineCluster() {
    return new JettyCluster();
  }
}
