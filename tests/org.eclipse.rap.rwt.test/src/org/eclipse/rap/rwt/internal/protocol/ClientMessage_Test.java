/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.junit.Test;


public class ClientMessage_Test {

  @Test
  public void testConstructWithNull() {
    try {
      new ClientMessage( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testConstructWithEmptyString() {
    try {
      new ClientMessage( "" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Could not parse json" ) );
    }
  }

  @Test
  public void testConstructWithInvalidJson() {
    try {
      new ClientMessage( "{" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Could not parse json" ) );
    }
  }

  @Test
  public void testConstructWithoutHeader() {
    try {
      new ClientMessage( "{ \"foo\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing header object" ) );
    }
  }

  @Test
  public void testConstructWithoutOperations() {
    try {
      new ClientMessage( "{ " + ClientMessage.PROP_HEAD + " : {}, \"foo\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testGetHeaderParameter() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : { \"abc\" : \"foo\" },"
                + ClientMessage.PROP_OPERATIONS + " : [] }";
    ClientMessage message = new ClientMessage( json );

    assertEquals( "foo", message.getHeadProperty( "abc" ) );
  }

  @Test
  public void testGetHeaderParameter_NoParameter() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : [] }";
    ClientMessage message = new ClientMessage( json );

    assertNull( message.getHeadProperty( "abc" ) );
  }

  @Test
  public void testConstructWithInvalidOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : 23 }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  @Test
  public void testConstructWithOperationUnknownType() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"abc\", \"w3\", { \"action\" : \"foo\" } ]"
                + "] }";
    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Unknown operation action: abc" ) );
    }
  }

  @Test
  public void testGetAllOperationsFor() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
    		    + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    Operation[] operations = message.getAllOperationsFor( "w3" );

    assertEquals( 2, operations.length );
    assertTrue( operations[ 0 ] instanceof SetOperation );
    assertTrue( operations[ 1 ] instanceof NotifyOperation );
  }

  @Test
  public void testGetAllOperationsFor_NoOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    Operation[] operations = message.getAllOperationsFor( "w5" );

    assertEquals( 0, operations.length );
  }

  @Test
  public void testGetAllOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    Operation[] operations = message.getAllOperations();

    assertEquals( 3, operations.length );
  }

  @Test
  public void testGetLastSetOperation_ByProperty() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p1\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getLastSetOperationFor( "w3", "p1" );

    assertEquals( "bar", operation.getProperty( "p1" ) );
  }

  @Test
  public void testGetLastNotifyOperation() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation operation = message.getLastNotifyOperationFor( "w3", "widgetSelected" );

    assertNotNull( operation );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertNull( operation.getProperty( "detail" ) );
  }

  @Test
  public void testGetLastNotifyOperation_WithoutTarget() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"notify\", \"w4\", \"widgetSelected\", {} ],"
        + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
        + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, "widgetSelected" );
    assertNotNull( operation );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertEquals( "w3", operation.getTarget() );
  }

  @Test
  public void testGetLastNotifyOperation_WithoutTargetAndName() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"notify\", \"w4\", \"widgetSelected\", {} ],"
        + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
        + "[ \"notify\", \"w3\", \"widgetDefaultSelected\", {} ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation operation = message.getLastNotifyOperationFor( null, null );
    assertNotNull( operation );
    assertEquals( "widgetDefaultSelected", operation.getEventName() );
    assertEquals( "w3", operation.getTarget() );
  }

  @Test
  public void testGetAllCallOperations() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
        + "[ \"call\", \"w2\", \"store\", {} ],"
        + "[ \"call\", \"w3\", \"foo\", {} ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation[] operations = message.getAllCallOperationsFor( null, null );

    assertEquals( 2, operations.length );
    assertEquals( "store", operations[ 0 ].getMethodName() );
    assertEquals( "foo", operations[ 1 ].getMethodName() );
  }

  @Test
  public void testGetAllCallOperations_ByTarget() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"call\", \"w3\", \"store\", {} ],"
                + "[ \"call\", \"w4\", \"foo\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation[] operations = message.getAllCallOperationsFor( "w3", null );

    assertEquals( 1, operations.length );
    assertEquals( "store", operations[ 0 ].getMethodName() );
  }

  @Test
  public void testGetAllCallOperations_ByTargetAnMethodName() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
        + "[ \"call\", \"w3\", \"store\", {} ],"
        + "[ \"call\", \"w4\", \"foo\", { \"p1\" : \"abc\" } ],"
        + "[ \"call\", \"w4\", \"foo\", { \"p2\" : \"def\" } ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation[] operations = message.getAllCallOperationsFor( "w4", "foo" );

    assertEquals( 2, operations.length );
    assertEquals( "abc", operations[ 0 ].getProperty( "p1" ) );
    assertEquals( "def", operations[ 1 ].getProperty( "p2" ) );
  }

  @Test
  public void testSetOperation_WithoutTarget() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", { \"p1\" : \"foo\", \"p2\" : true } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\" ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testNotifyOperation_WithoutEventType() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"notify\", \"w3\", { \"check\" : true } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testNotifyOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"notify\", \"w3\", \"widgetSelected\""
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testCallOperation() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", \"store\", { \"id\" : 123 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation operation = message.getAllCallOperationsFor( "w3", null )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "store", operation.getMethodName() );
    assertEquals( Integer.valueOf( 123 ), operation.getProperty( "id" ) );
  }

  @Test
  public void testCallOperation_WithoutMethodName() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", { \"id\" : 123 } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testCallOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", \"store\""
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testOperationGetPropertyAsArray() {
    String json = "{ "
                + ClientMessage.PROP_HEAD + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"result\" : [1,2] } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    Integer[] extected = new Integer[] { Integer.valueOf( 1 ),  Integer.valueOf( 2 ) };
    assertTrue( Arrays.equals( extected, ( Object[] )operation.getProperty( "result" ) ) );
  }

  @Test
  public void testOperationGetProperty_MixedArray() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"set\", \"w3\", { \"result\" : [1,\"foo\",3,4] } ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    Object[] extected = new Object[] {
      Integer.valueOf( 1 ),
      "foo",
      Integer.valueOf( 3 ),
      Integer.valueOf( 4 ) };
    assertTrue( Arrays.equals( extected, ( Object[] )operation.getProperty( "result" ) ) );
  }

  @Test
  public void testOperationGetPropertyAsMap() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"set\", \"w3\", { \"result\" : { \"p1\" : \"foo\", \"p2\" : \"bar\" } } ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    Object value = operation.getProperty( "result" );
    assertTrue( value instanceof Map );
    Map map = ( Map )value;
    assertEquals( "foo", map.get( "p1" ) );
    assertEquals( "bar", map.get( "p2" ) );
  }

  @Test
  public void testOperationGetPropertyAsMap_WithArray() {
    String json = "{ "
        + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : ["
        + "[ \"set\", \"w3\", { \"result\" : { \"p1\" : [1,2], \"p2\" : \"bar\" } } ]"
        + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getLastSetOperationFor( "w3", "result" );

    Object value = operation.getProperty( "result" );
    assertTrue( value instanceof Map );
    Map map = ( Map )value;
    Integer[] extected = new Integer[] { Integer.valueOf( 1 ),  Integer.valueOf( 2 ) };
    assertTrue( Arrays.equals( extected, ( Object[] )map.get( "p1" ) ) );
    assertEquals( "bar", map.get( "p2" ) );
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
    String json = "{ " + ClientMessage.PROP_HEAD + " : {},"
        + ClientMessage.PROP_OPERATIONS + " : [" + operationJson + "] }";
    ClientMessage message = new ClientMessage( json );
    return message.getAllOperations()[ 0 ];
  }

}
