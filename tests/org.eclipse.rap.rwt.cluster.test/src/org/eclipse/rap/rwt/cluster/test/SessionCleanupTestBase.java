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
package org.eclipse.rap.rwt.cluster.test;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.SessionCleanupEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.swt.widgets.Display;


public abstract class SessionCleanupTestBase extends TestCase {
  
  private IServletEngine servletEngine;
  private RWTClient client;

  abstract IServletEngineFactory getServletEngineFactory();

  public void testInvalidateSession() throws Exception {
    servletEngine.start( SessionCleanupEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    client.sendDisplayResizeRequest( 400, 300 );
    
    HttpSession httpSession = ClusterFixture.getFirstSession( servletEngine );
    Display display = ClusterFixture.getSessionDisplay( httpSession );
    httpSession.invalidate();
    
    assertTrue( display.isDisposed() );
  }
  
  @Override
  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    servletEngine = getServletEngineFactory().createServletEngine();
    client = new RWTClient( servletEngine );
  }
  
  @Override
  protected void tearDown() throws Exception {
    servletEngine.stop();
    ClusterFixture.tearDown();
  }
}
