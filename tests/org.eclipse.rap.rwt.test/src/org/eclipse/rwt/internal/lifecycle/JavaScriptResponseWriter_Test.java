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
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.ExecuteScriptOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
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
    writer.write( "  alert( 23 );  " );
    writer.finish();

    Message message = new Message( getMessage() );

    assertSingleJson();
    assertEquals( 1, message.getOperationCount() );
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "text/javascript", operation.getScriptType() );
    assertEquals( "alert( 23 );", operation.getScript() );
  }

  public void testEncoding() {
    String responseEncoding = response.getCharacterEncoding().toUpperCase( Locale.ENGLISH );
    assertEquals( HTTP.CHARSET_UTF_8, responseEncoding );
  }

  public void testContentType() {
    // obsolete, but IE <= 8 does not recognize application/javascript correctly
    assertEquals( "text/javascript", response.getContentType() );
  }

  public void testJavascriptJson() {
    writer.write( "alert();" );
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.finish();

    assertSingleJson();
    Message message = new Message( getMessage() );
    assertEquals( 2, message.getOperationCount() );

    ExecuteScriptOperation executeOperation = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "alert();", executeOperation.getScript() );

    SetOperation setOperation = ( SetOperation )message.getOperation( 1 );
    assertEquals( "target", setOperation.getTarget() );
  }

  public void testJsonJavascript() {
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.write( "alert();" );
    writer.finish();

    assertSingleJson();
    Message message = new Message( getMessage() );
    assertEquals( 2, message.getOperationCount() );

    SetOperation setOperation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "target", setOperation.getTarget() );
    assertEquals( Integer.valueOf( 23 ), setOperation.getProperty( "foo" ) );

    ExecuteScriptOperation executeOperation = ( ExecuteScriptOperation )message.getOperation( 1 );
    assertEquals( "alert();", executeOperation.getScript() );
  }

  public void testJavascriptJsonJavascript() {
    writer.write( "alert( 1 );" );
    writer.getProtocolWriter().appendSet( "target", "foo", 23 );
    writer.write( "alert( 2 );" );
    writer.finish();

    assertSingleJson();
    Message message = new Message( getMessage() );
    assertEquals( 3, message.getOperationCount() );

    ExecuteScriptOperation executeOperation1 = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "alert( 1 );", executeOperation1.getScript() );

    SetOperation setOperation = ( SetOperation )message.getOperation( 1 );
    assertEquals( "target", setOperation.getTarget() );

    ExecuteScriptOperation executeOperation2 = ( ExecuteScriptOperation )message.getOperation( 2 );
    assertEquals( "alert( 2 );", executeOperation2.getScript() );
  }

  public void testJsonJavascriptJson() {
    writer.getProtocolWriter().appendSet( "target1", "foo", 23 );
    writer.write( "alert( 1 );" );
    writer.getProtocolWriter().appendSet( "target2", "foo", 23 );
    writer.finish();

    assertSingleJson();
    Message message = new Message( getMessage() );
    assertEquals( 3, message.getOperationCount() );

    SetOperation setOperation1 = ( SetOperation )message.getOperation( 0 );
    assertEquals( "target1", setOperation1.getTarget() );

    ExecuteScriptOperation executeOperation1 = ( ExecuteScriptOperation )message.getOperation( 1 );
    assertEquals( "alert( 1 );", executeOperation1.getScript() );

    SetOperation setOperation2 = ( SetOperation )message.getOperation( 2 );
    assertEquals( "target2", setOperation2.getTarget() );
  }

  public void testJavascriptIsConcatenated() {
    writer.write( "var x = foo();" );
    writer.write( "x.bar( 23 );" );
    writer.finish();

    assertSingleJson();
    Message message = new Message( getMessage() );
    assertEquals( 1, message.getOperationCount() );

    ExecuteScriptOperation operation = ( ExecuteScriptOperation )message.getOperation( 0 );
    assertEquals( "var x = foo();x.bar( 23 );", operation.getScript() );
  }

  private void assertSingleJson() {
    String json = extractJson( getMessage() );
    assertTrue( json.startsWith( "{" ) );
    assertTrue( json.endsWith( "}" ) );
  }

  private String getMessage() {
    return response.getContent();
  }

  private static String extractJson( String message ) {
    String head = PROCESS_MESSAGE + "(";
    String tail = ");/*EOM*/";
    if( !message.startsWith( head ) || !message.endsWith( tail ) ) {
      throw new IllegalArgumentException( "Message is not enclosed in processMessage(): " + message );
    }
    String json = message.substring( head.length(), message.length() - tail.length() ).trim();
    if( json.contains( PROCESS_MESSAGE ) ) {
      throw new IllegalArgumentException( "Duplicate processMessage() in message: " + message );
    }
    return json;
  }
}
