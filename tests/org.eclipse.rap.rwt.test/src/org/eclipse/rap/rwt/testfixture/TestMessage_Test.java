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
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.ListenOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.junit.Before;
import org.junit.Test;


public class TestMessage_Test {

  private ProtocolMessageWriter writer;

  @Before
  public void setUp() {
    writer = new ProtocolMessageWriter();
  }

  @Test( expected = NullPointerException.class )
  public void testConstructor_withNull() {
    new TestMessage( (JsonObject)null );
  }

  @Test
  public void testGetOperationCount() {
    writer.appendCall( "w1", "method1", null );
    writer.appendCall( "w2", "method2", null );

    assertEquals( 2, getMessage().getOperationCount() );
  }

  @Test
  public void testGetOperationCount_whenEmpty() {
    assertEquals( 0, getMessage().getOperationCount() );
  }

  @Test
  public void testGetRequestCounter() {
    String json = "{ \"head\" : { \"requestCounter\" : 23 }, \"operations\" : [] }";
    TestMessage message = new TestMessage( JsonObject.readFrom( json ) );

    int requestCounter = message.getRequestCounter();

    assertEquals( 23, requestCounter );
  }

  @Test
  public void testGetOperation() {
    writer.appendCreate( "w1", "type" );
    writer.appendCreate( "w2", "type" );
    writer.appendCreate( "w3", "type" );

    Operation operation = getMessage().getOperation( 1 );

    assertEquals( "w2", operation.getTarget() );
  }

  @Test
  public void testFindSetOperation() {
    writer.appendSet( "w1", "key", true );
    TestMessage message = getMessage();

    SetOperation operation = message.findSetOperation( "w1", "key" );

    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
  }

  @Test
  public void testFindSetOperation_withoutMatch() {
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

  @Test( expected = IllegalStateException.class )
  public void testFindSetProperty_withoutTargetMatch() {
    writer.appendSet( "w1", "key1", true );
    TestMessage message = getMessage();

    message.findSetProperty( "w2", "key1" );
  }

  @Test( expected = IllegalStateException.class )
  public void testFindSetProperty_withoutPropertyMatch() {
    writer.appendSet( "w1", "key1", true );
    TestMessage message = getMessage();

    message.findSetProperty( "w1", "key2" );
  }

  @Test
  public void testFindListenOperation() {
    writer.appendListen( "w1", "key", true );
    TestMessage message = getMessage();

    ListenOperation operation = message.findListenOperation( "w1", "key" );

    assertEquals( JsonValue.TRUE, operation.getProperties().get( "key" ) );
  }

  @Test
  public void testFindListenOperation_wihoutMatch() {
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

  @Test( expected = IllegalStateException.class )
  public void testFindListenProperty_withoutPropertyMatch() {
    writer.appendListen( "w1", "key1", true );
    TestMessage message = getMessage();

    message.findListenProperty( "w1", "key2" );
  }

  @Test( expected = IllegalStateException.class )
  public void testFindListenProperty_withoutTargetMatch() {
    writer.appendListen( "w1", "key1", true );
    TestMessage message = getMessage();

    message.findListenProperty( "w2", "key1" );
  }

  @Test
  public void testFindCreateOperation() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key", true );
    TestMessage message = getMessage();

    CreateOperation operation = message.findCreateOperation( "w2" );

    assertNotNull( operation );
  }

  @Test
  public void testFindCreate_withoutMatch() {
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

  @Test( expected = IllegalStateException.class )
  public void testFindCreateProperty_withoutTargetMatch() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key1", true );
    TestMessage message = getMessage();

    message.findCreateProperty( "w1", "key1" );
  }

  @Test( expected = IllegalStateException.class )
  public void testFindCreateProperty_withoutPropertyMatch() {
    writer.appendCreate( "w2", "myType" );
    writer.appendSet( "w2", "key1", true );
    TestMessage message = getMessage();

    message.findCreateProperty( "w2", "key2" );
  }

  @Test
  public void testFindCallOperation() {
    writer.appendCall( "w1", "method", null );
    TestMessage message = getMessage();

    CallOperation operation = message.findCallOperation( "w1", "method" );

    assertNotNull( operation );
  }

  @Test
  public void testFindCallOperation_withoutMatch() {
    writer.appendCall( "w2", "method1", null );
    writer.appendCall( "w1", "method2", null );
    TestMessage message = getMessage();

    assertNull( message.findCallOperation( "w1", "method1" ) );
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
