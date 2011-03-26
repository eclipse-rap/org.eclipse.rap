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

import java.io.IOException;

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

  public void testTokenAppending() throws Exception {
    HtmlResponseWriter tokenBuffer = new HtmlResponseWriter();
    tokenBuffer.append( "|Token 1" );
    tokenBuffer.append( "|Token 2" );
    tokenBuffer.write( new char[] { '|', 'a', 'b' } );
    tokenBuffer.write( new char[] { 'a', 'b', '|', 'c', 'd' }, 2, 3 );
    tokenBuffer.write( 124 );
    tokenBuffer.write( "Token 3" );
    tokenBuffer.write( "my|Token 4|trallala", 2, 8 );
    String result = tokenBuffer.getContents();
    assertEquals( "|Token 1|Token 2|ab|cd|Token 3|Token 4", result );
  }

  public void testFlush() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.write( "foo" );
    assertEquals( "foo", writer.getContents() );
    writer.flush();
    assertEquals( "foo", writer.getContents() );
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

  public void testUseJSLibrary() {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    try {
      writer.useJSLibrary( null );
      fail( "NullPointerException expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    writer.useJSLibrary( "z" );
    writer.useJSLibrary( "a" );
    writer.useJSLibrary( "b" );
    writer.useJSLibrary( "a" );
    String[] libraries = writer.getJSLibraries();
    assertEquals( 3, libraries.length );
    String[] expected = new String[] { "z", "a", "b" };
    for( int i = 0; i < expected.length; i++ ) {
      assertEquals( expected[ i ], libraries[ i ] );
    }
  }

  public void testGetResults() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    assertEquals( "", writer.getContents() );
    writer.write( "Test" );
    assertEquals( "Test", writer.getContents() );
  }
}
