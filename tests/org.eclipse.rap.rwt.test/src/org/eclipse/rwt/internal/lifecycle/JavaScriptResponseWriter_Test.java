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

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;


public class JavaScriptResponseWriter_Test extends TestCase {

  private StringWriter recorder;
  private JavaScriptResponseWriter writer;

  protected void setUp() throws Exception {
    recorder = new StringWriter();
    writer = new JavaScriptResponseWriter( new PrintWriter( recorder ) );
  }

  public void testEmptyResponse() {
    assertEquals( "", getContents() );
  }

  public void testWrite() throws Exception {
    writer.write( " Text " );
    assertEquals( " Text ", getContents( ) );
  }

  private String getContents() {
    return recorder.getBuffer().toString();
  }
}
