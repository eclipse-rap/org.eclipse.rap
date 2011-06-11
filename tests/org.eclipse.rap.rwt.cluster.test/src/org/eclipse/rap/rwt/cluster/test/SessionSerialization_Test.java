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

import java.io.Serializable;
import java.util.*;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.ButtonEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.*;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.internal.jetty.JettyEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.service.ISessionStore;



public class SessionSerialization_Test extends TestCase {

  private IServletEngine servletEngine;
  private RWTClient client;

  public void testSessionIsSerializable() throws Exception {
    client.sendStartupRequest();
    
    client.sendInitializationRequest();
    
    HttpSession httpSession = servletEngine.getSessions()[ 0 ];
    ISessionStore sessionStore = ClusterFixture.getSessionStore( httpSession );
    assertSerializable( httpSession );
    assertSerializable( sessionStore );
  }

  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    servletEngine = new JettyEngine();
    servletEngine.start( ButtonEntryPoint.class );
    client = new RWTClient( servletEngine );
  }

  protected void tearDown() throws Exception {
    servletEngine.stop();
    ClusterFixture.tearDown();
  }

  private static void assertSerializable( HttpSession session ) {
    Enumeration names = session.getAttributeNames();
    while( names.hasMoreElements() ) {
      String name = ( String )names.nextElement();
      Object value = session.getAttribute( name );
      String msg = "Session attribute not serializable: " + name;
      assertIsSerializable( msg, value );
    }
  }

  private static void assertSerializable( ISessionStore sessionStore ) {
    Enumeration names = sessionStore.getAttributeNames();
    while( names.hasMoreElements() ) {
      String name = ( String )names.nextElement();
      Object value = sessionStore.getAttribute( name );
      String msg = "SessionStore attribute not serializable: " + name;
      assertIsSerializable( msg, value );
    }
  }

  private static void assertIsSerializable( String msg, Object value ) {
    if( value != null ) {
      assertTrue( msg, value instanceof Serializable );
    }
  }
}
