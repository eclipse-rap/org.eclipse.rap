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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DeferredRemoteObject_Test {

  private String objectId;
  private DeferredRemoteObject remoteObject;
  private ProtocolMessageWriter writer;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    objectId = "testId";
    remoteObject = new DeferredRemoteObject( objectId, "type" );
    writer = mock( ProtocolMessageWriter.class );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDoesNotRenderOperationsImmediately() {
    remoteObject.call( "method", new JsonObject() );

    assertEquals( 0, getMessage().getOperationCount() );
  }

  @Test
  public void testOperationsAreRenderedDeferred() {
    remoteObject.call( "method", null );

    remoteObject.render( writer );

    verify( writer ).appendCreate( anyString(), anyString() );
    verify( writer ).appendCall( anyString(), anyString(), isNull() );
  }

  @Test
  public void testCreateIsRendered() {
    remoteObject.render( writer );

    verify( writer ).appendCreate( eq( objectId ), eq( "type" ) );
  }

  @Test
  public void testCreateIsNotRenderedIfCreateTypeIsNull() {
    DeferredRemoteObject remoteObject = new DeferredRemoteObject( "id", null );

    remoteObject.render( writer );

    verify( writer, times( 0 ) ).appendCreate( anyString(), anyString() );
  }

  @Test
  public void testSet_int_isRendered() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  @Test
  public void testSet_int_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 23 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_double_isRendered() {
    remoteObject.set( "property", 47.11 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 47.11 ) );
  }

  @Test
  public void testSet_double_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", 47.11 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_boolean_isRendered() {
    remoteObject.set( "property", true );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( true ) );
  }

  @Test
  public void testSet_boolean_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_string_isRendered() {
    remoteObject.set( "property", "foo" );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( "foo" ) );
  }

  @Test
  public void testSet_string_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", "foo" );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_jsonValue_isRendered() {
    JsonValue value = JsonValue.valueOf( 23 );
    remoteObject.set( "property", value );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( value ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSet_jsonValue_checksValue() {
    remoteObject.set( "foo", ( JsonValue )null );
  }

  @Test
  public void testSet_jsonValue_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "property", JsonValue.TRUE );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testListen_isRendered() {
    remoteObject.listen( "event", true );

    remoteObject.render( writer );

    verify( writer ).appendListen( eq( objectId ), eq( "event" ), eq( true ) );
  }

  @Test
  public void testListen_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.listen( "event", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testCall_isRendered() {
    JsonObject parameters = mock( JsonObject.class );
    remoteObject.call( "method", parameters );

    remoteObject.render( writer );

    verify( writer ).appendCall( eq( objectId ), eq( "method" ), eq( parameters ) );
  }

  @Test
  public void testCall_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.call( "method", mock( JsonObject.class ) );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testDestroy_isRendered() {
    remoteObject.render( writer );
    reset( writer );

    remoteObject.destroy();
    remoteObject.render( writer );

    verify( writer ).appendDestroy( eq( objectId ) );
  }

  @Test
  public void testDestroy_checksState() {
    DeferredRemoteObject remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.destroy();

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testRenderQueueIsClearedAfterRender() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );
    remoteObject.render( writer );

    verify( writer, times( 1 ) ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  @Test
  public void testRender_omitsImmediatelyDestroyedObjects() {
    remoteObject.destroy();

    remoteObject.render( writer );

    verifyNoInteractions( writer );
  }

  @Test
  public void testIsSerializable() throws Exception {
    remoteObject.set( "property", 23 );

    DeferredRemoteObject deserializedRemoteObject = serializeAndDeserialize( remoteObject );
    deserializedRemoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  private static TestMessage getMessage() {
    return new TestMessage( getProtocolWriter().createMessage().toJson() );
  }

}
