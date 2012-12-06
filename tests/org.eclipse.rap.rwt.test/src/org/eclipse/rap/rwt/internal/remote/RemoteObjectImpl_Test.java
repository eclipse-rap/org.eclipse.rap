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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
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

  public void testReturnsId() {
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( "id", "type" );

    String id = remoteObject.getId();

    assertEquals( "id", id );
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

  public void testCreateIsNotRenderedIfCreateTypeIsNull() {
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( "id", null );

    remoteObject.render( writer );

    verify( writer, times( 0 ) ).appendCreate( anyString(), anyString() );
  }

  public void testSetIntIsRendered() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  public void testChecksNameForSetInt() {
    try {
      remoteObject.set( null, 23 );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForSetInt() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 23 );

    verify( remoteObjectSpy ).checkState();
  }

  public void testSetDoubleIsRendered() {
    remoteObject.set( "property", 47.11 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 47.11 ) );
  }

  public void testChecksNameForSetDouble() {
    try {
      remoteObject.set( null, 47.11 );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForSetDouble() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 47.11 );

    verify( remoteObjectSpy ).checkState();
  }

  public void testSetBooleanIsRendered() {
    remoteObject.set( "property", true );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( true ) );
  }

  public void testChecksNameForSetBoolean() {
    try {
      remoteObject.set( null, true );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForSetBoolean() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", true );

    verify( remoteObjectSpy ).checkState();
  }

  public void testSetStringIsRendered() {
    remoteObject.set( "property", "foo" );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( "foo" ) );
  }

  public void testChecksNameForSetString() {
    try {
      remoteObject.set( null, "foo" );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForSetString() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", "foo" );

    verify( remoteObjectSpy ).checkState();
  }

  public void testSetObjectIsRendered() {
    Object object = new Object();
    remoteObject.set( "property", object );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), same( object ) );
  }

  public void testChecksNameForSetObject() {
    try {
      remoteObject.set( null, new Object() );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForSetObject() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", new Object() );

    verify( remoteObjectSpy ).checkState();
  }

  public void testListenIsRendered() {
    remoteObject.listen( "event", true );

    remoteObject.render( writer );

    verify( writer ).appendListen( eq( objectId ), eq( "event" ), eq( true ) );
  }

  public void testChecksNameForListen() {
    try {
      remoteObject.listen( null, true );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForListen() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.listen( "event", true );

    verify( remoteObjectSpy ).checkState();
  }

  public void testCallIsRendered() {
    Map<String, Object> properties = mockProperties();
    remoteObject.call( "method", properties );

    remoteObject.render( writer );

    verify( writer ).appendCall( eq( objectId ), eq( "method" ), same( properties ) );
  }

  public void testChecksNameForCall() {
    try {
      remoteObject.call( null, mockProperties() );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testChecksStateForCall() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.call( "method", mockProperties() );

    verify( remoteObjectSpy ).checkState();
  }

  public void testDestroyIsRendered() {
    remoteObject.destroy();

    remoteObject.render( writer );

    verify( writer ).appendDestroy( eq( objectId ) );
  }

  public void testChecksStateForDestroy() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.destroy();

    verify( remoteObjectSpy ).checkState();
  }

  public void testRenderQueueIsClearedAfterRender() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );
    remoteObject.render( writer );

    verify( writer, times( 1 ) ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  public void testIsNotDestroyedInitially() {
    assertFalse( remoteObject.isDestroyed() );
  }

  public void testIsDestroyedAfterDestroy() {
    remoteObject.destroy();

    assertTrue( remoteObject.isDestroyed() );
  }

  public void testPreventsCallWhenDestroyed() {
    remoteObject.destroy();
    try {
      remoteObject.call( "method", mockProperties() );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Remote object is destroyed", exception.getMessage() );
    }
  }

  public void testPreventsCallFromBackgroundThread() {
    try {
      runInBackgroundThread( new Runnable() {
        public void run() {
          remoteObject.call( "method", mockProperties() );
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Remote object called from wrong thread", exception.getMessage() );
    }
  }

  public void testHandleSetDelegatesToHandler() {
    RemoteOperationHandler handler = mock( RemoteOperationHandler.class );
    remoteObject.setHandler( handler );
    Map<String, Object> properties = mockProperties();

    remoteObject.handleSet( properties );

    verify( handler ).handleSet( eq( properties ) );
  }

  public void testHandleSetDoesNotFailWithoutHandler() {
    remoteObject.handleSet( mockProperties() );
  }

  public void testHandleCallDelegatesToHandler() {
    RemoteOperationHandler handler = mock( RemoteOperationHandler.class );
    remoteObject.setHandler( handler );
    Map<String, Object> properties = mockProperties();

    remoteObject.handleCall( "method", properties );

    verify( handler ).handleCall( eq( "method" ), eq( properties ) );
  }

  public void testHandleCallDoesNotFailWithoutHandler() {
    remoteObject.handleCall( "method", mockProperties() );
  }

  public void testHandleNotifyDelegatesToHandler() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteOperationHandler handler = mock( RemoteOperationHandler.class );
    remoteObject.setHandler( handler );
    Map<String, Object> properties = mockProperties();

    remoteObject.handleNotify( "event", properties );

    verify( handler ).handleNotify( eq( "event" ), eq( properties ) );
  }

  public void testHandleNotifyDelegatesToHandlerNotInReadData() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    RemoteOperationHandler handler = mock( RemoteOperationHandler.class );
    remoteObject.setHandler( handler );
    Map<String, Object> properties = mockProperties();

    remoteObject.handleNotify( "event", properties );

    verify( handler, never() ).handleNotify( eq( "event" ), eq( properties ) );
  }

  public void testHandleNotifyDelegatesToHandlerNotInRender() {
    Fixture.fakePhase( PhaseId.RENDER );
    RemoteOperationHandler handler = mock( RemoteOperationHandler.class );
    remoteObject.setHandler( handler );
    Map<String, Object> properties = mockProperties();

    remoteObject.handleNotify( "event", properties );

    verify( handler, never() ).handleNotify( eq( "event" ), eq( properties ) );
  }

  public void testHandleNotifyDoesNotFailWithoutHandler() {
    remoteObject.handleNotify( "event", mockProperties() );
  }

  private static void runInBackgroundThread( Runnable runnable ) {
    try {
      Fixture.runInThread( runnable );
    } catch( RuntimeException exception ) {
      throw exception;
    } catch( Throwable exception ) {
      throw new RuntimeException( exception );
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
