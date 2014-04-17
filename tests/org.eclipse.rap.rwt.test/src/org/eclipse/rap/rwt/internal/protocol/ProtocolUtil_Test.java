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

}
