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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletResponse;

import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.util.HTTP;


public class JavaScriptResponseWriter {

  private final PrintWriter writer;
  private final ProtocolMessageWriter protocolWriter;

  public JavaScriptResponseWriter( ServletResponse response ) throws IOException {
    configureResponseContentEncoding( response );
    writer = response.getWriter();
    protocolWriter = new ProtocolMessageWriter( writer );
  }

  public void write( String content ) {
    writer.write( content );
  }

  public ProtocolMessageWriter getProtocolWriter() {
    return protocolWriter;
  }

  private static void configureResponseContentEncoding( ServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TEXT_JAVASCRIPT );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }
}
