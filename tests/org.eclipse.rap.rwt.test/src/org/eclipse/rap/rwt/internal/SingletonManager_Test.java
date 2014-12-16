/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal;

import static org.eclipse.rap.rwt.test.util.AttributeStoreTestUtil.fakeAttributeStore;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.joinThreads;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.startThreads;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serialize;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.UISession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SingletonManager_Test {

  private UISession uiSession;
  private ApplicationContext applicationContext;

  @Before
  public void setUp() {
    uiSession = mock( UISession.class );
    fakeAttributeStore( uiSession );
    applicationContext = mock( ApplicationContext.class );
    fakeAttributeStore( applicationContext );
  }

  @After
  public void tearDown() {
    DependantTestSingleton.currentUISession = null;
  }

  @Test( expected = IllegalStateException.class )
  public void testInstall_uiSession_failsIfAlreadyInstalled() {
    SingletonManager.install( uiSession );

    SingletonManager.install( uiSession );
  }

  @Test( expected = IllegalStateException.class )
  public void testInstall_applicationContext_failsIfAlreadyInstalled() {
    SingletonManager.install( applicationContext );

    SingletonManager.install( applicationContext );
  }

  @Test
  public void testGetInstance_uiSession_nullIfNotInstalled() {
    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );

    assertNull( singletonManager );
  }

  @Test
  public void testGetInstance_applicationContext_nullIfNotInstalled() {
    SingletonManager singletonManager = SingletonManager.getInstance( applicationContext );

    assertNull( singletonManager );
  }

  @Test
  public void testGetInstance_uiSession_afterInstall() {
    SingletonManager.install( uiSession );

    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );

    assertNotNull( singletonManager );
  }

  @Test
  public void testGetInstance_applicationContext_afterInstall() {
    SingletonManager.install( applicationContext );

    SingletonManager singletonManager = SingletonManager.getInstance( applicationContext );

    assertNotNull( singletonManager );
  }

  @Test
  public void testGetInstance_uiSession_returnsSameInstance() {
    SingletonManager.install( uiSession );

    SingletonManager instance1 = SingletonManager.getInstance( uiSession );
    SingletonManager instance2 = SingletonManager.getInstance( uiSession );

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetInstance_applicationContext_returnsSameInstance() {
    SingletonManager.install( applicationContext );

    SingletonManager instance1 = SingletonManager.getInstance( applicationContext );
    SingletonManager instance2 = SingletonManager.getInstance( applicationContext );

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetSingleton() {
    SingletonManager singletonManager = new SingletonManager();

    Object singleton = singletonManager.getSingleton( TestSingleton.class );

    assertNotNull( singleton );
  }

  @Test
  public void testGetSingleton_fromDifferentSessions() {
    UISession otherUISession = mock( UISession.class );
    fakeAttributeStore( otherUISession );
    SingletonManager.install( uiSession );
    SingletonManager.install( otherUISession );
    SingletonManager singletonManager1 = SingletonManager.getInstance( uiSession );
    SingletonManager singletonManager2 = SingletonManager.getInstance( otherUISession );

    Object singleton1 = singletonManager1.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager2.getSingleton( TestSingleton.class );

    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( TestSingleton.class, singleton2.getClass() );
    assertNotSame( singleton1, singleton2 );
  }

  @Test
  public void testGetSingleton_withSameType() {
    SingletonManager singletonManager = new SingletonManager();

    Object singleton1 = singletonManager.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager.getSingleton( TestSingleton.class );

    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( singleton1, singleton2 );
  }

  @Test
  public void testGetSingleton_withDifferentTypes() {
    SingletonManager singletonManager = new SingletonManager();

    Object singleton = singletonManager.getSingleton( TestSingleton.class );
    Object otherSingleton = singletonManager.getSingleton( OtherTestSingleton.class );

    assertSame( TestSingleton.class, singleton.getClass() );
    assertSame( OtherTestSingleton.class, otherSingleton.getClass() );
  }

  @Test
  public void testGetSingleton_fromConcurrentThreads() throws InterruptedException {
    SingletonManager.install( uiSession );
    final AtomicReference<Throwable> problem = new AtomicReference<Throwable>();
    final Set<Object> instances = Collections.synchronizedSet( new HashSet<Object>() );
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          SingletonManager singletonManager = SingletonManager.getInstance( uiSession );
          Object singleton = singletonManager.getSingleton( TestSingleton.class );
          instances.add( singleton );
          Object otherSingleton = singletonManager.getSingleton( OtherTestSingleton.class );
          instances.add( otherSingleton );
        } catch( Throwable t ) {
          problem.set( t );
        }
      }
    };

    joinThreads( startThreads( 50, runnable ) );

    assertNull( problem.get() );
    assertEquals( 2, instances.size() );
  }

  @Test
  public void testGetSingleton_fromMultiThreadedNestedCalls() {
    SingletonManager.install( uiSession );
    DependantTestSingleton.currentUISession = uiSession;
    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );

    Object singleton = singletonManager.getSingleton( DependantTestSingleton.class );

    assertNotNull( singleton );
  }

  @Test
  public void testSerialize() throws Exception {
    SingletonManager singletonManager = new SingletonManager();
    SerializableTestSingleton singleton
      = singletonManager.getSingleton( SerializableTestSingleton.class );
    singleton.value = new Integer( 4711 );

    SingletonManager deserialized = serializeAndDeserialize( singletonManager );

    SerializableTestSingleton deserializedSingleton
      = deserialized.getSingleton( SerializableTestSingleton.class );
    assertEquals( singleton.value, deserializedSingleton.value );
  }

  @Test( expected = NotSerializableException.class )
  public void testSerializableWithNonSerializableSingleton() throws IOException {
    SingletonManager singletonManager = new SingletonManager();
    singletonManager.getSingleton( NonSerializableTestSingleton.class );

    serialize( singletonManager );
  }

  private static class TestSingleton {
  }

  private static class OtherTestSingleton {
  }

  private static class DependantTestSingleton {

    static UISession currentUISession;

    private DependantTestSingleton() throws Throwable {
      Runnable runnable = new Runnable() {
        public void run() {
          SingletonManager.getInstance( currentUISession ).getSingleton( TestSingleton.class );
        }
      };
      runInThread( runnable );
    }
  }

  private static class SerializableTestSingleton implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer value;
  }

  private static class NonSerializableTestSingleton {
  }

}
