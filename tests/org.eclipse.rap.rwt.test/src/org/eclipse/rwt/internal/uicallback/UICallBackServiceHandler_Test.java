/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.uicallback;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler_Test extends TestCase {

  private final static String UI_CALLBACK_ID = "uicb";
  private final static String PROP_ACTIVE = "active";
  private final static String METHOD_SEND_UI_REQUEST = "sendUIRequest";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testResponseContentType() {
    UICallBackServiceHandler.writeUIRequestNeeded( getResponseWriter() );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "text/javascript; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testWriteUICallBackActivate() throws Exception {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );

    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( UI_CALLBACK_ID, PROP_ACTIVE ) );
  }

  public void testWriteUICallBackDeactivate() throws Exception {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackDeactivation( getResponseWriter() );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( UI_CALLBACK_ID, PROP_ACTIVE ) );
  }

  public void testWriteUICallBackDeactivateWithDisposedDisplay() throws Exception {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Fixture.fakeNewRequest();
    display.dispose();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackDeactivation( getResponseWriter() );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( UI_CALLBACK_ID, PROP_ACTIVE ) );
  }

  public void testWriteUICallBackDeactivateIsNotSentFromUIRequest() throws Exception {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new DisplayLCA().render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( UI_CALLBACK_ID, PROP_ACTIVE ) );
  }

  public void testWriteUICallBackDeactivateIsSentFromServiceHandler() throws Exception {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( UI_CALLBACK_ID, PROP_ACTIVE ) );
  }

  public void testWriteUICallBackActivateTwice() throws Exception {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testNoUICallBackByDefault() throws Exception {
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testWriteUICallBackActivationWithoutDisplay() throws Exception {
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );

    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testWriteUiRequestNeeded() throws IOException {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();

    display.asyncExec( new NoOpRunnable() );
    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST ) );
  }

  public void testWriteUiRequestNeededAfterDeactivate() throws IOException {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();

    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST ) );
  }

  public void testWriteUiRequestNeededAfterDeactivateWithRunnable() throws IOException {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();

    display.asyncExec( new NoOpRunnable() );
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST ) );
  }

  public void testWriteUiRequestNeededAfterWake() throws Throwable {
    final Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    Fixture.fakeNewRequest();

    Runnable runnable = new Runnable() {
      public void run() {
        display.wake();
      }
    };
    Fixture.runInThread( runnable );
    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST ) );
  }

  public void testWriteUICallBackActivateWithoutStateInfo() throws Exception {
    Fixture.replaceStateInfo( null );

    JavaScriptResponseWriter responseWriter = mock( JavaScriptResponseWriter.class );
    try {
      UICallBackServiceHandler.writeUICallBackActivation( responseWriter );
    } catch( NullPointerException notExpected ) {
      fail();
    }
  }

  private static JavaScriptResponseWriter getResponseWriter() {
    return ContextProvider.getStateInfo().getResponseWriter();
  }
}
