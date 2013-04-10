/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
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
    ServiceContext serviceContext = new ServiceContext( mock( HttpServletRequest.class ),
                                                        mock( HttpServletResponse.class ),
                                                        mock( ApplicationContextImpl.class ) );
    ContextProvider.setContext( serviceContext );
    assertTrue( ContextProvider.hasContext() );
    ContextProvider.disposeContext();
    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testThreadLocalFunctionalityWithAnyThread() throws Exception {
    ServiceContext serviceContext = new ServiceContext( mock( HttpServletRequest.class ),
                                                        mock( HttpServletResponse.class ),
                                                        mock( ApplicationContextImpl.class ) );

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
  public void testApplicationContextIsAttachedToUISession() {
    Fixture.createServiceContext();

    UISession uiSession = ContextProvider.getUISession();

    assertNotNull( uiSession.getApplicationContext() );
  }

}
