/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;


public class StreamWritingUtil_Test extends TestCase {
  private static final byte[] CONTENT = new byte[] { 1 };

  public void testWrite() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    StreamWritingUtil.write( CONTENT, out );
    
    byte[] buffered = out.toByteArray();
    assertEquals( CONTENT.length, buffered.length );
    assertEquals( CONTENT[ 0 ], buffered[ 0 ] );
  }
  
  public void testWriteBuffered() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    StreamWritingUtil.writeBuffered( CONTENT, out );
    
    byte[] buffered = out.toByteArray();
    assertEquals( CONTENT.length, buffered.length );
    assertEquals( CONTENT[ 0 ], buffered[ 0 ] );
  }
}
