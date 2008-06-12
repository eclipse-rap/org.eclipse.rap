/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.service.*;



public class SessionSingleton_Test extends TestCase {
  
  static class TestSingleton extends SessionSingletonBase {
    
    private TestSingleton() {}
    
    public static TestSingleton getInstance() {
      return ( TestSingleton )getInstance( TestSingleton.class );
    }
  }
  
  public void testSessionSingletons() throws Exception {
    Fixture.TestSession session1 = new Fixture.TestSession();
    Fixture.TestResponse testResponse1 = new Fixture.TestResponse();
    Fixture.TestRequest testRequest1 = new Fixture.TestRequest();
    testRequest1.setSession( session1 );
    final ServiceContext serviceContext1
      = new ServiceContext( testRequest1, testResponse1 );
    serviceContext1.setStateInfo( new ServiceStateInfo() );
    final TestSingleton[] instance1 = new TestSingleton[ 1 ];
    
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( serviceContext1 );
        instance1[ 0 ] = TestSingleton.getInstance();
      }
    };
    Thread thread = new Thread( runnable );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    
    assertNotNull( instance1[ 0 ] );
    
    TestSession session2 = new TestSession();
    TestResponse testResponse2 = new TestResponse();
    TestRequest testRequest2 = new TestRequest();
    testRequest2.setSession( session2 );
    final ServiceContext serviceContext2
      = new ServiceContext( testRequest2, testResponse2 );
    serviceContext2.setStateInfo( new ServiceStateInfo() );
    ContextProvider.setContext( serviceContext2 );
    try {
      ContextProvider.setContext( null );
      fail();
    } catch( final NullPointerException npe ) {
    }
    try {
      ContextProvider.setContext( serviceContext1 );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    

    TestSingleton instance2 = TestSingleton.getInstance();
    assertNotSame( instance1[ 0 ], instance2 );
    assertSame( instance2, TestSingleton.getInstance() );
    ContextProvider.disposeContext();
    try {
      TestSingleton.getInstance();
      fail();
    } catch( final IllegalStateException iae ) {
    }
    try {
      serviceContext2.getRequest();
      fail();
    } catch( final IllegalStateException ise ) {
    }    
  }
  
  public void testSessionSingletonsWithoutStateInfo() throws Exception {
    Fixture.TestSession session1 = new Fixture.TestSession();
    Fixture.TestResponse testResponse1 = new Fixture.TestResponse();
    Fixture.TestRequest testRequest1 = new Fixture.TestRequest();
    testRequest1.setSession( session1 );
    final ServiceContext serviceContext1
    = new ServiceContext( testRequest1, testResponse1 );
    final TestSingleton[] instance1 = new TestSingleton[ 1 ];
    
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( serviceContext1 );
        instance1[ 0 ] = TestSingleton.getInstance();
      }
    };
    Thread thread = new Thread( runnable );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    
    assertNotNull( instance1[ 0 ] );
    
    TestSession session2 = new TestSession();
    TestResponse testResponse2 = new TestResponse();
    TestRequest testRequest2 = new TestRequest();
    testRequest2.setSession( session2 );
    final ServiceContext serviceContext2
    = new ServiceContext( testRequest2, testResponse2 );
    serviceContext2.setStateInfo( new ServiceStateInfo() );
    ContextProvider.setContext( serviceContext2 );
    try {
      ContextProvider.setContext( null );
      fail();
    } catch( final NullPointerException npe ) {
    }
    try {
      ContextProvider.setContext( serviceContext1 );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    
    
    TestSingleton instance2 = TestSingleton.getInstance();
    assertNotSame( instance1[ 0 ], instance2 );
    assertSame( instance2, TestSingleton.getInstance() );
    ContextProvider.disposeContext();
    try {
      TestSingleton.getInstance();
      fail();
    } catch( final IllegalStateException iae ) {
    }
    try {
      serviceContext2.getRequest();
      fail();
    } catch( final IllegalStateException ise ) {
    }
    
    
  }
}
