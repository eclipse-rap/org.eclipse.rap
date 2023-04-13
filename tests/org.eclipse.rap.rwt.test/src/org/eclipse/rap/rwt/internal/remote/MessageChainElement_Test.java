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
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestResponseMessage;
import org.junit.Before;
import org.junit.Test;


public class MessageChainElement_Test {

  private List<String> log;
  private RequestMessage message;

  @Before
  public void setUp() {
    log = new ArrayList<String>();
    message = new TestMessage();
  }

  @Test
  public void testHandleMessage_callsFilter() {
    MessageFilter filter = mock( MessageFilter.class );
    MessageChainElement chain = mock( MessageChainElement.class );
    MessageChainElement element = new MessageChainElement( filter, chain );

    element.handleMessage( message );

    verify( filter ).handleMessage( eq( message ), eq( chain ) );
  }

  @Test
  public void testHandleMessage_returnsResultFromFilter() {
    ResponseMessage response = new TestResponseMessage();
    MessageFilter filter = mock( MessageFilter.class );
    when( filter.handleMessage( any( RequestMessage.class ), isNull() ) )
      .thenReturn( response );
    MessageChainElement element = new MessageChainElement( filter, null );

    ResponseMessage result = element.handleMessage( message );

    assertSame( response, result );
  }

  @Test
  public void testRemove_firstElement() {
    MessageFilter filter1 = createLoggingFilter( "h1" );
    MessageFilter filter2 = createLoggingFilter( "h2" );
    MessageChainElement element = createFilterChain( filter1, filter2 );

    MessageChainElement result = element.remove( filter2 );

    result.handleMessage( message );
    assertEquals( asList( "h1" ), log );
  }

  @Test
  public void testRemove_lastElement() {
    MessageFilter filter1 = createLoggingFilter( "h1" );
    MessageFilter filter2 = createLoggingFilter( "h2" );
    MessageChainElement element = createFilterChain( filter1, filter2 );

    MessageChainElement result = element.remove( filter1 );

    result.handleMessage( message );
    assertEquals( asList( "h2" ), log );
  }

  @Test
  public void testRemove_middleElement() {
    MessageFilter filter1 = createLoggingFilter( "h1" );
    MessageFilter filter2 = createLoggingFilter( "h2" );
    MessageFilter filter3 = createLoggingFilter( "h3" );
    MessageChainElement element = createFilterChain( filter1, filter2, filter3 );

    MessageFilterChain result = element.remove( filter2 );

    result.handleMessage( message );
    assertEquals( asList( "h3", "h1" ), log );
  }

  @Test
  public void testRemove_missingElement() {
    MessageFilter filter1 = createLoggingFilter( "h1" );
    MessageChainElement element = createFilterChain( filter1 );
    MessageFilter missingFilter = mock( MessageFilter.class );

    MessageFilterChain result = element.remove( missingFilter );

    result.handleMessage( message );
    assertEquals( asList( "h1" ), log );
  }

  @Test
  public void testRemove_missingElement_returnsSameInstance() {
    MessageChainElement element = createFilterChain( mock( MessageFilter.class ) );
    MessageFilterChain result = element.remove( mock( MessageFilter.class ) );

    assertSame( element, result );
  }

  private MessageFilter createLoggingFilter( final String name ) {
    return new MessageFilter() {
      @Override
      public ResponseMessage handleMessage( RequestMessage message, MessageFilterChain parent ) {
        log.add( name );
        if( parent != null ) {
          return parent.handleMessage( message );
        }
        return new TestResponseMessage();
      }
    };
  }

  private static MessageChainElement createFilterChain( MessageFilter... filters ) {
    MessageChainElement element = null;
    for( MessageFilter filter : filters ) {
      element = new MessageChainElement( filter, element );
    }
    return element;
  }

}
