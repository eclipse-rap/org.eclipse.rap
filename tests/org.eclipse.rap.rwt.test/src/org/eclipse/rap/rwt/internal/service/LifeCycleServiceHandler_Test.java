/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;
import org.eclipse.rap.rwt.internal.remote.MessageChainElement;
import org.eclipse.rap.rwt.internal.remote.MessageChainReference;
import org.eclipse.rap.rwt.internal.remote.MessageFilter;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.eclipse.rap.rwt.testfixture.internal.TestResponseMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class LifeCycleServiceHandler_Test {

  private static final int THREAD_COUNT = 10;
  private static final String ENTER = "enter|";
  private static final String EXIT = "exit|";

  private MessageFilter filter;
  private MessageChainReference messageChainReference;
  private LifeCycleServiceHandler serviceHandler;
  private StringBuilder log;

  @Before
  public void setUp() {
    Fixture.setUp();
    filter = mockMessageFilter();
    MessageChainElement handlerWrapper = new MessageChainElement( filter, null );
    messageChainReference = new MessageChainReference( handlerWrapper );
    serviceHandler = new LifeCycleServiceHandler( messageChainReference );
    log = new StringBuilder();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRequestSynchronization() throws InterruptedException {
    List<Thread> threads = new ArrayList<Thread>();
    // initialize session, see bug 344549
    getUISession();
    ServiceContext context = ContextProvider.getContext();
    for( int i = 0; i < THREAD_COUNT; i++ ) {
      ServiceHandler syncHandler = new TestHandler( messageChainReference );
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
  public void testShutdownUISession() throws IOException {
    UISession uiSession = getUISession();

    simulateShutdownUiRequest();
    service( serviceHandler );

    assertFalse( uiSession.isBound() );
  }

  @Test
  public void testShutdownUISession_returnsValidJson() throws IOException {
    simulateShutdownUiRequest();
    service( serviceHandler );

    TestResponse response = getResponse();
    JsonObject message = JsonObject.readFrom( response.getContent() );
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertNotNull( message.get( "head" ) );
    assertNotNull( message.get( "operations" ) );
  }

  @Test
  public void testShutdownUISession_removesUISessionFromHttpSession() throws IOException {
    UISession uiSession = getUISession();
    HttpSession httpSession = uiSession.getHttpSession();

    simulateShutdownUiRequest();
    service( serviceHandler );

    assertNull( UISessionImpl.getInstanceFromSession( httpSession, null ) );
  }

  @Test
  public void testStartUISession_AfterPreviousShutdown() throws IOException {
    UISession oldUiSession = getUISession();

    simulateShutdownUiRequest();
    service( serviceHandler );

    simulateInitialUiRequest();
    service( serviceHandler );

    UISession newUiSession = getUISession();
    assertNotSame( oldUiSession, newUiSession );
  }

  @Test
  public void testUISessionListerenerCalledOnce_AfterPreviousShutdown() throws IOException {
    UISession uiSession = getUISession();
    UISessionListener listener = mock( UISessionListener.class );
    uiSession.addUISessionListener(listener );

    simulateShutdownUiRequest();
    service( serviceHandler );

    simulateInitialUiRequest();
    service( serviceHandler );

    verify( listener, times( 1 ) ).beforeDestroy( any( UISessionEvent.class ) );
  }

  @Test
  public void testIncrementRequestCounter() throws IOException {
    RequestCounter requestCounter = RequestCounter.getInstance();
    requestCounter.nextRequestId();
    simulateUiRequest();

    service( serviceHandler );

    assertEquals( 2, requestCounter.currentRequestId() );
  }

  @Test
  public void testFinishesProtocolWriter() throws IOException {
    simulateUiRequest();

    service( serviceHandler );

    assertTrue( getResponse().getContent().contains( "\"head\":" ) );
  }

  @Test
  public void testContentType() throws IOException {
    simulateUiRequest();

    service( serviceHandler );

    assertEquals( "application/json; charset=UTF-8", getResponse().getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentType_forSessionTimeout() throws IOException {
    simulateUiRequest();

    service( serviceHandler );

    assertEquals( "application/json; charset=UTF-8", getResponse().getHeader( "Content-Type" ) );
  }

  @Test
  public void testContentType_forIllegalRequestCounter() throws IOException {
    simulateUiRequestWithIllegalCounter();

    service( serviceHandler );

    assertEquals( "application/json; charset=UTF-8", getResponse().getHeader( "Content-Type" ) );
  }

  @Test
  public void testStartupRequest_shutsDownUISession_ifExceptionInStartupPage() throws IOException {
    Fixture.fakeNewGetRequest();
    Fixture.fakeClient( mock( WebClient.class ) );
    StartupPage startupPage = mock( StartupPage.class );
    doThrow( new RuntimeException() ).when( startupPage ).send( any( HttpServletResponse.class ) );

    try {
      service( new LifeCycleServiceHandler( messageChainReference ) );
    } catch( RuntimeException exception ) {
    }

    assertNull( getUISession() );
  }

  @Test
  public void testHandleInvalidRequestCounter() throws IOException {
    simulateUiRequestWithIllegalCounter();

    service( serviceHandler );

    assertEquals( HttpServletResponse.SC_PRECONDITION_FAILED, getResponse().getStatus() );
    JsonObject message = JsonObject.readFrom( getResponse().getContent() );
    assertEquals( "invalid request counter", getError( message ) );
    assertTrue( message.get( "operations" ).asArray().isEmpty() );
  }

  @Test
  public void testHandlesSessionTimeout() throws IOException {
    simulateUiRequest();
    ( ( UISessionImpl )getUISession() ).shutdown();

    service( serviceHandler );

    TestResponse response = getResponse();
    assertEquals( HttpServletResponse.SC_FORBIDDEN, response.getStatus() );
    JsonObject message = JsonObject.readFrom( getResponse().getContent() );
    assertEquals( "session timeout", getError( message ) );
    assertTrue( message.get( "operations" ).asArray().isEmpty() );
  }

  @Test
  public void testSendBufferedResponse() throws IOException {
    simulateUiRequest();
    RequestCounter.getInstance().nextRequestId();
    int requestCounter = RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", requestCounter );

    service( serviceHandler );
    JsonObject firstResponse = JsonObject.readFrom( getResponse().getContent() );

    simulateUiRequest();
    Fixture.fakeHeadParameter( "requestCounter", requestCounter );
    service( serviceHandler );
    JsonObject secondResponse = JsonObject.readFrom( getResponse().getContent() );

    assertEquals( firstResponse, secondResponse );
  }

  @Test
  public void testWritesValidJson() throws IOException {
    simulateUiRequest();

    service( serviceHandler );

    JsonObject.readFrom( getResponse().getContent() );
  }

  @Test
  public void testIsRequestCounterValid_trueWithValidParameter() {
    int nextRequestId = RequestCounter.getInstance().nextRequestId();
    RequestMessage message = new TestMessage();
    message.getHead().set( "requestCounter", nextRequestId );

    boolean valid = LifeCycleServiceHandler.isRequestCounterValid( message );

    assertTrue( valid );
  }

  @Test
  public void testIsRequestCounterValid_falseWithInvalidParameter() {
    RequestCounter.getInstance().nextRequestId();
    RequestMessage requestMessage = new TestMessage();
    requestMessage.getHead().set( "requestCounter", 23 );

    boolean valid = LifeCycleServiceHandler.isRequestCounterValid( requestMessage );

    assertFalse( valid );
  }

  @Test
  public void testIsRequestCounterValid_failsWithIllegalParameterFormat() {
    RequestCounter.getInstance().nextRequestId();
    RequestMessage requestMessage = new TestMessage();
    requestMessage.getHead().set( "requestCounter", "not-a-number" );

    try {
      LifeCycleServiceHandler.isRequestCounterValid( requestMessage );
      fail();
    } catch( Exception exception ) {
      assertTrue( exception.getMessage().contains( "Not a number" ) );
    }
  }

  @Test
  public void testIsRequestCounterValid_falseWithMissingParameter() {
    RequestCounter.getInstance().nextRequestId();
    RequestMessage requestMessage = new TestMessage();

    boolean valid = LifeCycleServiceHandler.isRequestCounterValid( requestMessage );

    assertFalse( valid );
  }

  @Test
  public void testProcessesMessage() throws IOException {
    simulateUiRequest();
    JsonObject message = createExampleMessage();
    getRequest().setBody( message.toString() );
    ArgumentCaptor<RequestMessage> messageCaptor = ArgumentCaptor.forClass( RequestMessage.class );

    service( serviceHandler );

    verify( filter ).handleMessage( messageCaptor.capture(), isNull() );
    assertEquals( message, messageCaptor.getValue().toJson() );
  }

  @Test
  public void testUIRequest_shutsDownUISession_ifRuntimeExceptionInHandler() throws IOException {
    simulateUiRequest();
    doThrow( new RuntimeException() )
      .when( filter ).handleMessage( any( RequestMessage.class ), isNull() );

    try {
      service( new LifeCycleServiceHandler( messageChainReference ) );
    } catch( RuntimeException exception ) {
    }

    assertNull( getUISession() );
  }

  @Test
  public void testUIRequest_shutsDownUISession_ifIOException() throws IOException {
    simulateUiRequest();
    HttpServletResponse response = mock( HttpServletResponse.class );
    doThrow( new IOException() ).when( response ).getWriter();

    try {
      serviceHandler.service( getRequest(), response );
    } catch( IOException exception ) {
    }

    assertNull( getUISession() );
  }

  private void simulateUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", RequestCounter.getInstance().currentRequestId() );
  }

  private void simulateInitialUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", 0 );
  }

  private void simulateShutdownUiRequest() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( ClientMessageConst.SHUTDOWN, true );
  }

  private void simulateUiRequestWithIllegalCounter() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", 23 );
  }

  private static MessageFilter mockMessageFilter() {
    MessageFilter filter = mock( MessageFilter.class );
    ResponseMessage responseMessage = new TestResponseMessage();
    responseMessage.getHead().add( "test", true );
    when( filter.handleMessage( any( RequestMessage.class ), isNull() ) )
      .thenReturn( responseMessage  );
    return filter;
  }

  private static JsonObject createExampleMessage() {
    return new JsonObject()
      .add( "head", new JsonObject().add( "test", true ).add( "requestCounter", 0 ) )
      .add( "operations", new JsonArray() );
  }

  private static void service( LifeCycleServiceHandler serviceHandler ) throws IOException {
    serviceHandler.service( getRequest(), getResponse() );
  }

  private static TestRequest getRequest() {
    return ( TestRequest )ContextProvider.getRequest();
  }

  private static TestResponse getResponse() {
    return ( TestResponse )ContextProvider.getResponse();
  }

  private static String getError( JsonObject message ) {
    return message.get( "head" ).asObject().get( "error" ).asString();
  }

  private class TestHandler extends LifeCycleServiceHandler {

    public TestHandler( MessageChainReference messageChainReference ) {
      super( messageChainReference );
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

    @Override
    public void run() {
      ContextProvider.setContext( context );
      try {
        serviceHandler.service( context.getRequest(), context.getResponse() );
      } catch( Exception exception ) {
        throw new RuntimeException( exception );
      } finally {
        ContextProvider.releaseContextHolder();
      }
    }
  }

}
