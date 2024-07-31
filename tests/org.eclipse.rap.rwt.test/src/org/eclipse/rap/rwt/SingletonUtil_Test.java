/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt;

import static org.eclipse.rap.rwt.test.util.AttributeStoreTestUtil.mockApplicationContextWithAttributeStore;
import static org.eclipse.rap.rwt.test.util.AttributeStoreTestUtil.mockUISessionWithAttributeStore;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SingletonUtil_Test {

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    createUISession();
    SingletonManager.install( ContextProvider.getUISession() );
  }

  @After
  public void tearDown() {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.disposeOfApplicationContext();
  }

  @Test( expected = NullPointerException.class )
  public void testGetUniqueInstance_uiSession_failsWithNullType() {
    SingletonUtil.getUniqueInstance( null, mock( UISession.class ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetUniqueInstance_applicationContext_failsWithNullType() {
    SingletonUtil.getUniqueInstance( null, mock( ApplicationContext.class ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetUniqueInstance_uiSession_failsWithNullUISession() {
    SingletonUtil.getUniqueInstance( TestSingleton.class, (UISession)null );
  }

  @Test( expected = NullPointerException.class )
  public void testGetUniqueInstance_applicationContext_failsWithNullApplicationContext() {
    SingletonUtil.getUniqueInstance( TestSingleton.class, (ApplicationContext)null );
  }

  @Test
  public void testGetUniqueInstance_uiSession_returnsInstanceOfGivenClass() {
    UISession uiSession = mockUISessionWithAttributeStore();
    SingletonManager.install( uiSession );

    Object instance = SingletonUtil.getUniqueInstance( TestSingleton.class, uiSession );

    assertNotNull( instance );
    assertSame( TestSingleton.class, instance.getClass() );
  }

  @Test
  public void testGetUniqueInstance_applicationContext_returnsInstanceOfGivenClass() {
    ApplicationContext applicationContext = mockApplicationContextWithAttributeStore();
    SingletonManager.install( applicationContext );

    Object instance = SingletonUtil.getUniqueInstance( TestSingleton.class, applicationContext );

    assertNotNull( instance );
    assertSame( TestSingleton.class, instance.getClass() );
  }

  @Test
  public void testGetUniqueInstance_uiSession_returnsSameInstanceInSameScope() {
    UISession uiSession = mockUISessionWithAttributeStore();
    SingletonManager.install( uiSession );

    Object instance1 = SingletonUtil.getUniqueInstance( TestSingleton.class, uiSession );
    Object instance2 = SingletonUtil.getUniqueInstance( TestSingleton.class, uiSession );

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetUniqueInstance_applicationContext_returnsSameInstanceInSameScope() {
    ApplicationContext applicationContext = mockApplicationContextWithAttributeStore();
    SingletonManager.install( applicationContext );

    Object instance1 = SingletonUtil.getUniqueInstance( TestSingleton.class, applicationContext );
    Object instance2 = SingletonUtil.getUniqueInstance( TestSingleton.class, applicationContext );

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetUniqueInstance_uiSession_returnsNewInstanceInOtherScope() {
    UISession uiSession1 = mockUISessionWithAttributeStore();
    UISession uiSession2 = mockUISessionWithAttributeStore();
    SingletonManager.install( uiSession1 );
    SingletonManager.install( uiSession2 );

    Object instance1 = SingletonUtil.getUniqueInstance( TestSingleton.class, uiSession1 );
    Object instance2 = SingletonUtil.getUniqueInstance( TestSingleton.class, uiSession2 );

    assertNotSame( instance1, instance2 );
  }

  @Test
  public void testGetUniqueInstance_applicationContext_returnsNewInstanceInOtherScope() {
    ApplicationContext applicationContext1 = mockApplicationContextWithAttributeStore();
    ApplicationContext applicationContext2 = mockApplicationContextWithAttributeStore();
    SingletonManager.install( applicationContext1 );
    SingletonManager.install( applicationContext2 );

    Object instance1 = SingletonUtil.getUniqueInstance( TestSingleton.class, applicationContext1 );
    Object instance2 = SingletonUtil.getUniqueInstance( TestSingleton.class, applicationContext2 );

    assertNotSame( instance1, instance2 );
  }

  @Test( expected = NullPointerException.class )
  public void testGetSessionInstance_failsWithNullType() {
    SingletonUtil.getSessionInstance( null );
  }

  @Test
  public void testGetSessionInstance_returnsInstanceOfGivenClass() {
    Object instance = SingletonUtil.getSessionInstance( TestSingleton.class );

    assertNotNull( instance );
    assertSame( TestSingleton.class, instance.getClass() );
  }

  @Test
  public void testGetSessionInstance_returnsSameInstanceInSameSession() {
    Object instance1 = SingletonUtil.getSessionInstance( TestSingleton.class );
    Object instance2 = SingletonUtil.getSessionInstance( TestSingleton.class );

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetSessionInstance_returnsNewInstanceInAnotherSession() throws Throwable {
    Object instance1 = SingletonUtil.getSessionInstance( TestSingleton.class );
    final AtomicReference<Object> instance2 = new AtomicReference<Object>();

    runInThread( new Runnable() {
      @Override
      public void run() {
        Fixture.createServiceContext();
        instance2.set( SingletonUtil.getSessionInstance( TestSingleton.class ) );
      }
    } );

    assertNotSame( instance1, instance2.get() );
  }

  @Test
  public void testGetSessionInstance_returnsInstanceWithFakeContext() throws Throwable {
    final ServiceContext serviceContext = ContextProvider.getContext();
    final AtomicReference<Object> instance = new AtomicReference<>();

    runInThread( new Runnable() {
      @Override
      public void run() {
        ContextProvider.setContext( serviceContext );
        instance.set( SingletonUtil.getSessionInstance( TestSingleton.class ) );
      }
    } );

    assertNotNull( instance.get() );
  }

  @Test( expected = IllegalStateException.class )
  public void testGetSessionInstance_failsWithoutContext() {
    ContextProvider.disposeContext();
    SingletonUtil.getSessionInstance( TestSingleton.class );
  }

  private static void createUISession() {
    ServiceContext serviceContext = ContextProvider.getContext();
    HttpSession session = serviceContext.getRequest().getSession();
    ApplicationContextImpl applicationContext = serviceContext.getApplicationContext();
    serviceContext.setUISession( new UISessionImpl( applicationContext, session ) );
  }

  private static class TestSingleton {
  }

}
