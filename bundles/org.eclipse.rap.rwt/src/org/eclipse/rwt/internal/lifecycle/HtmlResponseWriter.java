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

import org.eclipse.rwt.internal.util.ParamCheck;


public class HtmlResponseWriter extends Writer {

  private final List body = new ArrayList();

  private boolean closed;

  private List jsLibraries = new ArrayList();

  ///////////////////////////////////////////////////
  // control methods for javascript library rendering

  /**
   * Returns the names of the JavaScript libraries that the components which were rendered into this
   * HtmlResponseWriter need.
   */
  public String[] getJSLibraries() {
    String[] result = new String[ jsLibraries.size() ];
    jsLibraries.toArray( result );
    return result;
  }

  /**
   * Informs the HtmlResponseWriter that the given library is needed. This will cause a
   * &lt;script&gt;-tag referencing the library to be rendered at the adequate place.
   * <p>
   * Prior to calling this method, the given <code>libraryName</code> must be registered with the
   * {@link org.eclipse.rwt.resources.IResourceManager IResourceManager} using one of these two
   * register-methods: {@link org.eclipse.rwt.resources.IResourceManager#register(String, String)
   * register(String, String)},
   * {@link org.eclipse.rwt.resources.IResourceManager#register(String, String, org.eclipse.rwt.resources.IResourceManager.RegisterOptions)
   * register(String, String, RegisterOptions)}
   * </p>
   * <p>
   * Calling this method for an already registered <code>libraryName</code> has no effect.
   * </p>
   * 
   * @param libraryName the name of the library, must not be <code>null</code>.
   */
  public void useJSLibrary( String libraryName ) {
    ParamCheck.notNull( libraryName, "libraryName" );
    if( !jsLibraries.contains( libraryName ) ) {
      jsLibraries.add( libraryName );
    }
  }

  //////////////////
  // response writer

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

  ///////////////////
  // content delivery

  public void printContents( PrintWriter writer ) {
    for( int i = 0; i < body.size(); i++ ) {
      writer.print( body.get( i ).toString() );
    }
  }

  // helping methods
  //////////////////

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
