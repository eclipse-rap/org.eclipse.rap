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


public class JavaScriptResponseWriter_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testWriteMethods() throws Exception {
    JavaScriptResponseWriter writer = new JavaScriptResponseWriter();
    writer.write( " Text " );
    String result = getContents( writer );
    assertEquals( " Text ", result );
  }

  public void testPrintContents() {
    JavaScriptResponseWriter writer = new JavaScriptResponseWriter();
    StringWriter stringWriter = new StringWriter();
    writer.printContents( new PrintWriter( stringWriter ) );
    assertEquals( "", stringWriter.getBuffer().toString() );
    writer.write( "Test" );
    writer.printContents( new PrintWriter( stringWriter ) );
    assertEquals( "Test", stringWriter.getBuffer().toString() );
  }

  private static String getContents( JavaScriptResponseWriter writer ) {
    StringWriter recorder = new StringWriter();
    writer.printContents( new PrintWriter( recorder ) );
    return recorder.getBuffer().toString();
  }
}
