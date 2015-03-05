/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.fileupload.internal.FileUploadHandlerStore;
import org.eclipse.rap.fileupload.internal.FileUploadServiceHandler;
import org.eclipse.rap.fileupload.test.FileUploadTestUtil;
import org.eclipse.rap.fileupload.test.TestFileUploadEvent;
import org.eclipse.rap.fileupload.test.TestFileUploadListener;
import org.eclipse.rap.fileupload.test.TestFileUploadReceiver;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class FileUploadHandler_Test {

  private FileUploadServiceHandler serviceHandler;
  private TestFileUploadListener uploadListener;
  private FileUploadHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    handler = new FileUploadHandler( new TestFileUploadReceiver() );
    uploadListener = new TestFileUploadListener();
    serviceHandler = new FileUploadServiceHandler();
  }

  @After
  public void tearDown() {
    serviceHandler = null;
    uploadListener = null;
    handler.dispose();
    handler = null;
    Fixture.tearDown();
  }

  @Test
  public void testCannotCreateWithNull() {
    try {
      new FileUploadHandler( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testInitialized() {
    assertThat( handler.getUploadUrl(), containsString( handler.getToken() ) );
    assertSame( handler, getRegisteredHandler( handler.getToken() ) );
  }

  @Test
  public void testDispose() {
    handler.dispose();

    assertNull( getRegisteredHandler( handler.getToken() ) );
  }

  @Test
  public void testGetReceiver() {
    FileUploadReceiver receiver = new TestFileUploadReceiver();
    FileUploadHandler handler = new FileUploadHandler( receiver );

    assertSame( receiver, handler.getReceiver() );
  }

  @Test
  public void testAddListenerWithNull() {
    try {
      handler.addUploadListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testAddListener() {
    handler.addUploadListener( uploadListener );

    TestFileUploadEvent event = new TestFileUploadEvent( handler );
    event.dispatchProgress();

    assertEquals( "progress.", uploadListener.getLog() );
    assertSame( event, uploadListener.getLastEvent() );
  }

  @Test
  public void testAddListenerTwice() {
    handler.addUploadListener( uploadListener );
    handler.addUploadListener( uploadListener );

    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "progress.", uploadListener.getLog() );
  }

  @Test
  public void testAddMultipleListeners() {
    TestFileUploadListener anotherUploadListener = new TestFileUploadListener();

    handler.addUploadListener( uploadListener );
    handler.addUploadListener( anotherUploadListener );
    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "progress.", uploadListener.getLog() );
    assertEquals( "progress.", anotherUploadListener.getLog() );
  }

  @Test
  public void testRemoveListenerWithNull() {
    handler.addUploadListener( uploadListener );

    try {
      handler.removeUploadListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testRemoveListener() {
    handler.addUploadListener( uploadListener );

    handler.removeUploadListener( uploadListener );
    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "", uploadListener.getLog() );
  }

  @Test
  public void testRemoveListenerTwice() {
    handler.addUploadListener( uploadListener );

    handler.removeUploadListener( uploadListener );
    handler.removeUploadListener( uploadListener );
    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "", uploadListener.getLog() );
  }

  @Test
  public void testRemoveOneOfTwoListeners() {
    handler.addUploadListener( uploadListener );
    TestFileUploadListener anotherUploadListener = new TestFileUploadListener();
    handler.addUploadListener( anotherUploadListener );

    handler.removeUploadListener( anotherUploadListener );
    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "progress.", uploadListener.getLog() );
    assertEquals( "", anotherUploadListener.getLog() );
  }

  @Test
  public void testUpload() throws IOException, ServletException {
    TestFileUploadReceiver receiver = new TestFileUploadReceiver();
    FileUploadHandler handler = new FileUploadHandler( receiver );
    String content = "Lorem ipsum dolor sit amet.";

    fakeUploadRequest( handler, content, "text/plain", "short.txt" );
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    assertEquals( content.length(), receiver.getTotal() );
  }

  @Test
  public void testUploadWithMaxLimit() throws IOException, ServletException {
    TestFileUploadReceiver receiver = new TestFileUploadReceiver();
    FileUploadHandler handler = new FileUploadHandler( receiver );
    handler.setMaxFileSize( 1000 );
    String content = "Lorem ipsum dolor sit amet.\n";

    fakeUploadRequest( handler, content, "text/plain", "short.txt" );
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    assertEquals( content.length(), receiver.getTotal() );
  }

  @Test
  public void testUploadWithExceedMaxLimit() throws IOException, ServletException {
    TestFileUploadReceiver receiver = new TestFileUploadReceiver();
    FileUploadHandler handler = new FileUploadHandler( receiver );
    handler.setMaxFileSize( 1000 );
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < 40; i++ ) {
      buffer.append( "Lorem ipsum dolor sit amet.\n" );
    }
    String content = buffer.toString();

    fakeUploadRequest( handler, content, "text/plain", "short.txt" );
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertEquals( HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, getResponseErrorStatus() );
    assertThat( getResponseContent(), containsString( "HTTP ERROR 413" ) );
  }

  @Test
  public void testUploadWithException() throws IOException, ServletException {
    FileUploadReceiver receiver = new FileUploadReceiver() {
      @Override
      public void receive( InputStream dataStream, FileDetails details ) throws IOException {
        throw new IOException( "the error message" );
      }
    };
    FileUploadHandler handler = new FileUploadHandler( receiver );

    fakeUploadRequest( handler, "The content", "text/plain", "short.txt" );
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertEquals( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getResponseErrorStatus() );
    assertThat( getResponseContent(), containsString( "the error message" ) );
  }

  @Test
  public void testServiceHandlerIsRegistered() throws IOException, ServletException {
    TestFileUploadListener listener = new TestFileUploadListener();
    handler.addUploadListener( listener );

    fakeUploadRequest( handler, "The content", "text/plain", "short.txt" );
    ServiceHandler serviceHandler = getApplicationContext().getServiceManager().getHandler();
    serviceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertNotNull( listener.getLastEvent() );
  }

  private static void fakeUploadRequest( FileUploadHandler handler,
                                         String content,
                                         String contentType,
                                         String fileName )
  {
    FileUploadTestUtil.fakeUploadRequest( handler, content, contentType, fileName );
  }

  private static FileUploadHandler getRegisteredHandler( String token ) {
    return FileUploadHandlerStore.getInstance().getHandler( token );
  }

  private static int getResponseErrorStatus() {
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    return response.getErrorStatus();
  }

  private static String getResponseContent() {
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    return response.getContent();
  }

}
