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
package org.eclipse.rap.rwt.internal.remote;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestResponseMessage;
import org.junit.Before;
import org.junit.Test;


public class MessageChainReference_Test {

  private MessageChainReference reference;

  @Before
  public void setUp() {
    LoggingFilter defaultHandler = new LoggingFilter( "default" );
    reference = new MessageChainReference( new MessageChainElement( defaultHandler, null ) );
  }

  @Test
  public void testHandleMessage_callsDefaultHandler() {
    ResponseMessage response = reference.get().handleMessage( new TestMessage() );

    assertEquals( asList( "default" ), getLog( response ) );
  }

  @Test
  public void testAdd_addsHandlersToChain() {
    reference.add( new LoggingFilter( "custom1" ) );
    reference.add( new LoggingFilter( "custom2" ) );

    ResponseMessage response = reference.get().handleMessage( new TestMessage() );

    assertEquals( asList( "custom2", "custom1", "default" ), getLog( response ) );
  }

  @Test
  public void testRemove_removesFirstHandlerFromChain() {
    MessageFilter filter = new LoggingFilter( "custom" );
    reference.add( filter );

    reference.remove( filter );

    ResponseMessage response = reference.get().handleMessage( new TestMessage() );
    assertEquals( asList( "default" ), getLog( response ) );
  }

  @Test
  public void testRemove_removesEmbeddedHandlerFromChain() {
    reference.add( new LoggingFilter( "custom1" ) );
    MessageFilter filter = new LoggingFilter( "custom2" );
    reference.add( filter );
    reference.add( new LoggingFilter( "custom3" ) );

    reference.remove( filter );

    ResponseMessage response = reference.get().handleMessage( new TestMessage() );
    assertEquals( asList( "custom3", "custom1", "default" ), getLog( response ) );
  }

  @Test
  public void testRemove_filterCanRemoveItself() {
    reference.add( new LoggingFilter( "custom" ) {
      @Override
      public ResponseMessage handleMessage( RequestMessage message, MessageFilterChain parent ) {
        reference.remove( this );
        return super.handleMessage( message, parent );
      }
    } );

    ResponseMessage response1 = reference.get().handleMessage( new TestMessage() );
    ResponseMessage response2 = reference.get().handleMessage( new TestMessage() );

    assertEquals( asList( "custom", "default" ), getLog( response1 ) );
    assertEquals( asList( "default" ), getLog( response2 ) );
  }

  private static List<String> getLog( ResponseMessage response ) {
    List<String> list = new ArrayList<String>();
    for( JsonValue element : response.getHead().get( "log" ).asArray() ) {
      list.add( element.asString() );
    }
    return list;
  }

  private static class LoggingFilter implements MessageFilter {

    private final String name;

    private LoggingFilter( String name ) {
      this.name = name;
    }

    public ResponseMessage handleMessage( RequestMessage request, MessageFilterChain chain )
    {
      if( request.getHead().get( "log" ) == null ) {
        request.getHead().add( "log", new JsonArray() );
      }
      request.getHead().get( "log" ).asArray().add( name );
      if( chain == null ) {
        TestResponseMessage response = new TestResponseMessage();
        response.getHead().add( "log", request.getHead().get( "log" ) );
        return response;
      }
      return chain.handleMessage( request );
    }

  }

}
