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
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.*;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestEntryPoint;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;


@SuppressWarnings("restriction")
public abstract class ServletEngineCluster_Test extends TestCase {

  private IServletEngineCluster cluster;

  protected abstract IServletEngineFactory getServletEngineFactory(); 

  public void testAddServletEngineWithPort() throws Exception {
    int freePort = SocketUtil.getFreePort();
    
    IServletEngine servletEngine = cluster.addServletEngine( freePort );
    cluster.start( TestEntryPoint.class );
    
    assertEquals( freePort, servletEngine.getPort() );
  }
  
  public void testRemoveServletEngineWithUnknownServletEngine() {
    IServletEngine unknownServletEngine = getServletEngineFactory().createServletEngine();

    try {
      cluster.removeServletEngine( unknownServletEngine );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRemoveServletEngineTwice() {
    IServletEngine servletEngine = cluster.addServletEngine();
    cluster.removeServletEngine( servletEngine );
    
    try {
      cluster.removeServletEngine( servletEngine );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    System.setProperty( "lifecycle", SimpleLifeCycle.class.getName() );
    cluster = getServletEngineFactory().createServletEngineCluster();
  }

  protected void tearDown() throws Exception {
    cluster.stop();
    System.getProperties().remove( "lifecycle" );
  }
}
