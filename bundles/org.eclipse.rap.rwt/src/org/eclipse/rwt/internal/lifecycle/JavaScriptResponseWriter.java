/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class JavaScriptResponseWriter extends Writer {

  private final List body = new ArrayList();

  private boolean closed;

  public void write( char[] cbuf, int off, int len ) throws IOException {
    checkIfWriterClosed();
    append( String.valueOf( cbuf, off, len ) );
  }

  public void write( int c ) throws IOException {
    checkIfWriterClosed();
    append( new String( new char[] { ( char )c } ) );
  }

  public void write( String content ) throws IOException {
    checkIfWriterClosed();
    append( content );
  }

  public void write( String str, int off, int len ) throws IOException {
    checkIfWriterClosed();
    append( str.substring( off, off + len ) );
  }

  public void flush() throws IOException {
    checkIfWriterClosed();
  }

  public void close() throws IOException {
    checkIfWriterClosed();
    closed = true;
  }

  public void printContents( PrintWriter writer ) {
    for( int i = 0; i < body.size(); i++ ) {
      writer.print( body.get( i ).toString() );
    }
  }

  private void append( String token ) {
    body.add( token );
  }

  private void checkIfWriterClosed() {
    if( closed ) {
      String msg = "Operation is not allowed since the writer was closed.";
      throw new IllegalStateException( msg );
    }
  }
}
