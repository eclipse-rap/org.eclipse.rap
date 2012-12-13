/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RequestId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.lifecycle.ILifeCycle;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class LifeCycleServiceHandler_Test extends TestCase {

  private static final String SESSION_STORE_ATTRIBUTE = "session-store-attribute";
  private static final String HTTP_SESSION_ATTRIBUTE = "http-session-attribute";

  private static final int THREAD_COUNT = 10;
  private static final String ENTER = "enter|";
  private static final String EXIT = "exit|";

  private final StringBuilder log = new StringBuilder();

  @Override
  protected void setUp() {
    Fixture.setUp();
    RWTFactory.getEntryPointManager().register( "/rap", TestEntryPoint.class, null );
    RWTFactory.getEntryPointManager().register( "/test", TestEntryPoint.class, null );
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

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

  public void testUISessionClearedOnSessionRestart() throws IOException {
    initializeUISession();
    UISession uiSession = ContextProvider.getUISession();
    uiSession.setAttribute( SESSION_STORE_ATTRIBUTE, new Object() );

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( mockLifeCycleFactory(), mockStartupPage() ) );

    assertNull( uiSession.getAttribute( SESSION_STORE_ATTRIBUTE ) );
  }

  public void testHttpSessionNotClearedOnSessionRestart() throws IOException {
    initializeUISession();
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    Object attribute = new Object();
    httpSession.setAttribute( HTTP_SESSION_ATTRIBUTE, attribute );

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( mockLifeCycleFactory(), mockStartupPage() ) );

    assertSame( attribute, httpSession.getAttribute( HTTP_SESSION_ATTRIBUTE ) );
  }

  public void testRequestCounterAfterSessionRestart() throws IOException {
    initializeUISession();
    RequestId.getInstance().nextRequestId();
    Integer versionBeforeRestart = RequestId.getInstance().nextRequestId();

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    Integer versionAfterRestart = RequestId.getInstance().getCurrentRequestId();
    assertEquals( versionBeforeRestart.intValue() + 1, versionAfterRestart.intValue() );
  }

  public void testApplicationContextAfterSessionRestart() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    UISession uiSession = ContextProvider.getUISession();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.getInstance();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    uiSession = ContextProvider.getUISession();
    assertSame( applicationContext, ApplicationContextUtil.get( uiSession ) );
  }

  public void testRequestParametersAreBufferedAfterSessionRestart() throws IOException {
    initializeUISession();
    Fixture.fakeNewGetRequest();
    Fixture.fakeRequestParam( "foo", "bar" );
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    assertEquals( "bar", ContextProvider.getRequest().getParameter( "foo" ) );
  }

  /*
   * When cleaning the session store, the display is disposed. This put a list with all disposed
   * widgets into the service store. As application is restarted in the same request, we have to
   * prevent these dispose calls to be rendered.
   * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=373084
   */
  public void testClearServiceStoreAfterSessionRestart() throws IOException {
    initializeUISession();
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    simulateInitialUiRequest();
    ContextProvider.getServiceStore().setAttribute( "foo", "bar" );
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    assertNull( ContextProvider.getServiceStore().getAttribute( "foo" ) );
  }

  public void testClearServiceStoreAfterSessionRestart_RestoreMessage() throws IOException {
    initializeUISession();
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    simulateInitialUiRequest();
    ClientMessage message = ProtocolUtil.getClientMessage();
    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    assertSame( message, ProtocolUtil.getClientMessage() );
  }

  public void testFinishesProtocolWriter() throws IOException {
    simulateUiRequest();

    service( new LifeCycleServiceHandler( mockLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertTrue( response.getContent().contains( "\"head\":" ) );
  }

  public void testContentType() throws IOException {
    simulateUiRequest();

    service( new LifeCycleServiceHandler( mockLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForIllegalRequestCounter() throws IOException {
    simulateUiRequestWithIllegalCounter();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForStartupJson() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( Client.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( HTTP.METHOD_GET );

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), RWTFactory.getStartupPage() ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForStartupPage() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( WebClient.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( HTTP.METHOD_GET );
    StartupPage startupPage = RWTFactory.getStartupPage();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), startupPage ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testContentTypeForHeadRequest() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeClient( mock( WebClient.class ) );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( "HEAD" );
    StartupPage startupPage = RWTFactory.getStartupPage();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), startupPage ) );

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testHandleInvalidRequestCounter() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateUiRequestWithIllegalCounter();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
    assertEquals( "invalid request counter", message.getError() );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( HttpServletResponse.SC_PRECONDITION_FAILED, response.getStatus() );
  }

  public void testHandleSessionTimeout() throws IOException {
    simulateUiRequest();

    service( new LifeCycleServiceHandler( getLifeCycleFactory(), mockStartupPage() ) );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
    assertEquals( "session timeout", message.getError() );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( HttpServletResponse.SC_FORBIDDEN, response.getStatus() );
  }

  private void simulateInitialUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( RequestParams.RWT_INITIALIZE, "true" );
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
    Fixture.fakeHeadParameter( "requestCounter", "23" );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private static LifeCycleFactory mockLifeCycleFactory() {
    ILifeCycle lifecycle = mock( LifeCycle.class );
    LifeCycleFactory lifeCycleFactory = mock( LifeCycleFactory.class );
    when( lifeCycleFactory.getLifeCycle() ).thenReturn( lifecycle );
    return lifeCycleFactory;
  }

  private LifeCycleFactory getLifeCycleFactory() {
    return RWTFactory.getLifeCycleFactory();
  }

  private static StartupPage mockStartupPage() {
    return mock( StartupPage.class );
  }

  private void initializeUISession() {
    UISession uiSession = ContextProvider.getUISession();
    ServletContext servletContext = Fixture.getServletContext();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    ApplicationContextUtil.set( uiSession, applicationContext );
  }

  private static void service( LifeCycleServiceHandler serviceHandler ) throws IOException {
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );
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
