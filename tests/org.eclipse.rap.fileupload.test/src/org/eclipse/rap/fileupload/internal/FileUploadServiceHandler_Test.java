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
package org.eclipse.rap.fileupload.internal;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.TestAdapter;
import org.eclipse.rap.fileupload.test.FileUploadTestUtil;
import org.eclipse.rap.fileupload.test.TestFileUploadListener;
import org.eclipse.rap.fileupload.test.TestFileUploadReceiver;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class FileUploadServiceHandler_Test {

  private FileUploadServiceHandler serviceHandler;
  private TestFileUploadListener testListener;
  private TestFileUploadReceiver testReceiver;
  private FileUploadHandler uploadHandler;

  @Before
  public void setUp() {
    Fixture.setUp();
    serviceHandler = new FileUploadServiceHandler();
    testReceiver = new TestFileUploadReceiver();
    uploadHandler = new FileUploadHandler( testReceiver );
    testListener = new TestFileUploadListener();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUploadShortFile() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );
    String content = "Lorem ipsum dolor sit amet.";

    fakeUploadRequest( content, "text/plain", "short.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    assertEquals( "progress.finished.", testListener.getLog() );
    FileUploadEvent event = testListener.getLastEvent();
    assertEquals( "short.txt", event.getFileDetails()[ 0 ].getFileName() );
    assertEquals( "text/plain", event.getFileDetails()[ 0 ].getContentType() );
    assertTrue( event.getContentLength() > content.length() );
    assertEquals( event.getContentLength(), event.getBytesRead() );
    assertEquals( content, new String( testReceiver.getContent() ) );
  }

  @Test
  public void testUploadBigFile() throws IOException, ServletException {
    TestFileUploadListener testListener = new TestFileUploadListener() {
      @Override
      public void uploadProgress( FileUploadEvent info ) {
        log.append( "progress(" + info.getBytesRead() + "/" + info.getContentLength() + ").");
      }
    };
    uploadHandler.addUploadListener( testListener );
    String content = createExampleContent( 12000 );

    fakeUploadRequest( content, "text/plain", "test.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    String expected = "progress(4096/12134).progress(8174/12134).progress(12134/12134).finished.";
    assertEquals( expected, testListener.getLog() );
    FileUploadEvent uploadedItem = testListener.getLastEvent();
    assertEquals( content, new String( testReceiver.getContent() ) );
    assertEquals( "text/plain", uploadedItem.getFileDetails()[ 0 ].getContentType() );
  }

  @Test
  public void testCanUploadEmptyFile() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "", "text/plain", "empty.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    assertEquals( "progress.finished.", testListener.getLog() );
    assertEquals( "", new String( testReceiver.getContent() ) );
  }

  @Test
  public void testCanUploadFileWithoutContentType() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "Some content", null, "test.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( 0, getResponseErrorStatus() );
    assertEquals( "progress.finished.", testListener.getLog() );
    FileUploadEvent uploadedItem = testListener.getLastEvent();
    assertEquals( "Some content", new String( testReceiver.getContent() ) );
    assertEquals( null, uploadedItem.getFileDetails()[ 0 ].getContentType() );
  }

  @Test
  public void testUploadWithoutToken() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( null );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( HttpServletResponse.SC_FORBIDDEN, getResponseErrorStatus() );
    assertEquals( "", testListener.getLog() );
  }

  @Test
  public void testUploadWithInvalidToken() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "unknown-id" );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( HttpServletResponse.SC_FORBIDDEN, getResponseErrorStatus() );
    assertEquals( "", testListener.getLog() );
  }

  @Test
  public void testUploadWithGetRequest() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "Some content", "text/plain", "test.txt"  );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setMethod( "GET" );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( HttpServletResponse.SC_METHOD_NOT_ALLOWED, getResponseErrorStatus() );
    assertEquals( "", testListener.getLog() );
  }

  @Test
  public void testUploadRequestWithWrongContentType() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "Some content", "text/plain", "test.txt"  );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setContentType( "application/octet-stream" );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, getResponseErrorStatus() );
    assertEquals( "", testListener.getLog() );
  }

  @Test
  public void testUploadRequestWithoutBoundary() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "Some content", "text/plain", "test.txt"  );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setBody( "some bogus body content" );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( "progress.failed.", testListener.getLog() );
  }

  // Some browsers, such as IE and Opera, include path information, we cut them off for consistency
  @Test
  public void testOriginalFileNameWithPathSegment() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "some content", "text/plain", "/tmp/some.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    FileUploadEvent uploadedItem = testListener.getLastEvent();
    assertEquals( "some.txt", uploadedItem.getFileDetails()[ 0 ].getFileName() );
  }

  // Some browsers, such as IE and Opera, include path information, we cut them off for consistency
  @Test
  public void testOriginalFileWithWindowsPath() throws IOException, ServletException {
    uploadHandler.addUploadListener( testListener );

    fakeUploadRequest( "some content", "text/plain", "C:\\temp\\some.txt"  );
    serviceHandler.service( RWT.getRequest(), RWT.getResponse() );

    FileUploadEvent uploadedItem = testListener.getLastEvent();
    assertEquals( "some.txt", uploadedItem.getFileDetails()[ 0 ].getFileName() );
  }

  @Test
  public void testGetURL_returnsAbsoluteUrl() {
    String url = FileUploadServiceHandler.getUrl( "token" );

    assertThat( url, containsString( "/" ) );
  }

  @Test
  public void testGetURL_includesToken() {
    String url = FileUploadServiceHandler.getUrl( "foo" );

    assertThat( getQueryParameters( url ), hasItem( "token=foo" ) );
  }

  @Test
  public void testGetURL_isServiceHandlerUrl() {
    String url = FileUploadServiceHandler.getUrl( "foo" );

    assertThat( getQueryParameters( url ), hasItem( startsWith( "servicehandler=" ) ) );
  }

  private void fakeUploadRequest( String token ) {
    FileUploadTestUtil.fakeUploadRequest( token, "TestContent", "text/plain", "test.txt" );
  }

  private void fakeUploadRequest( String content, String contentType, String fileName ) {
    String token = TestAdapter.getTokenFor( uploadHandler );
    FileUploadTestUtil.fakeUploadRequest( token, content, contentType, fileName );
  }

  private static List<String> getQueryParameters( String url ) {
    int queryIndex = url.indexOf( '?' );
    String queryString = queryIndex == -1 ? "" : url.substring( queryIndex + 1 );
    return asList( queryString.split( "\\&" ) );
  }

  private static int getResponseErrorStatus() {
    TestResponse response = ( TestResponse )RWT.getResponse();
    return response.getErrorStatus();
  }

  private static String createExampleContent( int length ) {
    byte[] bytes = new byte[ length ];
    for( int i = 0; i < length; i++ ) {
      int col = i % 91;
      bytes[ i ] = ( byte )( col == 90 ? 10 : 33 + col );
    }
    return new String( bytes );
  }

}
