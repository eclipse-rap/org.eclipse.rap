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
package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.internal.util.StreamWritingUtil;


public class JSLibraryConcatenator {
  private ByteArrayOutputStream jsConcatenator;
  private String hashCode;
  private byte[] compressed;
  private byte[] uncompressed;
  private String content;

  public JSLibraryConcatenator() {
    content = "";
  }

  public String getHashCode() {
    return hashCode;
  }

  public byte[] getCompressed() {
    return compressed;
  }

  public byte[] getUncompressed() {
    return uncompressed;
  }

  public String getContent() {
    if( jsConcatenator != null ) {
      try {
        content = jsConcatenator.toString( HTTP.CHARSET_UTF_8 );
      } catch( UnsupportedEncodingException shouldNotHappen ) {
        throw new RuntimeException( shouldNotHappen );
      }
      jsConcatenator = null;
    }
    return content;
  }

  public void startJSConcatenation() {
    jsConcatenator = new ByteArrayOutputStream();
  }

  public void appendJSLibrary( File toWrite, byte[] content ) {
    if( isAllowed( toWrite ) ) {
      for( int i = 0; i < content.length; i++ ) {
        jsConcatenator.write( content[ i ] );
        if( isLastCharacter( content, i ) ) {
          writeNewLine();
        }
      }
    }
  }

  public void activate() {
    try {
      initialize();
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }
  
  public void deactivate() {
    jsConcatenator = null;
    hashCode = null;
    compressed = null;
    uncompressed = null;
    content = null;
  }
  
  //////////////////
  // helping methods

  private boolean isAllowed( File toWrite ) {
    return jsConcatenator != null && toWrite.getName().endsWith( "js" );
  }

  private void writeNewLine() {
    jsConcatenator.write( '\n' );
  }

  private static boolean isLastCharacter( byte[] content, int position ) {
    return position == content.length - 1;
  }

  private void initialize() throws UnsupportedEncodingException, IOException {
    synchronized( JSLibraryServiceHandler.class ) {
      if( !isInitialized() ) {
        initializeUncompressed();
        initializeCompressed();
        initializeHashCode();
      }
    }
  }

  private boolean isInitialized() {
    return uncompressed != null;
  }

  private void initializeCompressed() throws IOException {
    // Note [fappel]: We do not close all streams or writers, since this is
    //                not so crucial here as we only do in-memory opperations.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzipStream = new GZIPOutputStream( baos );
    StreamWritingUtil.write( uncompressed, gzipStream );
    gzipStream.close();
    compressed = baos.toByteArray();
  }

  private void initializeHashCode() {
    hashCode = "H" + getContent().hashCode();
  }

  private void initializeUncompressed() throws UnsupportedEncodingException {
    uncompressed = getContent().getBytes( HTTP.CHARSET_UTF_8 );
  }
}
