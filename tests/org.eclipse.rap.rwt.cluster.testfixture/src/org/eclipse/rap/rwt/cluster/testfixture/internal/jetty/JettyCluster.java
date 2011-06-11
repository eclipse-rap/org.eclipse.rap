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
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.*;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class JettyCluster implements IServletEngineCluster {
  private final DatabaseServer databaseServer;
  private final ISessionManagerProvider sessionManagerProvider;
  private final List<IServletEngine> servletEngines;
  
  public JettyCluster() {
    databaseServer = new DatabaseServer();
    sessionManagerProvider = new ClusteredSessionManagerProvider( databaseServer );
    servletEngines = new LinkedList<IServletEngine>();
  }
  
  public IServletEngine addServletEngine() {
    JettyEngine jettyEngine = new JettyEngine( sessionManagerProvider );
    IServletEngine result = new DelegatingServletEngine( jettyEngine );
    servletEngines.add( result );
    return result; 
  }
  
  public void removeServletEngine( IServletEngine servletEngine ) {
    // not supported
  }

  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    databaseServer.start();
    for( IServletEngine servletEngine : servletEngines ) {
      servletEngine.start( entryPointClass );
    }
  }

  public void stop() throws Exception {
    for( IServletEngine servletEngine : servletEngines ) {
      servletEngine.stop();
    }
    databaseServer.stop();
  }
}
