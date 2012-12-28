/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ContextProvider_Test {

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
  }

  @After
  public void tearDown() {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.disposeOfApplicationContext();
    Fixture.disposeOfServletContext();
  }

  @Test
  public void testThreadLocalFunctionalityWithCurrentThread() {
    assertFalse( ContextProvider.hasContext() );
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    ServiceContext serviceContext = new ServiceContext( request, response );
    ContextProvider.setContext( serviceContext );
    assertTrue( ContextProvider.hasContext() );
    ContextProvider.disposeContext();
    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testThreadLocalFunctionalityWithAnyThread() throws Exception {
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    ServiceContext serviceContext = new ServiceContext( request, response );

    final boolean[] hasContext = new boolean[ 1 ];
    Thread thread1 = new Thread() {
      @Override
      public void run() {
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    };
    ContextProvider.setContext( serviceContext, thread1 );
    thread1.start();
    thread1.join();

    assertTrue( hasContext[ 0 ] );
    assertFalse( ContextProvider.hasContext() );

    hasContext[ 0 ] = false;
    Thread thread2 = new Thread() {
      @Override
      public void run() {
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    };
    ContextProvider.setContext( serviceContext, thread2 );
    ContextProvider.disposeContext( thread2 );
    thread2.start();
    thread2.join();

    assertFalse( hasContext[ 0 ] );
    assertFalse( ContextProvider.hasContext() );

    hasContext[ 0 ] = true;
    final boolean[] useMapped = { false };
    Thread thread3 = new Thread() {
      @Override
      public void run() {
        useMapped[ 0 ] = ContextProvider.releaseContextHolder();
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    };
    ContextProvider.setContext( serviceContext, thread3 );
    thread3.start();
    thread3.join();

    assertTrue( useMapped[ 0 ] );
    assertFalse( hasContext[ 0 ] );
  }

  @Test
  public void testContextCreation() {
    TestResponse response = new TestResponse();
    try {
      new ServiceContext( null, response );
      fail( "Request parameter must not be null." );
    } catch( NullPointerException npe ) {
    }

    try {
      TestRequest request = new TestRequest();
      new ServiceContext( request, null );
      fail( "Response parameter must not be null." );
    } catch( NullPointerException npe ) {
    }
  }

  @Test
  public void testApplicationContextIsAttachedToUISession() {
    Fixture.createServiceContext();

    UISession uiSession = ContextProvider.getUISession();

    assertNotNull( ApplicationContextUtil.get( uiSession ) );
  }

}
