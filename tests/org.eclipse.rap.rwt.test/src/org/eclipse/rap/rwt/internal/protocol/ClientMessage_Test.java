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

  public void testConstructWithoutOperations() {
    try {
      new ClientMessage( "{ \"foo\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testConstructWithInvalidOperations() {
    try {
      new ClientMessage( "{ \"operations\": 23 }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testConstructWithOperationUnknownType() {
    try {
      new ClientMessage( "{ \"operations\" : [ [ \"abc\", \"w3\", { \"action\" : \"foo\" } ] ] }" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Unknown operation action: abc" ) );
    }
  }

  public void testGetAllOperations() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", {} ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    Operation[] operations = message.getAllOperations( "w3" );

    assertEquals( 2, operations.length );
    assertTrue( operations[ 0 ] instanceof SetOperation );
    assertTrue( operations[ 1 ] instanceof NotifyOperation );
  }

  public void testGetAllOperations_NoOperations() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"set\", \"w4\", { \"p2\" : \"bar\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", {} ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    Operation[] operations = message.getAllOperations( "w5" );

    assertEquals( 0, operations.length );
  }

  public void testGetSetOperations() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", {} ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : true, \"p3\" : null } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    SetOperation[] operations = message.getSetOperations( "w3" );

    assertEquals( 2, operations.length );
    assertEquals( "foo", operations[ 0 ].getProperty( "p1" ) );
    assertEquals( Boolean.TRUE, operations[ 1 ].getProperty( "p2" ) );
    assertEquals( JSONObject.NULL, operations[ 1 ].getProperty( "p3" ) );
  }

  public void testGetSetOperations_ByProperty() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"foo\" } ]," );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    SetOperation[] operations = message.getSetOperations( "w3", "p1" );

    assertEquals( 2, operations.length );
    assertEquals( "foo", operations[ 0 ].getProperty( "p1" ) );
    assertEquals( "bar", operations[ 1 ].getProperty( "p1" ) );
  }

  public void testGetNotifyOperations() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", {} ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    NotifyOperation[] operations = message.getNotifyOperations( "w3" );

    assertEquals( 1, operations.length );
    assertEquals( "widgetSelected", operations[ 0 ].getEventName() );
  }

  public void testGetNotifyOperations_ByEventName() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", "widgetSelected", null );

    assertEquals( 1, operations.length );
    assertEquals( "widgetSelected", operations[ 0 ].getEventName() );
  }

  public void testGetNotifyOperations_ByProperty() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetDefaultSelected\", { \"detail\" : \"check\" } ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", null, "detail" );

    assertEquals( 2, operations.length );
  }

  public void testGetNotifyOperations_ByEventNameAndProperty() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", { \"detail\" : \"check\" } ]," );
    json.append( "[ \"notify\", \"w3\", \"widgetDefaultSelected\", { \"detail\" : \"check\" } ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    NotifyOperation[] operations = message.getNotifyOperations( "w3", "widgetSelected", "detail" );

    assertEquals( 1, operations.length );
  }

  public void testGetCallOperations() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\" } ]," );
    json.append( "[ \"call\", \"w3\", \"store\", {} ]," );
    json.append( "[ \"set\", \"w3\", { \"p2\" : \"bar\" } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    CallOperation[] operations = message.getCallOperations( "w3" );

    assertEquals( 1, operations.length );
    assertEquals( "store", operations[ 0 ].getMethodName() );
  }

  public void testSetOperation() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\", \"p2\" : true } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    SetOperation operation = message.getSetOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "foo", operation.getProperty( "p1" ) );
    assertEquals( Boolean.TRUE, operation.getProperty( "p2" ) );
  }

  public void testSetOperation_WithoutTarget() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", { \"p1\" : \"foo\", \"p2\" : true } ]" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetOperation_WithoutProperties() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\" ]" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetOperation_InvalidProperty() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"set\", \"w3\", { \"p1\" : \"foo\", \"p2\" : true } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );
    SetOperation operation = message.getSetOperations( "w3" )[ 0 ];

    assertNull( operation.getProperty( "abc" ) );
  }

  public void testNotifyOperation() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\", { \"check\" : true } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    NotifyOperation operation = message.getNotifyOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "widgetSelected", operation.getEventName() );
    assertEquals( Boolean.TRUE, operation.getProperty( "check" ) );
  }

  public void testNotifyOperation_WithoutEventType() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"notify\", \"w3\", { \"check\" : true } ]" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testNotifyOperation_WithoutProperties() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"notify\", \"w3\", \"widgetSelected\"" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCallOperation() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"call\", \"w3\", \"store\", { \"id\" : 123 } ]" );
    json.append( "] }" );
    ClientMessage message = new ClientMessage( json.toString() );

    CallOperation operation = message.getCallOperations( "w3" )[ 0 ];

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "store", operation.getMethodName() );
    assertEquals( Integer.valueOf( 123 ), operation.getProperty( "id" ) );
  }

  public void testCallOperation_WithoutMethodName() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"call\", \"w3\", { \"id\" : 123 } ]" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCallOperation_WithoutProperties() {
    StringBuilder json = new StringBuilder();
    json.append( "{ \"operations\" : [" );
    json.append( "[ \"call\", \"w3\", \"store\"" );
    json.append( "] }" );

    try {
      new ClientMessage( json.toString() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

}
