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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadReceiver;


public class TestFileUploadReceiver extends FileUploadReceiver {

  long total = 0;
  private byte[] uploadedContent;

  @Override
  public void receive( InputStream dataStream, FileDetails details ) throws IOException {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[ 4096 ];
    boolean finished = false;
    while( !finished ) {
      int read = dataStream.read( buffer );
      if( read != -1 ) {
        outputStream.write( buffer, 0, read );
        total += read;
      } else {
        uploadedContent = outputStream.toByteArray();
        finished = true;
      }
    }
  }

  public long getTotal() {
    return total;
  }

  public byte[] getContent() {
    return uploadedContent;
  }

}
