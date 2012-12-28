/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class TestUtil {

  public static String readContent( InputStream inputStream, String encoding )
    throws IOException, UnsupportedEncodingException
  {
    ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[ 40960 ];
    int read = inputStream.read( buffer );
    while( read != -1 ) {
      bufferOutputStream.write( buffer, 0, read );
      read = inputStream.read( buffer );
    }
    return bufferOutputStream.toString( encoding );
  }

}
