/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.junit.After;
import org.junit.Test;


public class ContextProvider_Test {

  @After
  public void tearDown() {
    // clear thread locals in ContextProvider
    ContextProvider.disposeContext();
    ContextProvider.disposeContext( Thread.currentThread() );
  }

  @Test
  public void testHasContext_falseByDefault() {
    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testSetContext() {
    ServiceContext serviceContext = mockServiceContext();

    ContextProvider.setContext( serviceContext );

    assertTrue( ContextProvider.hasContext() );
  }

  public void testDisposeContext() {
    ServiceContext serviceContext = mockServiceContext();
    ContextProvider.setContext( serviceContext );

    ContextProvider.disposeContext();

    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testSetContext_withThread() throws Exception {
    ServiceContext serviceContext = mockServiceContext();
    final AtomicBoolean hasContext = new AtomicBoolean();
    Thread thread = new Thread() {
      @Override
      public void run() {
        hasContext.set( ContextProvider.hasContext() );
      }
    };

    ContextProvider.setContext( serviceContext, thread );
    thread.start();
    thread.join();

    assertTrue( hasContext.get() );
    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testDisposeContext_withThread() throws Exception {
    ServiceContext serviceContext = mockServiceContext();
    final AtomicBoolean hasContext = new AtomicBoolean();
    Thread thread = new Thread() {
      @Override
      public void run() {
        hasContext.set( ContextProvider.hasContext() );
      }
    };

    ContextProvider.setContext( serviceContext, thread );
    ContextProvider.disposeContext( thread );
    thread.start();
    thread.join();

    assertFalse( hasContext.get() );
    assertFalse( ContextProvider.hasContext() );
  }

  @Test
  public void testReleaseContextHolder_withThread() throws Exception {
    ServiceContext serviceContext = mockServiceContext();
    final AtomicBoolean useMapped = new AtomicBoolean();
    final AtomicBoolean hasContext = new AtomicBoolean( true );
    Thread thread = new Thread() {
      @Override
      public void run() {
        useMapped.set( ContextProvider.releaseContextHolder() );
        hasContext.set( ContextProvider.hasContext() );
      }
    };

    ContextProvider.setContext( serviceContext, thread );
    thread.start();
    thread.join();

    assertTrue( useMapped.get() );
    assertFalse( hasContext.get() );
  }

  private static ServiceContext mockServiceContext() {
    return new ServiceContext( mock( HttpServletRequest.class ),
                               mock( HttpServletResponse.class ),
                               mock( ApplicationContextImpl.class ) );
  }

}
