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
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.protocol.Message;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTMessageHandler_Test {

  private List<Object> log;

  @Before
  public void setUp() {
    Fixture.setUp();
    log = new ArrayList<Object>();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleMessage_executesLifeCycleWithMessage() {
    LifeCycle lifeCycle = createLoggingLifeCycle( getApplicationContext() );
    RWTMessageHandler messageHandler = new RWTMessageHandler( mockLifeCycleFactory( lifeCycle ) );
    Message message = new TestMessage();

    messageHandler.handleMessage( message, null );

    assertEquals( 1, log.size() );
    assertEquals( message.toString(), log.get( 0 ).toString() );
  }

  private LifeCycle createLoggingLifeCycle( ApplicationContextImpl applicationContext ) {
    return new LifeCycle( applicationContext ) {
      @Override
      public void execute() throws IOException {
        log.add( ProtocolUtil.getClientMessage() );
      }
      @Override
      public void requestThreadExec( Runnable runnable ) {
      }
      @Override
      public void sleep() {
      }
    };
  }

  private static LifeCycleFactory mockLifeCycleFactory( LifeCycle lifeCycle ) {
    LifeCycleFactory lifeCycleFactory = mock( LifeCycleFactory.class );
    when( lifeCycleFactory.getLifeCycle() ).thenReturn( lifeCycle );
    return lifeCycleFactory;
  }

}
