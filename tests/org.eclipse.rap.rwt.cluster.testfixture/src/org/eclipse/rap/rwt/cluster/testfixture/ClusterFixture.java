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
package org.eclipse.rap.rwt.cluster.testfixture;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;



public class ClusterFixture {
  private static final String ATTR_SESSION_DISPLAY 
    = LifeCycleUtil.class.getName() + "#sessionDisplay";

  public static void setUp() {
    System.setProperty( "lifecycle", SimpleLifeCycle.class.getName() );
  }

  public static void tearDown() {
    System.getProperties().remove( "lifecycle" );
  }
  
  public static HttpSession getFirstSession( IServletEngine servletEngine ) {
    Map sessions = servletEngine.getSessions();
    return ( HttpSession )sessions.values().iterator().next();
  }

  public static ISessionStore getSessionStore( HttpSession httpSession ) {
    return ( ISessionStore )httpSession.getAttribute( SessionStoreImpl.ATTR_SESSION_STORE );
  }

  public static Display getSessionDisplay( HttpSession httpSession ) {
    ISessionStore sessionStore = getSessionStore( httpSession );
    return ( Display )sessionStore.getAttribute( ATTR_SESSION_DISPLAY );
  }

  private ClusterFixture() {
    // prevent instantiation
  }
}
