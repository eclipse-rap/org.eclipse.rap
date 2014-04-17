/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProtocolUtil_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsClientMessageProcessed_No() {
    assertFalse( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testIsClientMessageProcessed_Yes() {
    ProtocolUtil.getClientMessage();

    assertTrue( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testGetClientMessage() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.TRUE );

    ClientMessage message = ProtocolUtil.getClientMessage();

    assertNotNull( message );
    assertFalse( message.getAllOperationsFor( "w3" ).isEmpty() );
  }

  @Test
  public void testGetClientMessage_SameInstance() {
    ClientMessage message1 = ProtocolUtil.getClientMessage();
    ClientMessage message2 = ProtocolUtil.getClientMessage();

    assertSame( message1, message2 );
  }

  @Test
  public void testReadProperyValue_MissingProperty() {
    assertNull( ProtocolUtil.readPropertyValueAsString( "w3", "p0" ) );
  }

  @Test
  public void testReadProperyValueAsString_string() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.valueOf( "foo" ) );

    assertEquals( "foo", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_int() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.valueOf( 23 ) );

    assertEquals( "23", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_boolean() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.TRUE );

    assertEquals( "true", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_null() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.NULL );

    assertEquals( "null", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadPropertyValue_LastSetValue() {
    Fixture.fakeSetProperty( "w3", "p1", "foo" );
    Fixture.fakeSetProperty( "w3", "p1", "bar" );

    assertEquals( "bar", ProtocolUtil.readPropertyValueAsString( "w3", "p1" ) );
  }

  @Test
  public void testReadEventPropertyValue_MissingProperty() {
    assertNull( ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "item" ) );
  }

  @Test
  public void testReadEventPropertyValue() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject().add( "detail", "check" ) );

    String value = ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "detail" );

    assertEquals( "check", value );
  }

  @Test
  public void testWasEventSent_sent() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject() );

    assertTrue( ProtocolUtil.wasEventSent( "w3", "widgetSelected" ) );
  }

  @Test
  public void testWasEventSend_notSent() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject() );

    assertFalse( ProtocolUtil.wasEventSent( "w3", "widgetDefaultSelected" ) );
  }

  @Test
  public void testReadPropertyValue() {
    Fixture.fakeSetProperty( "w3", "prop", new JsonArray().add( "a" ).add( 2 ).add( true ) );

    JsonValue result = ProtocolUtil.readPropertyValue( "w3", "prop" );

    JsonValue expected = JsonValue.readFrom( "[\"a\", 2, true]" );
    assertEquals( expected, result );
  }

  @Test
  public void testWasCallReceived() {
    Fixture.fakeCallOperation( "w3", "resize", null );

    assertTrue( ProtocolUtil.wasCallReceived( "w3", "resize" ) );
    assertFalse( ProtocolUtil.wasCallReceived( "w4", "resize" ) );
  }

  @Test
  public void testReadCallProperty() {
    Fixture.fakeCallOperation( "w3", "resize", new JsonObject().add( "width", 10 ) );

    assertEquals( "10", ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testReadCallProperty_missingProperty() {
    Fixture.fakeCallOperation( "w3", "resize", null );

    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testReadCallProperty_missingOperation() {
    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

}
