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
package org.eclipse.rap.fileupload.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.TestAdapter;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;


@SuppressWarnings( "restriction" )
public final class FileUploadTestUtil {

  private FileUploadTestUtil() {
    // prevent instantiation
  }

  public static File createTempDirectory() {
    File result;
    try {
      result = File.createTempFile( "temp-", "-dir" );
    } catch( IOException exception ) {
      throw new RuntimeException( "Could not create temp file", exception );
    }
    if( !result.delete() ) {
      throw new RuntimeException( "Could not delete temp file: " + result.getAbsolutePath() );
    }
    if( !result.mkdir() ) {
      throw new RuntimeException( "Could not create temp directory: " + result.getAbsolutePath() );
    }
    return result;
  }

  public static void deleteRecursively( File file ) {
    if( file.exists() ) {
      File[] files = file.listFiles();
      if( files != null ) {
        for( int i = 0; i < files.length; i++ ) {
          deleteRecursively( files[ i ] );
        }
      }
      boolean deleted = file.delete();
      if( !deleted ) {
        throw new RuntimeException( "Could not delete file or directory: " + file.getAbsolutePath() );
      }
    }
  }

  public static void fakeUploadRequest( FileUploadHandler handler,
                                        String content,
                                        String contentType,
                                        String fileName )
  {
    String token = TestAdapter.getTokenFor( handler );
    fakeUploadRequest( token, content, contentType, fileName );
  }

  public static void fakeUploadRequest( String token,
                                        String content,
                                        String contentType,
                                        String fileName )
  {
    TestRequest request = Fixture.fakeNewRequest();
    request.setMethod( "POST" );
    request.setParameter( "servicehandler", "org.eclipse.rap.fileupload" );
    String boundary = "-----4711-----";
    String body = createMultipartBody( boundary, new FileData( content, contentType, fileName ) );
    if( token != null ) {
      request.setParameter( "token", token );
    }
    request.setBody( body );
    request.setContentType( "multipart/form-data; boundary=" + boundary );
  }

  public static TestRequest createFakeUploadRequest( String content,
                                                     String contentType,
                                                     String fileName )
  {
    TestRequest request = new TestRequest();
    String boundary = "-----4711-----";
    String body = createMultipartBody( boundary, new FileData( content, contentType, fileName ) );
    request.setMethod( "POST" );
    request.setBody( body );
    request.setContentType( "multipart/form-data; boundary=" + boundary );
    return request;
  }

  public static void fakeUploadRequest( FileUploadHandler handler, FileData... fileData ) {
    fakeUploadRequest( TestAdapter.getTokenFor( handler ), fileData );
  }

  public static void fakeUploadRequest( String token, FileData... fileData ) {
    TestRequest request = Fixture.fakeNewRequest();
    request.setMethod( "POST" );
    request.setParameter( "servicehandler", "org.eclipse.rap.fileupload" );
    String boundary = "-----4711-----";
    String body = createMultipartBody( boundary, fileData );
    if( token != null ) {
      request.setParameter( "token", token );
    }
    request.setBody( body );
    request.setContentType( "multipart/form-data; boundary=" + boundary );
  }


  private static String createMultipartBody( String boundary, FileData... fileData ) {
    StringBuffer buffer = new StringBuffer();
    String newline = "\r\n";
    for( int i = 0; i < fileData.length; i++ ) {
      buffer.append( "--" );
      buffer.append( boundary );
      buffer.append( newline );
      buffer.append( "Content-Disposition: form-data; name=\"file\"; filename=\"" );
      buffer.append( fileData[ i ].fileName );
      buffer.append( "\"" );
      buffer.append( newline );
      if( fileData[ i ].contentType != null ) {
        buffer.append( "Content-Type: " );
        buffer.append( fileData[ i ].contentType );
        buffer.append( newline );
      }
      buffer.append( newline );
      buffer.append( fileData[ i ].content );
      buffer.append( newline );
    }
    buffer.append( "--" );
    buffer.append( boundary );
    buffer.append( "--" );
    buffer.append( newline );
    return buffer.toString();
  }

  public static String getFileContents( File testFile ) {
    // TODO [rst] Buffer can get very big with the wrong file
    byte[] buffer = new byte[ ( int )testFile.length() ];
    try (BufferedInputStream bis = new BufferedInputStream( new FileInputStream( testFile ) ) ) {
      bis.read( buffer );
      return new String( buffer );
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  public final static class FileData {

    public final String content;
    public final String contentType;
    public final String fileName;

    public FileData( String content, String contentType, String fileName ) {
      this.content = content;
      this.contentType = contentType;
      this.fileName = fileName;
    }

  }

}
