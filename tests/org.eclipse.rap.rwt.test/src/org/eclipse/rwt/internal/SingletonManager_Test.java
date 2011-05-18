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
package org.eclipse.rwt.internal;

import java.util.*;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;


public class SingletonManager_Test extends TestCase {
  
  private static class TestSingleton {
  }

  private static class OtherTestSingleton {
  }
  
  private static class DependantTestSingleton {
    
    static ISessionStore currentSessionStore;
    
    private DependantTestSingleton() throws Throwable {
      Runnable runnable = new Runnable() {
        public void run() {
          SingletonManager.getInstance( currentSessionStore ).getSingleton( TestSingleton.class );
        }
      };
      Fixture.runInThread( runnable );
    }
  }
  
  private ISessionStore sessionStore;

  public void testInstallMultipleTimes() {
    SingletonManager.install( sessionStore );
    
    try {
      SingletonManager.install( sessionStore );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testGetInstanceAfterInstall() {
    SingletonManager.install( sessionStore );
    SingletonManager singletonManager = SingletonManager.getInstance( sessionStore );
    
    assertNotNull( singletonManager );
  }
  
  public void testGetInstanceBeforeInstall() {
    SingletonManager singletonManager = SingletonManager.getInstance( sessionStore );
    assertNull( singletonManager );
  }
  
  public void testGetSingletonReturnsSameInstance() {
    SingletonManager instance1 = createSingletonManager();
    SingletonManager instance2 = SingletonManager.getInstance( sessionStore );
    assertSame( instance1, instance2 );
  }
  
  public void testGetSingleton() {
    SingletonManager singletonManager = createSingletonManager();
    
    Object singleton = singletonManager.getSingleton( TestSingleton.class );
    
    assertNotNull( singleton );
  }

  public void testGetSingletonFromDifferentSessions() {
    SingletonManager singletonManager1 = createSingletonManager();
    ISessionStore otherSessionStore = createSessionStore();
    SingletonManager singletonManager2 = createSingletonManager( otherSessionStore );
    
    Object singleton1 = singletonManager1.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager2.getSingleton( TestSingleton.class );
    
    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( TestSingleton.class, singleton2.getClass() );
    assertNotSame( singleton1, singleton2 );
  }
  
  public void testGetSingletonWithSameType() {
    SingletonManager singletonManager = createSingletonManager();
    Object singleton1 = singletonManager.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager.getSingleton( TestSingleton.class );
    
    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( singleton1, singleton2 );
  }

  public void testGetSingletonWithDifferentTypes() {
    SingletonManager singletonManager = createSingletonManager();
    Object singleton = singletonManager.getSingleton( TestSingleton.class );
    Object otherSingleton = singletonManager.getSingleton( OtherTestSingleton.class );
    
    assertSame( TestSingleton.class, singleton.getClass() );
    assertSame( OtherTestSingleton.class, otherSingleton.getClass() );
    assertNotSame( singleton, otherSingleton );
  }
  
  public void testGetSingletonFromConcurrentThreads() throws InterruptedException {
    SingletonManager.install( sessionStore );
    final Throwable[] problem = { null };
    final Set instances = Collections.synchronizedSet( new HashSet() );
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          SingletonManager singletonManager = SingletonManager.getInstance( sessionStore );
          Object singleton = singletonManager.getSingleton( TestSingleton.class );
          instances.add( singleton );
          Object otherSingleton = singletonManager.getSingleton( OtherTestSingleton.class );
          instances.add( otherSingleton );
        } catch( Throwable t ) {
          synchronized( problem ) {
            problem[ 0 ] = t;
          }
        }
      }
    };
    
    Thread[] threads = Fixture.startThreads( 50, runnable );
    Fixture.joinThreads( threads );
    
    assertNull( problem[ 0 ] );
    assertEquals( 2, instances.size() );
  }
  
  public void testGetSingletonFromMultiThreadedNestedCalls() {
    SingletonManager.install( sessionStore );
    DependantTestSingleton.currentSessionStore = sessionStore;
    SingletonManager singletonManager = SingletonManager.getInstance( sessionStore );
    
    Object singleton = singletonManager.getSingleton( DependantTestSingleton.class );
    
    assertNotNull( singleton );
  }
  
  protected void setUp() throws Exception {
    sessionStore = createSessionStore();
  }
  
  protected void tearDown() throws Exception {
    DependantTestSingleton.currentSessionStore = null;
  }

  private static ISessionStore createSessionStore() {
    TestRequest request = new TestRequest();
    HttpSession session = new TestSession();
    request.setSession( session );
    return new SessionStoreImpl( session );
  }

  private SingletonManager createSingletonManager() {
    return createSingletonManager( sessionStore );
  }

  private static SingletonManager createSingletonManager( ISessionStore sessionStore ) {
    SingletonManager.install( sessionStore );
    return SingletonManager.getInstance( sessionStore );
  }
}
