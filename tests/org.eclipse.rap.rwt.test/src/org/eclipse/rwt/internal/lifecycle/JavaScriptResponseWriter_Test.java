/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter.PROCESS_MESSAGE;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rwt.internal.util.HTTP;


public class JavaScriptResponseWriter_Test extends TestCase {

  private TestResponse response;
  private JavaScriptResponseWriter writer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    response = new TestResponse();
    writer = new JavaScriptResponseWriter( response );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testEmptyResponse() {
    assertEquals( "", getMessage() );
  }

  public void testWrite() throws Exception {
    writer.write( " Text " );
    assertEquals( " Text ", getMessage() );
  }

  public void testEncoding() {
    String responseEncoding = response.getCharacterEncoding().toUpperCase( Locale.ENGLISH );
    assertEquals( HTTP.CHARSET_UTF_8, responseEncoding );
  }

  public void testContentType() {
    assertEquals( HTTP.CONTENT_TEXT_JAVASCRIPT, response.getContentType() );
  }

  public void testJavascriptJson() {
    writer.write( "javascript;" );
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.finish();

    String expected = "javascript;" + PROCESS_MESSAGE + "( {";
    assertTrue( getMessage().startsWith( expected ) );
  }

  public void testJsonJavascript() {
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.write( "javascript;" );
    writer.finish();

    assertTrue( getMessage().endsWith( "} );/*EOM*/javascript;" ) );
  }

  public void testJavascriptJsonJavascript() {
    writer.write( "javascript;" );
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.write( "javascript2;" );
    writer.finish();

    String message = getMessage();
    assertTrue( message.startsWith( "javascript;" + PROCESS_MESSAGE + "( {" ) );
    assertTrue( message.endsWith( "} );/*EOM*/javascript2;" ) );
  }

  public void testJsonJavascriptJson() {
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.write( "javascript;" );
    writer.getProtocolWriter().appendSet( "target2", "foo", 23 );
    writer.finish();

    String message = getMessage();
    assertTrue( message.startsWith( PROCESS_MESSAGE + "( {" ) );
    assertTrue( message.contains( "javascript;" + PROCESS_MESSAGE ) );
  }

  private String getMessage() {
    return response.getContent();
  }
}
