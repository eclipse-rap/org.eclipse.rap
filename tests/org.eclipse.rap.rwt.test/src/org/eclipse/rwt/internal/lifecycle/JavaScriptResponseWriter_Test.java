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

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rwt.TestResponse;
import org.eclipse.rwt.internal.util.HTTP;


public class JavaScriptResponseWriter_Test extends TestCase {

  private TestResponse response;
  private JavaScriptResponseWriter writer;

  protected void setUp() throws Exception {
    response = new TestResponse();
    writer = new JavaScriptResponseWriter( response );
  }

  public void testEmptyResponse() {
    assertEquals( "", getContents() );
  }

  public void testWrite() throws Exception {
    writer.write( " Text " );
    assertEquals( " Text ", getContents() );
  }
  
  public void testEncoding() {
    String responseEncoding = response.getCharacterEncoding().toUpperCase( Locale.ENGLISH );
    assertEquals( HTTP.CHARSET_UTF_8, responseEncoding );
  }
  
  public void testContentType() {
    assertEquals( HTTP.CONTENT_TEXT_JAVASCRIPT, response.getContentType() );
  }

  private String getContents() {
    return response.getContent();
  }
}
