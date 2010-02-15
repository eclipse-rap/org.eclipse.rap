/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler_Test extends TestCase {
  
  private static final String SESSION_STORE_ATTRIBUTE
    = "session-store-attribute";
  private static final String HTTP_SESSION_ATTRIBUTE
    = "http-session-attribute";

  private static final int THREAD_COUNT = 10;
  private static final String ENTER = "enter|";
  private static final String EXIT = "exit|";

  private StringBuffer log = new StringBuffer();

  private class TestHandler extends LifeCycleServiceHandler {
    void synchronizedService() {
      log.append( ENTER );
      try {
        Thread.sleep( 2 );
      } catch( InterruptedException e ) {
        // ignore
      }
      log.append( EXIT );
    }
  }
  
  private class Worker implements Runnable {
    private final ServiceContext context;
    private final IServiceHandler serviceHandler;

    private Worker( final ServiceContext context,
                    final IServiceHandler serviceHandler )
    {
      this.context = context;
      this.serviceHandler = serviceHandler;
    }

    public void run() {
      ContextProvider.setContext( context );
      try {
        serviceHandler.service();
      } catch( ServletException e ) {
        throw new RuntimeException( e );
      } catch( IOException e ) {
        throw new RuntimeException( e );
      } finally {
        ContextProvider.releaseContextHolder();
      }
    }
  }
  
  public void testRequestSynchronization() throws InterruptedException {
    List threads = new ArrayList();
    ServiceContext context = ContextProvider.getContext();
    for( int i = 0; i < THREAD_COUNT; i++ ) {
      IServiceHandler syncHandler = new TestHandler();
      Thread thread = new Thread( new Worker( context, syncHandler ) );
      thread.setDaemon( true );
      thread.start();
      threads.add( thread );
    }
    while( threads.size() > 0 ) {
      Thread thread = ( Thread )threads.get( 0 );
      thread.join();
      threads.remove( 0 );
    }
    String expected = "";
    for( int i = 0; i < THREAD_COUNT; i++ ) {
      expected += ENTER + EXIT;
    }
    assertEquals( expected, log.toString() );
  }
  
  public void testSessionRestart() throws Exception {
    ISessionStore sessionStore = ContextProvider.getSession();
    // set up session-store and http-session
    sessionStore.setAttribute( SESSION_STORE_ATTRIBUTE, new Object() );
    HttpSession httpSession = sessionStore.getHttpSession();
    httpSession.setAttribute( HTTP_SESSION_ATTRIBUTE, new Object() );
    // fake required environment settings
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );
    Fixture.fakeResponseWriter();
    sessionStore.setAttribute( LifeCycleServiceHandler.SESSION_INITIALIZED,
                               Boolean.TRUE );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    // run life cycle
    new LifeCycleServiceHandler().service();
    assertNull( sessionStore.getAttribute( SESSION_STORE_ATTRIBUTE ) );
    assertNull( httpSession.getAttribute( HTTP_SESSION_ATTRIBUTE ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
