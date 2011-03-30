/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class HtmlResponseWriter_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  //////////////////////
  // actual testing code

  public void testWriteMethods() throws Exception {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.write( new char[] { 'a', 'b' } );
    writer.write( new char[] { 'a', 'b', '|', 'c', 'd' }, 2, 3 );
    writer.write( 124 );
    writer.write( "Token 3" );
    writer.write( "my|Token 4|trallala", 2, 8 );
    String result = getContents( writer );
    assertEquals( "ab|cd|Token 3|Token 4", result );
  }

  public void testFlush() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.write( "foo" );
    assertEquals( "foo", getContents( writer ) );
    writer.flush();
    assertEquals( "foo", getContents( writer ) );
  }

  public void testClosedAssertions() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.close();
    try {
      writer.close();
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.close();
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.flush();
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.write( new char[]{ 'X' } );
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.write( new char[]{ 'X' }, 0, 1 );
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.write( 13 );
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.write( "xxx" );
      fail();
    } catch( IllegalStateException ioe ) {
    }
    try {
      writer.write( "xxx", 0, 3 );
      fail();
    } catch( IllegalStateException ioe ) {
    }
  }

  public void testPrintContents() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    StringWriter stringWriter = new StringWriter();
    writer.printContents( new PrintWriter( stringWriter ) );
    assertEquals( "", stringWriter.getBuffer().toString() );
    writer.write( "Test" );
    writer.printContents( new PrintWriter( stringWriter ) );
    assertEquals( "Test", stringWriter.getBuffer().toString() );
  }

  private static String getContents( HtmlResponseWriter writer ) {
    StringWriter recorder = new StringWriter();
    writer.printContents( new PrintWriter( recorder ) );
    return recorder.getBuffer().toString();
  }
}
