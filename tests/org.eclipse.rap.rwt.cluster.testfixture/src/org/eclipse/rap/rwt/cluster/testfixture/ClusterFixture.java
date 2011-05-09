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

import javax.servlet.http.HttpSession;

import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;



public class ClusterFixture {
  private static final String ATTR_SESSION_STORE
    = SessionStoreImpl.class.getName();
  private static final String ATTR_SESSION_DISPLAY 
    = LifeCycleUtil.class.getName() + "#sessionDisplay";

  public static void setUp() {
    System.setProperty( "lifecycle", "org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle" );
  }

  public static void tearDown() {
    System.getProperties().remove( "lifecycle" );
  }
  
  private ClusterFixture() {
    // prevent instantiation
  }

  public static ISessionStore getSessionStore( HttpSession httpSession ) {
    return ( ISessionStore )httpSession.getAttribute( ATTR_SESSION_STORE );
  }

  public static Display getSessionDisplay( HttpSession httpSession ) {
    ISessionStore sessionStore = getSessionStore( httpSession );
    return ( Display )sessionStore.getAttribute( ATTR_SESSION_DISPLAY );
  }
}
