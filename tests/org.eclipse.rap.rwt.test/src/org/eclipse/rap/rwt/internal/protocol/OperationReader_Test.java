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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.junit.Test;


public class OperationReader_Test {

  @Test( expected = NullPointerException.class )
  public void testReadOperation_withNull() {
    OperationReader.readOperation( (JsonValue)null );
  }

  @Test
  public void testReadOperation_withWrongJsonType() {
    assertOperationRejected( "false" );
  }

  @Test
  public void testReadOperation_withEmptyArray() {
    assertOperationRejected( "[]" );
  }

  @Test
  public void testReadOperation_withUnknownType() {
    assertOperationRejected( "[\"unknown\"]" );
  }

  @Test
  public void testReadOperation_withMissingTarget() {
    assertOperationRejected( "[\"destroy\"]" );
  }

  @Test
  public void testSetOperation() {
    JsonValue json = JsonValue.readFrom( "[ \"set\", \"w3\", { \"foo\": 23 } ]" );

    SetOperation operation = ( SetOperation )OperationReader.readOperation( json );

    assertEquals( "w3", operation.getTarget() );
    assertEquals( new JsonObject().add( "foo", 23 ), operation.getProperties() );
  }

  @Test
  public void testSetOperation_withoutProperties() {
    assertOperationRejected( "[ \"set\", \"w3\" ]" );
  }

  @Test
  public void testNotifyOperation() {
    JsonValue json = JsonValue.readFrom( "[ \"notify\", \"w3\", \"event\", { \"foo\" : 23 } ]" );

    NotifyOperation operation = ( NotifyOperation )OperationReader.readOperation( json );

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "event", operation.getEventName() );
    assertEquals( new JsonObject().add( "foo", 23 ), operation.getProperties() );
  }

  @Test
  public void testNotifyOperation_withoutEventType() {
    assertOperationRejected( "[ \"notify\", \"w3\", { \"check\" : true } ]" );
  }

  @Test
  public void testNotifyOperation_withoutProperties() {
    assertOperationRejected( "[ \"notify\", \"w3\", \"widgetSelected\" ]" );
  }

  @Test
  public void testCallOperation() {
    JsonValue json = JsonValue.readFrom( "[ \"call\", \"w3\", \"method\", { \"foo\" : 23 } ]" );

    CallOperation operation = ( CallOperation )OperationReader.readOperation( json );

    assertEquals( "w3", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
    assertEquals( new JsonObject().add( "foo", 23 ), operation.getParameters() );
  }

  @Test
  public void testCallOperation_withoutMethodName() {
    assertOperationRejected( "[ \"call\", \"w3\", { \"id\" : 123 } ]" );
  }

  @Test
  public void testCallOperation_withoutProperties() {
    assertOperationRejected( "[ \"call\", \"w3\", \"store\" ]" );
  }

  private static void assertOperationRejected( String json ) {
    try {
      OperationReader.readOperation( JsonValue.readFrom( json ) );
      fail();
    } catch( Exception exception ) {
      assertThat( exception.getMessage(), startsWith( "Could not read operation" ) );
    }
  }

}
