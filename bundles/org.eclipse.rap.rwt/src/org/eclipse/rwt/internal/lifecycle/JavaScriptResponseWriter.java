/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

  public static final String PROCESS_MESSAGE = "org.eclipse.rwt.protocol.Processor.processMessage";

  private final PrintWriter writer;
  private ProtocolMessageWriter protocolWriter;

  public JavaScriptResponseWriter( ServletResponse response ) throws IOException {
    configureResponseContentEncoding( response );
    writer = response.getWriter();
  }

  public void write( String content ) {
    writePendingProtocolMessage();
    writer.write( content );
  }

  public boolean checkError() {
    return writer.checkError();
  }

  public void finish() {
    writePendingProtocolMessage();
  }

  public ProtocolMessageWriter getProtocolWriter() {
    if( protocolWriter == null ) {
      protocolWriter = new ProtocolMessageWriter();
    }
    return protocolWriter;
  }

  private void writePendingProtocolMessage() {
    if( protocolWriter != null ) {
      writer.write( PROCESS_MESSAGE + "( " );
      writer.write( protocolWriter.createMessage() );
      writer.write( " );/*EOM*/" );
    }
    protocolWriter = null;
  }

  private static void configureResponseContentEncoding( ServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TEXT_JAVASCRIPT );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }
}
