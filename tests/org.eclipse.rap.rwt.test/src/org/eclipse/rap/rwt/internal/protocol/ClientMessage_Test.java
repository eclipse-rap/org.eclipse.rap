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

import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.json.JSONObject;

import junit.framework.TestCase;


public class ClientMessage_Test extends TestCase {

  public void testConstructWithNull() {
    try {
      new ClientMessage( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testConstructWithEmptyString() {
    try {
      new ClientMessage( "" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Could not parse json" ) );
    }
  }

  public void testConstructWithInvalidJson() {
    try {
      new ClientMessage( "{" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Could not parse json" ) );
    }
  }

  public void testConstructWithoutHeader() {
    try {
      new ClientMessage( "{ \"foo\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing header object" ) );
    }
  }

  public void testConstructWithoutOperations() {
    try {
      new ClientMessage( "{ " + ClientMessage.PROP_HEADER + " : {}, \"foo\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testGetHeaderParameter() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : { \"abc\" : \"foo\" },"
                + ClientMessage.PROP_OPERATIONS + " : [] }";
    ClientMessage message = new ClientMessage( json );

    assertEquals( "foo", message.getHeaderProperty( "abc" ) );
  }

  public void testGetHeaderParameter_NoParameter() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : [] }";
    ClientMessage message = new ClientMessage( json );

    assertNull( message.getHeaderProperty( "abc" ) );
  }

  public void testConstructWithInvalidOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : 23 }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testConstructWithOperationUnknownType() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
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

  public void testGetAllOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
    		    + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    Operation[] operations = message.getAllOperations( "w3" );

    assertEquals( 2, operations.length );
    assertTrue( operations[ 0 ] instanceof SetOperation );
    assertTrue( operations[ 1 ] instanceof NotifyOperation );
  }

  public void testGetAllOperations_NoOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    Operation[] operations = message.getAllOperations( "w5" );

    assertEquals( 0, operations.length );
  }

  public void testGetSetOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w3\", { \"p2\" : true, \"p3\" : null } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation[] operations = message.getSetOperations( "w3" );

    assertEquals( 2, operations.length );
    assertEquals( "foo", operations[ 0 ].getProperty( "p1" ) );
    assertEquals( Boolean.TRUE, operations[ 1 ].getProperty( "p2" ) );
    assertEquals( JSONObject.NULL, operations[ 1 ].getProperty( "p3" ) );
  }

  public void testGetSetOperations_ByProperty() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"foo\" } ],"
                + "[ \"set\", \"w3\", { \"p1\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation[] operations = message.getSetOperations( "w3", "p1" );

    assertEquals( 2, operations.length );
    assertEquals( "foo", operations[ 0 ].getProperty( "p1" ) );
    assertEquals( "bar", operations[ 1 ].getProperty( "p1" ) );
  }

  public void testGetNotifyOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", {} ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation[] operations = message.getNotifyOperations( "w3" );

    assertEquals( 1, operations.length );
    assertEquals( "widgetSelected", operations[ 0 ].getEventName() );
  }

  public void testGetNotifyOperations_ByEventName() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", "widgetSelected", null );

    assertEquals( 1, operations.length );
    assertEquals( "widgetSelected", operations[ 0 ].getEventName() );
  }

  public void testGetNotifyOperations_ByProperty() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"notify\", \"w3\", \"widgetDefaultSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", null, "detail" );

    assertEquals( 2, operations.length );
  }

  public void testGetNotifyOperations_ByEventNameAndProperty() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"notify\", \"w3\", \"widgetDefaultSelected\", { \"detail\" : \"check\" } ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", "widgetSelected", "detail" );

    assertEquals( 1, operations.length );
  }

  public void testGetCallOperations() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ],"
                + "[ \"call\", \"w3\", \"store\", {} ],"
                + "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation[] operations = message.getCallOperations( "w3" );

    assertEquals( 1, operations.length );
    assertEquals( "store", operations[ 0 ].getMethodName() );
  }

  public void testSetOperation() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\", \"p2\" : true } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    SetOperation operation = message.getSetOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "foo", operation.getProperty( "p1" ) );
    assertEquals( Boolean.TRUE, operation.getProperty( "p2" ) );
  }

  public void testSetOperation_WithoutTarget() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", { \"p1\" : \"foo\", \"p2\" : true } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\" ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetOperation_InvalidProperty() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"set\", \"w3\", { \"p1\" : \"foo\", \"p2\" : true } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );
    SetOperation operation = message.getSetOperations( "w3" )[ 0 ];

    assertNull( operation.getProperty( "abc" ) );
  }

  public void testNotifyOperation() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"notify\", \"w3\", \"widgetSelected\", { \"check\" : true } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    NotifyOperation operation = message.getNotifyOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertEquals( Boolean.TRUE, operation.getProperty( "check" ) );
  }

  public void testNotifyOperation_WithoutEventType() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"notify\", \"w3\", { \"check\" : true } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testNotifyOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"notify\", \"w3\", \"widgetSelected\""
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCallOperation() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", \"store\", { \"id\" : 123 } ]"
                + "] }";
    ClientMessage message = new ClientMessage( json );

    CallOperation operation = message.getCallOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "store", operation.getMethodName() );
    assertEquals( Integer.valueOf( 123 ), operation.getProperty( "id" ) );
  }

  public void testCallOperation_WithoutMethodName() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", { \"id\" : 123 } ]"
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCallOperation_WithoutProperties() {
    String json = "{ "
                + ClientMessage.PROP_HEADER + " : {},"
                + ClientMessage.PROP_OPERATIONS + " : ["
                + "[ \"call\", \"w3\", \"store\""
                + "] }";

    try {
      new ClientMessage( json );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

}
