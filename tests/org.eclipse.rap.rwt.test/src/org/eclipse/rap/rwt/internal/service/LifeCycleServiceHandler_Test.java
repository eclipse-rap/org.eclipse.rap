/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.lifecycle.ILifeCycle;
import org.eclipse.rap.rwt.service.IServiceHandler;
import org.eclipse.rap.rwt.service.ISessionStore;
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
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, null );
    RWTFactory.getEntryPointManager().registerByPath( "/test", TestEntryPoint.class, null );
  }

  @Override
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

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    assertNull( sessionStore.getAttribute( SESSION_STORE_ATTRIBUTE ) );
    assertSame( httpSessionAttribute, httpSession.getAttribute( HTTP_SESSION_ATTRIBUTE ) );
  }

  public void testRequestCounterAfterSessionRestart() throws Exception {
    RWTRequestVersionControl.getInstance().nextRequestId();
    Integer versionBeforeRestart = RWTRequestVersionControl.getInstance().nextRequestId();

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    Integer versionAfterRestart = RWTRequestVersionControl.getInstance().getCurrentRequestId();
    assertEquals( versionBeforeRestart.intValue() + 1, versionAfterRestart.intValue() );
  }

  public void testApplicationContextAfterSessionRestart() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    ApplicationContext applicationContext = ApplicationContextUtil.getInstance();

    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    assertSame( applicationContext, ApplicationContextUtil.get( sessionStore ) );
  }

  public void testRequestParametersAreBufferedAfterSessionRestart() throws IOException {
    Fixture.fakeNewGetRequest();
    Fixture.fakeRequestParam( "foo", "bar" );
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    assertEquals( "bar", ContextProvider.getRequest().getParameter( "foo" ) );
  }

  /*
   * When cleaning the session store, the display is disposed. This put a list with all disposed
   * widgets into the service store. As application is restarted in the same request, we have to
   * prevent these dispose calls to be rendered.
   * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=373084
   */
  public void testClearServiceStoreAfterSessionRestart() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateInitialUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    simulateInitialUiRequest();
    ContextProvider.getServiceStore().setAttribute( "foo", "bar" );
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    assertNull( ContextProvider.getServiceStore().getAttribute( "foo" ) );
  }

  public void testFinishesProtocolWriter() throws IOException {
    simulateUiRequest();

    new LifeCycleServiceHandler( mockLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertTrue( response.getContent().contains( "\"meta\":" ) );
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
    Fixture.fakeNewRequest();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setMethod( HTTP.METHOD_GET );

    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testHandleInvalidRequestCounter() throws IOException {
    LifeCycleServiceHandler.markSessionStarted();
    simulateUiRequestWithIllegalCounter();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
    assertEquals( "invalid request counter", message.getError() );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( HttpServletResponse.SC_PRECONDITION_FAILED, response.getStatus() );
  }

  public void testHandleSessionTimeout() throws IOException {
    simulateUiRequest();
    new LifeCycleServiceHandler( getLifeCycleFactory(), getStartupPage() ).service();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
    assertEquals( "session timeout", message.getError() );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( HttpServletResponse.SC_FORBIDDEN, response.getStatus() );
  }

  private void simulateInitialUiRequest() {
    Fixture.fakeNewRequest( new Display() );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
    Fixture.fakeHeaderParameter( RequestParams.RWT_INITIALIZE, "true" );
  }

  private void simulateUiRequest() {
    Fixture.fakeNewRequest( new Display() );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
  }

  private void simulateUiRequestWithIllegalCounter() {
    Fixture.fakeNewRequest( new Display() );
    Fixture.fakeHeaderParameter( "requestCounter", "23" );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
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

    @Override
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

  public static final class TestEntryPoint implements IEntryPoint {
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
