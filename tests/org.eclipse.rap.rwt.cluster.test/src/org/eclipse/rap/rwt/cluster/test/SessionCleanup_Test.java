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
package org.eclipse.rap.rwt.cluster.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.cluster.test.entrypoints.SessionCleanupEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterTestHelper;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.cluster.testfixture.server.JettyFactory;
import org.eclipse.rap.rwt.cluster.testfixture.server.TomcatFactory;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith( Parameterized.class )
public class SessionCleanup_Test {

  private final IServletEngineFactory servletEngineFactory;
  private IServletEngine servletEngine;
  private RWTClient client;

  @Parameters
  public static Collection<Object[]> getParameters() {
    return Arrays.asList( new Object[][] { { new JettyFactory() }, { new TomcatFactory() } } );
  }

  public SessionCleanup_Test( IServletEngineFactory servletEngineFactory ) {
    this.servletEngineFactory = servletEngineFactory;
  }

  @Before
  public void setUp() throws Exception {
    servletEngine = servletEngineFactory.createServletEngine();
    client = new RWTClient( servletEngine );
  }

  @After
  public void tearDown() throws Exception {
    servletEngine.stop();
  }

  @Test
  public void testInvalidateSession() throws Exception {
    servletEngine.start( SessionCleanupEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    client.sendDisplayResizeRequest( 400, 300 );

    HttpSession httpSession = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( httpSession );
    httpSession.invalidate();

    assertTrue( display.isDisposed() );
  }

}
