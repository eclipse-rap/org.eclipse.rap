/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineCluster;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.cluster.testfixture.server.JettyFactory;
import org.eclipse.rap.rwt.cluster.testfixture.server.TomcatFactory;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestEntryPoint;
import org.eclipse.rap.rwt.internal.lifecycle.SimpleLifeCycle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@SuppressWarnings("restriction")
@RunWith( Parameterized.class )
public class ServletEngineCluster_Test {

  private final IServletEngineFactory servletEngineFactory;
  private IServletEngineCluster cluster;

  @Parameters
  public static Collection<Object[]> getParameters() {
    return Arrays.asList( new Object[][] { { new JettyFactory() }, { new TomcatFactory() } } );
  }

  public ServletEngineCluster_Test( IServletEngineFactory servletEngineFactory ) {
    this.servletEngineFactory = servletEngineFactory;
  }

  @Before
  public void setUp() throws Exception {
    System.setProperty( "lifecycle", SimpleLifeCycle.class.getName() );
    cluster = servletEngineFactory.createServletEngineCluster();
  }

  @After
  public void tearDown() throws Exception {
    cluster.stop();
    System.getProperties().remove( "lifecycle" );
  }

  @Test
  public void testAddServletEngineWithPort() throws Exception {
    int freePort = SocketUtil.getFreePort();

    IServletEngine servletEngine = cluster.addServletEngine( freePort );
    cluster.start( TestEntryPoint.class );

    assertEquals( freePort, servletEngine.getPort() );
  }

  @Test
  public void testRemoveServletEngineWithUnknownServletEngine() {
    IServletEngine unknownServletEngine = servletEngineFactory.createServletEngine();

    try {
      cluster.removeServletEngine( unknownServletEngine );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveServletEngineTwice() {
    IServletEngine servletEngine = cluster.addServletEngine();
    cluster.removeServletEngine( servletEngine );

    try {
      cluster.removeServletEngine( servletEngine );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
