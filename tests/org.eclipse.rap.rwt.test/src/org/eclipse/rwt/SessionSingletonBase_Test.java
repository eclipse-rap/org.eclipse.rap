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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.service.*;


public class SessionSingletonBase_Test extends TestCase {
  
  private static class TestSingleton {
  }

  private ServiceContext serviceContext;
  
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
    assertSame( instance.getClass(), TestSingleton.class );
  }

  public void testGetInstanceWithSameType() {
    Object instance1 = SessionSingletonBase.getInstance( TestSingleton.class );
    Object instance2 = SessionSingletonBase.getInstance( TestSingleton.class );
    
    assertSame( instance1, instance2 );
  }
  
  public void testGetInstanceFromBackgroundThreadWithContext() throws Throwable {
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
    serviceContext = createServiceContext();
    ContextProvider.setContext( serviceContext );
    SingletonManager.install( serviceContext.getSessionStore() );
  }
  
  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
  }

  private static ServiceContext createServiceContext() {
    TestSession session = new TestSession();
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( session );
    ServiceContext result = new ServiceContext( request, response );
    result.setSessionStore( new SessionStoreImpl( session ) );
    return result;
  }
}
