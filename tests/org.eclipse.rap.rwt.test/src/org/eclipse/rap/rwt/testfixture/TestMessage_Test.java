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
package org.eclipse.rap.rwt.testfixture;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.ListenOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestMessage_Test {

  private ProtocolMessageWriter writer;

  @Before
  public void setUp() {
    Fixture.setUp();
    writer = new ProtocolMessageWriter();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test( expected = NullPointerException.class )
  public void testConstructWithNull() {
    new TestMessage( (JsonObject)null );
  }

  @Test
  public void testConstructWithoutOperations() {
    try {
      new TestMessage( new JsonObject().add( "foo", 23 ) );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testConstructWithInvalidOperations() {
    try {
      new TestMessage( new JsonObject().add( "operations", 23 ) );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testGetOperationCountWhenEmpty() {
    assertEquals( 0, getMessage().getOperationCount() );
  }

  @Test
  public void testGetOperationCount() {
    writer.appendCall( "w1", "method1", null );
    writer.appendCall( "w2", "method2", null );

    assertEquals( 2, getMessage().getOperationCount() );
  }

  @Test
  public void testGetRequestCounter() {
    new Display();
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 1, Fixture.getProtocolMessage().getRequestCounter() );
  }

  @Test
  public void testGetOperation() {
    writer.appendCall( "w2", "method", null );

    assertNotNull( getMessage().getOperation( 0 ) );
  }

  @Test
  public void testGetCreateOperation() {
    writer.appendCreate( "w1", "type" );

    assertTrue( getMessage().getOperation( 0 ) instanceof CreateOperation );
  }

  @Test
  public void testGetCallOperation() {
    writer.appendCall( "w2", "method", null );

    assertTrue( getMessage().getOperation( 0 ) instanceof CallOperation );
  }

  @Test
  public void testGetSetOperation() {
    writer.appendSet( "w1", "key", true );

    assertTrue( getMessage().getOperation( 0 ) instanceof SetOperation );
  }

  @Test
  public void testGetListenOperation() {
    writer.appendListen( "w1", "event", true );

    assertTrue( getMessage().getOperation( 0 ) instanceof ListenOperation );
  }

  @Test
  public void testGetDestroyOperation() {
    writer.appendDestroy( "w1" );

    assertTrue( getMessage().getOperation( 0 ) instanceof DestroyOperation );
  }

  @Test
  public void testCreateOperation() {
    writer.appendCreate( "w1", "type" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( "w1", operation.getTarget() );
    assertEquals( "type", operation.getType() );
  }

  @Test
  public void testCallOperation() {
    JsonObject parameters = new JsonObject()
      .add( "key1", "a" )
      .add( "key2", 2 );
    writer.appendCall( "w2", "method", parameters );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( "w2", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
    assertEquals( "a", operation.getParameters().get( "key1" ).asString() );
    assertEquals( 2, operation.getParameters().get( "key2" ).asInt() );
  }

  @Test
  public void testSetOperation() {
    writer.appendSet( "w1", "key", true );
    writer.appendSet( "w1", "key2", "value" );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( "w1", operation.getTarget() );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
    assertEquals( JsonValue.valueOf( "value" ), operation.getProperties().get( "key2" ) );
  }

  @Test
  public void testListenOperation() {
    writer.appendListen( "w1", "event", true );
    writer.appendListen( "w1", "event2", false );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "event" ) );
    assertEquals( JsonValue.FALSE, operation.getProperties().get( "event2" ) );
  }

  @Test
  public void testFindSetOperation() {
    writer.appendSet( "w1", "key", true );

    TestMessage message = getMessage();

    SetOperation operation = message.findSetOperation( "w1", "key" );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
  }

  @Test
  public void testFindSetOperationFailed() {
    writer.appendSet( "w1", "key1", true );

    TestMessage message = getMessage();

    assertNull( message.findSetOperation( "w1", "key2" ) );
    assertNull( message.findSetOperation( "w2", "key1" ) );
  }

  @Test
  public void testFindSetProperty() {
    writer.appendSet( "w1", "key", true );

    TestMessage message = getMessage();

    assertEquals( JsonValue.TRUE, message.findSetProperty( "w1", "key" ) );
  }

  @Test
  public void testFindSetPropertyFailed() {
    writer.appendSet( "w1", "key1", true );

    TestMessage message = getMessage();

    try {
      message.findSetProperty( "w1", "key2" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
    try {
      message.findSetProperty( "w2", "key1" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
  }

  @Test
  public void testFindListenOperation() {
    writer.appendListen( "w1", "key", true );

    TestMessage message = getMessage();

    ListenOperation operation = message.findListenOperation( "w1", "key" );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
  }

  @Test
  public void testFindListenOperationFailed() {
    writer.appendListen( "w1", "key1", true );

    TestMessage message = getMessage();

    assertNull( message.findListenOperation( "w1", "key2" ) );
    assertNull( message.findListenOperation( "w2", "key1" ) );
  }

  @Test
  public void testFindListenProperty() {
    writer.appendListen( "w1", "key", true );

    TestMessage message = getMessage();

    assertEquals( JsonValue.TRUE, message.findListenProperty( "w1", "key" ) );
  }

  @Test
  public void testFindListenPropertyFailed() {
    writer.appendListen( "w1", "key1", true );

    TestMessage message = getMessage();

    try {
      message.findListenProperty( "w1", "key2" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
    try {
      message.findListenProperty( "w2", "key1" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
  }

  @Test
  public void testFindCreateOperation() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key", true );

    TestMessage message = getMessage();

    CreateOperation operation = message.findCreateOperation( "w2" );
    assertEquals( "w2", operation.getTarget() );
    assertEquals( "myType", operation.getType() );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
  }

  @Test
  public void testFindCreateFailed() {
    writer.appendCreate( "w2", "myType" );

    TestMessage message = getMessage();

    assertNull( message.findCreateOperation( "w1" ) );
  }

  @Test
  public void testFindCreateProperty() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key", true );

    TestMessage message = getMessage();

    assertEquals( JsonValue.TRUE, message.findCreateProperty( "w2", "key" ) );
  }

  @Test
  public void testFindCreatePropertyFailed() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key1", true );

    TestMessage message = getMessage();

    try {
      message.findCreateProperty( "w1", "key1" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
    try {
      message.findCreateProperty( "w2", "key2" );
      fail();
    } catch( IllegalStateException exception ) {
      //expected
    }
  }

  @Test
  public void testFindCallOperation() {
    writer.appendCall( "w1", "method", null );

    TestMessage message = getMessage();

    CallOperation operation = message.findCallOperation( "w1", "method" );
    assertEquals( "w1", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
  }

  @Test
  public void testFindCallOperationFailed() {
    writer.appendCall( "w2", "method1", null );
    writer.appendCall( "w1", "method2", null );

    TestMessage message = getMessage();

    assertNull( message.findCallOperation( "w1", "method1" ) );
  }

  @Test
  public void testOperationGetProperty() {
    writer.appendSet( "w1", "foo", 23 );
    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );

    assertEquals( 23, operation.getProperties().get( "foo" ).asInt() );
  }

  @Test
  public void testGetError() {
    writer.appendHead( "error", JsonValue.valueOf( "test error" ) );

    assertEquals( "test error", getMessage().getError() );
  }

  @Test
  public void testGetNoError() {
    try {
      getMessage().getError();
      fail();
    } catch ( RuntimeException expected ) {
    }
  }

  @Test
  public void testGetErrorMessage() {
    writer.appendHead( "message", JsonValue.valueOf( "test message" ) );

    assertEquals( "test message", getMessage().getErrorMessage() );
  }

  @Test
  public void testGetNoErrorMessage() {
    try {
      getMessage().getErrorMessage();
      fail();
    } catch ( RuntimeException expected ) {
    }
  }

  @Test
  public void testEquals() {
    JsonObject json = new JsonObject().add( "operations", new JsonArray() );

    TestMessage message1 = new TestMessage( json );
    TestMessage message2 = new TestMessage( json );

    assertEquals( message1, message2 );
  }

  @Test
  public void testEquals_withDifferentObjects() {
    JsonObject json1 = new JsonObject().add( "operations", new JsonArray() );
    JsonObject json2 = new JsonObject()
      .add( "head", new JsonObject() )
      .add( "operations", new JsonArray() );

    TestMessage message1 = new TestMessage( json1 );
    TestMessage message2 = new TestMessage( json2 );

    assertFalse( message1.equals( message2 ) );
  }

  @Test
  public void testGetParent() {
    CreateOperation operation = mock( CreateOperation.class );
    when( operation.getProperties() ).thenReturn( new JsonObject().add( "parent", "w3" ) );

    String parent = TestMessage.getParent( operation );

    assertEquals( "w3", parent );
  }

  @Test
  public void testGetParent_withoutParentProperty() {
    CreateOperation operation = mock( CreateOperation.class );
    when( operation.getProperties() ).thenReturn( new JsonObject() );

    String parent = TestMessage.getParent( operation );

    assertNull( parent );
  }

  @Test
  public void testGetStyles() {
    CreateOperation operation = mock( CreateOperation.class );
    JsonArray stylesJson = new JsonArray().add( "FOO" ).add( "BAR" );
    when( operation.getProperties() ).thenReturn( new JsonObject().add( "style", stylesJson ) );

    List<String> styles = TestMessage.getStyles( operation );

    assertEquals( asList( "FOO", "BAR" ), styles );
  }

  @Test
  public void testGetStyles_withoutStylesProperty() {
    CreateOperation operation = mock( CreateOperation.class );
    when( operation.getProperties() ).thenReturn( new JsonObject().add( "style", new JsonArray() ) );

    List<String> styles = TestMessage.getStyles( operation );

    assertEquals( emptyList(), styles );
  }

  private TestMessage getMessage() {
    return new TestMessage( writer.createMessage() );
  }

}
