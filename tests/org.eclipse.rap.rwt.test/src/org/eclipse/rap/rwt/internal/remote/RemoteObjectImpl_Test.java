/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;


public class RemoteObjectImpl_Test extends TestCase {

  private String objectId;
  private RemoteObjectImpl remoteObject;
  private ProtocolMessageWriter writer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    objectId = "testId";
    remoteObject = new RemoteObjectImpl( objectId, "type" );
    writer = mock( ProtocolMessageWriter.class );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDoesNotRenderOperationsImmediately() {
    remoteObject.call( "method", mockProperties() );

    assertEquals( 0, getMessage().getOperationCount() );
  }

  @SuppressWarnings( "unchecked" )
  public void testOperationsAreRenderedDeferred() {
    remoteObject.call( "method", null );

    remoteObject.render( writer );

    verify( writer ).appendCreate( anyString(), anyString() );
    verify( writer ).appendCall( anyString(), anyString(), anyMap() );
  }

  public void testCreateIsRendered() {
    remoteObject.render( writer );

    verify( writer ).appendCreate( eq( objectId ), eq( "type" ) );
  }

  public void testSetIntIsRendered() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  public void testSetDoubleIsRendered() {
    remoteObject.set( "property", 47.11 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 47.11 ) );
  }

  public void testSetBooleanIsRendered() {
    remoteObject.set( "property", true );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( true ) );
  }

  public void testSetStringIsRendered() {
    remoteObject.set( "property", "foo" );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( "foo" ) );
  }

  public void testSetObjectIsRendered() {
    Object object = new Object();
    remoteObject.set( "property", object );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), same( object ) );
  }

  public void testListenIsRendered() {
    remoteObject.listen( "event", true );

    remoteObject.render( writer );

    verify( writer ).appendListen( eq( objectId ), eq( "event" ), eq( true ) );
  }

  public void testCallIsRendered() {
    Map<String, Object> properties = mockProperties();
    remoteObject.call( "method", properties );

    remoteObject.render( writer );

    verify( writer ).appendCall( eq( objectId ), eq( "method" ), same( properties ) );
  }

  public void testDestroyIsRendered() {
    remoteObject.destroy();

    remoteObject.render( writer );

    verify( writer ).appendDestroy( eq( objectId ) );
  }

  public void testIsNotDestroyedInitially() {
    assertFalse( remoteObject.isDestroyed() );
  }

  public void testIsDestroyedAfterDestroy() {
    remoteObject.destroy();

    assertTrue( remoteObject.isDestroyed() );
  }

  public void testPreventsSetIntWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.set( "properties", 23 );
      }
    } );
  }

  public void testPreventsSetDoubleWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.set( "properties", 47.11 );
      }
    } );
  }

  public void testPreventsSetBooleanWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.set( "properties", true );
      }
    } );
  }

  public void testPreventsSetStringWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.set( "properties", "foo" );
      }
    } );
  }

  public void testPreventsSetObjectWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.set( "properties", new Object() );
      }
    } );
  }

  public void testPreventsListenWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.listen( "event", true );
      }
    } );
  }

  public void testPreventsCallWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.call( "method", mockProperties() );
      }
    } );
  }

  public void testPreventsDestroyWhenDestroyed() {
    remoteObject.destroy();

    assertFailsWithIsDestroyed( new Runnable() {
      public void run() {
        remoteObject.destroy();
      }
    } );
  }

  public void testRenderQueueIsClearedAfterRender() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );
    remoteObject.render( writer );

    verify( writer, times( 1 ) ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  private static void assertFailsWithIsDestroyed( Runnable runnable ) {
    try {
      runnable.run();
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Remote object is destroyed", exception.getMessage() );
    }
  }

  @SuppressWarnings( "unchecked" )
  private static Map<String, Object> mockProperties() {
    return mock( Map.class );
  }

  private static Message getMessage() {
    return new Message( ContextProvider.getProtocolWriter().createMessage() );
  }

}
