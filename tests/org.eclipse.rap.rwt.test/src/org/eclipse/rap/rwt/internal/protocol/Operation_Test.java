/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.testfixture.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.ListenOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.junit.Test;


public class Operation_Test {

  @Test
  public void testCreateOperation() {
    CreateOperation operation = new CreateOperation( "target", "type" );

    assertEquals( "target", operation.getTarget() );
    assertEquals( JsonArray.readFrom( "[\"create\", \"target\", \"type\", {}]" ),
                  operation.toJson() );
  }

  @Test
  public void testCreateOperation_putProperty() {
    CreateOperation operation = new CreateOperation( "target", "type" );

    operation.putProperty( "prop", JsonValue.TRUE );

    assertEquals( JsonArray.readFrom( "[\"create\", \"target\", \"type\", {\"prop\": true}]" ),
                  operation.toJson() );
  }

  @Test
  public void testCreateOperation_putProperty_overwrites() {
    CreateOperation operation = new CreateOperation( "target", "type" );

    operation.putProperty( "prop", JsonValue.TRUE );
    operation.putProperty( "prop", JsonValue.FALSE );

    assertEquals( JsonArray.readFrom( "[\"create\", \"target\", \"type\", {\"prop\": false}]" ),
                  operation.toJson() );
  }

  @Test
  public void testDestroyOperation() {
    DestroyOperation operation = new DestroyOperation( "target" );

    assertEquals( "target", operation.getTarget() );
    assertEquals( JsonArray.readFrom( "[\"destroy\", \"target\"]" ),
                  operation.toJson() );
  }

  @Test
  public void testSetOperation() {
    SetOperation operation = new SetOperation( "target" );

    assertEquals( "target", operation.getTarget() );
    assertEquals( new JsonObject(), operation.getProperties() );
    assertEquals( JsonArray.readFrom( "[\"set\", \"target\", {}]" ),
                  operation.toJson() );
  }

  @Test
  public void testSetOperation_putProperty() {
    SetOperation operation = new SetOperation( "target" );

    operation.putProperty( "prop", JsonValue.TRUE );

    assertEquals( new JsonObject().add( "prop",  true ), operation.getProperties() );
    assertEquals( JsonArray.readFrom( "[\"set\", \"target\", {\"prop\": true}]" ),
                  operation.toJson() );
  }

  @Test
  public void testSetOperation_putProperty_overwrites() {
    SetOperation operation = new SetOperation( "target" );

    operation.putProperty( "prop", JsonValue.TRUE );
    operation.putProperty( "prop", JsonValue.FALSE );

    assertEquals( new JsonObject().add( "prop",  false ), operation.getProperties() );
  }

  @Test
  public void testCallOperation() {
    CallOperation operation = new CallOperation( "target", "method", null );

    assertEquals( "target", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
    assertEquals( new JsonObject(), operation.getParameters() );
    assertEquals( JsonArray.readFrom( "[\"call\", \"target\", \"method\", {}]" ),
                  operation.toJson() );
  }

  @Test
  public void testCallOperation_withParameters() {
    JsonObject parameters = new JsonObject().add( "param", 23 );
    CallOperation operation = new CallOperation( "target", "method", parameters );

    assertEquals( parameters, operation.getParameters() );
    assertEquals( JsonArray.readFrom( "[\"call\", \"target\", \"method\", {\"param\": 23}]" ),
                  operation.toJson() );
  }

  @Test
  public void testListenOperation() {
    ListenOperation operation = new ListenOperation( "target" );

    assertEquals( "target", operation.getTarget() );
    assertEquals( JsonArray.readFrom( "[\"listen\", \"target\", {}]" ),
                  operation.toJson() );
  }

  @Test
  public void testListenOperation_putListener() {
    ListenOperation operation = new ListenOperation( "target" );

    operation.putListener( "event", true );

    assertEquals( JsonArray.readFrom( "[\"listen\", \"target\", {\"event\": true}]" ),
                  operation.toJson() );
  }

  @Test
  public void testListenOperation_putListener_overwrites() {
    ListenOperation operation = new ListenOperation( "target" );

    operation.putListener( "event", true );
    operation.putListener( "event", false );

    assertEquals( JsonArray.readFrom( "[\"listen\", \"target\", {\"event\": false}]" ),
                  operation.toJson() );
  }

  @Test
  public void testNotifyOperation() {
    NotifyOperation operation = new NotifyOperation( "target", "event" );

    assertEquals( "target", operation.getTarget() );
    assertEquals( "event", operation.getEventName() );
    assertEquals( new JsonObject(), operation.getProperties() );
    assertEquals( JsonArray.readFrom( "[\"notify\", \"target\", \"event\", {}]" ),
                  operation.toJson() );
  }

  @Test
  public void testNotifyOperation_putListener() {
    NotifyOperation operation = new NotifyOperation( "target", "event" );

    operation.putProperty( "prop", JsonValue.TRUE );

    assertEquals( new JsonObject().add( "prop", true ), operation.getProperties() );
    assertEquals( JsonArray.readFrom( "[\"notify\", \"target\", \"event\", {\"prop\": true}]" ),
                  operation.toJson() );
  }

  @Test
  public void testNotifyOperation_putListener_overwrites() {
    NotifyOperation operation = new NotifyOperation( "target", "event" );

    operation.putProperty( "prop", JsonValue.TRUE );
    operation.putProperty( "prop", JsonValue.FALSE );

    assertEquals( new JsonObject().add( "prop", false ), operation.getProperties() );
  }

  @Test
  public void testOperationsAreSerializable() throws Exception {
    SetOperation operation = new SetOperation( "target", new JsonObject().add( "foo", 23 ) );

    SetOperation deserialized = serializeAndDeserialize( operation );

    assertEquals( operation.toJson(), deserialized.toJson() );
  }

}
