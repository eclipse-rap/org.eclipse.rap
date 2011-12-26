/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class LifeCycleServiceHandler_Test extends TestCase {

  private static final String SESSION_STORE_ATTRIBUTE = "session-store-attribute";
  private static final String HTTP_SESSION_ATTRIBUTE = "http-session-attribute";

  private static final int THREAD_COUNT = 10;
  private static final String ENTER = "enter|";
  private static final String EXIT = "exit|";

  private final StringBuilder log = new StringBuilder();

  protected void setUp() {
    Fixture.setUp();
  }

  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testRequestSynchronization() throws InterruptedException {
    List<Thread> threads = new ArrayList<Thread>();
    // initialize session, see bug 344549
    ContextProvider.getSessionStore();
    ServiceContext context = ContextProvider.getContext();
    for( int i = 0; i < THREAD_COUNT; i++ ) {
      IServiceHandler syncHandler = new TestHandler( getLifeCycleFactory(), getStartupPage() );
      Thread thread = new Thread( new Worker( context, syncHandler ) );
      thread.setDaemon( true );
      thread.start();
      threads.add( thread );
    }
    while( threads.size() > 0 ) {
      Thread thread = threads.get( 0 );
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
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    // set up session-store and http-session
    sessionStore.setAttribute( SESSION_STORE_ATTRIBUTE, new Object() );
    HttpSession httpSession = sessionStore.getHttpSession();
    Object httpSessionAttribute = new Object();
    httpSession.setAttribute( HTTP_SESSION_ATTRIBUTE, httpSessionAttribute );

    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    assertNull( sessionStore.getAttribute( SESSION_STORE_ATTRIBUTE ) );
    assertSame( httpSessionAttribute, httpSession.getAttribute( HTTP_SESSION_ATTRIBUTE ) );
  }

  public void testRequestCounterAfterSessionRestart() throws Exception {
    RWTRequestVersionControl.getInstance().nextRequestId();
    Integer version = RWTRequestVersionControl.getInstance().nextRequestId();
    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();
    Integer versionAfterRestart = RWTRequestVersionControl.getInstance().getCurrentRequestId();

    assertEquals( version.intValue(), versionAfterRestart.intValue() );

    Fixture.fakeNewRequest();
    Integer versionForNextRequest = RWTRequestVersionControl.getInstance().nextRequestId();

    assertFalse( versionAfterRestart.equals( versionForNextRequest ) );
  }

  public void testFinishesProtocolWriter() throws IOException {
    simulateUiRequest();

    new LifeCycleServiceHandler( mockLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertTrue( response.getContent().contains( "\"meta\":" ) );
  }

  public void testNoJsonAppendedToStartupPage() throws IOException {
    simulateInitialUiRequest();

    new LifeCycleServiceHandler( mockLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertTrue( response.getContent().trim().endsWith( "</html>" ) );
  }

  public void testContentType() throws IOException {
    simulateUiRequest();

    new LifeCycleServiceHandler( mockLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForIllegalRequestCounter() throws IOException {
    simulateUiRequestWithIllegalCounter();

    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForStartupPage() throws IOException {
    simulateInitialUiRequest();

    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  private void simulateInitialUiRequest() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( LifeCycleServiceHandler.SESSION_INITIALIZED, Boolean.TRUE );
  }

  private void simulateUiRequest() {
    Fixture.fakeNewRequest( new Display() );
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( LifeCycleServiceHandler.SESSION_INITIALIZED, Boolean.TRUE );
  }

  private void simulateUiRequestWithIllegalCounter() {
    Fixture.fakeNewRequest( new Display() );
    Fixture.fakeRequestParam( "requestCounter", "23" );
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( LifeCycleServiceHandler.SESSION_INITIALIZED, Boolean.TRUE );
  }

  private LifeCycleFactory mockLifeCycleFactory() {
    ILifeCycle lifecycle = mock( LifeCycle.class );
    LifeCycleFactory lifeCycleFactory = mock( LifeCycleFactory.class );
    when( lifeCycleFactory.getLifeCycle() ).thenReturn( lifecycle );
    return lifeCycleFactory;
  }

  private StartupPage getStartupPage() {
    return RWTFactory.getStartupPage();
  }

  private LifeCycleFactory getLifeCycleFactory() {
    return RWTFactory.getLifeCycleFactory();
  }

  private class TestHandler extends LifeCycleServiceHandler {

    public TestHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
      super( lifeCycleFactory, startupPage );
    }

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

  private static class Worker implements Runnable {
    private final ServiceContext context;
    private final IServiceHandler serviceHandler;

    private Worker( ServiceContext context, IServiceHandler serviceHandler ) {
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
}
