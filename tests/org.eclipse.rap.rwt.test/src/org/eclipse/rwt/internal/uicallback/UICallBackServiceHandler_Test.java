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
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.uicallback;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler_Test extends TestCase {

  private static final String SEND_UI_REQUEST
    = "org.eclipse.swt.Request.getInstance()._sendImmediate( true );";
  private static final String ENABLE_UI_CALLBACK
    = "org.eclipse.swt.Request.getInstance().setUiCallBackActive( true );";
  private static final String DISABLE_UI_CALLBACK
    = "org.eclipse.swt.Request.getInstance().setUiCallBackActive( false );";
  
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
    
    assertEquals( ENABLE_UI_CALLBACK, Fixture.getAllMarkup() );
  }

  public void testWriteUICallBackDeactivate() throws Exception {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    
    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    
    assertEquals( DISABLE_UI_CALLBACK, Fixture.getAllMarkup() );
  }

  public void testWriteUICallBackDeactivateWithDisposedDisplay() throws Exception {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    
    Fixture.fakeNewRequest();
    display.dispose();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    
    assertEquals( DISABLE_UI_CALLBACK, Fixture.getAllMarkup() );
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

  public void testUICallBackActivationUpdated() throws Exception {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    display.asyncExec( new NoOpRunnable() );    

    new UICallBackServiceHandler().service();

    assertTrue( Fixture.getAllMarkup().contains( DISABLE_UI_CALLBACK ) );
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

    assertEquals( SEND_UI_REQUEST, Fixture.getAllMarkup() );
  }
  
  public void testWriteUiRequestNeededAfterDeactivate() throws IOException {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();
    
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new UICallBackServiceHandler().service();
    
    assertFalse( Fixture.getAllMarkup().contains( SEND_UI_REQUEST ) );
  }

  public void testWriteUiRequestNeededAfterDeactivateWithRunnable() throws IOException {
    Display display = new Display();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    UICallBackServiceHandler.writeUICallBackActivation( getResponseWriter() );
    Fixture.fakeNewRequest();
    
    display.asyncExec( new NoOpRunnable() );
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    new UICallBackServiceHandler().service();
    
    assertEquals( SEND_UI_REQUEST, Fixture.getAllMarkup() );
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
