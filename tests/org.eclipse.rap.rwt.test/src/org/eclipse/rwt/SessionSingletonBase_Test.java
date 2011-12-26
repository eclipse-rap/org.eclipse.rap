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
package org.eclipse.rwt;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class SessionSingletonBase_Test extends TestCase {

  public void testGetInstanceWithNullArgument() {
    try {
      SessionSingletonBase.getInstance( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetInstance() {
    Object instance = SessionSingletonBase.getInstance( TestSingleton.class );

    assertNotNull( instance );
    assertSame( TestSingleton.class, instance.getClass() );
  }

  public void testGetInstanceWithSameType() {
    Object instance1 = SessionSingletonBase.getInstance( TestSingleton.class );
    Object instance2 = SessionSingletonBase.getInstance( TestSingleton.class );

    assertSame( instance1, instance2 );
  }

  public void testGetInstanceFromBackgroundThreadWithContext() throws Throwable {
    final ServiceContext serviceContext = ContextProvider.getContext();
    final Object[] instance = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( serviceContext );
        instance[ 0 ] = SessionSingletonBase.getInstance( TestSingleton.class );
      }
    };

    Fixture.runInThread( runnable );

    assertNotNull( instance[ 0 ] );
  }

  public void testGetInstanceFromBackgroundThreadWithoutContext() {
    ContextProvider.disposeContext();
    try {
      SessionSingletonBase.getInstance( TestSingleton.class );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  protected void setUp() throws Exception {
    Fixture.createServiceContext();
    createSessionStore();
    SingletonManager.install( ContextProvider.getSessionStore() );
  }

  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
  }

  private static void createSessionStore() {
    ServiceContext serviceContext = ContextProvider.getContext();
    HttpSession session = serviceContext.getRequest().getSession();
    serviceContext.setSessionStore( new SessionStoreImpl( session ) );
  }

  private static class TestSingleton {
  }

}
