/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LifeCycleServiceHandler_Test {

  private static final String SESSION_STORE_ATTRIBUTE = "session-store-attribute";
  private static final String HTTP_SESSION_ATTRIBUTE = "http-session-attribute";

  private static final int THREAD_COUNT = 10;
  private static final String ENTER = "enter|";
  private static final String EXIT = "exit|";

  private final StringBuilder log = new StringBuilder();
  private LifeCycleServiceHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    EntryPointManager entryPointManager = getApplicationContext().getEntryPointManager();
    entryPointManager.register( "/rap", TestEntryPoint.class, null );
    entryPointManager.register( "/test", TestEntryPoint.class, null );
    handler = new LifeCycleServiceHandler( mockLifeCycleFactory(), mockStartupPage() );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRequestSynchronization() throws InterruptedException {
    List<Thread> threads = new ArrayList<Thread>();
    // initialize session, see bug 344549
    ContextProvider.getUISession();
    ServiceContext context = ContextProvider.getContext();
    for( int i = 0; i < THREAD_COUNT; i++ ) {
      ServiceHandler syncHandler = new TestHandler( getLifeCycleFactory(), mockStartupPage() );
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

  @Test
  public void testUISessionClearedOnSessionRestart() throws IOException {
    UISession uiSession = ContextProvider.getUISession();
    uiSession.setAttribute( SESSION_STORE_ATTRIBUTE, new Object() );

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( handler );

    assertNull( uiSession.getAttribute( SESSION_STORE_ATTRIBUTE ) );
  }

  @Test
  public void testShutdownUISession() throws IOException {
    UISession uiSession = ContextProvider.getUISession();

    LifeCycleServiceHandler.markSessionStarted();
    simulateShutdownUiRequest();
    service( handler );

    assertFalse( uiSession.isBound() );
  }

  @Test
  public void testShutdownUISession_RemoveUISessionFromHttpSession() throws IOException {
    UISession uiSession = ContextProvider.getUISession();
    HttpSession httpSession = uiSession.getHttpSession();

    LifeCycleServiceHandler.markSessionStarted();
    simulateShutdownUiRequest();
    service( handler );

    assertNull( UISessionImpl.getInstanceFromSession( httpSession, null ) );
  }

  @Test
  public void testStartUISession_AfterPreviousShutdown() throws IOException {
    UISession oldUiSession = ContextProvider.getUISession();

    LifeCycleServiceHandler.markSessionStarted();
    simulateShutdownUiRequest();
    service( handler );

    simulateInitialUiRequest();
    service( handler );

    UISession newUiSession = ContextProvider.getUISession();
    assertNotSame( oldUiSession, newUiSession );
  }

  @Test
  public void testUISessionListerenerCalledOnce_AfterPreviousShutdown() throws IOException {
    UISession uiSession = ContextProvider.getUISession();
    UISessionListener listener = mock( UISessionListener.class );
    uiSession.addUISessionListener(listener );

    LifeCycleServiceHandler.markSessionStarted();
    simulateShutdownUiRequest();
    service( handler );

    simulateInitialUiRequest();
    service( handler );

    verify( listener, times( 1 ) ).beforeDestroy( any( UISessionEvent.class ) );
  }

  @Test
  public void testHttpSessionNotClearedOnSessionRestart() throws IOException {
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    Object attribute = new Object();
    httpSession.setAttribute( HTTP_SESSION_ATTRIBUTE, attribute );

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( handler );

    assertSame( attribute, httpSession.getAttribute( HTTP_SESSION_ATTRIBUTE ) );
  }

  @Test
  public void testRequestCounterAfterSessionRestart() throws IOException {
    RequestCounter.getInstance().nextRequestId();
    RequestCounter.getInstance().nextRequestId();
    int versionBeforeRestart = RequestCounter.getInstance().currentRequestId();

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    int versionAfterRestart = RequestCounter.getInstance().currentRequestId();
    assertEquals( versionBeforeRestart + 1, versionAfterRestart );
  }

  @Test
  public void testApplicationContextAfterSessionRestart() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    ApplicationContextImpl applicationContext = getApplicationContext();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    UISession uiSession = ContextProvider.getUISession();
    assertSame( applicationContext, uiSession.getApplicationContext() );
  }

  /*
   * When cleaning the session store, the display is disposed. This put a list with all disposed
   * widgets into the service store. As application is restarted in the same request, we have to
   * prevent these dispose calls to be rendered.
   * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=373084
   */
  @Test
  public void testClearServiceStoreAfterSessionRestart() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    simulateInitialUiRequest();
    ContextProvider.getServiceStore().setAttribute( "foo", "bar" );
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    assertNull( ContextProvider.getServiceStore().getAttribute( "foo" ) );
  }

  @Test
  public void testClearServiceStoreAfterSessionRestart_RestoreMessage() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    simulateInitialUiRequest();
    ClientMessage message = ProtocolUtil.getClientMessage();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    assertSame( message, ProtocolUtil.getClientMessage() );
  }

  @Test
  public void testFinishesProtocolWriter() throws IOException {
    simulateUiRequest();

    service( handler );

    TestResponse response = getResponse();
    assertTrue( response.getContent().contains( "\"head\":" ) );
  }

  @Test
  public void testContentType() throws IOException {
    simulateUiRequest();

    service( handler );

    TestResponse response = getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentTypeForIllegalRequestCounter() throws IOException {
    simulateUiRequestWithIllegalCounter();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentTypeForStartupJson() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( Client.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( HTTP.METHOD_GET );

    service( new LifeCycleServiceHandler( getLifeCycleFactory(),
                                          getApplicationContext().getStartupPage() ) );

    TestResponse response = getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentTypeForStartupPage() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( WebClient.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( HTTP.METHOD_GET );
    StartupPage startupPage = getApplicationContext().getStartupPage();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), startupPage ) );

    TestResponse response = getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentTypeForHeadRequest() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( WebClient.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( "HEAD" );
    StartupPage startupPage = getApplicationContext().getStartupPage();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), startupPage ) );

    TestResponse response = getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  @Test
  public void testHandleInvalidRequestCounter() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateUiRequestWithIllegalCounter();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = getResponse();
    assertEquals( HttpServletResponse.SC_PRECONDITION_FAILED, response.getStatus() );
    Message message = getMessageFromResponse();
    assertEquals( "invalid request counter", message.getError() );
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testHandlesSessionTimeout() throws IOException {
    simulateUiRequest();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = getResponse();
    assertEquals( HttpServletResponse.SC_FORBIDDEN, response.getStatus() );
    Message message = getMessageFromResponse();
    assertEquals( "session timeout", message.getError() );
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testSendBufferedResponse() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateUiRequest();
    RequestCounter.getInstance().nextRequestId();
    int requestCounter = RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", requestCounter );

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );
    Message firstResponse = getMessageFromResponse();

    simulateUiRequest();
    Fixture.fakeHeadParameter( "requestCounter", requestCounter );
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );
    Message secondResponse = getMessageFromResponse();

    assertEquals( firstResponse, secondResponse );
  }

  @Test
  public void testHasValidRequestCounter_trueWithValidParameter() {
    int nextRequestId = RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", nextRequestId );

    boolean valid = LifeCycleServiceHandler.hasValidRequestCounter();

    assertTrue( valid );
  }

  @Test
  public void testHasValidRequestCounter_falseWithInvalidParameter() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", 23 );

    boolean valid = LifeCycleServiceHandler.hasValidRequestCounter();

    assertFalse( valid );
  }

  @Test
  public void testHasValidRequestCounter_failsWithIllegalParameterFormat() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", "not-a-number" );

    try {
      LifeCycleServiceHandler.hasValidRequestCounter();
      fail();
    } catch( Exception exception ) {
      assertTrue( exception.getMessage().contains( "Not a number" ) );
    }
  }

  @Test
  public void testHasValidRequestCounter_toleratesMissingParameterInFirstRequest() {
    boolean valid = LifeCycleServiceHandler.hasValidRequestCounter();

    assertTrue( valid );
  }

  @Test
  public void testHasValidRequestCounter_falseWithMissingParameter() {
    RequestCounter.getInstance().nextRequestId();

    boolean valid = LifeCycleServiceHandler.hasValidRequestCounter();

    assertFalse( valid );
  }

  private void simulateInitialUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( ClientMessageConst.RWT_INITIALIZE, true );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private void simulateShutdownUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( ClientMessageConst.RWT_SHUTDOWN, true );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private void simulateUiRequest() {
    Fixture.fakeNewRequest();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private void simulateUiRequestWithIllegalCounter() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", 23 );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private static LifeCycleFactory mockLifeCycleFactory() {
    LifeCycle lifecycle = mock( LifeCycle.class );
    LifeCycleFactory lifeCycleFactory = mock( LifeCycleFactory.class );
    when( lifeCycleFactory.getLifeCycle() ).thenReturn( lifecycle );
    return lifeCycleFactory;
  }

  private LifeCycleFactory getLifeCycleFactory() {
    return getApplicationContext().getLifeCycleFactory();
  }

  private static StartupPage mockStartupPage() {
    return mock( StartupPage.class );
  }

  private static void service( LifeCycleServiceHandler serviceHandler ) throws IOException {
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );
  }

  private static TestResponse getResponse() {
    return ( TestResponse )ContextProvider.getResponse();
  }

  private static Message getMessageFromResponse() {
    return new Message( JsonObject.readFrom( getResponse().getContent() ) );
  }

  private class TestHandler extends LifeCycleServiceHandler {

    public TestHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
      super( lifeCycleFactory, startupPage );
    }

    @Override
    void synchronizedService( HttpServletRequest request, HttpServletResponse response ) {
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
    private final ServiceHandler serviceHandler;

    private Worker( ServiceContext context, ServiceHandler serviceHandler ) {
      this.context = context;
      this.serviceHandler = serviceHandler;
    }

    public void run() {
      ContextProvider.setContext( context );
      try {
        serviceHandler.service( context.getRequest(), context.getResponse() );
      } catch( ServletException e ) {
        throw new RuntimeException( e );
      } catch( IOException e ) {
        throw new RuntimeException( e );
      } finally {
        ContextProvider.releaseContextHolder();
      }
    }
  }

  public static final class TestEntryPoint implements EntryPoint {
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      shell.setSize( 100, 100 );
      shell.layout();
      shell.open();
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      return 0;
    }
  }

}
