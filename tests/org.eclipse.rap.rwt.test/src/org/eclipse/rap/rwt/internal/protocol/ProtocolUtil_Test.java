/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
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
import static org.mockito.Mockito.mock;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
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
  public void testGetClientMessage() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.TRUE );

    ClientMessage message = ProtocolUtil.getClientMessage();

    assertNotNull( message );
    assertFalse( message.getAllOperationsFor( "w3" ).isEmpty() );
  }

  @Test
  public void testGetClientMessage_sameInstance() {
    ProtocolUtil.setClientMessage( mock( ClientMessage.class ) );

    ClientMessage message1 = ProtocolUtil.getClientMessage();
    ClientMessage message2 = ProtocolUtil.getClientMessage();

    assertSame( message1, message2 );
  }

  @Test
  public void testSetClientMessage() {
    ClientMessage message = mock( ClientMessage.class );

    ProtocolUtil.setClientMessage( message );

    assertSame( message, ProtocolUtil.getClientMessage() );
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
  public void testIsInitialRequest_withZeroRequestCounter() {
    assertTrue( ProtocolUtil.isInitialRequest( createMessageWithRequestCounter( 0 ) ) );
  }

  @Test
  public void testIsInitialRequest_withNonZeroRequestCounter() {
    assertFalse( ProtocolUtil.isInitialRequest( createMessageWithRequestCounter( 3 ) ) );
  }

  private static RequestMessage createMessageWithRequestCounter( int requestCounter ) {
    return new RequestMessage( new JsonObject()
      .add( "head", new JsonObject().add( "requestCounter", requestCounter ) )
      .add( "operations", new JsonArray() ) );
  }

}
