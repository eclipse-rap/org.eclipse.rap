/*******************************************************************************
 * Copyright (c) 2011, 2024 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.ProgressListener;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadReceiver;
import org.eclipse.rap.fileupload.UploadSizeLimitExceededException;
import org.eclipse.rap.fileupload.UploadTimeLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


final class FileUploadProcessor {

  private final FileUploadHandler handler;
  private final FileUploadTracker tracker;
  private String fileName;
  private long deadline;

  FileUploadProcessor( FileUploadHandler handler ) {
    this.handler = handler;
    tracker = new FileUploadTracker( handler );
    deadline = -1;
  }

  void handleFileUpload( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( handler.getUploadTimeLimit() > 0 ) {
      deadline = System.currentTimeMillis() + handler.getUploadTimeLimit();
    }
    try {
      JakartaServletFileUpload upload = createUpload();
      FileItemInputIterator iter = upload.getItemIterator( request );
      while( iter.hasNext() ) {
        FileItemInput item = iter.next();
        if( !item.isFormField() ) {
          receive( item );
        }
      }
      if( tracker.isEmpty() ) {
        String errorMessage = "No file upload data found in request";
        tracker.setException( new Exception( errorMessage ) );
        tracker.handleFailed();
        response.sendError( HttpServletResponse.SC_BAD_REQUEST, errorMessage );
      } else {
        tracker.handleFinished();
      }
    } catch( Exception exception ) {
      Throwable cause = exception.getCause();
      if( exception instanceof FileUploadByteCountLimitException ) {
        long sizeLimit = handler.getMaxFileSize();
        exception = new UploadSizeLimitExceededException( sizeLimit, fileName );
      } else if( cause instanceof UploadTimeLimitExceededException ) {
        exception = ( UploadTimeLimitExceededException )cause;
      }
      tracker.setException( exception );
      tracker.handleFailed();
      int errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      if( exception instanceof UploadSizeLimitExceededException ) {
        errorCode = HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
      } else if( exception instanceof UploadTimeLimitExceededException ) {
        errorCode = HttpServletResponse.SC_REQUEST_TIMEOUT;
      }
      response.sendError( errorCode, exception.getMessage() );
    }
  }

  private JakartaServletFileUpload createUpload() {
    JakartaServletFileUpload upload = new JakartaServletFileUpload();
    upload.setFileSizeMax( handler.getMaxFileSize() );
    upload.setProgressListener( createProgressListener() );
    return upload;
  }

  private ProgressListener createProgressListener() {
    ProgressListener result = new ProgressListener() {
      long prevTotalBytesRead = -1;
      @Override
      public void update( long totalBytesRead, long contentLength, int item ) {
        // Depending on the servlet engine and other environmental factors,
        // this listener may be notified for every network packet, so don't notify unless there
        // is an actual increase.
        if ( totalBytesRead > prevTotalBytesRead ) {
          if( deadline > 0 && System.currentTimeMillis() > deadline ) {
            long timeLimit = handler.getUploadTimeLimit();
            Exception exception = new UploadTimeLimitExceededException( timeLimit, fileName );
            throw new RuntimeException( exception );
          }
          prevTotalBytesRead = totalBytesRead;
          tracker.setContentLength( contentLength );
          tracker.setBytesRead( totalBytesRead );
          tracker.handleProgress();
        }
      }
    };
    return result;
  }

  private void receive( FileItemInput item ) throws IOException {
    InputStream stream = item.getInputStream();
    try {
      fileName = FilenameUtils.getName( item.getName() );
      String contentType = item.getContentType();
      FileDetails details = new FileDetailsImpl( fileName, contentType );
      FileUploadReceiver receiver = handler.getReceiver();
      receiver.receive( stream, details );
      tracker.addFile( details );
    } finally {
      stream.close();
    }
  }

}
