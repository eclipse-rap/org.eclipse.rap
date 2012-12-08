/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;


public class StreamUtil_Test extends TestCase {
  private static final byte[] CONTENT = new byte[] { 1 };

  public void testWrite() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    StreamUtil.write( CONTENT, out );
    
    byte[] buffered = out.toByteArray();
    assertEquals( CONTENT.length, buffered.length );
    assertEquals( CONTENT[ 0 ], buffered[ 0 ] );
  }
  
  public void testWriteBuffered() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    StreamUtil.writeBuffered( CONTENT, out );
    
    byte[] buffered = out.toByteArray();
    assertEquals( CONTENT.length, buffered.length );
    assertEquals( CONTENT[ 0 ], buffered[ 0 ] );
  }
  
  
  @SuppressWarnings( "resource" )
  public void testClose() throws IOException {
    InputStream inputStream = mock( InputStream.class );
    
    StreamUtil.close( inputStream );
    
    verify( inputStream ).close();
  }
  
  @SuppressWarnings( "resource" )
  public void testCloseWithException() throws IOException {
    InputStream inputStream = mock( InputStream.class );
    doThrow( new IOException() ).when( inputStream ).close();
    try {
      StreamUtil.close( inputStream );
      fail();
    } catch( RuntimeException expected ) {
    }
  }
}
