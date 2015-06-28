/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RemoteObjectImpl_Test {

  private String objectId;
  private RemoteObjectImpl remoteObject;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    objectId = "testId";
    remoteObject = new RemoteObjectImpl( objectId ) {};
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetId() {
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( "id" ) {};

    String id = remoteObject.getId();

    assertEquals( "id", id );
  }

  @Test( expected = NullPointerException.class )
  public void testSet_int_checksName() {
    remoteObject.set( null, 23 );
  }

  @Test
  public void testSet_int_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 23 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testSet_double_checksName() {
    remoteObject.set( null, 47.11 );
  }

  @Test
  public void testSet_double_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 47.11 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testSet_boolean_checksName() {
    remoteObject.set( null, true );
  }

  @Test
  public void testSet_boolean_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testSet_string_checksName() {
    remoteObject.set( null, "foo" );
  }

  @Test
  public void testSet_string_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", "foo" );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testSet_jsonValue_checksName() {
    remoteObject.set( null, JsonValue.TRUE );
  }

  @Test( expected = NullPointerException.class )
  public void testSet_jsonValue_checksValue() {
    remoteObject.set( "foo", ( JsonValue )null );
  }

  @Test
  public void testSet_jsonValue_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", JsonValue.TRUE );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testListen_checksName() {
    remoteObject.listen( null, true );
  }

  @Test
  public void testListen_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.listen( "event", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test( expected = NullPointerException.class )
  public void testCall_checksName() {
    remoteObject.call( null, mock( JsonObject.class ) );
  }

  @Test
  public void testCall_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.call( "method", mock( JsonObject.class ) );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testDestroy_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.destroy();

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testIsDestroyed_initiallyFalse() {
    assertFalse( remoteObject.isDestroyed() );
  }

  @Test
  public void testIsDestroyed_trueAfterDestroy() {
    remoteObject.destroy();

    assertTrue( remoteObject.isDestroyed() );
  }

  @Test
  public void testIsDestroyed_trueAfterMarkDestroyed() {
    remoteObject.markDestroyed();

    assertTrue( remoteObject.isDestroyed() );
  }

  @Test
  public void testPreventsCallWhenDestroyed() {
    remoteObject.destroy();
    try {
      remoteObject.call( "method", mock( JsonObject.class ) );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Remote object is destroyed", exception.getMessage() );
    }
  }

  @Test
  public void testPreventsCallFromBackgroundThread() {
    try {
      runInBackgroundThread( new Runnable() {
        public void run() {
          remoteObject.call( "method", mock( JsonObject.class ) );
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Remote object called from wrong thread", exception.getMessage() );
    }
  }

  @Test
  public void testSetHandler() {
    OperationHandler handler = mock( OperationHandler.class );
    remoteObject.setHandler( handler );

    OperationHandler result = remoteObject.getHandler();

    assertEquals( handler, result );
  }

  private static void runInBackgroundThread( Runnable runnable ) {
    try {
      runInThread( runnable );
    } catch( RuntimeException exception ) {
      throw exception;
    } catch( Throwable exception ) {
      throw new RuntimeException( exception );
    }
  }

}
