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
package org.eclipse.rap.rwt.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SingletonManager_Test {

  private UISession uiSession;

  @Before
  public void setUp() {
    uiSession = createUISession();
  }

  @After
  public void tearDown() {
    DependantTestSingleton.currentUISession = null;
  }

  @Test
  public void testInstallMultipleTimes() {
    SingletonManager.install( uiSession );

    try {
      SingletonManager.install( uiSession );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testGetInstanceAfterInstall() {
    SingletonManager.install( uiSession );
    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );

    assertNotNull( singletonManager );
  }

  @Test
  public void testGetInstanceBeforeInstall() {
    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );
    assertNull( singletonManager );
  }

  @Test
  public void testGetSingletonReturnsSameInstance() {
    SingletonManager instance1 = createSingletonManager();
    SingletonManager instance2 = SingletonManager.getInstance( uiSession );
    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetSingleton() {
    SingletonManager singletonManager = createSingletonManager();

    Object singleton = singletonManager.getSingleton( TestSingleton.class );

    assertNotNull( singleton );
  }

  @Test
  public void testGetSingletonFromDifferentSessions() {
    SingletonManager singletonManager1 = createSingletonManager();
    UISession otherUISession = createUISession();
    SingletonManager singletonManager2 = createSingletonManager( otherUISession );

    Object singleton1 = singletonManager1.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager2.getSingleton( TestSingleton.class );

    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( TestSingleton.class, singleton2.getClass() );
    assertNotSame( singleton1, singleton2 );
  }

  @Test
  public void testGetSingletonWithSameType() {
    SingletonManager singletonManager = createSingletonManager();
    Object singleton1 = singletonManager.getSingleton( TestSingleton.class );
    Object singleton2 = singletonManager.getSingleton( TestSingleton.class );

    assertSame( TestSingleton.class, singleton1.getClass() );
    assertSame( singleton1, singleton2 );
  }

  @Test
  public void testGetSingletonWithDifferentTypes() {
    SingletonManager singletonManager = createSingletonManager();
    Object singleton = singletonManager.getSingleton( TestSingleton.class );
    Object otherSingleton = singletonManager.getSingleton( OtherTestSingleton.class );

    assertSame( TestSingleton.class, singleton.getClass() );
    assertSame( OtherTestSingleton.class, otherSingleton.getClass() );
    assertNotSame( singleton, otherSingleton );
  }

  @Test
  public void testGetSingletonFromConcurrentThreads() throws InterruptedException {
    SingletonManager.install( uiSession );
    final Throwable[] problem = { null };
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

  @Test
  public void testGetSingletonFromMultiThreadedNestedCalls() {
    SingletonManager.install( uiSession );
    DependantTestSingleton.currentUISession = uiSession;
    SingletonManager singletonManager = SingletonManager.getInstance( uiSession );

    Object singleton = singletonManager.getSingleton( DependantTestSingleton.class );

    assertNotNull( singleton );
  }

  @Test
  public void testSerialize() throws Exception {
    SingletonManager singletonManager = createSingletonManager();
    Object instance = singletonManager.getSingleton( SerializableTestSingleton.class );
    SerializableTestSingleton singleton = ( SerializableTestSingleton )instance;
    singleton.value = new Integer( 4711 );
    SingletonManager deserialized = Fixture.serializeAndDeserialize( singletonManager );

    instance = deserialized.getSingleton( SerializableTestSingleton.class );
    SerializableTestSingleton deserializedSingleton = ( SerializableTestSingleton )instance;

    assertEquals( singleton.value, deserializedSingleton.value );
  }

  @Test
  public void testSerializableWithNonSerializableSingleton() throws IOException {
    SingletonManager singletonManager = createSingletonManager();
    singletonManager.getSingleton( NonSerializableTestSingleton.class );
    try {
      Fixture.serialize( singletonManager );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }

  private static UISession createUISession() {
    TestRequest request = new TestRequest();
    HttpSession session = new TestSession();
    request.setSession( session );
    return new UISessionImpl( mock( ApplicationContextImpl.class ), session );
  }

  private SingletonManager createSingletonManager() {
    return createSingletonManager( uiSession );
  }

  private static SingletonManager createSingletonManager( UISession uiSession ) {
    SingletonManager.install( uiSession );
    return SingletonManager.getInstance( uiSession );
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
      Fixture.runInThread( runnable );
    }
  }

  private static class SerializableTestSingleton implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer value;
  }

  private static class NonSerializableTestSingleton {
  }

}
