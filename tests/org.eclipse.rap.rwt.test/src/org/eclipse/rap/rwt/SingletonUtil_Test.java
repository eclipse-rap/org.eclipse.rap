/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt;

import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class SingletonUtil_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    createUISession();
    SingletonManager.install( ContextProvider.getUISession() );
  }

  @Override
  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.disposeOfApplicationContext();
  }

  public void testGetSessionInstance_failsWithNullArgument() {
    try {
      SingletonUtil.getSessionInstance( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetSessionInstance_returnsInstanceOfGivenClass() {
    Object instance = SingletonUtil.getSessionInstance( TestSingleton.class );

    assertNotNull( instance );
    assertSame( TestSingleton.class, instance.getClass() );
  }

  public void testGetSessionInstance_returnsSameInstanceInSameSession() {
    Object instance1 = SingletonUtil.getSessionInstance( TestSingleton.class );
    Object instance2 = SingletonUtil.getSessionInstance( TestSingleton.class );

    assertSame( instance1, instance2 );
  }

  public void testGetSessionInstance_returnsNewInstanceInAnotherSession() throws Throwable {
    Object instance1 = SingletonUtil.getSessionInstance( TestSingleton.class );
    final AtomicReference<Object> instance2 = new AtomicReference<Object>();

    Fixture.runInThread( new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        instance2.set( SingletonUtil.getSessionInstance( TestSingleton.class ) );
      }
    } );

    assertNotSame( instance1, instance2.get() );
  }

  public void testGetSessionInstance_returnsInstanceWithFakeContext()
    throws Throwable
  {
    final ServiceContext serviceContext = ContextProvider.getContext();
    final Object[] instance = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( serviceContext );
        instance[ 0 ] = SingletonUtil.getSessionInstance( TestSingleton.class );
      }
    };

    Fixture.runInThread( runnable );

    assertNotNull( instance[ 0 ] );
  }

  public void testGetSessionInstance_failsWithoutContext() {
    ContextProvider.disposeContext();
    try {
      SingletonUtil.getSessionInstance( TestSingleton.class );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private static void createUISession() {
    ServiceContext serviceContext = ContextProvider.getContext();
    HttpSession session = serviceContext.getRequest().getSession();
    serviceContext.setUISession( new UISessionImpl( session ) );
  }

  private static class TestSingleton {
  }

}
