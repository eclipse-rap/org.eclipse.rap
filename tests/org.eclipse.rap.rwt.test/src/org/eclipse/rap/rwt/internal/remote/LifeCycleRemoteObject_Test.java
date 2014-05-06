/*******************************************************************************
* Copyright (c) 2011, 2014 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.rap.rwt.testfixture.TestMessage.CallOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.CreateOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.ListenOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage.SetOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LifeCycleRemoteObject_Test {

  private LifeCycleRemoteObject remoteObject;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    remoteObject = new LifeCycleRemoteObject( "id", null );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructor_writesCreateOperation() {
    remoteObject = new LifeCycleRemoteObject( "id", "type" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
    assertEquals( "type", operation.getType() );
  }

  @Test
  public void testConstructor_nullTypeOmitsCreateOperation() {

    assertEquals( 0, getMessage().getOperationCount() );
  }

  @Test
  public void testCreateIncludesSetProperties() {
    remoteObject = new LifeCycleRemoteObject( "id", "type" );
    remoteObject.set( "foo", 23 );

    TestMessage message = getMessage();
    assertEquals( 1, message.getOperationCount() );
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertEquals( 23, message.getOperation( 0 ).getProperty( "foo" ).asInt() );
  }

  @Test
  public void testSet() {
    remoteObject.set( "int", 2 );
    remoteObject.set( "double", 3.5 );
    remoteObject.set( "boolean", true );
    remoteObject.set( "string", "foo" );
    remoteObject.set( "array", new JsonArray().add( 23 ).add( 42 ) );
    remoteObject.set( "object", new JsonObject().add( "foo", 23 ) );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
    assertEquals( JsonValue.valueOf( 2 ), operation.getProperty( "int" ) );
    assertEquals( JsonValue.valueOf( 3.5 ), operation.getProperty( "double" ) );
    assertEquals( JsonValue.TRUE, operation.getProperty( "boolean" ));
    assertEquals( JsonValue.valueOf( "foo" ), operation.getProperty( "string" ) );
    assertEquals( new JsonArray().add( 23 ).add( 42 ), operation.getProperty( "array" ) );
    assertEquals( new JsonObject().add( "foo", 23 ), operation.getProperty( "object" ) );
  }

  @Test
  public void testSet_int_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "foo", 23 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_double_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "foo", 3.14 );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_boolean_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "foo", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_String_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "foo", "bar" );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testSet_JsonValue_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.set( "foo", JsonValue.TRUE );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testListen() {
    remoteObject.listen( "foo", true );
    remoteObject.listen( "bar", false );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
    assertTrue( operation.listensTo( "foo" ) );
    assertFalse( operation.listensTo( "bar" ) );
  }

  @Test
  public void testListen_addAndRemove() {
    remoteObject.listen( "foo", true );
    remoteObject.listen( "foo", false );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
    assertFalse( operation.listensTo( "foo" ) );
  }

  @Test
  public void testListen_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.listen( "foo", true );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testCall() {
    remoteObject.call( "method", null );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
  }

  @Test
  public void testCall_twice() {
    remoteObject.call( "method", null );

    remoteObject.call( "method2", new JsonObject().add( "key1", "a" ).add( "key2", 3 ) );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( "id", operation.getTarget() );
    assertEquals( "method2", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ).asString() );
    assertEquals( 3, operation.getProperty( "key2" ).asInt() );
  }

  @Test
  public void testCall_checksState() {
    RemoteObjectImpl remoteObjectSpy = spy( remoteObject );

    remoteObjectSpy.call( "foo", new JsonObject() );

    verify( remoteObjectSpy ).checkState();
  }

  @Test
  public void testDestroy() {
    remoteObject.destroy();

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( "id", operation.getTarget() );
  }

  private TestMessage getMessage() {
    ProtocolMessageWriter writer = getProtocolWriter();
    return new TestMessage( writer.createMessage() );
  }

}
