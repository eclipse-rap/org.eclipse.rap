/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.junit.Test;


public class ClientMessage_Test {

  @Test
  public void testConstructor_JsonObject_createsIndex() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"foo\" : 23 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w3" );

    assertFalse( operations.isEmpty() );
  }

  @Test
  public void testConstructor_Message_createsIndex() {
    TestMessage testMessage = new TestMessage();
    testMessage.getOperations().add( new SetOperation( "w3", new JsonObject().add( "foo", 23 ) ) );

    ClientMessage message = new ClientMessage( testMessage );

    assertFalse( message.getAllOperationsFor( "w3" ).isEmpty() );
  }

  @Test
  public void testGetAllOperationsFor_selectsMatchingOperations() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"foo\" : 23 } ],"
                + "[ \"set\", \"w4\", { \"foo\" : 42 } ],"
                + "[ \"notify\", \"w3\", \"event\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w3" );

    assertEquals( 2, operations.size() );
    assertTrue( operations.get( 0 ) instanceof SetOperation );
    assertTrue( operations.get( 1 ) instanceof NotifyOperation );
  }

  @Test
  public void testGetAllOperationsFor_withEmptyMessage() {
    String json = "{ \"head\" : {}, \"operations\" : [] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w3" );

    assertTrue( operations.isEmpty() );
  }

  @Test
  public void testGetAllOperationsFor_withEmtpyMessage() {
    String json = "{ \"head\" : {}, \"operations\" : [] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w5" );

    assertTrue( operations.isEmpty() );
  }

  @Test
  public void testGetAllOperationsFor_withoutMatchingOperations() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w5" );

    assertTrue( operations.isEmpty() );
  }

  @Test
  public void testGetAllCallOperations() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"count\" : 1 } ],"
                + "[ \"call\", \"w3\", \"foo\", { \"count\" : 2 } ]," // <---
                + "[ \"call\", \"w3\", \"bar\", { \"count\" : 3 } ],"
                + "[ \"call\", \"w3\", \"foo\", { \"count\" : 4 } ]," // <---
                + "[ \"call\", \"w3\", \"bar\", { \"count\" : 5 } ],"
                + "[ \"call\", \"w4\", \"foo\", { \"count\" : 6 } ],"
                + "[ \"call\", \"w4\", \"bar\", { \"count\" : 7 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( "w3", "foo" );

    assertEquals( 2, operations.size() );
    assertEquals( 2, operations.get( 0 ).getParameters().get( "count" ).asInt() );
    assertEquals( 4, operations.get( 1 ).getParameters().get( "count" ).asInt() );
  }

  @Test
  public void testGetAllCallOperations_withoutMethodName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"count\" : 1 } ],"
                + "[ \"call\", \"w3\", \"foo\", { \"count\" : 2 } ]," // <---
                + "[ \"call\", \"w3\", \"bar\", { \"count\" : 3 } ]," // <---
                + "[ \"call\", \"w4\", \"foo\", { \"count\" : 4 } ],"
                + "[ \"call\", \"w4\", \"bar\", { \"count\" : 5 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( "w3", null );

    assertEquals( 2, operations.size() );
    assertEquals( 2, operations.get( 0 ).getParameters().get( "count" ).asInt() );
    assertEquals( 3, operations.get( 1 ).getParameters().get( "count" ).asInt() );
  }

  @Test
  public void testGetAllCallOperations_withoutTarget() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"count\" : 1 } ],"
                + "[ \"call\", \"w3\", \"foo\", { \"count\" : 2 } ]," // <---
                + "[ \"call\", \"w3\", \"bar\", { \"count\" : 3 } ],"
                + "[ \"call\", \"w4\", \"foo\", { \"count\" : 4 } ]," // <---
                + "[ \"call\", \"w4\", \"bar\", { \"count\" : 5 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( null, "foo" );

    assertEquals( 2, operations.size() );
    assertEquals( 2, operations.get( 0 ).getParameters().get( "count" ).asInt() );
    assertEquals( 4, operations.get( 1 ).getParameters().get( "count" ).asInt() );
  }

  @Test
  public void testGetAllCallOperations_withoutTargetAndMethodName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"count\" : 1 } ],"
                + "[ \"call\", \"w3\", \"foo\", { \"count\" : 2 } ]," // <---
                + "[ \"call\", \"w4\", \"bar\", { \"count\" : 3 } ]"  // <---
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( null, null );

    assertEquals( 2, operations.size() );
    assertEquals( 2, operations.get( 0 ).getParameters().get( "count" ).asInt() );
    assertEquals( 3, operations.get( 1 ).getParameters().get( "count" ).asInt() );
  }

  @Test
  public void testGetLastSetOperationFor() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"foo\" : 1 } ],"
                + "[ \"set\", \"w3\", { \"bar\" : 2 } ],"
                + "[ \"set\", \"w3\", { \"foo\" : 3 } ]," // <---
                + "[ \"set\", \"w3\", { \"bar\" : 4 } ],"
                + "[ \"set\", \"w4\", { \"foo\" : 5 } ],"
                + "[ \"set\", \"w4\", { \"bar\" : 6 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "foo" );

    assertEquals( 3, operation.getProperties().get( "foo" ).asInt() );
  }

  @Test
  public void testGetLastNotifyOperation() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w3\", \"foo\", { \"count\" : 1 } ],"
                + "[ \"notify\", \"w3\", \"bar\", { \"count\" : 2 } ],"
                + "[ \"notify\", \"w3\", \"foo\", { \"count\" : 3 } ]," // <---
                + "[ \"notify\", \"w3\", \"bar\", { \"count\" : 4 } ],"
                + "[ \"notify\", \"w4\", \"foo\", { \"count\" : 5 } ],"
                + "[ \"notify\", \"w4\", \"bar\", { \"count\" : 6 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( "w3", "foo" );

    assertEquals( 3, operation.getProperties().get( "count" ).asInt() );
  }

  @Test
  public void testGetLastNotifyOperation_withNullTarget() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w3\", \"foo\", {} ],"
                + "[ \"notify\", \"w4\", \"foo\", {} ]," // <---
                + "[ \"notify\", \"w5\", \"bar\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, "foo" );

    assertEquals( "w4", operation.getTarget() );
  }

  @Test
  public void testGetLastNotifyOperation_WithoutTargetAndName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w3\", \"foo\", {} ],"
                + "[ \"notify\", \"w4\", \"bar\", {} ]," // <---
                + "[ \"set\", \"w4\", { \"foo\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, null );

    assertEquals( "bar", operation.getEventName() );
    assertEquals( "w4", operation.getTarget() );
  }

}
