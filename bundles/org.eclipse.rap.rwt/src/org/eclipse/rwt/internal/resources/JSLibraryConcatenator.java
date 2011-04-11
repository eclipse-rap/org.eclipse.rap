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

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.internal.util.StreamWritingUtil;


public class JSLibraryConcatenator {
  private ByteArrayOutputStream jsConcatenator;
  private String hashCode;
  private byte[] compressed;
  private byte[] uncompressed;
  private String content;

  JSLibraryConcatenator() {
    content = "";
  }

  public static JSLibraryConcatenator getInstance() {
    return ( JSLibraryConcatenator )ApplicationContext.getSingleton( JSLibraryConcatenator.class );
  }

  public String getHashCode() {
    finishJSConcatenation();
    return hashCode;
  }

  public byte[] getCompressed() {
    finishJSConcatenation();
    return compressed;
  }

  public byte[] getUncompressed() {
    finishJSConcatenation();
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

  public void appendJSLibrary( File toWrite, int[] content ) {
    if( isAllowed( toWrite ) ) {
      for( int i = 0; i < content.length; i++ ) {
        jsConcatenator.write( content[ i ] );
        if( isLastCharacter( content, i ) ) {
          writeNewLine();
        }
      }
    }
  }

  //////////////////
  // helping methods

  // TODO [SystemStart]: use this method explicitly instead of beeing lazy invoked on first access
  private void finishJSConcatenation() {
    try {
      initialize();
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private boolean isAllowed( File toWrite ) {
    return jsConcatenator != null && toWrite.getName().endsWith( "js" );
  }

  private void writeNewLine() {
    jsConcatenator.write( '\n' );
  }

  private boolean isLastCharacter( int[] content, int i ) {
    return i == content.length - 1;
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
