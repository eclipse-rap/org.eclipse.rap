/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.junit.Test;


public class ClientMessage_Test {

  @Test
  public void testConstructor_withNull() {
    try {
      new ClientMessage( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testConstructor_withEmptyObject() {
    try {
      new ClientMessage( new JsonObject() );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().startsWith( "Missing header object" ) );
    }
  }

  @Test
  public void testConstructor_withoutOperations() {
    try {
      new ClientMessage( new JsonObject().add( "head", new JsonObject() ) );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testConstructor_withInvalidOperations() {
    String json = "{ \"head\" : {}, \"operations\" : 23 }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testConstructor_withOperationOfUnknownType() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"abc\", \"w3\", { \"action\" : \"foo\" } ]"
                + "] }";
    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Unknown operation action: abc" ) );
    }
  }

  @Test
  public void testGetHeader() {
    String json = "{ \"head\": { \"abc\" : \"foo\" }, \"operations\": [] }";

    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    assertEquals( JsonValue.valueOf( "foo" ), message.getHeader( "abc" ) );
  }

  @Test
  public void testGetHeader_withEmptyMessage() {
    String json = "{ \"head\": {}, \"operations\": [] }";

    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    assertEquals( null, message.getHeader( "abc" ) );
  }

  @Test
  public void testGetAllOperations() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperations();

    assertEquals( 3, operations.size() );
  }

  @Test
  public void testGetAllOperations_withEmptyMessage() {
    String json = "{ \"head\" : {}, \"operations\" : [] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperations();

    assertTrue( operations.isEmpty() );
  }

  @Test
  public void testGetAllOperationsFor() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<Operation> operations = message.getAllOperationsFor( "w3" );

    assertEquals( 2, operations.size() );
    assertTrue( operations.get( 0 ) instanceof SetOperation );
    assertTrue( operations.get( 1 ) instanceof NotifyOperation );
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
  public void testGetLastSetOperation_ByProperty() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p1\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "p1" );

    assertEquals( "bar", operation.getProperty( "p1" ).asString() );
  }

  @Test
  public void testGetLastNotifyOperation() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( "w3", "widgetSelected" );

    assertNotNull( operation );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertNull( operation.getProperty( "detail" ) );
  }

  @Test
  public void testGetLastNotifyOperation_WithoutTarget() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w4\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, "widgetSelected" );
    assertNotNull( operation );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertEquals( "w3", operation.getTarget() );
  }

  @Test
  public void testGetLastNotifyOperation_WithoutTargetAndName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w4\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetDefaultSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, null );
    assertNotNull( operation );
    assertEquals( "widgetDefaultSelected", operation.getEventName() );
    assertEquals( "w3", operation.getTarget() );
  }

  @Test
  public void testGetAllCallOperations() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"call\", \"w2\", \"store\", {} ],"
                + "[ \"call\", \"w3\", \"foo\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( null, null );

    assertEquals( 2, operations.size() );
    assertEquals( "store", operations.get( 0 ).getMethodName() );
    assertEquals( "foo", operations.get( 1 ).getMethodName() );
  }

  @Test
  public void testGetAllCallOperations_ByTarget() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"call\", \"w3\", \"store\", {} ],"
                + "[ \"call\", \"w4\", \"foo\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( "w3", null );

    assertEquals( 1, operations.size() );
    assertEquals( "store", operations.get( 0 ).getMethodName() );
  }

  @Test
  public void testGetAllCallOperations_ByTargetAnMethodName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"call\", \"w3\", \"store\", {} ],"
                + "[ \"call\", \"w4\", \"foo\", { \"p1\" : \"abc\" } ],"
                + "[ \"call\", \"w4\", \"foo\", { \"p2\" : \"def\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    List<CallOperation> operations = message.getAllCallOperationsFor( "w4", "foo" );

    assertEquals( 2, operations.size() );
    assertEquals( "abc", operations.get( 0 ).getProperty( "p1" ).asString() );
    assertEquals( "def", operations.get( 1 ).getProperty( "p2" ).asString() );
  }

  @Test
  public void testSetOperation_WithoutTarget() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", { \"p1\" : \"foo\", \"p2\" : true } ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetOperation_WithoutProperties() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\" ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testNotifyOperation_WithoutEventType() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w3\", { \"check\" : true } ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testNotifyOperation_WithoutProperties() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"notify\", \"w3\", \"widgetSelected\" ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testCallOperation() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"call\", \"w3\", \"store\", { \"id\" : 123 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    CallOperation operation = message.getAllCallOperationsFor( "w3", null ).get( 0 );

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "store", operation.getMethodName() );
    assertEquals( 123, operation.getProperty( "id" ).asInt() );
  }

  @Test
  public void testCallOperation_WithoutMethodName() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"call\", \"w3\", { \"id\" : 123 } ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testCallOperation_WithoutProperties() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"call\", \"w3\", \"store\" ]"
                + "] }";

    try {
      new ClientMessage( JsonObject.readFrom( json ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testOperationGetPropertyAsArray() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"result\" : [1,2] } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    JsonArray expected = new JsonArray().add( 1 ).add( 2 );
    assertEquals( expected, operation.getProperty( "result" ) );
  }

  @Test
  public void testOperationGetProperty_MixedArray() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"result\" : [1,\"foo\",3,4] } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    JsonArray expected = new JsonArray().add( 1 ).add( "foo" ).add( 3 ).add( 4 );
    assertEquals( expected, operation.getProperty( "result" ) );
  }

  @Test
  public void testOperationGetPropertyAsMap() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"result\" : { \"p1\" : \"foo\", \"p2\" : \"bar\" } } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    JsonObject value = operation.getProperty( "result" ).asObject();
    assertEquals( "foo", value.get( "p1" ).asString() );
    assertEquals( "bar", value.get( "p2" ).asString() );
  }

  @Test
  public void testOperationGetPropertyAsMap_WithArray() {
    String json = "{ \"head\" : {}, \"operations\" : ["
                + "[ \"set\", \"w3\", { \"result\" : { \"p1\" : [1,2], \"p2\" : \"bar\" } } ]"
                + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    JsonObject value = operation.getProperty( "result" ).asObject();
    assertEquals( new JsonArray().add( 1 ).add( 2 ), value.get( "p1" ) );
    assertEquals( "bar", value.get( "p2" ).asString() );
  }

  @Test
  public void testOperationGetPropertyNames() {
    Operation operation = createOperation( "[ \"set\", \"w3\", { \"foo\" : 23, \"bar\" : 42 } ]" );

    List<String> names = operation.getPropertyNames();

    assertEquals( 2, names.size() );
    assertTrue( names.contains( "foo" ) );
    assertTrue( names.contains( "bar" ) );
  }

  @Test
  public void testOperationGetPropertyNamesWhenEmtpy() {
    Operation operation = createOperation( "[ \"set\", \"w3\", {} ]" );

    List<String> names = operation.getPropertyNames();

    assertTrue( names.isEmpty() );
  }

  private static Operation createOperation( String operationJson ) {
    String json = "{ \"head\" : {}, \"operations\" : [" + operationJson + "] }";
    ClientMessage message = new ClientMessage( JsonObject.readFrom( json ) );
    return message.getAllOperations().get( 0 );
  }

}
